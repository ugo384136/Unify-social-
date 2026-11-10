package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun AdminScreen(
  kpis: AdminKPIs,
  moderationReports: List<ModerationReport>,
  managedUsers: List<User>,
  announcements: List<SystemAnnouncement>,
  platformHealthList: List<PlatformIntegrationHealth>,
  selectedTab: Int, // 0 = KPIs, 1 = Moderation, 2 = Users, 3 = Ops & Announcements
  onTabSelected: (Int) -> Unit,
  onResolveReport: (reportId: String, status: ReportStatus) -> Unit,
  onToggleUserVerification: (userId: String) -> Unit,
  onToggleUserSuspension: (userId: String) -> Unit,
  onBroadcastAnnouncement: (title: String, body: String, priority: AnnouncementPriority) -> Unit,
  onCloseAdmin: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isAnnouncementDialogVisible by remember { mutableStateOf(false) }
  var announcementTitle by remember { mutableStateOf("") }
  var announcementBody by remember { mutableStateOf("") }
  var announcementPriority by remember { mutableStateOf(AnnouncementPriority.NORMAL) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("admin_screen")
  ) {
    // Top Control Centre Header
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            IconButton(
              onClick = onCloseAdmin,
              modifier = Modifier.size(36.dp).testTag("close_admin_btn")
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
              text = "🛡️ CONTROL CENTRE",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              ),
              color = UnifyViolet
            )
          }

          // Super Admin Badge
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = UnifyViolet.copy(alpha = 0.2f),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Text(
              text = "Super Admin",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = UnifyViolet,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Operations, Trust & Safety, Moderation, and Real-Time Platform Telemetry",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Tab Navigation
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.background,
      contentColor = UnifyViolet
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { onTabSelected(0) },
        text = { Text("KPIs", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("tab_admin_kpis")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { onTabSelected(1) },
        text = {
          BadgedBox(
            badge = {
              val pending = moderationReports.count { it.status == ReportStatus.PENDING }
              if (pending > 0) Badge { Text("$pending") }
            }
          ) {
            Text("Reports", fontWeight = FontWeight.Bold)
          }
        },
        modifier = Modifier.testTag("tab_admin_moderation")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { onTabSelected(2) },
        text = { Text("Users", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("tab_admin_users")
      )
      Tab(
        selected = selectedTab == 3,
        onClick = { onTabSelected(3) },
        text = { Text("Ops & Alerts", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("tab_admin_ops")
      )
    }

    // Tab Contents
    when (selectedTab) {
      0 -> {
        // Tab 0: KPIs & System Health
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          item {
            Text(
              text = "Real-Time Platform Health",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              KpiCard(title = "Total Users", value = kpis.totalUsers, subtitle = "+12% this week", modifier = Modifier.weight(1f))
              KpiCard(title = "Daily Active", value = kpis.activeUsersDAU, subtitle = "37.5% DAU/MAU", modifier = Modifier.weight(1f))
            }
          }

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              KpiCard(title = "Total Posts", value = kpis.totalPosts, subtitle = "89.4k today", modifier = Modifier.weight(1f))
              KpiCard(title = "Engagements", value = kpis.totalEngagements, subtitle = "Likes, shares & audio", modifier = Modifier.weight(1f))
            }
          }

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              KpiCard(title = "MIRROR Syncs", value = kpis.activeMirrorConnections, subtitle = "All 8 platforms", modifier = Modifier.weight(1f))
              KpiCard(title = "System Uptime", value = "${kpis.systemHealthPercent}%", subtitle = "${kpis.serverLatencyMs}ms latency", modifier = Modifier.weight(1f))
            }
          }

          item {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "Security Foundation & Zero-Trust RLS",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "• Backend-enforced authorization active across all feeds\n• Private account follow boundaries strictly isolated\n• Blocked relationships filtered at database query layer\n• Encrypted OAuth tokens isolated from dashboard client",
                  style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
      1 -> {
        // Tab 1: Moderation & Trust & Safety
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(moderationReports, key = { it.id }) { report ->
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder(),
              modifier = Modifier.fillMaxWidth().testTag("report_card_${report.id}")
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (report.severity == "CRITICAL") UnifyRose.copy(alpha = 0.2f)
                    else UnifyAmber.copy(alpha = 0.2f)
                  ) {
                    Text(
                      text = "${report.category.name} • ${report.severity}",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (report.severity == "CRITICAL") UnifyRose else UnifyAmber
                      ),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }

                  Text(
                    text = report.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = "Reported User: @${report.reportedUser.username} (${report.reportedUser.displayName})",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "Reporter: @${report.reporter.username}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                      text = "Snippet / Evidence:",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = "\"${report.contentSnippet}\"",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    if (report.isVoiceContent) {
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "🎙️ Voice Comment / Audio Evidence Attached",
                        style = MaterialTheme.typography.labelSmall,
                        color = UnifyViolet
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action buttons if pending
                if (report.status == ReportStatus.PENDING) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    OutlinedButton(
                      onClick = { onResolveReport(report.id, ReportStatus.DISMISSED) },
                      modifier = Modifier.weight(1f),
                      shape = RoundedCornerShape(12.dp)
                    ) {
                      Text("Dismiss", style = MaterialTheme.typography.labelSmall)
                    }
                    Button(
                      onClick = { onResolveReport(report.id, ReportStatus.RESOLVED_TAKEDOWN) },
                      colors = ButtonDefaults.buttonColors(
                        containerColor = UnifyRose,
                        contentColor = Color.White
                      ),
                      modifier = Modifier.weight(1.3f).testTag("takedown_btn_${report.id}"),
                      shape = RoundedCornerShape(12.dp)
                    ) {
                      Text("Take Down", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                  }
                } else {
                  Text(
                    text = "Status: ${report.status.name}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = UnifyEmerald
                  )
                }
              }
            }
          }
        }
      }
      2 -> {
        // Tab 2: User Directory & Verification Management
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(managedUsers, key = { it.id }) { user ->
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder(),
              modifier = Modifier.fillMaxWidth().testTag("admin_user_row_${user.username}")
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                AsyncImage(
                  model = user.avatarUrl,
                  contentDescription = null,
                  modifier = Modifier.size(44.dp).clip(CircleShape),
                  contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                      text = user.displayName,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    if (user.isVerified) {
                      Icon(Icons.Default.CheckCircle, contentDescription = "Verified", tint = UnifyCyan, modifier = Modifier.size(14.dp))
                    }
                  }
                  Text(
                    text = "@${user.username} • ${user.followersCount} followers",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                // Verify Button Toggle
                IconButton(
                  onClick = { onToggleUserVerification(user.id) },
                  modifier = Modifier.size(36.dp).testTag("verify_toggle_${user.id}")
                ) {
                  Icon(
                    imageVector = if (user.isVerified) Icons.Default.Verified else Icons.Default.VerifiedUser,
                    contentDescription = "Toggle verification badge",
                    tint = if (user.isVerified) UnifyCyan else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                // Suspend / Reinstate Toggle
                IconButton(
                  onClick = { onToggleUserSuspension(user.id) },
                  modifier = Modifier.size(36.dp).testTag("suspend_toggle_${user.id}")
                ) {
                  Icon(
                    imageVector = if (user.isBlocked) Icons.Default.Block else Icons.Default.Check,
                    contentDescription = "Toggle suspension",
                    tint = if (user.isBlocked) UnifyRose else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }
      3 -> {
        // Tab 3: Operations, Integration Health & Announcements
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Platform Announcements",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Button(
                onClick = { isAnnouncementDialogVisible = true },
                colors = ButtonDefaults.buttonColors(
                  containerColor = UnifyViolet,
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp).testTag("new_announcement_btn")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Broadcast", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
              }
            }
          }

          items(announcements, key = { it.id }) { ann ->
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder(),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = ann.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (ann.priority == AnnouncementPriority.HIGH) UnifyRose.copy(alpha = 0.2f) else UnifyCyan.copy(alpha = 0.2f)
                  ) {
                    Text(
                      text = ann.priority.name,
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = ann.body,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = "${ann.authorAdmin} • ${ann.timestamp} • Delivered: ${ann.deliveredCount}",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
              }
            }
          }

          item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "UNIFY MIRROR Adapter Health",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          items(platformHealthList, key = { it.platform.id }) { health ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder(),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(
                    text = "${health.platform.displayName} Adapter",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                  Text(
                    text = "${health.apiVersion} • Error rate: ${health.errorRate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = UnifyEmerald.copy(alpha = 0.2f)
                ) {
                  Text(
                    text = "Uptime ${health.uptime}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      color = UnifyEmerald
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  // Dialog to broadcast announcement
  if (isAnnouncementDialogVisible) {
    Dialog(onDismissRequest = { isAnnouncementDialogVisible = false }) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("announcement_dialog")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Broadcast System Announcement",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = announcementTitle,
            onValueChange = { announcementTitle = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = announcementBody,
            onValueChange = { announcementBody = it },
            label = { Text("Message Body") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
          )
          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(onClick = { isAnnouncementDialogVisible = false }) {
              Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                if (announcementTitle.isNotBlank() && announcementBody.isNotBlank()) {
                  onBroadcastAnnouncement(announcementTitle, announcementBody, announcementPriority)
                  isAnnouncementDialogVisible = false
                  announcementTitle = ""
                  announcementBody = ""
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = UnifyViolet,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("Broadcast Now")
            }
          }
        }
      }
    }
  }
}

@Composable
fun KpiCard(
  title: String,
  value: String,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder(),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
        color = UnifyCyan
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
