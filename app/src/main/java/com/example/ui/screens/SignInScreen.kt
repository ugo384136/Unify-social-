package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.auth.AuthManager
import com.example.ui.theme.*
import com.google.firebase.auth.FirebaseUser

@Composable
fun SignInScreen(
  onAuthSuccess: (FirebaseUser) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }

  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var activeAuthTab by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Sign Up

  // Attempt silent auto-sign-in on initial load if user already authorized
  LaunchedEffect(Unit) {
    AuthManager.attemptAutoSignIn(
      context = context,
      credentialManager = credentialManager,
      onAuthSuccess = onAuthSuccess,
      onUnauthenticated = { /* stay on sign-in screen */ },
      scope = coroutineScope
    )
  }

  Surface(
    modifier = modifier
      .fillMaxSize()
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("sign_in_screen"),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Branding Section
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 28.dp)
      ) {
        // Glowing App Icon
        Box(
          modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
              Brush.linearGradient(
                listOf(UnifyCyan, UnifyViolet, UnifyPink)
              )
            )
            .padding(3.dp),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "U",
              fontWeight = FontWeight.Black,
              fontSize = 38.sp,
              color = UnifyCyan
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "UNIFY Socials",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          ),
          color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "One platform. Multiple connections. A unified social experience.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 12.dp)
        )
      }

      // Middle Features Highlights Card
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          FeatureHighlightRow(
            icon = Icons.Default.Public,
            iconTint = UnifyCyan,
            title = "Unified Social Networking",
            subtitle = "Rich posts, stories carousel, threaded voice comments & direct DMs"
          )
          FeatureHighlightRow(
            icon = Icons.Default.SwapHoriz,
            iconTint = UnifyViolet,
            title = "UNIFY MIRROR",
            subtitle = "Connect & syndicate posts across 8 external social platforms"
          )
          FeatureHighlightRow(
            icon = Icons.Default.Shield,
            iconTint = UnifyEmerald,
            title = "UNIFY CONTROL CENTRE",
            subtitle = "Zero-trust verification, moderation telemetry & admin safety"
          )
        }
      }

      // Bottom Auth Actions Section
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Tab indicator (Sign In vs Sign Up)
        TabRow(
          selectedTabIndex = activeAuthTab,
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          contentColor = UnifyCyan,
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
        ) {
          Tab(
            selected = activeAuthTab == 0,
            onClick = { activeAuthTab = 0 },
            text = { Text("Sign In", fontWeight = FontWeight.Bold) },
            modifier = Modifier.testTag("tab_auth_signin")
          )
          Tab(
            selected = activeAuthTab == 1,
            onClick = { activeAuthTab = 1 },
            text = { Text("Create Account", fontWeight = FontWeight.Bold) },
            modifier = Modifier.testTag("tab_auth_signup")
          )
        }

        // Error message card if any
        AnimatedVisibility(visible = errorMessage != null) {
          errorMessage?.let { error ->
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = UnifyRose.copy(alpha = 0.15f),
              border = BorderStroke(1.dp, UnifyRose.copy(alpha = 0.4f)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = UnifyRose)
                Text(
                  text = error,
                  style = MaterialTheme.typography.bodySmall,
                  color = UnifyRose
                )
              }
            }
          }
        }

        // Primary Google Sign-In Button
        Button(
          onClick = {
            isLoading = true
            errorMessage = null
            AuthManager.onGoogleSignInClicked(
              context = context,
              credentialManager = credentialManager,
              onAuthSuccess = { user ->
                isLoading = false
                onAuthSuccess(user)
              },
              onAuthError = { err ->
                isLoading = false
                errorMessage = err
              },
              scope = coroutineScope,
              onAuthCancelled = {
                isLoading = false
              }
            )
          },
          enabled = !isLoading,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFF1F1F1F)
          ),
          shape = RoundedCornerShape(20.dp),
          contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(1.dp, Color(0xFFDADCE0), RoundedCornerShape(20.dp))
            .testTag("google_sign_in_button")
        ) {
          if (isLoading) {
            CircularProgressIndicator(
              modifier = Modifier.size(24.dp),
              color = UnifyCyan,
              strokeWidth = 2.5.dp
            )
          } else {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              // Google Colored G Symbol
              Text(
                text = "G",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = Color(0xFF4285F4)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = if (activeAuthTab == 0) "Sign in with Google" else "Sign up with Google",
                style = MaterialTheme.typography.titleSmall.copy(
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.3.sp
                ),
                color = Color(0xFF1F1F1F)
              )
            }
          }
        }

        // Informational caption
        Text(
          text = if (activeAuthTab == 0)
            "Signing in with your Google account verifies your identity instantly."
          else
            "Creating an account automatically provisions your UNIFY profile and social graph.",
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))
      }
    }
  }
}

@Composable
fun FeatureHighlightRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  title: String,
  subtitle: String
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Box(
      modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(iconTint.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconTint,
        modifier = Modifier.size(20.dp)
      )
    }

    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
