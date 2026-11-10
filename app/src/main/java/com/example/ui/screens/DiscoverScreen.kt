package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
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
import coil.compose.AsyncImage
import com.example.model.Post
import com.example.model.User
import com.example.ui.theme.UnifyCyan
import com.example.ui.theme.UnifyViolet

@Composable
fun DiscoverScreen(
  searchQuery: String,
  suggestedUsers: List<User>,
  posts: List<Post>,
  onSearchQueryChange: (String) -> Unit,
  onFollowUser: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val trendingHashtags = remember {
    listOf(
      Pair("#UNIFY", "42.8K posts"),
      Pair("#CreativeStudio", "29.1K posts"),
      Pair("#CrossPlatform", "18.4K posts"),
      Pair("#SpatialAudio", "14.2K posts"),
      Pair("#DesignSystems", "9.8K posts"),
      Pair("#SecurityFirst", "6.4K posts")
    )
  }

  val exploreImages = remember {
    listOf(
      "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=600&auto=format&fit=crop&q=80",
      "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=600&auto=format&fit=crop&q=80",
      "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop&q=80",
      "https://images.unsplash.com/photo-1518770660439-4636190af475?w=600&auto=format&fit=crop&q=80",
      "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&auto=format&fit=crop&q=80",
      "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=600&auto=format&fit=crop&q=80"
    )
  }

  val filteredUsers = remember(searchQuery, suggestedUsers) {
    if (searchQuery.isBlank()) suggestedUsers
    else suggestedUsers.filter {
      it.displayName.contains(searchQuery, ignoreCase = true) ||
          it.username.contains(searchQuery, ignoreCase = true) ||
          it.bio.contains(searchQuery, ignoreCase = true)
    }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("discover_screen"),
    contentPadding = PaddingValues(bottom = 80.dp)
  ) {
    // 1. Search Bar
    item {
      Box(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChange,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("discover_search_input"),
          placeholder = { Text("Search creators, topics, or #hashtags...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = UnifyCyan)
          },
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = UnifyCyan,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
          ),
          singleLine = true
        )
      }
    }

    // 2. Trending Hashtags Section
    item {
      Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.TrendingUp,
            contentDescription = null,
            tint = UnifyViolet,
            modifier = Modifier.size(20.dp)
          )
          Text(
            text = "Trending on UNIFY",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(trendingHashtags) { (tag, count) ->
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clickable { onSearchQueryChange(tag) }
                .testTag("trending_tag_$tag")
            ) {
              Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text(
                  text = tag,
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = UnifyCyan
                )
                Text(
                  text = count,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // 3. Suggested Creators Section
    item {
      Spacer(modifier = Modifier.height(20.dp))
      Text(
        text = "Suggested Creators",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      Spacer(modifier = Modifier.height(10.dp))
      LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(filteredUsers) { user ->
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier
              .width(160.dp)
              .testTag("creator_card_${user.username}")
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              AsyncImage(
                model = user.avatarUrl,
                contentDescription = user.displayName,
                modifier = Modifier
                  .size(54.dp)
                  .clip(CircleShape)
                  .border(2.dp, UnifyCyan.copy(alpha = 0.5f), CircleShape),
                contentScale = ContentScale.Crop
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
              ) {
                Text(
                  text = user.displayName,
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  maxLines = 1
                )
                if (user.isVerified) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = UnifyCyan,
                    modifier = Modifier.size(13.dp)
                  )
                }
              }
              Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "${user.followersCount} followers",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = { onFollowUser(user.id) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (user.isFollowing) MaterialTheme.colorScheme.surfaceVariant
                  else UnifyCyan,
                  contentColor = if (user.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant
                  else Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(30.dp)
              ) {
                Text(
                  text = if (user.isFollowing) "Following" else if (user.followRequested) "Requested" else "Follow",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
              }
            }
          }
        }
      }
    }

    // 4. Explore Visual Media Grid
    item {
      Spacer(modifier = Modifier.height(24.dp))
      Text(
        text = "Explore Public Content",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(horizontal = 16.dp)
      )
      Spacer(modifier = Modifier.height(10.dp))
    }

    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val pairs = exploreImages.chunked(2)
        pairs.forEach { pair ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            pair.forEach { imgUrl ->
              AsyncImage(
                model = imgUrl,
                contentDescription = "Explore media",
                modifier = Modifier
                  .weight(1f)
                  .height(140.dp)
                  .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
              )
            }
            if (pair.size == 1) {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
        }
      }
    }
  }
}
