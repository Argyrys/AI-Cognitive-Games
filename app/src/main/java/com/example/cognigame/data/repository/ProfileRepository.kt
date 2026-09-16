package com.example.cognigame.data.repository

import com.example.cognigame.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val profilesCollection = firestore.collection("user_profiles")

    suspend fun saveProfile(profile: UserProfile): Result<String> {
        return try {
            val docRef = profilesCollection.document(profile.id).set(profile.toMap()).await()
            Result.success(profile.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(userId: String): Result<UserProfile?> {
        return try {
            val doc = profilesCollection.document(userId).get().await()
            val profile = doc.data?.let { data ->
                UserProfile(
                    id = doc.id,
                    name = data["name"] as? String ?: "",
                    age = (data["age"] as? Number)?.toInt() ?: 0,
                    createdAt = (data["createdAt"] as? Number)?.toLong() ?: 0L,
                    lastActive = (data["lastActive"] as? Number)?.toLong() ?: 0L
                )
            }
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun UserProfile.toMap(): Map<String, Any> = mapOf(
        "name" to name,
        "age" to age,
        "createdAt" to createdAt,
        "lastActive" to System.currentTimeMillis()
    )
}
