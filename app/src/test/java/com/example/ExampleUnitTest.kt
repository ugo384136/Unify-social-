package com.example

import com.example.model.ReportStatus
import com.example.model.SocialPlatformType
import com.example.viewmodel.MainNavDestination
import com.example.viewmodel.UnifyViewModel
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testNavigationDestinationUpdates() {
    val viewModel = UnifyViewModel()
    assertEquals(MainNavDestination.HOME, viewModel.uiState.value.currentNav)

    viewModel.setNavigation(MainNavDestination.DISCOVER)
    assertEquals(MainNavDestination.DISCOVER, viewModel.uiState.value.currentNav)

    viewModel.setNavigation(MainNavDestination.MESSAGES)
    assertEquals(MainNavDestination.MESSAGES, viewModel.uiState.value.currentNav)
  }

  @Test
  fun testLikeAndCommentOnPost() {
    val viewModel = UnifyViewModel()
    val initialPosts = viewModel.uiState.value.posts
    val firstPost = initialPosts.first()

    // Like post
    viewModel.toggleLikePost(firstPost.id)
    val updatedPost = viewModel.uiState.value.posts.first { it.id == firstPost.id }
    assertEquals(!firstPost.isLiked, updatedPost.isLiked)

    // Add voice comment
    val initialCommentsCount = updatedPost.comments.size
    viewModel.addComment(firstPost.id, "", isVoice = true, voiceDuration = 12)
    val postWithComment = viewModel.uiState.value.posts.first { it.id == firstPost.id }
    assertEquals(initialCommentsCount + 1, postWithComment.comments.size)
    assertNotNull(postWithComment.comments.last().voiceDurationSec)
  }

  @Test
  fun testUnifyMirrorOAuthAndPublishing() {
    val viewModel = UnifyViewModel()

    // Connect TikTok via OAuth
    val tiktokBefore = viewModel.uiState.value.connectedAccounts.find { it.platform == SocialPlatformType.TIKTOK }
    assertFalse(tiktokBefore?.isConnected == true)

    viewModel.confirmConnectAccount(SocialPlatformType.TIKTOK)
    val tiktokAfter = viewModel.uiState.value.connectedAccounts.find { it.platform == SocialPlatformType.TIKTOK }
    assertTrue(tiktokAfter?.isConnected == true)

    // Publish multi-platform post
    val initialMirrorPostsCount = viewModel.uiState.value.mirrorPosts.size
    viewModel.publishMultiPlatformPost(
      caption = "Testing UNIFY MIRROR live syndication",
      mediaUrl = null,
      platforms = listOf(SocialPlatformType.TIKTOK, SocialPlatformType.INSTAGRAM)
    )
    assertEquals(initialMirrorPostsCount + 1, viewModel.uiState.value.mirrorPosts.size)
  }

  @Test
  fun testControlCentreModeration() {
    val viewModel = UnifyViewModel()
    val reports = viewModel.uiState.value.moderationReports
    val firstReport = reports.first()

    viewModel.resolveModerationReport(firstReport.id, ReportStatus.RESOLVED_TAKEDOWN)
    val updatedReport = viewModel.uiState.value.moderationReports.first { it.id == firstReport.id }
    assertEquals(ReportStatus.RESOLVED_TAKEDOWN, updatedReport.status)
  }
}
