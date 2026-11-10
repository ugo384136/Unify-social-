package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.UnifyCyan
import com.example.ui.theme.UnifyViolet
import kotlinx.coroutines.delay

@Composable
fun VoiceWaveformPlayer(
  durationSec: Int,
  waveform: List<Float>?,
  modifier: Modifier = Modifier,
  tintColor: Color = UnifyCyan,
  showLabel: Boolean = true
) {
  var isPlaying by remember { mutableStateOf(false) }
  var playbackProgress by remember { mutableFloatStateOf(0f) }

  val defaultWaveform = remember {
    waveform ?: listOf(0.2f, 0.5f, 0.8f, 0.4f, 0.9f, 0.6f, 0.3f, 0.7f, 0.5f, 0.8f, 0.4f, 0.2f)
  }

  // Simulated playback timer
  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      val totalSteps = durationSec * 10
      while (isPlaying && playbackProgress < 1f) {
        delay(100)
        playbackProgress += 1f / totalSteps
      }
      if (playbackProgress >= 1f) {
        isPlaying = false
        playbackProgress = 0f
      }
    }
  }

  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .testTag("voice_player_surface"),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    shape = RoundedCornerShape(16.dp),
    tonalElevation = 2.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Play/Pause Button
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(tintColor)
          .clickable {
            if (isPlaying) {
              isPlaying = false
            } else {
              if (playbackProgress >= 1f) playbackProgress = 0f
              isPlaying = true
            }
          }
          .testTag("play_pause_voice_btn"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
          contentDescription = if (isPlaying) "Pause voice note" else "Play voice note",
          tint = Color.Black,
          modifier = Modifier.size(20.dp)
        )
      }

      // Animated Waveform Bars
      Row(
        modifier = Modifier
          .weight(1f)
          .height(28.dp)
          .clickable {
            // Tap waveform to reset/play
            isPlaying = !isPlaying
          },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
      ) {
        defaultWaveform.forEachIndexed { index, weight ->
          val barProgress = index.toFloat() / defaultWaveform.size
          val isPassed = barProgress <= playbackProgress
          val barHeight = (10 + (weight * 18)).dp

          Box(
            modifier = Modifier
              .weight(1f)
              .height(barHeight)
              .clip(RoundedCornerShape(2.dp))
              .background(
                if (isPassed) tintColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
              )
          )
        }
      }

      // Duration readout
      if (showLabel) {
        val currentSeconds = (playbackProgress * durationSec).toInt()
        val formattedTime = String.format("%02d:%02d", currentSeconds / 60, currentSeconds % 60)
        Text(
          text = if (isPlaying) formattedTime else "${durationSec}s",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
