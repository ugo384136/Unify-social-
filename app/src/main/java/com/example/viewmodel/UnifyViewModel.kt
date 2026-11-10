package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.UnifySampleData
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MainNavDestination(val label: String, val testTag: String) {
  HOME("Home", "nav_home"),
  DISCOVER("Discover", "nav_discover"),
  MESSAGES("Messages", "nav_messages"),
  ALERTS("Alerts", "nav_alerts"),
  PROFILE("Profile", "nav_profile")
}

data class UnifyUiState(
  val currentNav: MainNavDestination = MainNavDestination.HOME,
  val isMirrorModeOpen: Boolean = false,
  val isAdminControlCentreOpen: Boolean = false,
  val currentUser: User = UnifySampleData.currentUser,
  val stories: List<UserStories> = UnifySampleData.sampleStories,
  val activeStoryViewer: UserStories? = null,
  val activeStoryIndex: Int = 0,
  val posts: List<Post> = UnifySampleData.samplePosts,
  val feedTab: Int = 0, // 0 = For You, 1 = Following
  val activeCommentsPostId: String? = null,
  val isCreatePostOpen: Boolean = false,
  val conversations: List<Conversation> = UnifySampleData.sampleConversations,
  val activeConversationId: String? = null,
  val messagesTab: Int = 0, // 0 = Direct, 1 = Requests
  val alerts: List<AlertNotification> = UnifySampleData.sampleAlerts,
  val alertsFilter: AlertCategory = AlertCategory.ALL,
  val connectedAccounts: List<ConnectedAccount> = UnifySampleData.sampleConnectedAccounts,
  val mirrorPosts: List<MirrorPost> = UnifySampleData.sampleMirrorPosts,
  val mirrorFeed: List<MirrorFeedItem> = UnifySampleData.sampleMirrorFeed,
  val isConnectAccountModalOpen: SocialPlatformType? = null,
  val isUnifiedPublisherOpen: Boolean = false,
  val mirrorTab: Int = 0, // 0 = Accounts, 1 = Publisher & Queue, 2 = Feeds
  val adminKPIs: AdminKPIs = AdminKPIs(),
  val moderationReports: List<ModerationReport> = UnifySampleData.sampleModerationReports,
  val managedUsers: List<User> = listOf(
    UnifySampleData.currentUser,
    UnifySampleData.elena,
    UnifySampleData.marcus,
    UnifySampleData.sophia,
    UnifySampleData.david
  ),
  val systemAnnouncements: List<SystemAnnouncement> = UnifySampleData.sampleAnnouncements,
  val platformHealth: List<PlatformIntegrationHealth> = UnifySampleData.platformHealthList,
  val adminTab: Int = 0, // 0 = Overview, 1 = Moderation, 2 = Users, 3 = Integrations
  val searchQuery: String = "",
  val activeRecordingDurationSec: Int = 0,
  val isRecordingVoiceMessage: Boolean = false,
  val statusSnackbarMessage: String? = null
)

class UnifyViewModel : ViewModel() {
  private val _uiState = MutableStateFlow(UnifyUiState())
  val uiState: StateFlow<UnifyUiState> = _uiState.asStateFlow()

  fun setNavigation(destination: MainNavDestination) {
    _uiState.update { it.copy(currentNav = destination, isMirrorModeOpen = false, isAdminControlCentreOpen = false) }
  }

  fun setAuthenticatedUser(firebaseUser: com.google.firebase.auth.FirebaseUser) {
    val name = firebaseUser.displayName?.ifBlank { null } ?: "UNIFY Creator"
    val username = firebaseUser.email?.substringBefore("@")?.lowercase() ?: "creator"
    val avatar = firebaseUser.photoUrl?.toString() ?: _uiState.value.currentUser.avatarUrl

    _uiState.update { state ->
      val updatedUser = state.currentUser.copy(
        id = firebaseUser.uid,
        displayName = name,
        username = username,
        avatarUrl = avatar,
        bio = if (state.currentUser.bio.isNotBlank()) state.currentUser.bio else "UNIFY Socials member • Building connections",
        isVerified = true
      )
      state.copy(currentUser = updatedUser)
    }
  }

  fun toggleMirrorHub(open: Boolean) {
    _uiState.update { it.copy(isMirrorModeOpen = open, isAdminControlCentreOpen = false) }
  }

  fun toggleAdminControlCentre(open: Boolean) {
    _uiState.update { it.copy(isAdminControlCentreOpen = open, isMirrorModeOpen = false) }
  }

  fun setFeedTab(tab: Int) {
    _uiState.update { it.copy(feedTab = tab) }
  }

  fun setMessagesTab(tab: Int) {
    _uiState.update { it.copy(messagesTab = tab) }
  }

  fun setMirrorTab(tab: Int) {
    _uiState.update { it.copy(mirrorTab = tab) }
  }

  fun setAdminTab(tab: Int) {
    _uiState.update { it.copy(adminTab = tab) }
  }

  fun setAlertsFilter(category: AlertCategory) {
    _uiState.update { it.copy(alertsFilter = category) }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun showSnackbar(message: String) {
    _uiState.update { it.copy(statusSnackbarMessage = message) }
  }

  fun clearSnackbar() {
    _uiState.update { it.copy(statusSnackbarMessage = null) }
  }

  // --- Story Handling ---
  fun openStoryViewer(userStories: UserStories) {
    _uiState.update {
      it.copy(activeStoryViewer = userStories, activeStoryIndex = 0)
    }
  }

  fun closeStoryViewer() {
    _uiState.update { it.copy(activeStoryViewer = null, activeStoryIndex = 0) }
  }

  fun nextStory() {
    val current = _uiState.value.activeStoryViewer ?: return
    val nextIndex = _uiState.value.activeStoryIndex + 1
    if (nextIndex < current.stories.size) {
      _uiState.update { it.copy(activeStoryIndex = nextIndex) }
    } else {
      closeStoryViewer()
    }
  }

  fun previousStory() {
    val prevIndex = _uiState.value.activeStoryIndex - 1
    if (prevIndex >= 0) {
      _uiState.update { it.copy(activeStoryIndex = prevIndex) }
    }
  }

  fun addQuickStory(caption: String) {
    val newStory = StoryItem(
      id = "story_${System.currentTimeMillis()}",
      mediaUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=800&auto=format&fit=crop&q=80",
      caption = caption.ifBlank { "Just posted to my UNIFY story! 🌟" },
      timestamp = "Just now"
    )
    _uiState.update { state ->
      val updatedStories = state.stories.map { userStories ->
        if (userStories.user.id == state.currentUser.id) {
          userStories.copy(stories = listOf(newStory) + userStories.stories)
        } else userStories
      }
      state.copy(stories = updatedStories)
    }
    showSnackbar("Story added to your profile!")
  }

  // --- Post Interactions ---
  fun toggleLikePost(postId: String) {
    _uiState.update { state ->
      val updatedPosts = state.posts.map { post ->
        if (post.id == postId) {
          val newLiked = !post.isLiked
          val newCount = if (newLiked) post.likesCount + 1 else maxOf(0, post.likesCount - 1)
          val newReaction = if (newLiked) ReactionType.LIKE else null
          post.copy(isLiked = newLiked, likesCount = newCount, userReaction = newReaction)
        } else post
      }
      state.copy(posts = updatedPosts)
    }
  }

  fun reactToPost(postId: String, reaction: ReactionType) {
    _uiState.update { state ->
      val updatedPosts = state.posts.map { post ->
        if (post.id == postId) {
          val alreadyReacted = post.userReaction == reaction
          val newReaction = if (alreadyReacted) null else reaction
          val newLiked = newReaction != null
          val newCount = if (post.isLiked) {
            if (newLiked) post.likesCount else maxOf(0, post.likesCount - 1)
          } else {
            if (newLiked) post.likesCount + 1 else post.likesCount
          }
          post.copy(isLiked = newLiked, userReaction = newReaction, likesCount = newCount)
        } else post
      }
      state.copy(posts = updatedPosts)
    }
  }

  fun repostPost(postId: String) {
    _uiState.update { state ->
      val updatedPosts = state.posts.map { post ->
        if (post.id == postId) {
          val newReposted = !post.isReposted
          val newCount = if (newReposted) post.repostsCount + 1 else maxOf(0, post.repostsCount - 1)
          post.copy(isReposted = newReposted, repostsCount = newCount)
        } else post
      }
      state.copy(posts = updatedPosts)
    }
    showSnackbar("Reposted to your followers!")
  }

  fun openCommentsSheet(postId: String) {
    _uiState.update { it.copy(activeCommentsPostId = postId) }
  }

  fun closeCommentsSheet() {
    _uiState.update { it.copy(activeCommentsPostId = null) }
  }

  fun addComment(postId: String, text: String, isVoice: Boolean = false, voiceDuration: Int = 0) {
    if (text.isBlank() && !isVoice) return
    val newComment = Comment(
      id = "comment_${System.currentTimeMillis()}",
      author = _uiState.value.currentUser,
      text = if (isVoice) "Voice Comment ($voiceDuration sec)" else text,
      timestamp = "Just now",
      voiceDurationSec = if (isVoice) voiceDuration else null,
      voiceWaveform = if (isVoice) listOf(0.2f, 0.6f, 0.9f, 0.5f, 0.8f, 0.4f, 0.7f, 0.3f) else null
    )
    _uiState.update { state ->
      val updatedPosts = state.posts.map { post ->
        if (post.id == postId) {
          post.copy(comments = post.comments + newComment)
        } else post
      }
      state.copy(posts = updatedPosts)
    }
    showSnackbar(if (isVoice) "Voice comment posted! 🎙️" else "Comment posted!")
  }

  fun toggleCreatePostModal(open: Boolean) {
    _uiState.update { it.copy(isCreatePostOpen = open) }
  }

  fun createNewPost(
    text: String,
    mediaUrl: String? = null,
    hashtags: List<String> = emptyList(),
    tags: List<String> = emptyList()
  ) {
    if (text.isBlank() && mediaUrl == null) return
    val newPost = Post(
      id = "post_${System.currentTimeMillis()}",
      author = _uiState.value.currentUser,
      text = text,
      mediaUrls = if (mediaUrl != null) listOf(mediaUrl) else emptyList(),
      timestamp = "Just now",
      likesCount = 0,
      comments = emptyList(),
      hashtags = hashtags,
      tags = tags
    )
    _uiState.update { state ->
      state.copy(
        posts = listOf(newPost) + state.posts,
        isCreatePostOpen = false,
        currentUser = state.currentUser.copy(postsCount = state.currentUser.postsCount + 1)
      )
    }
    showSnackbar("Post published to UNIFY Feed!")
  }

  // --- Messaging Operations ---
  fun openConversation(convId: String) {
    _uiState.update { state ->
      val updated = state.conversations.map {
        if (it.id == convId) it.copy(unreadCount = 0) else it
      }
      state.copy(conversations = updated, activeConversationId = convId)
    }
  }

  fun closeConversation() {
    _uiState.update { it.copy(activeConversationId = null) }
  }

  fun sendTextMessage(convId: String, text: String) {
    if (text.isBlank()) return
    val newMsg = ChatMessage(
      id = "msg_${System.currentTimeMillis()}",
      senderId = _uiState.value.currentUser.id,
      type = MessageType.TEXT,
      text = text,
      timestamp = "Just now",
      deliveryStatus = DeliveryStatus.SENT
    )
    updateConversationWithNewMessage(convId, newMsg)

    // Simulate instant delivery & read receipts
    viewModelScope.launch {
      delay(600)
      updateMessageStatus(convId, newMsg.id, DeliveryStatus.DELIVERED)
      delay(1200)
      updateMessageStatus(convId, newMsg.id, DeliveryStatus.READ)
    }
  }

  fun sendVoiceMessage(convId: String, durationSec: Int) {
    val duration = maxOf(3, durationSec)
    val newMsg = ChatMessage(
      id = "msg_voice_${System.currentTimeMillis()}",
      senderId = _uiState.value.currentUser.id,
      type = MessageType.VOICE,
      voiceDurationSec = duration,
      voiceWaveform = listOf(0.1f, 0.4f, 0.8f, 0.95f, 0.6f, 0.85f, 0.5f, 0.7f, 0.3f, 0.2f),
      timestamp = "Just now",
      deliveryStatus = DeliveryStatus.SENT
    )
    updateConversationWithNewMessage(convId, newMsg)
    showSnackbar("Voice message sent! 🎙️ ($duration sec)")
  }

  private fun updateConversationWithNewMessage(convId: String, msg: ChatMessage) {
    _uiState.update { state ->
      val updatedConvs = state.conversations.map { conv ->
        if (conv.id == convId) {
          conv.copy(
            lastMessage = msg,
            messages = conv.messages + msg
          )
        } else conv
      }
      state.copy(conversations = updatedConvs)
    }
  }

  private fun updateMessageStatus(convId: String, msgId: String, status: DeliveryStatus) {
    _uiState.update { state ->
      val updatedConvs = state.conversations.map { conv ->
        if (conv.id == convId) {
          val updatedMsgs = conv.messages.map { m ->
            if (m.id == msgId) m.copy(deliveryStatus = status) else m
          }
          conv.copy(messages = updatedMsgs)
        } else conv
      }
      state.copy(conversations = updatedConvs)
    }
  }

  fun acceptMessageRequest(convId: String) {
    _uiState.update { state ->
      val updatedConvs = state.conversations.map { conv ->
        if (conv.id == convId) {
          conv.copy(isMessageRequest = false, isMutualFollower = true)
        } else conv
      }
      state.copy(conversations = updatedConvs)
    }
    showSnackbar("Message request accepted! Moved to Direct.")
  }

  fun declineMessageRequest(convId: String) {
    _uiState.update { state ->
      val updatedConvs = state.conversations.filterNot { it.id == convId }
      state.copy(conversations = updatedConvs, activeConversationId = null)
    }
    showSnackbar("Message request removed.")
  }

  // --- Follow / Relationship System ---
  fun toggleFollowUser(userId: String) {
    _uiState.update { state ->
      val updatedUsers = state.managedUsers.map { user ->
        if (user.id == userId) {
          if (user.isPrivate && !user.isFollowing && !user.followRequested) {
            user.copy(followRequested = true)
          } else {
            val newFollowing = !user.isFollowing
            val newFollowers = if (newFollowing) user.followersCount + 1 else maxOf(0, user.followersCount - 1)
            user.copy(isFollowing = newFollowing, followRequested = false, followersCount = newFollowers)
          }
        } else user
      }
      val updatedPosts = state.posts.map { post ->
        if (post.author.id == userId) {
          val authorUser = updatedUsers.find { it.id == userId } ?: post.author
          post.copy(author = authorUser)
        } else post
      }
      state.copy(managedUsers = updatedUsers, posts = updatedPosts)
    }
  }

  fun acceptFollowRequest(alertId: String) {
    _uiState.update { state ->
      val updatedAlerts = state.alerts.map { alert ->
        if (alert.id == alertId) alert.copy(actionRequired = false, isRead = true) else alert
      }
      state.copy(
        alerts = updatedAlerts,
        currentUser = state.currentUser.copy(followersCount = state.currentUser.followersCount + 1)
      )
    }
    showSnackbar("Follow request approved! Sophia now follows you.")
  }

  // --- UNIFY MIRROR Operations ---
  fun openConnectAccountModal(platform: SocialPlatformType) {
    _uiState.update { it.copy(isConnectAccountModalOpen = platform) }
  }

  fun closeConnectAccountModal() {
    _uiState.update { it.copy(isConnectAccountModalOpen = null) }
  }

  fun confirmConnectAccount(platform: SocialPlatformType) {
    _uiState.update { state ->
      val updated = state.connectedAccounts.map { acc ->
        if (acc.platform == platform) {
          acc.copy(
            isConnected = true,
            health = ConnectionHealth.HEALTHY,
            tokenExpiresInDays = 60,
            lastSynced = "Just now",
            accountHandle = "@alexrivera_${platform.id}"
          )
        } else acc
      }
      state.copy(connectedAccounts = updated, isConnectAccountModalOpen = null)
    }
    showSnackbar("Connected to ${platform.displayName} via OAuth! 🪞")
  }

  fun toggleAccountConnection(platform: SocialPlatformType) {
    val account = _uiState.value.connectedAccounts.find { it.platform == platform }
    if (account?.isConnected == true) {
      _uiState.update { state ->
        val updated = state.connectedAccounts.map {
          if (it.platform == platform) it.copy(isConnected = false, health = ConnectionHealth.DISCONNECTED) else it
        }
        state.copy(connectedAccounts = updated)
      }
      showSnackbar("Disconnected ${platform.displayName}")
    } else {
      openConnectAccountModal(platform)
    }
  }

  fun toggleAccountSync(platform: SocialPlatformType) {
    _uiState.update { state ->
      val updated = state.connectedAccounts.map {
        if (it.platform == platform) it.copy(syncEnabled = !it.syncEnabled) else it
      }
      state.copy(connectedAccounts = updated)
    }
  }

  fun toggleUnifiedPublisher(open: Boolean) {
    _uiState.update { it.copy(isUnifiedPublisherOpen = open) }
  }

  fun publishMultiPlatformPost(
    caption: String,
    mediaUrl: String?,
    platforms: List<SocialPlatformType>
  ) {
    if (caption.isBlank() && mediaUrl == null) return
    val results = platforms.map { p ->
      MirrorPublishedItem(
        platform = p,
        status = PublishStatus.PUBLISHED,
        externalPostUrl = "https://${p.id}.com/post/demo"
      )
    }
    val newMirrorPost = MirrorPost(
      id = "mp_${System.currentTimeMillis()}",
      caption = caption,
      mediaUrl = mediaUrl,
      selectedPlatforms = platforms,
      publishedAt = "Just now",
      publishResults = results
    )
    _uiState.update { state ->
      state.copy(
        mirrorPosts = listOf(newMirrorPost) + state.mirrorPosts,
        isUnifiedPublisherOpen = false
      )
    }
    showSnackbar("Published to ${platforms.size} platforms through UNIFY MIRROR! 🚀")
  }

  // --- UNIFY CONTROL CENTRE (Admin) ---
  fun resolveModerationReport(reportId: String, action: ReportStatus) {
    _uiState.update { state ->
      val updatedReports = state.moderationReports.map { report ->
        if (report.id == reportId) report.copy(status = action) else report
      }
      state.copy(
        moderationReports = updatedReports,
        adminKPIs = state.adminKPIs.copy(pendingReports = maxOf(0, state.adminKPIs.pendingReports - 1))
      )
    }
    val message = when (action) {
      ReportStatus.RESOLVED_TAKEDOWN -> "Content taken down and enforcement logged."
      ReportStatus.DISMISSED -> "Report dismissed as compliant."
      else -> "Report updated."
    }
    showSnackbar(message)
  }

  fun toggleUserVerification(userId: String) {
    _uiState.update { state ->
      val updatedUsers = state.managedUsers.map { user ->
        if (user.id == userId) user.copy(isVerified = !user.isVerified) else user
      }
      state.copy(managedUsers = updatedUsers)
    }
    showSnackbar("User verification badge updated.")
  }

  fun toggleUserSuspension(userId: String) {
    _uiState.update { state ->
      val user = state.managedUsers.find { it.id == userId }
      val isBlocked = user?.isBlocked ?: false
      val updatedUsers = state.managedUsers.map {
        if (it.id == userId) it.copy(isBlocked = !isBlocked) else it
      }
      state.copy(managedUsers = updatedUsers)
    }
    showSnackbar("User account status updated in directory.")
  }

  fun broadcastAnnouncement(title: String, body: String, priority: AnnouncementPriority) {
    if (title.isBlank() || body.isBlank()) return
    val newAnn = SystemAnnouncement(
      id = "ann_${System.currentTimeMillis()}",
      title = title,
      body = body,
      priority = priority,
      authorAdmin = "Super Admin (You)",
      timestamp = "Just now"
    )
    _uiState.update { state ->
      state.copy(systemAnnouncements = listOf(newAnn) + state.systemAnnouncements)
    }
    showSnackbar("Broadcast announcement pushed to all UNIFY users! 📢")
  }

  // --- Profile Settings ---
  fun toggleAccountPrivacy(isPrivate: Boolean) {
    _uiState.update { state ->
      state.copy(currentUser = state.currentUser.copy(isPrivate = isPrivate))
    }
    showSnackbar(if (isPrivate) "Account set to Private" else "Account set to Public")
  }

  fun updateProfile(displayName: String, bio: String, location: String, website: String) {
    _uiState.update { state ->
      state.copy(
        currentUser = state.currentUser.copy(
          displayName = displayName.ifBlank { state.currentUser.displayName },
          bio = bio.ifBlank { state.currentUser.bio },
          location = location.ifBlank { state.currentUser.location },
          website = website.ifBlank { state.currentUser.website }
        )
      )
    }
    showSnackbar("Profile updated successfully!")
  }
}
