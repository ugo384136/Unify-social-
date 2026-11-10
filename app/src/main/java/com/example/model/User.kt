package com.example.model

data class User(
  val id: String,
  val username: String,
  val displayName: String,
  val bio: String,
  val avatarUrl: String,
  val coverUrl: String = "",
  val isVerified: Boolean = false,
  val isPrivate: Boolean = false,
  val followersCount: Int = 0,
  val followingCount: Int = 0,
  val postsCount: Int = 0,
  val isFollowing: Boolean = false,
  val followRequested: Boolean = false,
  val isBlocked: Boolean = false,
  val isOnline: Boolean = false,
  val lastSeen: String = "Just now",
  val location: String = "San Francisco, CA",
  val website: String = "unifysocials.com",
  val connectedPlatforms: List<SocialPlatformType> = emptyList()
)
