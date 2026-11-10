package com.example.model

enum class UserAccountStatus {
  ACTIVE,
  VERIFIED,
  SUSPENDED,
  BANNED
}

data class AdminKPIs(
  val totalUsers: String = "128,490",
  val activeUsersDAU: String = "48,210",
  val totalPosts: String = "894,320",
  val totalEngagements: String = "3.4M",
  val pendingReports: Int = 14,
  val activeMirrorConnections: String = "312,800",
  val serverLatencyMs: Int = 42,
  val systemHealthPercent: Double = 99.98
)

enum class ReportCategory {
  HARASSMENT,
  SPAM,
  INAPPROPRIATE_CONTENT,
  IMPERSONATION,
  VOICE_ABUSE,
  COPYRIGHT
}

enum class ReportStatus {
  PENDING,
  INVESTIGATING,
  RESOLVED_TAKEDOWN,
  DISMISSED
}

data class ModerationReport(
  val id: String,
  val reporter: User,
  val reportedUser: User,
  val category: ReportCategory,
  val reason: String,
  val timestamp: String,
  val contentSnippet: String,
  val isVoiceContent: Boolean = false,
  val status: ReportStatus = ReportStatus.PENDING,
  val severity: String = "HIGH"
)

enum class AnnouncementPriority {
  NORMAL,
  HIGH,
  CRITICAL
}

data class SystemAnnouncement(
  val id: String,
  val title: String,
  val body: String,
  val priority: AnnouncementPriority,
  val authorAdmin: String,
  val timestamp: String,
  val deliveredCount: String = "All Users"
)

data class PlatformIntegrationHealth(
  val platform: SocialPlatformType,
  val apiVersion: String,
  val uptime: String,
  val errorRate: String,
  val status: String
)
