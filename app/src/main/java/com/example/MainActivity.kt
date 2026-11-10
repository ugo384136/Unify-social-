package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.auth.AuthManager
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainNavDestination
import com.example.viewmodel.UnifyViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
  private val viewModel: UnifyViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val credentialManager = remember { CredentialManager.create(context) }
        var firebaseUser by remember { mutableStateOf(Firebase.auth.currentUser) }

        // Realtime Firebase Auth listener
        DisposableEffect(Unit) {
          val listener = FirebaseAuth.AuthStateListener { auth ->
            firebaseUser = auth.currentUser
            auth.currentUser?.let { user ->
              viewModel.setAuthenticatedUser(user)
            }
          }
          Firebase.auth.addAuthStateListener(listener)
          onDispose {
            Firebase.auth.removeAuthStateListener(listener)
          }
        }

        // Composable Auth Gate: if not authenticated, display SignInScreen
        if (firebaseUser == null) {
          SignInScreen(
            onAuthSuccess = { user ->
              firebaseUser = user
              viewModel.setAuthenticatedUser(user)
              viewModel.showSnackbar("Welcome to UNIFY, ${user.displayName ?: "Creator"}!")
            }
          )
        } else {
          val currentUid = firebaseUser!!.uid
          key(currentUid) {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val snackbarHostState = remember { SnackbarHostState() }

            // Show transient status snacks
            LaunchedEffect(uiState.statusSnackbarMessage) {
              uiState.statusSnackbarMessage?.let { message ->
                snackbarHostState.showSnackbar(message)
                viewModel.clearSnackbar()
              }
            }

            // Back Handler for responsive Android navigation
            BackHandler(
              enabled = uiState.activeStoryViewer != null ||
                  uiState.activeConversationId != null ||
                  uiState.isMirrorModeOpen ||
                  uiState.isAdminControlCentreOpen ||
                  uiState.currentNav != MainNavDestination.HOME
            ) {
              when {
                uiState.activeStoryViewer != null -> viewModel.closeStoryViewer()
                uiState.activeConversationId != null -> viewModel.closeConversation()
                uiState.isMirrorModeOpen -> viewModel.toggleMirrorHub(false)
                uiState.isAdminControlCentreOpen -> viewModel.toggleAdminControlCentre(false)
                uiState.currentNav != MainNavDestination.HOME -> viewModel.setNavigation(MainNavDestination.HOME)
              }
            }

            Scaffold(
              modifier = Modifier
                .fillMaxSize()
                .testTag("unify_root_scaffold"),
              snackbarHost = { SnackbarHost(snackbarHostState) },
              topBar = {
                if (!uiState.isMirrorModeOpen && !uiState.isAdminControlCentreOpen && uiState.activeConversationId == null) {
                  UnifyTopBar(
                    isMirrorActive = uiState.isMirrorModeOpen,
                    isAdminActive = uiState.isAdminControlCentreOpen,
                    connectedPlatformsCount = uiState.connectedAccounts.count { it.isConnected },
                    unreadMessagesCount = uiState.conversations.sumOf { it.unreadCount },
                    onMirrorToggle = { viewModel.toggleMirrorHub(!uiState.isMirrorModeOpen) },
                    onAdminToggle = { viewModel.toggleAdminControlCentre(!uiState.isAdminControlCentreOpen) },
                    onMessagesShortcut = { viewModel.setNavigation(MainNavDestination.MESSAGES) }
                  )
                }
              },
              bottomBar = {
                if (!uiState.isMirrorModeOpen && !uiState.isAdminControlCentreOpen && uiState.activeConversationId == null) {
                  UnifyBottomNav(
                    currentNav = uiState.currentNav,
                    unreadMessagesCount = uiState.conversations.sumOf { it.unreadCount },
                    unreadAlertsCount = uiState.alerts.count { !it.isRead },
                    onNavSelected = { viewModel.setNavigation(it) }
                  )
                }
              }
            ) { innerPadding ->
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(innerPadding)
              ) {
                when {
                  uiState.isMirrorModeOpen -> {
                    // UNIFY MIRROR Flagship Suite
                    MirrorScreen(
                      connectedAccounts = uiState.connectedAccounts,
                      mirrorPosts = uiState.mirrorPosts,
                      mirrorFeed = uiState.mirrorFeed,
                      selectedTab = uiState.mirrorTab,
                      isConnectModalPlatform = uiState.isConnectAccountModalOpen,
                      onTabSelected = { viewModel.setMirrorTab(it) },
                      onToggleConnectAccount = { viewModel.toggleAccountConnection(it) },
                      onConfirmConnectOAuth = { viewModel.confirmConnectAccount(it) },
                      onDismissConnectModal = { viewModel.closeConnectAccountModal() },
                      onToggleAccountSync = { viewModel.toggleAccountSync(it) },
                      onPublishMultiPost = { caption, media, platforms ->
                        viewModel.publishMultiPlatformPost(caption, media, platforms)
                      },
                      onCloseMirror = { viewModel.toggleMirrorHub(false) }
                    )
                  }

                  uiState.isAdminControlCentreOpen -> {
                    // UNIFY CONTROL CENTRE Administrator Portal
                    AdminScreen(
                      kpis = uiState.adminKPIs,
                      moderationReports = uiState.moderationReports,
                      managedUsers = uiState.managedUsers,
                      announcements = uiState.systemAnnouncements,
                      platformHealthList = uiState.platformHealth,
                      selectedTab = uiState.adminTab,
                      onTabSelected = { viewModel.setAdminTab(it) },
                      onResolveReport = { reportId, status -> viewModel.resolveModerationReport(reportId, status) },
                      onToggleUserVerification = { viewModel.toggleUserVerification(it) },
                      onToggleUserSuspension = { viewModel.toggleUserSuspension(it) },
                      onBroadcastAnnouncement = { title, body, priority ->
                        viewModel.broadcastAnnouncement(title, body, priority)
                      },
                      onCloseAdmin = { viewModel.toggleAdminControlCentre(false) }
                    )
                  }

                  else -> {
                    // Primary UNIFY Socials destinations
                    when (uiState.currentNav) {
                      MainNavDestination.HOME -> {
                        HomeScreen(
                          posts = uiState.posts,
                          stories = uiState.stories,
                          currentUser = uiState.currentUser,
                          selectedTab = uiState.feedTab,
                          isCreatePostOpen = uiState.isCreatePostOpen,
                          onTabSelected = { viewModel.setFeedTab(it) },
                          onStoryClick = { viewModel.openStoryViewer(it) },
                          onAddStoryClick = { viewModel.addQuickStory("Just updated my story! 🚀") },
                          onLikePost = { viewModel.toggleLikePost(it) },
                          onReactPost = { postId, reaction -> viewModel.reactToPost(postId, reaction) },
                          onRepostPost = { viewModel.repostPost(it) },
                          onSharePost = { viewModel.showSnackbar("Link copied to clipboard!") },
                          onFollowAuthor = { viewModel.toggleFollowUser(it) },
                          onAddComment = { postId, text, isVoice, duration ->
                            viewModel.addComment(postId, text, isVoice, duration)
                          },
                          onToggleCreatePost = { viewModel.toggleCreatePostModal(it) },
                          onSubmitNewPost = { text, media -> viewModel.createNewPost(text, media) }
                        )
                      }

                      MainNavDestination.DISCOVER -> {
                        DiscoverScreen(
                          searchQuery = uiState.searchQuery,
                          suggestedUsers = uiState.managedUsers.filterNot { it.id == uiState.currentUser.id },
                          posts = uiState.posts,
                          onSearchQueryChange = { viewModel.setSearchQuery(it) },
                          onFollowUser = { viewModel.toggleFollowUser(it) }
                        )
                      }

                      MainNavDestination.MESSAGES -> {
                        MessagesScreen(
                          conversations = uiState.conversations,
                          activeConversationId = uiState.activeConversationId,
                          currentUserId = uiState.currentUser.id,
                          messagesTab = uiState.messagesTab,
                          onTabSelected = { viewModel.setMessagesTab(it) },
                          onOpenConversation = { viewModel.openConversation(it) },
                          onCloseConversation = { viewModel.closeConversation() },
                          onSendTextMessage = { convId, text -> viewModel.sendTextMessage(convId, text) },
                          onSendVoiceMessage = { convId, duration -> viewModel.sendVoiceMessage(convId, duration) },
                          onAcceptRequest = { viewModel.acceptMessageRequest(it) },
                          onDeclineRequest = { viewModel.declineMessageRequest(it) }
                        )
                      }

                      MainNavDestination.ALERTS -> {
                        AlertsScreen(
                          alerts = uiState.alerts,
                          selectedCategory = uiState.alertsFilter,
                          onCategorySelected = { viewModel.setAlertsFilter(it) },
                          onAcceptFollowRequest = { viewModel.acceptFollowRequest(it) }
                        )
                      }

                      MainNavDestination.PROFILE -> {
                        ProfileScreen(
                          user = uiState.currentUser,
                          userPosts = uiState.posts,
                          onPrivacyToggle = { viewModel.toggleAccountPrivacy(it) },
                          onUpdateProfile = { name, bio, loc, web ->
                            viewModel.updateProfile(name, bio, loc, web)
                          },
                          onSignOut = {
                            AuthManager.signOut(
                              credentialManager = credentialManager,
                              scope = coroutineScope,
                              onSignOutComplete = {
                                firebaseUser = null
                                viewModel.showSnackbar("Signed out successfully.")
                              }
                            )
                          }
                        )
                      }
                    }
                  }
                }

                // Fullscreen Story Viewer Modal
                uiState.activeStoryViewer?.let { userStories ->
                  StoryViewerModal(
                    userStories = userStories,
                    initialIndex = uiState.activeStoryIndex,
                    onClose = { viewModel.closeStoryViewer() },
                    onStoryFinished = { viewModel.nextStory() },
                    onReactionSent = { reaction ->
                      viewModel.showSnackbar("Reaction sent: $reaction")
                      viewModel.closeStoryViewer()
                    }
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
