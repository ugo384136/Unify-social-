package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.UnifyCyan
import com.example.viewmodel.MainNavDestination

@Composable
fun UnifyBottomNav(
  currentNav: MainNavDestination,
  unreadMessagesCount: Int,
  unreadAlertsCount: Int,
  onNavSelected: (MainNavDestination) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .testTag("unify_bottom_navigation"),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp
  ) {
    MainNavDestination.values().forEach { destination ->
      val isSelected = currentNav == destination
      val (iconSelected, iconUnselected) = when (destination) {
        MainNavDestination.HOME -> Pair(Icons.Default.Home, Icons.Outlined.Home)
        MainNavDestination.DISCOVER -> Pair(Icons.Default.Explore, Icons.Outlined.Explore)
        MainNavDestination.MESSAGES -> Pair(Icons.Default.ChatBubble, Icons.Outlined.ChatBubbleOutline)
        MainNavDestination.ALERTS -> Pair(Icons.Default.Notifications, Icons.Outlined.NotificationsNone)
        MainNavDestination.PROFILE -> Pair(Icons.Default.Person, Icons.Outlined.PersonOutline)
      }

      NavigationBarItem(
        selected = isSelected,
        onClick = { onNavSelected(destination) },
        icon = {
          BadgedBox(
            badge = {
              if (destination == MainNavDestination.MESSAGES && unreadMessagesCount > 0) {
                Badge { Text("$unreadMessagesCount") }
              } else if (destination == MainNavDestination.ALERTS && unreadAlertsCount > 0) {
                Badge { Text("$unreadAlertsCount") }
              }
            }
          ) {
            Icon(
              imageVector = if (isSelected) iconSelected else iconUnselected,
              contentDescription = destination.label,
              tint = if (isSelected) UnifyCyan else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        label = {
          Text(
            text = destination.label,
            style = MaterialTheme.typography.labelSmall
          )
        },
        colors = NavigationBarItemDefaults.colors(
          indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
          selectedTextColor = UnifyCyan,
          unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.testTag(destination.testTag)
      )
    }
  }
}
