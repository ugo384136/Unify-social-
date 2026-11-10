package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class PostRepository(private val db: FirebaseFirestore) {

  // Explicit initialization using firestore_database_id from string resources
  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth get() = Firebase.auth

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  /**
   * Real-time flow observing public posts from the Firestore "posts" collection.
   */
  fun observeFeedPosts(): Flow<List<FirestorePost>> = flow {
    val path = "posts"
    val query = db.collection(path)

    emitAll(
      query.snapshots()
        .map { snapshot ->
          snapshot.toObjects(FirestorePost::class.java)
            .sortedByDescending { it.createdAt?.toDate()?.time ?: Long.MAX_VALUE }
        }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser != null) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  /**
   * Real-time flow observing posts created by a specific user (for User Profile screen).
   */
  fun observeUserPosts(userId: String): Flow<List<FirestorePost>> = flow {
    val path = "posts"
    val query = db.collection(path).whereEqualTo("userId", userId)

    emitAll(
      query.snapshots()
        .map { snapshot ->
          snapshot.toObjects(FirestorePost::class.java)
            .sortedByDescending { it.createdAt?.toDate()?.time ?: Long.MAX_VALUE }
        }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          val exceptionToLog = firestoreError ?: (error as? Exception)
          if (exceptionToLog != null && auth.currentUser != null) {
            handleFirestoreError(exceptionToLog, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  /**
   * Creates and writes a new social post document to Firestore with server timestamps.
   */
  suspend fun createPost(
    text: String,
    mediaUrl: String? = null,
    authorName: String,
    authorUsername: String,
    authorAvatarUrl: String
  ): Result<String> {
    val uid = requireUserId()
    val docRef = db.collection("posts").document()
    val payload = hashMapOf<String, Any?>(
      "userId" to uid,
      "authorName" to authorName,
      "authorUsername" to authorUsername,
      "authorAvatarUrl" to authorAvatarUrl,
      "text" to text,
      "mediaUrl" to mediaUrl,
      "likesCount" to 0L,
      "likedUserIds" to emptyList<String>(),
      "repostsCount" to 0L,
      "commentsCount" to 0L,
      "isVoicePost" to false,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    return try {
      docRef.set(payload).await()
      Result.success(docRef.id)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      Result.failure(e)
    }
  }

  /**
   * Toggles like state for the current authenticated user on a post.
   */
  suspend fun toggleLike(postId: String): Result<Unit> {
    val uid = requireUserId()
    val docRef = db.collection("posts").document(postId)

    return try {
      db.runTransaction { transaction ->
        val snapshot = transaction.get(docRef)
        val post = snapshot.toObject(FirestorePost::class.java) ?: return@runTransaction
        val alreadyLiked = post.likedUserIds.contains(uid)
        val updatedLikes = if (alreadyLiked) {
          post.likedUserIds - uid
        } else {
          post.likedUserIds + uid
        }
        transaction.update(
          docRef,
          mapOf(
            "likedUserIds" to updatedLikes,
            "likesCount" to updatedLikes.size.toLong(),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )
      }.await()
      Result.success(Unit)
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.UPDATE, docRef.path)
      Result.failure(e)
    }
  }

  /**
   * Seeds realistic showcase posts on first launch if the collection is empty.
   */
  suspend fun seedInitialPostsIfEmpty(currentUserId: String) {
    try {
      val snapshot = db.collection("posts").limit(1).get().await()
      if (snapshot.isEmpty) {
        val sampleList = listOf(
          hashMapOf(
            "userId" to currentUserId,
            "authorName" to (auth.currentUser?.displayName ?: "Alex Rivera"),
            "authorUsername" to (auth.currentUser?.email?.substringBefore("@") ?: "alexrivera"),
            "authorAvatarUrl" to (auth.currentUser?.photoUrl?.toString() ?: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80"),
            "text" to "Welcome to UNIFY Socials! Live Firestore sync is now active across all feeds. All posts are stored and queried directly from our cloud database. 🚀✨ #UNIFY #FirestoreLive",
            "mediaUrl" to "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1000&auto=format&fit=crop&q=80",
            "likesCount" to 14L,
            "likedUserIds" to listOf(currentUserId),
            "repostsCount" to 4L,
            "commentsCount" to 2L,
            "isVoicePost" to false,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          ),
          hashMapOf(
            "userId" to "user_creator_elena",
            "authorName" to "Elena Chen",
            "authorUsername" to "elenachen",
            "authorAvatarUrl" to "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=400&auto=format&fit=crop&q=80",
            "text" to "Designing modern cloud-synchronized feeds with Jetpack Compose LazyColumn is pure joy. Smooth scrolling, minimal recomposition, and zero jank. What do you think of this visual hierarchy? 🎨✨ #DesignSystems #MobileUI",
            "mediaUrl" to "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=1000&auto=format&fit=crop&q=80",
            "likesCount" to 28L,
            "likedUserIds" to emptyList<String>(),
            "repostsCount" to 9L,
            "commentsCount" to 5L,
            "isVoicePost" to false,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
          )
        )
        for (item in sampleList) {
          db.collection("posts").add(item).await()
        }
      }
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.LIST, "posts")
    }
  }

  private suspend fun <T> kotlinx.coroutines.flow.FlowCollector<T>.emitAll(flow: Flow<T>) {
    flow.collect { value -> emit(value) }
  }
}
