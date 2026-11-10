package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.User
import com.example.model.UserStories
import com.example.ui.theme.UnifyCyan
import com.example.ui.theme.UnifyPink
import com.example.ui.theme.UnifyViolet

@Composable
fun StoriesCarousel(
  stories: List<UserStories>,
  currentUser: User,
  onStoryClick: (UserStories) -> Unit,
  onAddStoryClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val storyGradient = Brush.linearGradient(
    colors = listOf(UnifyCyan, UnifyViolet, UnifyPink)
  )

  LazyRow(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp)
      .testTag("stories_carousel"),
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Current User "+ Your Story" Item
    item {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .width(72.dp)
          .clickable { onAddStoryClick() }
          .testTag("add_story_item")
      ) {
        Box(
          modifier = Modifier.size(66.dp),
          contentAlignment = Alignment.Center
        ) {
          AsyncImage(
            model = currentUser.avatarUrl,
            contentDescription = "Your avatar",
            modifier = Modifier
              .size(60.dp)
              .clip(CircleShape)
              .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentScale = ContentScale.Crop
          )
          // Plus icon badge
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .size(22.dp)
              .clip(CircleShape)
              .background(UnifyCyan)
              .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Add story",
              tint = Color.Black,
              modifier = Modifier.size(14.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Your Story",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }

    // Other Users' Stories
    items(stories.filterNot { it.user.id == currentUser.id }) { userStories ->
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .width(72.dp)
          .clickable { onStoryClick(userStories) }
          .testTag("story_item_${userStories.user.username}")
      ) {
        Box(
          modifier = Modifier
            .size(66.dp)
            .clip(CircleShape)
            .background(
              if (userStories.hasUnviewedStories) storyGradient
              else Brush.linearGradient(
                listOf(
                  MaterialTheme.colorScheme.outline,
                  MaterialTheme.colorScheme.outlineVariant
                )
              )
            )
            .padding(2.5.dp),
          contentAlignment = Alignment.Center
        ) {
          AsyncImage(
            model = userStories.user.avatarUrl,
            contentDescription = "${userStories.user.displayName}'s story",
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape)
              .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
            contentScale = ContentScale.Crop
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = userStories.user.displayName.split(" ").firstOrNull() ?: userStories.user.username,
          style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            fontWeight = if (userStories.hasUnviewedStories) FontWeight.SemiBold else FontWeight.Normal
          ),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
