package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.UnifyCyan
import com.example.ui.theme.UnifyPink
import com.example.ui.theme.UnifyViolet

@Composable
fun UnifyTopBar(
  isMirrorActive: Boolean,
  isAdminActive: Boolean,
  connectedPlatformsCount: Int,
  unreadMessagesCount: Int,
  onMirrorToggle: () -> Unit,
  onAdminToggle: () -> Unit,
  onMessagesShortcut: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding()
      .testTag("unify_top_bar"),
    color = MaterialTheme.colorScheme.background,
    tonalElevation = 3.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // UNIFY Brand Logo & Title
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(listOf(UnifyCyan, UnifyViolet))
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "U",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Color.Black
          )
        }

        Column {
          Text(
            text = "UNIFY",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      }

      // Action Pills (MIRROR Flagship & Admin Control Centre)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // UNIFY MIRROR Switcher Pill
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onMirrorToggle() }
            .testTag("toggle_mirror_btn"),
          color = if (isMirrorActive) UnifyCyan else MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(18.dp),
          border = if (isMirrorActive) null else CardDefaults.outlinedCardBorder()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "🪞 MIRROR",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = if (isMirrorActive) Color.Black else MaterialTheme.colorScheme.onSurface
            )
            // Connected badge
            Box(
              modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (isMirrorActive) Color.Black else UnifyCyan),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$connectedPlatformsCount",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isMirrorActive) UnifyCyan else Color.Black
              )
            }
          }
        }

        // UNIFY CONTROL CENTRE (Admin) Switcher Pill
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onAdminToggle() }
            .testTag("toggle_admin_btn"),
          color = if (isAdminActive) UnifyViolet else MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(18.dp),
          border = if (isAdminActive) null else CardDefaults.outlinedCardBorder()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AdminPanelSettings,
              contentDescription = "Admin Control Centre",
              tint = if (isAdminActive) Color.White else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(15.dp)
            )
            Text(
              text = "CONTROL",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = if (isAdminActive) Color.White else MaterialTheme.colorScheme.onSurface
            )
          }
        }

        // Direct Messages Quick Icon with Badge
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onMessagesShortcut() }
            .testTag("top_bar_messages_btn"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Forum,
            contentDescription = "Messages",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(18.dp)
          )
          if (unreadMessagesCount > 0) {
            Box(
              modifier = Modifier
                .align(Alignment.TopEnd)
                .size(14.dp)
                .clip(CircleShape)
                .background(UnifyPink),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$unreadMessagesCount",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}
