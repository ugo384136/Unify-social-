package com.example.model

enum class AlertCategory {
  ALL,
  MENTIONS,
  FOLLOWERS,
  INTERACTIONS,
  MIRROR
}

enum class NotificationType {
  NEW_FOLLOWER,
  FOLLOW_REQUEST,
  ACCEPTED_FOLLOW_REQUEST,
  POST_LIKE,
  POST_COMMENT,
  COMMENT_REPLY,
  USER_MENTION,
  DIRECT_MESSAGE,
  VOICE_MESSAGE,
  MIRROR_PUBLISHED,
  MIRROR_SYNC_WARNING,
  SECURITY_ALERT
}

data class AlertNotification(
  val id: String,
  val type: NotificationType,
  val category: AlertCategory,
  val title: String,
  val body: String,
  val timestamp: String,
  val actor: User? = null,
  val targetPostId: String? = null,
  val platform: SocialPlatformType? = null,
  val isRead: Boolean = false,
  val actionRequired: Boolean = false
)
