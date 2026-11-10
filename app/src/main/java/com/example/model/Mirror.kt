package com.example.model

enum class SocialPlatformType(
  val id: String,
  val displayName: String,
  val iconName: String,
  val maxChars: Int,
  val supportsVideo: Boolean,
  val hexColor: Long
) {
  FACEBOOK("facebook", "Facebook", "facebook", 63206, true, 0xFF1877F2),
  INSTAGRAM("instagram", "Instagram", "instagram", 2200, true, 0xFFE1306C),
  TIKTOK("tiktok", "TikTok", "tiktok", 2200, true, 0xFF000000),
  YOUTUBE("youtube", "YouTube", "youtube", 5000, true, 0xFFFF0000),
  X("x", "X (Twitter)", "x", 280, true, 0xFF1DA1F2),
  LINKEDIN("linkedin", "LinkedIn", "linkedin", 3000, true, 0xFF0A66C2),
  SNAPCHAT("snapchat", "Snapchat", "snapchat", 250, true, 0xFFFFFC00),
  PINTEREST("pinterest", "Pinterest", "pinterest", 500, false, 0xFFE60023)
}

enum class ConnectionHealth {
  HEALTHY,
  REFRESH_NEEDED,
  RATE_LIMITED,
  DISCONNECTED
}

data class ConnectedAccount(
  val platform: SocialPlatformType,
  val accountHandle: String,
  val accountName: String,
  val isConnected: Boolean,
  val health: ConnectionHealth = ConnectionHealth.HEALTHY,
  val tokenExpiresInDays: Int = 45,
  val syncEnabled: Boolean = true,
  val lastSynced: String = "10m ago",
  val followersCount: String = "12.4K"
)

enum class PublishStatus {
  PUBLISHED,
  PUBLISHING,
  SCHEDULED,
  FAILED
}

data class MirrorPublishedItem(
  val platform: SocialPlatformType,
  val status: PublishStatus,
  val externalPostUrl: String? = null,
  val errorMessage: String? = null
)

data class MirrorPost(
  val id: String,
  val caption: String,
  val mediaUrl: String? = null,
  val selectedPlatforms: List<SocialPlatformType>,
  val publishedAt: String,
  val publishResults: List<MirrorPublishedItem>,
  val overallStatus: PublishStatus = PublishStatus.PUBLISHED,
  val scheduledTime: String? = null
)

data class MirrorFeedItem(
  val id: String,
  val platform: SocialPlatformType,
  val authorName: String,
  val authorHandle: String,
  val authorAvatar: String,
  val text: String,
  val mediaUrl: String? = null,
  val likesCount: Int,
  val commentsCount: Int,
  val timestamp: String,
  val externalUrl: String
)
