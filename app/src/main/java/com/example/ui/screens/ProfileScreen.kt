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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.model.Post
import com.example.model.User
import com.example.ui.components.PostCard
import com.example.ui.theme.UnifyCyan
import com.example.ui.theme.UnifyPink
import com.example.ui.theme.UnifyViolet

@Composable
fun ProfileScreen(
  user: User,
  userPosts: List<Post>,
  onPrivacyToggle: (Boolean) -> Unit,
  onUpdateProfile: (name: String, bio: String, location: String, website: String) -> Unit,
  onSignOut: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isEditDialogOpen by remember { mutableStateOf(false) }
  var profileTab by remember { mutableIntStateOf(0) } // 0 = Posts, 1 = Media

  var editName by remember { mutableStateOf(user.displayName) }
  var editBio by remember { mutableStateOf(user.bio) }
  var editLocation by remember { mutableStateOf(user.location) }
  var editWebsite by remember { mutableStateOf(user.website) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("profile_screen"),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // 1. Cover Photo & Avatar Header
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
      ) {
        if (user.coverUrl.isNotBlank()) {
          AsyncImage(
            model = user.coverUrl,
            contentDescription = "Cover Banner",
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp),
            contentScale = ContentScale.Crop
          )
        } else {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
              .background(
                Brush.horizontalGradient(listOf(UnifyCyan, UnifyViolet))
              )
          )
        }

        // Avatar overlapping bottom of cover
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 16.dp)
            .size(76.dp)
        ) {
          AsyncImage(
            model = user.avatarUrl,
            contentDescription = "Profile Avatar",
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape)
              .border(3.dp, MaterialTheme.colorScheme.background, CircleShape),
            contentScale = ContentScale.Crop
          )
        }

        // Edit Profile Button
        Row(
          modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp, bottom = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              editName = user.displayName
              editBio = user.bio
              editLocation = user.location
              editWebsite = user.website
              isEditDialogOpen = true
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("edit_profile_btn")
          ) {
            Text("Edit", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
          }

          Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant,
              contentColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("sign_out_button")
          ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Sign Out", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
          }
        }
      }
    }

    // 2. User Info & Bio
    item {
      Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = user.displayName,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
          )
          if (user.isVerified) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Verified creator",
              tint = UnifyCyan,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Text(
          text = "@${user.username}",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = user.bio,
          style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Location & Website
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              Icons.Default.LocationOn,
              contentDescription = null,
              modifier = Modifier.size(14.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = user.location,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              Icons.Default.Link,
              contentDescription = null,
              modifier = Modifier.size(14.dp),
              tint = UnifyCyan
            )
            Text(
              text = user.website,
              style = MaterialTheme.typography.labelSmall,
              color = UnifyCyan
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stats Row (Followers, Following, Posts)
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            ProfileStatItem(count = "${user.postsCount}", label = "Posts")
            ProfileStatItem(count = "${user.followersCount}", label = "Followers")
            ProfileStatItem(count = "${user.followingCount}", label = "Following")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Privacy Toggle Setting (Public / Private account)
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = CardDefaults.outlinedCardBorder()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = if (user.isPrivate) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                tint = if (user.isPrivate) UnifyPink else UnifyCyan
              )
              Column {
                Text(
                  text = if (user.isPrivate) "Private Account" else "Public Account",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = if (user.isPrivate) "Only approved followers can see posts" else "Anyone on UNIFY can view your posts",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            Switch(
              checked = user.isPrivate,
              onCheckedChange = onPrivacyToggle,
              modifier = Modifier.testTag("privacy_toggle_switch")
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Connected Accounts via UNIFY MIRROR
        Text(
          text = "Connected Networks (UNIFY MIRROR)",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          user.connectedPlatforms.forEach { platform ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(platform.hexColor).copy(alpha = 0.2f),
              border = CardDefaults.outlinedCardBorder()
            ) {
              Text(
                text = "${platform.displayName} ✓",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = Color(platform.hexColor)
                ),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    // 3. User Posts / Media Tabs
    item {
      TabRow(
        selectedTabIndex = profileTab,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = UnifyCyan,
        modifier = Modifier.padding(top = 10.dp)
      ) {
        Tab(
          selected = profileTab == 0,
          onClick = { profileTab = 0 },
          text = { Text("My Posts", fontWeight = FontWeight.Bold) }
        )
        Tab(
          selected = profileTab == 1,
          onClick = { profileTab = 1 },
          text = { Text("Media Grid", fontWeight = FontWeight.Bold) }
        )
      }
    }

    val myPosts = userPosts.filter { it.author.id == user.id }
    if (profileTab == 0) {
      if (myPosts.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("No posts published yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      } else {
        items(myPosts, key = { it.id }) { post ->
          PostCard(
            post = post,
            currentUserId = user.id,
            onLikeClick = {},
            onReactClick = {},
            onRepostClick = {},
            onShareClick = {},
            onFollowAuthorClick = {},
            onAddComment = { _, _, _ -> }
          )
        }
      }
    } else {
      item {
        val mediaUrls = myPosts.flatMap { it.mediaUrls }
        if (mediaUrls.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            contentAlignment = Alignment.Center
          ) {
            Text("No media uploaded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        } else {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            mediaUrls.chunked(3).forEach { rowUrls ->
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                rowUrls.forEach { url ->
                  AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                      .weight(1f)
                      .height(110.dp)
                      .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                  )
                }
                repeat(3 - rowUrls.size) {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
          }
        }
      }
    }
  }

  // Edit Profile Dialog
  if (isEditDialogOpen) {
    Dialog(onDismissRequest = { isEditDialogOpen = false }) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .testTag("edit_profile_dialog")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Edit Profile",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = editBio,
            onValueChange = { editBio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = editLocation,
            onValueChange = { editLocation = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = editWebsite,
            onValueChange = { editWebsite = it },
            label = { Text("Website") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
          )
          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            TextButton(onClick = { isEditDialogOpen = false }) {
              Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                onUpdateProfile(editName, editBio, editLocation, editWebsite)
                isEditDialogOpen = false
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = UnifyCyan,
                contentColor = Color.Black
              ),
              modifier = Modifier.testTag("save_profile_btn")
            ) {
              Text("Save")
            }
          }
        }
      }
    }
  }
}

@Composable
fun ProfileStatItem(count: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = count,
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
    )
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
