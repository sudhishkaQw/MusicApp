package com.example.musicapp

import android.util.Log
import com.example.musicapp.model.SongsModel
import com.example.musicapp.model.UserModel
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserFetcher {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun addNewUser(userData: Map<String, Any>) {
        // Get the current user's ID from Firebase Authentication
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

        // Access the "users" collection in Firestore and add a new document with the current user's ID
        db.collection("users").document(userId).set(userData).await()
    }

    fun deductCreditScoreFromUser(
        requiredCreditScore: Long,
        onSuccess: (Long) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Get the current user's document reference
        val userDocRef = db.collection("users").document(auth.currentUser?.uid ?: return)

        // Update the user's credit score by deducting the required credit score
        userDocRef.get()
            .addOnSuccessListener { document ->
                val currentCreditScore = document.get("creditScore") as Long
                val newCreditScore = currentCreditScore - requiredCreditScore

                // Update the user's document with the new credit score
                userDocRef.update("creditScore", newCreditScore)
                    .addOnSuccessListener {
                        // Credit score updated successfully
                        onSuccess(newCreditScore)
                    }
                    .addOnFailureListener { e ->
                        // Handle failure to update credit score
                        onFailure(e)
                    }
            }
            .addOnFailureListener { e ->
                // Handle failure to fetch user document
                onFailure(e)
            }
    }

    fun updateCreditScore(
        newCreditScore: Long,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Get the current user's document reference
        val userDocRef = db.collection("users").document(auth.currentUser?.uid ?: return)

        // Update the user's document with the new credit score
        userDocRef.update("creditScore", newCreditScore)
            .addOnSuccessListener {
                // Credit score updated successfully
                onSuccess()
            }
            .addOnFailureListener { e ->
                // Handle failure to update credit score
                onFailure(e)
            }
    }


   suspend fun fetchFavoriteSongs(
        onSuccess: (List<SongsModel>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            // Get the current user's ID from Firebase Authentication
            val userId = auth.currentUser?.uid
            if (userId != null) {
                // Fetch the user document from Firestore
                val userDocRef = db.collection("users").document(userId).get().await()
                if (userDocRef.exists()) {
                    // Parse the user document into a UserModel object
                    val user = userDocRef.toObject(UserModel::class.java)
                    user?.let { userModel ->
                        // Filter the favorite songs from the user's purchased songs list
                        val favoriteSongsIds = userModel.purchasedSongs.filter { it.isNotBlank() }
                        val favoriteSongs = mutableListOf<SongsModel>()

                        // Fetch details of the favorite songs using their IDs
                        for (songId in favoriteSongsIds) {
                            val song = fetchSongDetails(songId)
                            song?.let { favoriteSongs.add(it) }
                        }
                        // Invoke the onSuccess callback with the list of favorite songs
                        onSuccess(favoriteSongs)
                    } ?: onFailure(Exception("Failed to parse user data"))
                } else {
                    onFailure(Exception("User document does not exist"))
                }
            } else {
                onFailure(Exception("User ID not found"))
            }
        } catch (e: Exception) {
            // Invoke the onFailure callback if an error occurs
            onFailure(e)
        }
    }

    private suspend fun fetchSongDetails(songId: String): SongsModel? {
        return try {
            // Check if the songId is not empty or blank
            if (songId.isNotBlank()) {
                // Fetch the song document from Firestore
                val documentSnapshot = db.collection("songs").document(songId).get().await()
                // Parse the song document into a SongsModel object
                documentSnapshot.toObject(SongsModel::class.java)
            } else {
                Log.e(TAG, "Error fetching song details: Empty or blank songId")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching song details", e)
            null
        }
    }

    companion object {
        private const val TAG = "UserFetcher"
    }


    //    fun getUserDetails(onSuccess: (Map<String, Any>) -> Unit, onFailure: (Exception) -> Unit) {
//        // Get the current user's ID from Firebase Authentication
//        val userId = auth.currentUser?.uid ?: return
//
//        // Retrieve user details from Firestore
//        db.collection("users").document(userId)
//            .get()
//            .addOnSuccessListener { documentSnapshot ->
//                if (documentSnapshot.exists()) {
//                    val userData = documentSnapshot.data
//                    onSuccess(userData ?: mapOf()) // Pass user data to success callback
//                } else {
//                    onFailure(Exception("User document does not exist"))
//                }
//            }
//            .addOnFailureListener { e ->
//                onFailure(e)
//            }
//    }
    suspend fun updateUserDetails(
        updatedUserData: Map<String, Any>,
        creditScoreDeduction: Long = 0L
    ) {
        // Get the current user's ID from Firebase Authentication
        val userId = auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

        // Update the user's document in Firestore with the provided data
        db.collection("users").document(userId).update(updatedUserData).await()

        // Deduct credit score if creditScoreDeduction is specified
        if (creditScoreDeduction > 0L) {
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    val currentCreditScore = document.get("creditScore") as Long
                    val newCreditScore = currentCreditScore - creditScoreDeduction

                    // Update the user's document with the new credit score
                    userDocRef.update("creditScore", newCreditScore)
                        .addOnFailureListener { e ->
                            // Handle failure to update credit score
                            Log.e(TAG, "Error deducting credit score", e)
                        }
                }
                .addOnFailureListener { e ->
                    // Handle failure to fetch user document
                    Log.e(TAG, "Error fetching user document", e)
                }
        }
    }
    suspend fun fetchPurchasedSongsForCurrentUser(
        onSuccess: (List<SongsModel>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val userDocRef = db.collection("users").document(userId).get().await()
                if (userDocRef.exists()) {
                    val user = userDocRef.toObject(UserModel::class.java)
                    user?.let { userModel ->
                        val purchasedSongsIds = userModel.purchasedSongs.filter { it.isNotBlank() } // Filter out empty or blank song IDs
                        val purchasedSongs = mutableListOf<SongsModel>()

                        for (songId in purchasedSongsIds) {
                            val song = fetchSongDetails(songId)
                            song?.let { purchasedSongs.add(it) }
                        }
                        onSuccess(purchasedSongs)
                    } ?: onFailure(Exception("Failed to parse user data"))
                } else {
                    onFailure(Exception("User document does not exist"))
                }
            } else {
                onFailure(Exception("User ID not found"))
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
    suspend fun getUserDetails(): UserModel? {
        // Get the current user's ID from Firebase Authentication
        val userId = auth.currentUser?.uid ?: return null

        // Retrieve user details from Firestore
        val documentSnapshot = db.collection("users").document(userId).get().await()
        return documentSnapshot.toObject(UserModel::class.java)
    }

    suspend fun fetchPurchasedSongs(): List<SongsModel> {
        val user = getUserDetails() ?: return emptyList()
        val purchasedSongIds = user.purchasedSongs

        // Fetch details of purchased songs using their IDs
        val tasks = purchasedSongIds.map { songId ->
            db.collection("songs").document(songId).get()
        }

        val songSnapshots = Tasks.whenAllComplete(tasks).await()
        return songSnapshots.mapNotNull { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result as? DocumentSnapshot
                documentSnapshot?.toObject(SongsModel::class.java)
            } else {
                null
            }
        }
    }

    suspend fun purchaseSong(
        user: UserModel,
        songId: String,
        onSuccess: (UserModel) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            // Add the purchased song ID to the user's purchased songs list
            val updatedPurchasedSongs = user.purchasedSongs.toMutableList().apply {
                add(songId)
            }

            // Create a copy of the user object with the updated purchased songs list
            val updatedUser = user.copy(purchasedSongs = updatedPurchasedSongs)

            // Update the user document in Firestore
            db.collection("users")
                .document(user.uid)
                .set(updatedUser)
                .await()

            // Call the onSuccess callback with the updated user object
            onSuccess(updatedUser)
        } catch (e: Exception) {
            // Call the onFailure callback if an error occurs
            onFailure(e)
        }
    }
}








