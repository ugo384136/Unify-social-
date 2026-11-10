package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.model.AlertCategory
import com.example.model.AlertNotification
import com.example.model.NotificationType
import com.example.ui.theme.*

@Composable
fun AlertsScreen(
  alerts: List<AlertNotification>,
  selectedCategory: AlertCategory,
  onCategorySelected: (AlertCategory) -> Unit,
  onAcceptFollowRequest: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val filteredAlerts = if (selectedCategory == AlertCategory.ALL) alerts
  else alerts.filter { it.category == selectedCategory }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("alerts_screen")
  ) {
    // Category Chips Row
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(AlertCategory.values()) { category ->
        val isSelected = selectedCategory == category
        FilterChip(
          selected = isSelected,
          onClick = { onCategorySelected(category) },
          label = {
            Text(
              text = when (category) {
                AlertCategory.ALL -> "All"
                AlertCategory.MENTIONS -> "Mentions"
                AlertCategory.FOLLOWERS -> "Followers"
                AlertCategory.INTERACTIONS -> "Interactions"
                AlertCategory.MIRROR -> "MIRROR"
              },
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = UnifyCyan,
            selectedLabelColor = Color.Black
          ),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.testTag("filter_chip_${category.name}")
        )
      }
    }

    // Alerts List
    if (filteredAlerts.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(40.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(54.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No alerts in this category",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filteredAlerts, key = { it.id }) { alert ->
          AlertItemCard(
            alert = alert,
            onAcceptRequest = { onAcceptFollowRequest(alert.id) }
          )
        }
      }
    }
  }
}

@Composable
fun AlertItemCard(
  alert: AlertNotification,
  onAcceptRequest: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (!alert.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
      else MaterialTheme.colorScheme.surface
    ),
    border = CardDefaults.outlinedCardBorder(),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("alert_item_${alert.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.Top,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Icon or Actor Avatar
      Box(modifier = Modifier.size(44.dp)) {
        if (alert.actor != null) {
          AsyncImage(
            model = alert.actor.avatarUrl,
            contentDescription = alert.actor.displayName,
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
        } else {
          val (icon, bgColor) = when (alert.type) {
            NotificationType.MIRROR_PUBLISHED -> Pair(Icons.Default.CloudDone, UnifyCyan)
            NotificationType.SECURITY_ALERT -> Pair(Icons.Default.Shield, UnifyAmber)
            else -> Pair(Icons.Default.Notifications, UnifyViolet)
          }
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape)
              .background(bgColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = icon,
              contentDescription = null,
              tint = bgColor,
              modifier = Modifier.size(22.dp)
            )
          }
        }
      }

      // Notification Body
      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = alert.title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = alert.timestamp,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = alert.body,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Action Buttons if required (e.g. Follow request approval)
        if (alert.actionRequired && alert.type == NotificationType.FOLLOW_REQUEST) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
              onClick = onAcceptRequest,
              colors = ButtonDefaults.buttonColors(
                containerColor = UnifyCyan,
                contentColor = Color.Black
              ),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .height(30.dp)
                .testTag("accept_follow_alert_btn")
            ) {
              Text("Accept", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
            }
          }
        }
      }
    }
  }
}
