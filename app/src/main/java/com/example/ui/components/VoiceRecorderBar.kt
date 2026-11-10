package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.UnifyCyan
import com.example.ui.theme.UnifyRose
import kotlinx.coroutines.delay

@Composable
fun VoiceRecorderBar(
  onCancel: () -> Unit,
  onSendVoice: (durationSec: Int) -> Unit,
  modifier: Modifier = Modifier,
  label: String = "Recording voice comment..."
) {
  var secondsRecorded by remember { mutableIntStateOf(0) }

  // Timer loop
  LaunchedEffect(Unit) {
    while (true) {
      delay(1000)
      secondsRecorded++
    }
  }

  // Pulsing recording indicator animation
  val infiniteTransition = rememberInfiniteTransition(label = "rec_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(24.dp))
      .testTag("voice_recorder_bar"),
    color = MaterialTheme.colorScheme.surfaceVariant,
    tonalElevation = 6.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Cancel button
      IconButton(
        onClick = onCancel,
        modifier = Modifier
          .size(36.dp)
          .testTag("cancel_voice_record_btn")
      ) {
        Icon(
          imageVector = Icons.Default.Close,
          contentDescription = "Cancel recording",
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Pulsing red dot
      Box(
        modifier = Modifier
          .size(12.dp)
          .scale(pulseScale)
          .clip(CircleShape)
          .background(UnifyRose)
      )

      // Duration & Label
      Column(modifier = Modifier.weight(1f)) {
        val minutes = secondsRecorded / 60
        val remainingSec = secondsRecorded % 60
        Text(
          text = String.format("%02d:%02d", minutes, remainingSec),
          style = MaterialTheme.typography.titleSmall,
          color = UnifyRose
        )
        Text(
          text = label,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      // Simulated Waveform Animation
      Row(
        modifier = Modifier
          .width(70.dp)
          .height(20.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        repeat(6) { index ->
          val barScale = ((secondsRecorded + index) % 4 + 1) * 4
          Box(
            modifier = Modifier
              .weight(1f)
              .height(barScale.dp)
              .clip(RoundedCornerShape(2.dp))
              .background(UnifyCyan.copy(alpha = 0.8f))
          )
        }
      }

      // Send Button
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(UnifyCyan)
          .clickable {
            onSendVoice(maxOf(2, secondsRecorded))
          }
          .testTag("send_voice_note_btn"),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Send,
          contentDescription = "Send voice recording",
          tint = Color.Black,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
