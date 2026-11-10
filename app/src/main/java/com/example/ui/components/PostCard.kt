package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Comment
import com.example.model.Post
import com.example.model.ReactionType
import com.example.ui.theme.*

@Composable
fun PostCard(
  post: Post,
  currentUserId: String,
  onLikeClick: () -> Unit,
  onReactClick: (ReactionType) -> Unit,
  onRepostClick: () -> Unit,
  onShareClick: () -> Unit,
  onFollowAuthorClick: () -> Unit,
  onAddComment: (text: String, isVoice: Boolean, durationSec: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var isCommentsExpanded by remember { mutableStateOf(false) }
  var isReactionPickerVisible by remember { mutableStateOf(false) }
  var commentInputText by remember { mutableStateOf("") }
  var isRecordingVoiceComment by remember { mutableStateOf(false) }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("post_card_${post.id}"),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    shape = RoundedCornerShape(20.dp),
    border = CardDefaults.outlinedCardBorder()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // 1. Author Header Row
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
            model = post.author.avatarUrl,
            contentDescription = "${post.author.displayName}'s avatar",
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape),
            contentScale = ContentScale.Crop
          )
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = post.author.displayName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              if (post.author.isVerified) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Verified creator",
                  tint = UnifyCyan,
                  modifier = Modifier.size(15.dp)
                )
              }
            }
            Text(
              text = "@${post.author.username} • ${post.timestamp}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Follow / Following pill (if not current user)
        if (post.author.id != currentUserId) {
          Button(
            onClick = onFollowAuthorClick,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (post.author.isFollowing) MaterialTheme.colorScheme.surfaceVariant
              else UnifyCyan,
              contentColor = if (post.author.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant
              else Color.Black
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .height(32.dp)
              .testTag("follow_btn_${post.author.id}")
          ) {
            Text(
              text = if (post.author.isFollowing) "Following" else if (post.author.followRequested) "Requested" else "Follow",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Post Text Content with Hashtags
      if (post.text.isNotBlank()) {
        Text(
          text = post.text,
          style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      // Voice Post Waveform
      if (post.isVoicePost && post.voiceDurationSec != null) {
        Spacer(modifier = Modifier.height(10.dp))
        VoiceWaveformPlayer(
          durationSec = post.voiceDurationSec,
          waveform = post.voiceWaveform,
          tintColor = UnifyViolet
        )
      }

      // 3. Post Media Photos
      if (post.mediaUrls.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        if (post.mediaUrls.size == 1) {
          AsyncImage(
            model = post.mediaUrls.first(),
            contentDescription = "Post image",
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(max = 280.dp)
              .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
          )
        } else {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            post.mediaUrls.take(2).forEach { url ->
              AsyncImage(
                model = url,
                contentDescription = "Post image",
                modifier = Modifier
                  .weight(1f)
                  .fillMaxHeight()
                  .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 4. Reactions Pop-up Bar (if toggled)
      AnimatedVisibility(
        visible = isReactionPickerVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(20.dp),
          modifier = Modifier
            .padding(bottom = 8.dp)
            .testTag("reactions_picker")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            ReactionType.values().forEach { reaction ->
              IconButton(
                onClick = {
                  onReactClick(reaction)
                  isReactionPickerVisible = false
                },
                modifier = Modifier.size(36.dp)
              ) {
                Text(text = reaction.emoji, fontSize = 20.sp)
              }
            }
          }
        }
      }

      // 5. Interaction Buttons Row (Like/React, Comments, Repost, Share)
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Like Button & Custom Reaction Display
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              onLikeClick()
            }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("like_post_${post.id}")
        ) {
          IconButton(
            onClick = onLikeClick,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
              contentDescription = "Like post",
              tint = if (post.isLiked) UnifyRose else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
          }
          Text(
            text = "${post.likesCount}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (post.isLiked) UnifyRose else MaterialTheme.colorScheme.onSurfaceVariant
          )

          // Small button to trigger reactions picker
          IconButton(
            onClick = { isReactionPickerVisible = !isReactionPickerVisible },
            modifier = Modifier.size(26.dp)
          ) {
            Text(
              text = post.userReaction?.emoji ?: "➕",
              fontSize = 12.sp
            )
          }
        }

        // Comment Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { isCommentsExpanded = !isCommentsExpanded }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("comment_post_${post.id}")
        ) {
          Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = "Comments",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(19.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${post.comments.size}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Repost Button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onRepostClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag("repost_post_${post.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Repeat,
            contentDescription = "Repost",
            tint = if (post.isReposted) UnifyEmerald else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(19.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${post.repostsCount}",
            style = MaterialTheme.typography.labelMedium,
            color = if (post.isReposted) UnifyEmerald else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Share Button
        IconButton(
          onClick = onShareClick,
          modifier = Modifier
            .size(32.dp)
            .testTag("share_post_${post.id}")
        ) {
          Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = "Share post",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(19.dp)
          )
        }
      }

      // 6. Comments Expansion Section
      AnimatedVisibility(
        visible = isCommentsExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

          // List Existing Comments
          if (post.comments.isEmpty()) {
            Text(
              text = "No comments yet. Be the first to start the conversation!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(vertical = 4.dp)
            )
          } else {
            post.comments.forEach { comment ->
              CommentItemView(comment = comment)
            }
          }

          // Voice Comment Recorder or Text Comment Input
          if (isRecordingVoiceComment) {
            VoiceRecorderBar(
              onCancel = { isRecordingVoiceComment = false },
              onSendVoice = { duration ->
                onAddComment("", true, duration)
                isRecordingVoiceComment = false
              },
              label = "Recording voice comment..."
            )
          } else {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedTextField(
                value = commentInputText,
                onValueChange = { commentInputText = it },
                placeholder = { Text("Add comment or @mention...", style = MaterialTheme.typography.bodySmall) },
                modifier = Modifier
                  .weight(1f)
                  .testTag("comment_input_${post.id}"),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = UnifyCyan,
                  unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
              )

              // Mic button to switch to Voice Comment
              IconButton(
                onClick = { isRecordingVoiceComment = true },
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceVariant)
                  .testTag("record_voice_comment_btn_${post.id}")
              ) {
                Icon(
                  imageVector = Icons.Default.Mic,
                  contentDescription = "Record voice comment",
                  tint = UnifyViolet,
                  modifier = Modifier.size(18.dp)
                )
              }

              // Send Text Comment Button
              IconButton(
                onClick = {
                  if (commentInputText.isNotBlank()) {
                    onAddComment(commentInputText, false, 0)
                    commentInputText = ""
                  }
                },
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(UnifyCyan)
                  .testTag("send_comment_btn_${post.id}")
              ) {
                Icon(
                  imageVector = Icons.Default.Send,
                  contentDescription = "Send comment",
                  tint = Color.Black,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CommentItemView(comment: Comment) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    AsyncImage(
      model = comment.author.avatarUrl,
      contentDescription = comment.author.displayName,
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape),
      contentScale = ContentScale.Crop
    )
    Column(
      modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = comment.author.displayName,
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = comment.timestamp,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      if (comment.voiceDurationSec != null) {
        Spacer(modifier = Modifier.height(4.dp))
        VoiceWaveformPlayer(
          durationSec = comment.voiceDurationSec,
          waveform = comment.voiceWaveform,
          tintColor = UnifyCyan,
          showLabel = true
        )
      } else {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = comment.text,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
    }
  }
}
