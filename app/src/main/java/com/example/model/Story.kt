package com.example.model

data class StoryItem(
  val id: String,
  val mediaUrl: String,
  val isVideo: Boolean = false,
  val caption: String = "",
  val timestamp: String,
  val durationSeconds: Int = 6,
  val viewersCount: Int = 142
)

data class UserStories(
  val user: User,
  val stories: List<StoryItem>,
  val hasUnviewedStories: Boolean = true,
  val lastUpdated: String = "2h ago"
)
