package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.model.UserStories
import com.example.ui.theme.UnifyCyan
import kotlinx.coroutines.delay

@Composable
fun StoryViewerModal(
  userStories: UserStories,
  initialIndex: Int = 0,
  onClose: () -> Unit,
  onStoryFinished: () -> Unit,
  onReactionSent: (String) -> Unit
) {
  var currentIndex by remember { mutableIntStateOf(initialIndex) }
  var isPaused by remember { mutableStateOf(false) }
  var quickReplyText by remember { mutableStateOf("") }

  val currentStory = userStories.stories.getOrNull(currentIndex) ?: run {
    onClose()
    return
  }

  // Timer animation
  var timerProgress by remember(currentIndex) { mutableFloatStateOf(0f) }

  LaunchedEffect(currentIndex, isPaused) {
    if (!isPaused) {
      val durationMs = currentStory.durationSeconds * 1000
      val stepMs = 50L
      val stepProgress = stepMs.toFloat() / durationMs
      while (timerProgress < 1f && !isPaused) {
        delay(stepMs)
        timerProgress += stepProgress
      }
      if (timerProgress >= 1f) {
        if (currentIndex < userStories.stories.size - 1) {
          timerProgress = 0f
          currentIndex++
        } else {
          onStoryFinished()
        }
      }
    }
  }

  Dialog(
    onDismissRequest = onClose,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .testTag("story_viewer_modal")
    ) {
      // Background Image
      AsyncImage(
        model = currentStory.mediaUrl,
        contentDescription = "Story visual",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )

      // Gradient overlay for contrast
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              listOf(
                Color.Black.copy(alpha = 0.65f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.85f)
              )
            )
          )
      )

      // Tap navigation zones (Left 40% = previous, Right 40% = next, Center hold = pause)
      Row(modifier = Modifier.fillMaxSize()) {
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) {
              if (currentIndex > 0) {
                currentIndex--
                timerProgress = 0f
              }
            }
        )
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) {
              if (currentIndex < userStories.stories.size - 1) {
                currentIndex++
                timerProgress = 0f
              } else {
                onStoryFinished()
              }
            }
        )
      }

      // Foreground content
      Column(
        modifier = Modifier
          .fillMaxSize()
          .statusBarsPadding()
          .navigationBarsPadding()
          .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Header
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Progress Bars
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            userStories.stories.forEachIndexed { idx, _ ->
              val progress = when {
                idx < currentIndex -> 1f
                idx == currentIndex -> timerProgress
                else -> 0f
              }
              LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                  .weight(1f)
                  .height(3.dp)
                  .clip(RoundedCornerShape(2.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.35f)
              )
            }
          }

          // Author Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              AsyncImage(
                model = userStories.user.avatarUrl,
                contentDescription = userStories.user.displayName,
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape),
                contentScale = ContentScale.Crop
              )
              Column {
                Text(
                  text = userStories.user.displayName,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
                Text(
                  text = currentStory.timestamp,
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White.copy(alpha = 0.7f)
                )
              }
            }

            IconButton(
              onClick = onClose,
              modifier = Modifier.testTag("close_story_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close story",
                tint = Color.White
              )
            }
          }
        }

        // Bottom Caption & Quick Reactions
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (currentStory.caption.isNotBlank()) {
            Surface(
              color = Color.Black.copy(alpha = 0.5f),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = currentStory.caption,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
              )
            }
          }

          // Quick Emoji Reactions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            listOf("❤️", "🔥", "👏", "😮", "😂", "🚀").forEach { emoji ->
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.15f))
                  .clickable { onReactionSent(emoji) }
                  .testTag("story_react_$emoji"),
                contentAlignment = Alignment.Center
              ) {
                Text(text = emoji, fontSize = 20.sp)
              }
            }
          }

          // Quick Reply Bar
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedTextField(
              value = quickReplyText,
              onValueChange = { quickReplyText = it },
              placeholder = { Text("Send reply...", color = Color.White.copy(alpha = 0.6f)) },
              modifier = Modifier
                .weight(1f)
                .testTag("story_reply_input"),
              shape = RoundedCornerShape(24.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = UnifyCyan,
                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                cursorColor = UnifyCyan
              ),
              singleLine = true
            )

            IconButton(
              onClick = {
                if (quickReplyText.isNotBlank()) {
                  onReactionSent(quickReplyText)
                  quickReplyText = ""
                }
              },
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(UnifyCyan)
                .testTag("story_reply_send_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send reply",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }
  }
}
