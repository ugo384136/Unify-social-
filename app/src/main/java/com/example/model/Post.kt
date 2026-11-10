package com.example.model

enum class ReactionType(val emoji: String, val label: String) {
  LIKE("❤️", "Love"),
  FIRE("🔥", "Fire"),
  LAUGH("😂", "Haha"),
  MINDBLOWN("🤯", "Mindblown"),
  ROCKET("🚀", "Superb")
}

data class Comment(
  val id: String,
  val author: User,
  val text: String,
  val timestamp: String,
  val likesCount: Int = 0,
  val isLiked: Boolean = false,
  val voiceDurationSec: Int? = null,
  val voiceWaveform: List<Float>? = null,
  val replies: List<Comment> = emptyList()
)

data class Post(
  val id: String,
  val author: User,
  val text: String,
  val mediaUrls: List<String> = emptyList(),
  val isVideo: Boolean = false,
  val timestamp: String,
  val likesCount: Int = 0,
  val isLiked: Boolean = false,
  val userReaction: ReactionType? = null,
  val reactionsCount: Map<ReactionType, Int> = emptyMap(),
  val comments: List<Comment> = emptyList(),
  val repostsCount: Int = 0,
  val isReposted: Boolean = false,
  val tags: List<String> = emptyList(),
  val hashtags: List<String> = emptyList(),
  val isForYou: Boolean = true,
  val isFollowingFeed: Boolean = true,
  val location: String? = null,
  val isVoicePost: Boolean = false,
  val voiceDurationSec: Int? = null,
  val voiceWaveform: List<Float>? = null
)
