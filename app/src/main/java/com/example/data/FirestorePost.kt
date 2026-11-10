package com.example.data

import com.example.model.Post
import com.example.model.ReactionType
import com.example.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestorePost(
  @DocumentId val id: String = "",
  val userId: String = "",
  val authorName: String = "",
  val authorUsername: String = "",
  val authorAvatarUrl: String = "",
  val text: String = "",
  val mediaUrl: String? = null,
  val likesCount: Long = 0L,
  val likedUserIds: List<String> = emptyList(),
  val repostsCount: Long = 0L,
  val commentsCount: Long = 0L,
  val isVoicePost: Boolean = false,
  val voiceDurationSec: Long? = null,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
) {
  fun toUiPost(currentUserId: String): Post {
    val isLikedByMe = likedUserIds.contains(currentUserId)
    val timeFormatted = createdAt?.toDate()?.let { date ->
      val diffMs = System.currentTimeMillis() - date.time
      val diffMins = diffMs / (1000 * 60)
      when {
        diffMins < 1 -> "Just now"
        diffMins < 60 -> "${diffMins}m ago"
        diffMins < 1440 -> "${diffMins / 60}h ago"
        else -> "${diffMins / 1440}d ago"
      }
    } ?: "Just now"

    return Post(
      id = id.ifBlank { "post_${System.currentTimeMillis()}" },
      author = User(
        id = userId,
        username = authorUsername.ifBlank { "creator" },
        displayName = authorName.ifBlank { "UNIFY Creator" },
        bio = "",
        avatarUrl = authorAvatarUrl.ifBlank {
          "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80"
        },
        isVerified = true
      ),
      text = text,
      mediaUrls = if (!mediaUrl.isNullOrBlank()) listOf(mediaUrl) else emptyList(),
      timestamp = timeFormatted,
      likesCount = likesCount.toInt(),
      isLiked = isLikedByMe,
      userReaction = if (isLikedByMe) ReactionType.LIKE else null,
      repostsCount = repostsCount.toInt(),
      comments = emptyList(),
      isForYou = true,
      isVoicePost = isVoicePost,
      voiceDurationSec = voiceDurationSec?.toInt(),
      voiceWaveform = if (isVoicePost) listOf(0.2f, 0.5f, 0.8f, 0.4f, 0.9f, 0.6f, 0.3f, 0.7f) else null
    )
  }
}
