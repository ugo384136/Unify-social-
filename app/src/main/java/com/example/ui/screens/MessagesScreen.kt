package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.*
import com.example.ui.components.VoiceRecorderBar
import com.example.ui.components.VoiceWaveformPlayer
import com.example.ui.theme.*

@Composable
fun MessagesScreen(
  conversations: List<Conversation>,
  activeConversationId: String?,
  currentUserId: String,
  messagesTab: Int, // 0 = Direct, 1 = Requests
  onTabSelected: (Int) -> Unit,
  onOpenConversation: (String) -> Unit,
  onCloseConversation: () -> Unit,
  onSendTextMessage: (convId: String, text: String) -> Unit,
  onSendVoiceMessage: (convId: String, durationSec: Int) -> Unit,
  onAcceptRequest: (String) -> Unit,
  onDeclineRequest: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val activeConversation = conversations.find { it.id == activeConversationId }

  if (activeConversation != null) {
    // Active Conversation Detail View
    ConversationDetailView(
      conversation = activeConversation,
      currentUserId = currentUserId,
      onBack = onCloseConversation,
      onSendText = { text -> onSendTextMessage(activeConversation.id, text) },
      onSendVoice = { duration -> onSendVoiceMessage(activeConversation.id, duration) },
      onAcceptRequest = { onAcceptRequest(activeConversation.id) },
      onDeclineRequest = { onDeclineRequest(activeConversation.id) },
      modifier = modifier
    )
  } else {
    // Conversation List View with Direct vs Message Requests tabs
    Column(
      modifier = modifier
        .fillMaxSize()
        .testTag("messages_list_screen")
    ) {
      // Sub-tabs: Direct Messages vs Message Requests
      val directConversations = conversations.filter { !it.isMessageRequest }
      val requestConversations = conversations.filter { it.isMessageRequest }

      TabRow(
        selectedTabIndex = messagesTab,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = UnifyCyan,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Tab(
          selected = messagesTab == 0,
          onClick = { onTabSelected(0) },
          text = {
            Text(
              text = "Direct (${directConversations.size})",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = if (messagesTab == 0) FontWeight.Bold else FontWeight.Normal
              )
            )
          },
          modifier = Modifier.testTag("tab_direct_messages")
        )
        Tab(
          selected = messagesTab == 1,
          onClick = { onTabSelected(1) },
          text = {
            BadgedBox(
              badge = {
                if (requestConversations.isNotEmpty()) {
                  Badge { Text("${requestConversations.size}") }
                }
              }
            ) {
              Text(
                text = "Requests",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = if (messagesTab == 1) FontWeight.Bold else FontWeight.Normal
                )
              )
            }
          },
          modifier = Modifier.testTag("tab_message_requests")
        )
      }

      val currentList = if (messagesTab == 0) directConversations else requestConversations

      if (currentList.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.MarkChatUnread,
              contentDescription = null,
              modifier = Modifier.size(54.dp),
              tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = if (messagesTab == 0) "No direct conversations yet" else "No pending message requests",
              style = MaterialTheme.typography.titleMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Connect with mutual followers to chat freely!",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(currentList, key = { it.id }) { conv ->
            ConversationItemRow(
              conversation = conv,
              onClick = { onOpenConversation(conv.id) }
            )
          }
        }
      }
    }
  }
}

@Composable
fun ConversationItemRow(
  conversation: Conversation,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("conversation_item_${conversation.id}"),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Avatar with Online indicator
      Box(modifier = Modifier.size(48.dp)) {
        AsyncImage(
          model = conversation.recipient.avatarUrl,
          contentDescription = conversation.recipient.displayName,
          modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape),
          contentScale = ContentScale.Crop
        )
        if (conversation.recipient.isOnline) {
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .size(14.dp)
              .clip(CircleShape)
              .background(UnifyEmerald)
              .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
          )
        }
      }

      // Name & Last message snippet
      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = conversation.recipient.displayName,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            if (conversation.recipient.isVerified) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = UnifyCyan,
                modifier = Modifier.size(14.dp)
              )
            }
          }
          Text(
            text = conversation.lastMessage.timestamp,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          val previewText = when (conversation.lastMessage.type) {
            MessageType.VOICE -> "🎙️ Voice message (${conversation.lastMessage.voiceDurationSec ?: 0}s)"
            MessageType.IMAGE -> "📷 Photo attached"
            else -> conversation.lastMessage.text
          }

          Text(
            text = previewText,
            style = MaterialTheme.typography.bodySmall,
            color = if (conversation.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          if (conversation.unreadCount > 0) {
            Box(
              modifier = Modifier
                .padding(start = 6.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(UnifyCyan),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "${conversation.unreadCount}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun ConversationDetailView(
  conversation: Conversation,
  currentUserId: String,
  onBack: () -> Unit,
  onSendText: (String) -> Unit,
  onSendVoice: (durationSec: Int) -> Unit,
  onAcceptRequest: () -> Unit,
  onDeclineRequest: () -> Unit,
  modifier: Modifier = Modifier
) {
  var chatInputText by remember { mutableStateOf("") }
  var isRecordingVoice by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("conversation_detail_screen")
  ) {
    // Header
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 4.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier.testTag("chat_back_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back"
          )
        }

        Box(modifier = Modifier.size(40.dp)) {
          AsyncImage(
            model = conversation.recipient.avatarUrl,
            contentDescription = conversation.recipient.displayName,
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape),
            contentScale = ContentScale.Crop
          )
          if (conversation.recipient.isOnline) {
            Box(
              modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(12.dp)
                .clip(CircleShape)
                .background(UnifyEmerald)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = conversation.recipient.displayName,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = if (conversation.recipient.isOnline) "Active now" else "Last seen ${conversation.recipient.lastSeen}",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = if (conversation.recipient.isOnline) UnifyEmerald else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Message Request Banner (if applicable)
    if (conversation.isMessageRequest) {
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "${conversation.recipient.displayName} wants to send you a message.",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Non-mutual connections require your approval before joining normal Direct chats.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
              onClick = onAcceptRequest,
              colors = ButtonDefaults.buttonColors(
                containerColor = UnifyCyan,
                contentColor = Color.Black
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.testTag("accept_message_request_btn")
            ) {
              Text("Accept")
            }
            OutlinedButton(
              onClick = onDeclineRequest,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.testTag("decline_message_request_btn")
            ) {
              Text("Decline")
            }
          }
        }
      }
    }

    // Messages Bubble Stream
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 14.dp),
      reverseLayout = false,
      contentPadding = PaddingValues(vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(conversation.messages, key = { it.id }) { message ->
        val isMe = message.senderId == currentUserId
        MessageBubble(message = message, isMe = isMe)
      }
    }

    // Bottom Chat Input Bar / Voice Recorder
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
    ) {
      if (isRecordingVoice) {
        Box(modifier = Modifier.padding(10.dp)) {
          VoiceRecorderBar(
            onCancel = { isRecordingVoice = false },
            onSendVoice = { duration ->
              onSendVoice(duration)
              isRecordingVoice = false
            },
            label = "Recording voice message..."
          )
        }
      } else {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Mic Button
          IconButton(
            onClick = { isRecordingVoice = true },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .testTag("record_voice_message_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "Record voice note",
              tint = UnifyViolet
            )
          }

          // Text Field
          OutlinedTextField(
            value = chatInputText,
            onValueChange = { chatInputText = it },
            placeholder = { Text("Message...") },
            modifier = Modifier
              .weight(1f)
              .testTag("chat_input_field"),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = UnifyCyan,
              unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
          )

          // Send Button
          IconButton(
            onClick = {
              if (chatInputText.isNotBlank()) {
                onSendText(chatInputText)
                chatInputText = ""
              }
            },
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(UnifyCyan)
              .testTag("chat_send_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Send,
              contentDescription = "Send message",
              tint = Color.Black,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun MessageBubble(
  message: ChatMessage,
  isMe: Boolean
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
  ) {
    Surface(
      shape = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 18.dp,
        bottomStart = if (isMe) 18.dp else 4.dp,
        bottomEnd = if (isMe) 4.dp else 18.dp
      ),
      color = if (isMe) UnifyCyan else MaterialTheme.colorScheme.surfaceVariant,
      contentColor = if (isMe) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier
        .widthIn(max = 290.dp)
        .testTag("message_bubble_${message.id}")
    ) {
      Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        if (message.type == MessageType.VOICE && message.voiceDurationSec != null) {
          VoiceWaveformPlayer(
            durationSec = message.voiceDurationSec,
            waveform = message.voiceWaveform,
            tintColor = if (isMe) Color.Black else UnifyCyan
          )
        } else {
          Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    }

    // Timestamp & Delivery status (Double checks)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
    ) {
      Text(
        text = message.timestamp,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
      )
      if (isMe) {
        val checkIcon = when (message.deliveryStatus) {
          DeliveryStatus.READ -> Icons.Default.DoneAll
          DeliveryStatus.DELIVERED -> Icons.Default.DoneAll
          DeliveryStatus.SENT -> Icons.Default.Done
          DeliveryStatus.SENDING -> Icons.Default.Schedule
        }
        val checkTint = if (message.deliveryStatus == DeliveryStatus.READ) UnifyCyan
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

        Icon(
          imageVector = checkIcon,
          contentDescription = null,
          tint = checkTint,
          modifier = Modifier.size(13.dp)
        )
      }
    }
  }
}
