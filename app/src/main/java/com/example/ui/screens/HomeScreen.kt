package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.model.Post
import com.example.model.User
import com.example.model.UserStories
import com.example.ui.components.PostCard
import com.example.ui.components.StoriesCarousel
import com.example.ui.theme.UnifyCyan
import com.example.ui.theme.UnifyViolet

@Composable
fun HomeScreen(
  posts: List<Post>,
  stories: List<UserStories>,
  currentUser: User,
  selectedTab: Int, // 0 = For You, 1 = Following
  isCreatePostOpen: Boolean,
  onTabSelected: (Int) -> Unit,
  onStoryClick: (UserStories) -> Unit,
  onAddStoryClick: () -> Unit,
  onLikePost: (String) -> Unit,
  onReactPost: (String, com.example.model.ReactionType) -> Unit,
  onRepostPost: (String) -> Unit,
  onSharePost: (String) -> Unit,
  onFollowAuthor: (String) -> Unit,
  onAddComment: (postId: String, text: String, isVoice: Boolean, duration: Int) -> Unit,
  onToggleCreatePost: (Boolean) -> Unit,
  onSubmitNewPost: (text: String, mediaUrl: String?) -> Unit,
  modifier: Modifier = Modifier
) {
  var newPostText by remember { mutableStateOf("") }
  var newPostMediaUrl by remember { mutableStateOf<String?>(null) }
  var quickStoryCaption by remember { mutableStateOf("") }
  var isQuickStoryDialogVisible by remember { mutableStateOf(false) }

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("home_feed_list"),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // Stories Carousel Header
      item {
        StoriesCarousel(
          stories = stories,
          currentUser = currentUser,
          onStoryClick = onStoryClick,
          onAddStoryClick = { isQuickStoryDialogVisible = true }
        )
      }

      // Tab selector: "For You" vs "Following"
      item {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.background,
          contentColor = UnifyCyan,
          divider = {
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
          },
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = {
              Text(
                text = "For You",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                )
              )
            },
            modifier = Modifier.testTag("tab_for_you")
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = {
              Text(
                text = "Following",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                )
              )
            },
            modifier = Modifier.testTag("tab_following")
          )
        }
      }

      // Filtered Feed Items
      val displayedPosts = if (selectedTab == 0) {
        posts.filter { it.isForYou }
      } else {
        posts.filter { it.author.isFollowing || it.author.id == currentUser.id }
      }

      if (displayedPosts.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(48.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "No posts in Following yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Follow more creators or check the 'For You' stream!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(displayedPosts, key = { it.id }) { post ->
          PostCard(
            post = post,
            currentUserId = currentUser.id,
            onLikeClick = { onLikePost(post.id) },
            onReactClick = { reaction -> onReactPost(post.id, reaction) },
            onRepostClick = { onRepostPost(post.id) },
            onShareClick = { onSharePost(post.id) },
            onFollowAuthorClick = { onFollowAuthor(post.author.id) },
            onAddComment = { text, isVoice, duration ->
              onAddComment(post.id, text, isVoice, duration)
            }
          )
        }
      }
    }

    // Floating Action Button to Create Post
    FloatingActionButton(
      onClick = { onToggleCreatePost(true) },
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(18.dp)
        .testTag("fab_create_post"),
      containerColor = UnifyCyan,
      contentColor = Color.Black,
      shape = CircleShape
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Create post"
      )
    }

    // Modal Dialog: Create Post
    if (isCreatePostOpen) {
      Dialog(onDismissRequest = { onToggleCreatePost(false) }) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("create_post_dialog")
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Create Post",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              IconButton(onClick = { onToggleCreatePost(false) }) {
                Icon(Icons.Default.Close, contentDescription = "Close")
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
              value = newPostText,
              onValueChange = { newPostText = it },
              placeholder = { Text("What's on your mind? Share thoughts, #tags, or discussions...") },
              modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .testTag("create_post_text_field"),
              shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick photo attachment toggle
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = {
                  newPostMediaUrl = if (newPostMediaUrl == null) {
                    "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80"
                  } else null
                },
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (newPostMediaUrl != null) "Photo Added ✓" else "Add Photo")
              }

              Button(
                onClick = {
                  if (newPostText.isNotBlank() || newPostMediaUrl != null) {
                    onSubmitNewPost(newPostText, newPostMediaUrl)
                    newPostText = ""
                    newPostMediaUrl = null
                  }
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = UnifyCyan,
                  contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("submit_new_post_btn")
              ) {
                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Post")
              }
            }
          }
        }
      }
    }

    // Modal Dialog: Quick Story
    if (isQuickStoryDialogVisible) {
      Dialog(onDismissRequest = { isQuickStoryDialogVisible = false }) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Add to Your Story 🌟",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = quickStoryCaption,
              onValueChange = { quickStoryCaption = it },
              placeholder = { Text("Story caption...") },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End,
              verticalAlignment = Alignment.CenterVertically
            ) {
              TextButton(onClick = { isQuickStoryDialogVisible = false }) {
                Text("Cancel")
              }
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = {
                  // Add story
                  onAddStoryClick()
                  isQuickStoryDialogVisible = false
                  quickStoryCaption = ""
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = UnifyCyan,
                  contentColor = Color.Black
                )
              ) {
                Text("Upload Story")
              }
            }
          }
        }
      }
    }
  }
}
