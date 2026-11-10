package com.example.data

import com.example.model.*

object UnifySampleData {
  val currentUser = User(
    id = "user_me",
    username = "alexrivera",
    displayName = "Alex Rivera",
    bio = "Building the unified social future 🌐 | Creative Technologist & Explorer | @unifysocials",
    avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80",
    coverUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200&auto=format&fit=crop&q=80",
    isVerified = true,
    isPrivate = false,
    followersCount = 14820,
    followingCount = 684,
    postsCount = 92,
    isFollowing = false,
    isOnline = true,
    location = "San Francisco, CA",
    website = "alexrivera.design",
    connectedPlatforms = listOf(
      SocialPlatformType.INSTAGRAM,
      SocialPlatformType.X,
      SocialPlatformType.LINKEDIN,
      SocialPlatformType.FACEBOOK
    )
  )

  val elena = User(
    id = "user_elena",
    username = "elenachen",
    displayName = "Elena Chen",
    bio = "Product Designer & Visual Artist 🎨 Sharing design systems & motion UI",
    avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
    coverUrl = "https://images.unsplash.com/photo-1579546929518-9e396f3cc809?w=1200&auto=format&fit=crop&q=80",
    isVerified = true,
    isPrivate = false,
    followersCount = 28900,
    followingCount = 412,
    postsCount = 184,
    isFollowing = true,
    isOnline = true,
    location = "Seattle, WA"
  )

  val marcus = User(
    id = "user_marcus",
    username = "marcusvance",
    displayName = "Marcus Vance",
    bio = "Sound Architect & Producer 🎧 Podcaster on future audio & spatial sound",
    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80",
    coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=1200&auto=format&fit=crop&q=80",
    isVerified = true,
    isPrivate = false,
    followersCount = 9400,
    followingCount = 310,
    postsCount = 64,
    isFollowing = true,
    isOnline = false,
    lastSeen = "15m ago"
  )

  val sophia = User(
    id = "user_sophia",
    username = "sophiasterling",
    displayName = "Sophia Sterling",
    bio = "Photographer & Visual Storyteller. Chasing light across 40+ countries 📷",
    avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
    coverUrl = "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1200&auto=format&fit=crop&q=80",
    isVerified = false,
    isPrivate = true,
    followersCount = 5210,
    followingCount = 890,
    postsCount = 112,
    isFollowing = false,
    followRequested = false
  )

  val david = User(
    id = "user_david",
    username = "davidkim_ai",
    displayName = "David Kim",
    bio = "AI systems researcher & open web advocate. Building decentralized graphs",
    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80",
    coverUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=1200&auto=format&fit=crop&q=80",
    isVerified = true,
    isPrivate = false,
    followersCount = 41200,
    followingCount = 290,
    postsCount = 320,
    isFollowing = false
  )

  val sampleStories = listOf(
    UserStories(
      user = currentUser,
      stories = listOf(
        StoryItem(
          id = "story_me_1",
          mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop&q=80",
          caption = "Sunset brainstorming session for UNIFY MIRROR! 🌅",
          timestamp = "2h ago",
          durationSeconds = 6
        )
      ),
      hasUnviewedStories = false
    ),
    UserStories(
      user = elena,
      stories = listOf(
        StoryItem(
          id = "story_elena_1",
          mediaUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
          caption = "Sneak peek of the new dark iridescent components ✨",
          timestamp = "45m ago",
          durationSeconds = 5
        ),
        StoryItem(
          id = "story_elena_2",
          mediaUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80",
          caption = "Coffee and keyframe animations ☕",
          timestamp = "30m ago",
          durationSeconds = 5
        )
      ),
      hasUnviewedStories = true
    ),
    UserStories(
      user = marcus,
      stories = listOf(
        StoryItem(
          id = "story_marcus_1",
          mediaUrl = "https://images.unsplash.com/photo-1598488035139-bdbb2231ce04?w=800&auto=format&fit=crop&q=80",
          caption = "Recording fresh voice comment soundscapes in studio A 🎙️",
          timestamp = "3h ago",
          durationSeconds = 6
        )
      ),
      hasUnviewedStories = true
    ),
    UserStories(
      user = david,
      stories = listOf(
        StoryItem(
          id = "story_david_1",
          mediaUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&auto=format&fit=crop&q=80",
          caption = "Cross-platform social graph benchmarks are in!",
          timestamp = "5h ago",
          durationSeconds = 6
        )
      ),
      hasUnviewedStories = true
    )
  )

  val samplePosts = listOf(
    Post(
      id = "post_1",
      author = elena,
      text = "Thrilled to unveil our cross-platform design philosophy with UNIFY MIRROR! Managing multiple social streams shouldn't require jumping through 8 different apps. One canvas, synchronized reach. What do you all think? ✨🚀 #UNIFY #DesignSystems #CreativeStudio",
      mediaUrls = listOf(
        "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=1000&auto=format&fit=crop&q=80"
      ),
      timestamp = "15m ago",
      likesCount = 342,
      isLiked = true,
      userReaction = ReactionType.FIRE,
      reactionsCount = mapOf(ReactionType.LIKE to 240, ReactionType.FIRE to 88, ReactionType.MINDBLOWN to 14),
      repostsCount = 46,
      tags = listOf("alexrivera", "davidkim_ai"),
      hashtags = listOf("UNIFY", "DesignSystems", "CreativeStudio"),
      comments = listOf(
        Comment(
          id = "c_1",
          author = marcus,
          text = "The audio responsiveness and voice comment integration feels unreal! Just listened to the spatial preview.",
          timestamp = "10m ago",
          likesCount = 28,
          isLiked = true,
          voiceDurationSec = 14,
          voiceWaveform = listOf(0.2f, 0.5f, 0.8f, 0.4f, 0.9f, 0.6f, 0.3f, 0.7f, 0.5f, 0.2f, 0.6f, 0.4f)
        ),
        Comment(
          id = "c_2",
          author = currentUser,
          text = "Agreed Elena! Unified publishing while keeping platform-specific caption nuance is the game changer.",
          timestamp = "6m ago",
          likesCount = 14
        )
      )
    ),
    Post(
      id = "post_2",
      author = marcus,
      text = "Recorded a 30-second audio recap of my podcast session on decentralised social identities. Turn your sound up! 🎧🔊",
      isVoicePost = true,
      voiceDurationSec = 28,
      voiceWaveform = listOf(0.1f, 0.3f, 0.6f, 0.9f, 0.7f, 0.4f, 0.8f, 0.6f, 0.95f, 0.8f, 0.5f, 0.7f, 0.4f, 0.2f),
      timestamp = "1h ago",
      likesCount = 189,
      isLiked = false,
      repostsCount = 19,
      hashtags = listOf("AudioFirst", "Podcast", "SpatialAudio"),
      comments = listOf(
        Comment(
          id = "c_3",
          author = elena,
          text = "That ambient intro gave me chills. Brilliant composition Marcus!",
          timestamp = "40m ago",
          likesCount = 9
        )
      )
    ),
    Post(
      id = "post_3",
      author = david,
      text = "Social ecosystem architecture rule #1: Never compromise on backend-enforced privacy and zero-trust verification. When someone blocks an account, the entire content pipeline must immediately restrict access at the root layer.",
      timestamp = "3h ago",
      likesCount = 890,
      isLiked = false,
      repostsCount = 140,
      hashtags = listOf("SecurityFirst", "Privacy", "SocialEcosystem")
    ),
    Post(
      id = "post_4",
      author = sophia,
      text = "Golden hour reflections in the fjords. Captured with high dynamic range. Grateful for this community! 🌄✨",
      mediaUrls = listOf(
        "https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=1000&auto=format&fit=crop&q=80"
      ),
      timestamp = "6h ago",
      likesCount = 1420,
      isLiked = true,
      userReaction = ReactionType.LIKE,
      repostsCount = 87,
      hashtags = listOf("Photography", "Wanderlust", "Nature")
    )
  )

  val sampleConversations = listOf(
    Conversation(
      id = "conv_elena",
      recipient = elena,
      isMutualFollower = true,
      isMessageRequest = false,
      unreadCount = 1,
      lastMessage = ChatMessage(
        id = "m_last_elena",
        senderId = "user_elena",
        type = MessageType.VOICE,
        voiceDurationSec = 16,
        voiceWaveform = listOf(0.3f, 0.7f, 0.9f, 0.5f, 0.8f, 0.4f, 0.6f, 0.85f, 0.3f, 0.2f),
        timestamp = "3m ago",
        deliveryStatus = DeliveryStatus.DELIVERED
      ),
      messages = listOf(
        ChatMessage(
          id = "m1",
          senderId = "user_me",
          type = MessageType.TEXT,
          text = "Hey Elena! Did you test the unified Instagram & X scheduler on UNIFY MIRROR?",
          timestamp = "10:14 AM",
          deliveryStatus = DeliveryStatus.READ
        ),
        ChatMessage(
          id = "m2",
          senderId = "user_elena",
          type = MessageType.TEXT,
          text = "Yes! The token refresh and custom caption previews worked seamlessly. Listen to this quick idea:",
          timestamp = "10:16 AM",
          deliveryStatus = DeliveryStatus.READ
        ),
        ChatMessage(
          id = "m3",
          senderId = "user_elena",
          type = MessageType.VOICE,
          voiceDurationSec = 16,
          voiceWaveform = listOf(0.3f, 0.7f, 0.9f, 0.5f, 0.8f, 0.4f, 0.6f, 0.85f, 0.3f, 0.2f),
          timestamp = "10:17 AM",
          deliveryStatus = DeliveryStatus.DELIVERED
        )
      )
    ),
    Conversation(
      id = "conv_marcus",
      recipient = marcus,
      isMutualFollower = true,
      isMessageRequest = false,
      unreadCount = 0,
      lastMessage = ChatMessage(
        id = "m_last_marcus",
        senderId = "user_me",
        type = MessageType.TEXT,
        text = "The audio waveform component rendered perfectly! Thanks for the sample audio stem.",
        timestamp = "1h ago",
        deliveryStatus = DeliveryStatus.READ
      ),
      messages = listOf(
        ChatMessage(
          id = "m4",
          senderId = "user_marcus",
          type = MessageType.TEXT,
          text = "Sending over the 48kHz audio stems for the voice message waveform tests.",
          timestamp = "Yesterday",
          deliveryStatus = DeliveryStatus.READ
        ),
        ChatMessage(
          id = "m5",
          senderId = "user_me",
          type = MessageType.TEXT,
          text = "The audio waveform component rendered perfectly! Thanks for the sample audio stem.",
          timestamp = "1h ago",
          deliveryStatus = DeliveryStatus.READ
        )
      )
    ),
    Conversation(
      id = "conv_sophia",
      recipient = sophia,
      isMutualFollower = false,
      isMessageRequest = true,
      unreadCount = 1,
      lastMessage = ChatMessage(
        id = "m_last_sophia",
        senderId = "user_sophia",
        type = MessageType.TEXT,
        text = "Hi Alex! Loved your talk on cross-platform social syndication. Would love to collaborate on a visual series!",
        timestamp = "3h ago",
        deliveryStatus = DeliveryStatus.DELIVERED
      ),
      messages = listOf(
        ChatMessage(
          id = "m6",
          senderId = "user_sophia",
          type = MessageType.TEXT,
          text = "Hi Alex! Loved your talk on cross-platform social syndication. Would love to collaborate on a visual series!",
          timestamp = "3h ago",
          deliveryStatus = DeliveryStatus.DELIVERED
        )
      )
    )
  )

  val sampleConnectedAccounts = listOf(
    ConnectedAccount(
      platform = SocialPlatformType.INSTAGRAM,
      accountHandle = "@alexrivera.design",
      accountName = "Alex Rivera",
      isConnected = true,
      health = ConnectionHealth.HEALTHY,
      tokenExpiresInDays = 58,
      lastSynced = "5m ago",
      followersCount = "48.2K"
    ),
    ConnectedAccount(
      platform = SocialPlatformType.X,
      accountHandle = "@alex_rivera",
      accountName = "Alex Rivera",
      isConnected = true,
      health = ConnectionHealth.HEALTHY,
      tokenExpiresInDays = 42,
      lastSynced = "8m ago",
      followersCount = "29.4K"
    ),
    ConnectedAccount(
      platform = SocialPlatformType.LINKEDIN,
      accountHandle = "alex-rivera-tech",
      accountName = "Alex Rivera",
      isConnected = true,
      health = ConnectionHealth.HEALTHY,
      tokenExpiresInDays = 34,
      lastSynced = "20m ago",
      followersCount = "18.1K"
    ),
    ConnectedAccount(
      platform = SocialPlatformType.FACEBOOK,
      accountHandle = "Alex Rivera Creative",
      accountName = "Alex Rivera Page",
      isConnected = true,
      health = ConnectionHealth.HEALTHY,
      tokenExpiresInDays = 72,
      lastSynced = "1h ago",
      followersCount = "12.8K"
    ),
    ConnectedAccount(
      platform = SocialPlatformType.TIKTOK,
      accountHandle = "@alexrivera_official",
      accountName = "Alex Rivera",
      isConnected = false,
      health = ConnectionHealth.DISCONNECTED,
      tokenExpiresInDays = 0,
      lastSynced = "Never",
      followersCount = "-"
    ),
    ConnectedAccount(
      platform = SocialPlatformType.YOUTUBE,
      accountHandle = "AlexRiveraTech",
      accountName = "Alex Rivera Tech",
      isConnected = false,
      health = ConnectionHealth.DISCONNECTED,
      tokenExpiresInDays = 0,
      lastSynced = "Never",
      followersCount = "-"
    ),
    ConnectedAccount(
      platform = SocialPlatformType.SNAPCHAT,
      accountHandle = "alex.rivera",
      accountName = "Alex Rivera",
      isConnected = false,
      health = ConnectionHealth.DISCONNECTED,
      tokenExpiresInDays = 0,
      lastSynced = "Never",
      followersCount = "-"
    ),
    ConnectedAccount(
      platform = SocialPlatformType.PINTEREST,
      accountHandle = "alexriveradesign",
      accountName = "Alex Rivera Boards",
      isConnected = false,
      health = ConnectionHealth.DISCONNECTED,
      tokenExpiresInDays = 0,
      lastSynced = "Never",
      followersCount = "-"
    )
  )

  val sampleMirrorPosts = listOf(
    MirrorPost(
      id = "mp_1",
      caption = "Unified publishing launch: Why syncing your voice shouldn't mean losing your authentic message. #UNIFY #SocialMedia2026",
      mediaUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop&q=80",
      selectedPlatforms = listOf(
        SocialPlatformType.INSTAGRAM,
        SocialPlatformType.X,
        SocialPlatformType.LINKEDIN,
        SocialPlatformType.FACEBOOK
      ),
      publishedAt = "Today at 9:30 AM",
      publishResults = listOf(
        MirrorPublishedItem(SocialPlatformType.INSTAGRAM, PublishStatus.PUBLISHED, "https://instagram.com/p/sample1"),
        MirrorPublishedItem(SocialPlatformType.X, PublishStatus.PUBLISHED, "https://x.com/status/sample1"),
        MirrorPublishedItem(SocialPlatformType.LINKEDIN, PublishStatus.PUBLISHED, "https://linkedin.com/feed/sample1"),
        MirrorPublishedItem(SocialPlatformType.FACEBOOK, PublishStatus.PUBLISHED, "https://facebook.com/post/sample1")
      )
    ),
    MirrorPost(
      id = "mp_2",
      caption = "Behind the scenes look at the UNIFY CONTROL CENTRE moderation dashboard and real-time trust metrics.",
      selectedPlatforms = listOf(SocialPlatformType.X, SocialPlatformType.LINKEDIN),
      publishedAt = "Yesterday at 4:15 PM",
      publishResults = listOf(
        MirrorPublishedItem(SocialPlatformType.X, PublishStatus.PUBLISHED),
        MirrorPublishedItem(SocialPlatformType.LINKEDIN, PublishStatus.PUBLISHED)
      )
    )
  )

  val sampleMirrorFeed = listOf(
    MirrorFeedItem(
      id = "mf_1",
      platform = SocialPlatformType.INSTAGRAM,
      authorName = "Alex Rivera",
      authorHandle = "@alexrivera.design",
      authorAvatar = currentUser.avatarUrl,
      text = "Crafting modular design components that adapt dynamically across feeds. Design is intelligence made visible.",
      mediaUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=600&auto=format&fit=crop&q=80",
      likesCount = 1240,
      commentsCount = 88,
      timestamp = "4h ago",
      externalUrl = "https://instagram.com"
    ),
    MirrorFeedItem(
      id = "mf_2",
      platform = SocialPlatformType.X,
      authorName = "Alex Rivera",
      authorHandle = "@alex_rivera",
      authorAvatar = currentUser.avatarUrl,
      text = "Real-time communication in 2026 demands voice comments with animated waveform telemetry. Text alone loses half the nuance.",
      likesCount = 428,
      commentsCount = 62,
      timestamp = "6h ago",
      externalUrl = "https://x.com"
    ),
    MirrorFeedItem(
      id = "mf_3",
      platform = SocialPlatformType.LINKEDIN,
      authorName = "Alex Rivera",
      authorHandle = "alex-rivera-tech",
      authorAvatar = currentUser.avatarUrl,
      text = "Why we built UNIFY MIRROR: Bridging isolated walled gardens into a cohesive creator experience.",
      likesCount = 680,
      commentsCount = 45,
      timestamp = "1d ago",
      externalUrl = "https://linkedin.com"
    )
  )

  val sampleAlerts = listOf(
    AlertNotification(
      id = "alert_1",
      type = NotificationType.FOLLOW_REQUEST,
      category = AlertCategory.FOLLOWERS,
      title = "Follow Request",
      body = "Sophia Sterling (@sophiasterling) requested to follow your private updates.",
      timestamp = "10m ago",
      actor = sophia,
      actionRequired = true
    ),
    AlertNotification(
      id = "alert_2",
      type = NotificationType.POST_LIKE,
      category = AlertCategory.INTERACTIONS,
      title = "Elena Chen liked your comment",
      body = "\"Agreed Elena! Unified publishing while keeping platform-specific...\"",
      timestamp = "25m ago",
      actor = elena,
      targetPostId = "post_1"
    ),
    AlertNotification(
      id = "alert_3",
      type = NotificationType.USER_MENTION,
      category = AlertCategory.MENTIONS,
      title = "Mentioned in a discussion",
      body = "Elena Chen tagged you in a post: \"@alexrivera check out this layout...\"",
      timestamp = "1h ago",
      actor = elena,
      targetPostId = "post_1"
    ),
    AlertNotification(
      id = "alert_4",
      type = NotificationType.MIRROR_PUBLISHED,
      category = AlertCategory.MIRROR,
      title = "UNIFY MIRROR Published",
      body = "Your post was successfully syndicated to Instagram, X, LinkedIn & Facebook.",
      timestamp = "2h ago",
      platform = SocialPlatformType.INSTAGRAM
    ),
    AlertNotification(
      id = "alert_5",
      type = NotificationType.SECURITY_ALERT,
      category = AlertCategory.ALL,
      title = "Security & Session",
      body = "New login verified from Chrome on Android 16 (San Francisco, CA).",
      timestamp = "5h ago"
    )
  )

  val sampleModerationReports = listOf(
    ModerationReport(
      id = "rep_1",
      reporter = elena,
      reportedUser = User(
        id = "user_bad_1",
        username = "spambot_promo99",
        displayName = "Crypto Booster 99",
        bio = "DM for instant followers & crypto riches",
        avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80"
      ),
      category = ReportCategory.SPAM,
      reason = "Automated high-frequency spam comments under trending hashtags.",
      timestamp = "12m ago",
      contentSnippet = "EARN 10X DAILY CLICK LINK IN BIO GUARANTEED RETURNS!!!",
      severity = "HIGH"
    ),
    ModerationReport(
      id = "rep_2",
      reporter = david,
      reportedUser = User(
        id = "user_bad_2",
        username = "troll_shadow",
        displayName = "Anonymous User",
        bio = "Nothing to see",
        avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=400&auto=format&fit=crop&q=80"
      ),
      category = ReportCategory.HARASSMENT,
      reason = "Targeted harassment and abusive language in comment thread.",
      timestamp = "35m ago",
      contentSnippet = "Repeated targeted derogatory comments towards community members.",
      isVoiceContent = true,
      severity = "CRITICAL"
    )
  )

  val sampleAnnouncements = listOf(
    SystemAnnouncement(
      id = "ann_1",
      title = "UNIFY MIRROR v2.4 Live",
      body = "Multi-account OAuth token auto-refresh and unified character counter now active for all 8 connected networks.",
      priority = AnnouncementPriority.NORMAL,
      authorAdmin = "Super Admin (Alex R.)",
      timestamp = "Yesterday at 10:00 AM"
    ),
    SystemAnnouncement(
      id = "ann_2",
      title = "Scheduled Database Optimization",
      body = "Zero-downtime maintenance scheduled for Sunday at 02:00 UTC. Messaging and feeds remain uninterrupted.",
      priority = AnnouncementPriority.HIGH,
      authorAdmin = "Technical Ops Admin",
      timestamp = "2 days ago"
    )
  )

  val platformHealthList = listOf(
    PlatformIntegrationHealth(SocialPlatformType.FACEBOOK, "Graph API v21.0", "99.98%", "0.02%", "OPERATIONAL"),
    PlatformIntegrationHealth(SocialPlatformType.INSTAGRAM, "Basic Display v19.0", "100.00%", "0.00%", "OPERATIONAL"),
    PlatformIntegrationHealth(SocialPlatformType.X, "Twitter API v2.8", "99.91%", "0.08%", "OPERATIONAL"),
    PlatformIntegrationHealth(SocialPlatformType.LINKEDIN, "Share API v2", "99.95%", "0.04%", "OPERATIONAL"),
    PlatformIntegrationHealth(SocialPlatformType.TIKTOK, "Content Posting v2", "99.85%", "0.12%", "OPERATIONAL"),
    PlatformIntegrationHealth(SocialPlatformType.YOUTUBE, "Data API v3", "100.00%", "0.00%", "OPERATIONAL"),
    PlatformIntegrationHealth(SocialPlatformType.SNAPCHAT, "Creative Kit v1.4", "99.90%", "0.05%", "OPERATIONAL"),
    PlatformIntegrationHealth(SocialPlatformType.PINTEREST, "v5 Developer API", "99.80%", "0.15%", "OPERATIONAL")
  )
}
