package com.example.model

enum class MessageType {
  TEXT,
  VOICE,
  IMAGE
}

enum class DeliveryStatus {
  SENDING,
  SENT,
  DELIVERED,
  READ
}

data class ChatMessage(
  val id: String,
  val senderId: String,
  val type: MessageType = MessageType.TEXT,
  val text: String = "",
  val mediaUrl: String? = null,
  val voiceDurationSec: Int? = null,
  val voiceWaveform: List<Float>? = null,
  val timestamp: String,
  val deliveryStatus: DeliveryStatus = DeliveryStatus.DELIVERED
)

data class Conversation(
  val id: String,
  val recipient: User,
  val isMutualFollower: Boolean = true,
  val isMessageRequest: Boolean = false,
  val lastMessage: ChatMessage,
  val unreadCount: Int = 0,
  val messages: List<ChatMessage> = emptyList(),
  val isTyping: Boolean = false
)
