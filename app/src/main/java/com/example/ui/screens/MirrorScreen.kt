package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
fun MirrorScreen(
  connectedAccounts: List<ConnectedAccount>,
  mirrorPosts: List<MirrorPost>,
  mirrorFeed: List<MirrorFeedItem>,
  selectedTab: Int, // 0 = Accounts, 1 = Publisher, 2 = Cross-Platform Feed
  isConnectModalPlatform: SocialPlatformType?,
  onTabSelected: (Int) -> Unit,
  onToggleConnectAccount: (SocialPlatformType) -> Unit,
  onConfirmConnectOAuth: (SocialPlatformType) -> Unit,
  onDismissConnectModal: () -> Unit,
  onToggleAccountSync: (SocialPlatformType) -> Unit,
  onPublishMultiPost: (caption: String, mediaUrl: String?, platforms: List<SocialPlatformType>) -> Unit,
  onCloseMirror: () -> Unit,
  modifier: Modifier = Modifier
) {
  var composerCaption by remember { mutableStateOf("") }
  var composerMediaUrl by remember { mutableStateOf<String?>("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80") }
  val selectedPlatforms = remember {
    mutableStateListOf(
      SocialPlatformType.INSTAGRAM,
      SocialPlatformType.X,
      SocialPlatformType.LINKEDIN
    )
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("mirror_screen")
  ) {
    // Top Flagship Suite Header
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
              onClick = onCloseMirror,
              modifier = Modifier.size(36.dp).testTag("close_mirror_btn")
            ) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Feed")
            }
            Text(
              text = "🪞 UNIFY MIRROR",
              style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              ),
              color = UnifyCyan
            )
          }

          // Active Connections Badge
          val connectedCount = connectedAccounts.count { it.isConnected }
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = UnifyCyan.copy(alpha = 0.15f),
            border = CardDefaults.outlinedCardBorder()
          ) {
            Text(
              text = "$connectedCount / 8 Active",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = UnifyCyan,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "One platform. Multiple connections. Unified social publishing & syndication.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Tab Navigation
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.background,
      contentColor = UnifyCyan
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { onTabSelected(0) },
        text = { Text("Accounts (${connectedAccounts.count { it.isConnected }})", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("tab_mirror_accounts")
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { onTabSelected(1) },
        text = { Text("Publisher & Queue", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("tab_mirror_publisher")
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { onTabSelected(2) },
        text = { Text("Feeds", fontWeight = FontWeight.Bold) },
        modifier = Modifier.testTag("tab_mirror_feeds")
      )
    }

    // Tab Content
    when (selectedTab) {
      0 -> {
        // Tab 0: Connected Accounts Manager
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(connectedAccounts, key = { it.platform.id }) { account ->
            AccountIntegrationCard(
              account = account,
              onToggleConnect = { onToggleConnectAccount(account.platform) },
              onToggleSync = { onToggleAccountSync(account.platform) }
            )
          }
        }
      }
      1 -> {
        // Tab 1: Unified Multi-Platform Post Publisher & Queue
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          item {
            Card(
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder()
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Text(
                  text = "Unified Multi-Platform Composer",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Write once, syndicate simultaneously with platform-specific checks.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Select Destination Platforms Chips
                Text(
                  text = "Select Destinations:",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  items(connectedAccounts.filter { it.isConnected }) { acc ->
                    val isSelected = selectedPlatforms.contains(acc.platform)
                    FilterChip(
                      selected = isSelected,
                      onClick = {
                        if (isSelected) selectedPlatforms.remove(acc.platform)
                        else selectedPlatforms.add(acc.platform)
                      },
                      label = { Text(acc.platform.displayName) },
                      colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(acc.platform.hexColor),
                        selectedLabelColor = if (acc.platform == SocialPlatformType.SNAPCHAT) Color.Black else Color.White
                      ),
                      shape = RoundedCornerShape(12.dp),
                      modifier = Modifier.testTag("dest_chip_${acc.platform.id}")
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Caption TextField
                OutlinedTextField(
                  value = composerCaption,
                  onValueChange = { composerCaption = it },
                  placeholder = { Text("Draft your message for synchronized cross-posting...") },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .testTag("mirror_caption_input"),
                  shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Character count & Platform Warnings
                val charCount = composerCaption.length
                val hasXWarning = selectedPlatforms.contains(SocialPlatformType.X) && charCount > 280
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = if (hasXWarning) "⚠️ Exceeds X (Twitter) 280-char limit ($charCount/280)"
                    else "$charCount chars",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (hasXWarning) UnifyRose else MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  if (selectedPlatforms.isNotEmpty()) {
                    Text(
                      text = "${selectedPlatforms.size} destinations selected",
                      style = MaterialTheme.typography.labelSmall,
                      color = UnifyCyan
                    )
                  }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Publish Action Buttons
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Button(
                    onClick = {
                      if (composerCaption.isNotBlank() && selectedPlatforms.isNotEmpty()) {
                        onPublishMultiPost(composerCaption, composerMediaUrl, selectedPlatforms.toList())
                        composerCaption = ""
                      }
                    },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = UnifyCyan,
                      contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(14.dp),
                    enabled = composerCaption.isNotBlank() && selectedPlatforms.isNotEmpty(),
                    modifier = Modifier.testTag("publish_multi_platform_btn")
                  ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publish to ${selectedPlatforms.size} Networks")
                  }
                }
              }
            }
          }

          // Publishing Queue & History
          item {
            Text(
              text = "Syndication History & Status",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          items(mirrorPosts, key = { it.id }) { post ->
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
                    text = post.publishedAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = UnifyEmerald.copy(alpha = 0.2f)
                  ) {
                    Text(
                      text = "Published ✓",
                      style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = UnifyEmerald
                      ),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                  text = post.caption,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Delivered platforms tags
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  post.selectedPlatforms.forEach { platform ->
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = Color(platform.hexColor).copy(alpha = 0.15f)
                    ) {
                      Text(
                        text = "${platform.displayName} ✓",
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontSize = 10.sp,
                          color = Color(platform.hexColor)
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
      2 -> {
        // Tab 2: Unified Cross-Platform Feed
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(mirrorFeed, key = { it.id }) { feedItem ->
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              border = CardDefaults.outlinedCardBorder(),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = Color(feedItem.platform.hexColor)
                    ) {
                      Text(
                        text = feedItem.platform.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                          fontWeight = FontWeight.Bold,
                          color = if (feedItem.platform == SocialPlatformType.SNAPCHAT) Color.Black else Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                      )
                    }
                    Text(
                      text = "${feedItem.authorName} (${feedItem.authorHandle})",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                  }

                  Text(
                    text = feedItem.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = feedItem.text,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface
                )

                if (feedItem.mediaUrl != null) {
                  Spacer(modifier = Modifier.height(8.dp))
                  AsyncImage(
                    model = feedItem.mediaUrl,
                    contentDescription = null,
                    modifier = Modifier
                      .fillMaxWidth()
                      .height(160.dp)
                      .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "❤️ ${feedItem.likesCount}  💬 ${feedItem.commentsCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  TextButton(
                    onClick = {},
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                  ) {
                    Text("Open on ${feedItem.platform.displayName} ↗", color = UnifyCyan)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Simulated OAuth Connection Modal
  if (isConnectModalPlatform != null) {
    val platform = isConnectModalPlatform
    Dialog(onDismissRequest = onDismissConnectModal) {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .testTag("oauth_connect_dialog")
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(Color(platform.hexColor)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = platform.displayName.take(1),
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              color = if (platform == SocialPlatformType.SNAPCHAT) Color.Black else Color.White
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Authorize ${platform.displayName}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "UNIFY MIRROR requests permission to:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text("• Read stream and account insights", style = MaterialTheme.typography.bodySmall)
              Text("• Publish posts, media, and carousels on your behalf", style = MaterialTheme.typography.bodySmall)
              Text("• Manage sync webhooks and real-time alerts", style = MaterialTheme.typography.bodySmall)
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Your password is never shared. Access tokens are encrypted using zero-trust session management.",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(18.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onDismissConnectModal,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text("Cancel")
            }
            Button(
              onClick = { onConfirmConnectOAuth(platform) },
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(platform.hexColor),
                contentColor = if (platform == SocialPlatformType.SNAPCHAT) Color.Black else Color.White
              ),
              modifier = Modifier
                .weight(1f)
                .testTag("confirm_oauth_btn"),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text("Authorize")
            }
          }
        }
      }
    }
  }
}

@Composable
fun AccountIntegrationCard(
  account: ConnectedAccount,
  onToggleConnect: () -> Unit,
  onToggleSync: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = CardDefaults.outlinedCardBorder(),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("account_card_${account.platform.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color(account.platform.hexColor)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = account.platform.displayName.take(1),
              fontWeight = FontWeight.Black,
              color = if (account.platform == SocialPlatformType.SNAPCHAT) Color.Black else Color.White,
              fontSize = 18.sp
            )
          }

          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(
                text = account.platform.displayName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
              )
              if (account.isConnected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnifyCyan, modifier = Modifier.size(14.dp))
              }
            }
            Text(
              text = if (account.isConnected) account.accountHandle else "Not connected",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Connect / Disconnect Button
        Button(
          onClick = onToggleConnect,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (account.isConnected) MaterialTheme.colorScheme.surfaceVariant else UnifyCyan,
            contentColor = if (account.isConnected) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black
          ),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.height(32.dp).testTag("connect_btn_${account.platform.id}")
        ) {
          Text(
            text = if (account.isConnected) "Disconnect" else "Connect",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
        }
      }

      if (account.isConnected) {
        Spacer(modifier = Modifier.height(10.dp))
        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Token: Healthy (${account.tokenExpiresInDays}d left) • Synced ${account.lastSynced}",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text("Auto-Sync", style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
            Switch(
              checked = account.syncEnabled,
              onCheckedChange = { onToggleSync() },
              modifier = Modifier.scale(0.8f)
            )
          }
        }
      }
    }
  }
}
