package com.example.cognigame.data.repository

import com.example.cognigame.data.model.GameSession
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class GameRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val sessionsCollection = firestore.collection("game_sessions")

    suspend fun saveGameSession(session: GameSession): Result<String> {
        return try {
            val docRef = sessionsCollection.add(session.toMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserSessions(userId: String, limit: Long = 50): Result<List<GameSession>> {
        return try {
            val snapshot = sessionsCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()

            val sessions = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { data ->
                    GameSession(
                        id = doc.id,
                        userId = data["userId"] as? String ?: "",
                        gameType = data["gameType"] as? String ?: "",
                        score = (data["score"] as? Number)?.toInt() ?: 0,
                        maxScore = (data["maxScore"] as? Number)?.toInt() ?: 100,
                        roundsPlayed = (data["roundsPlayed"] as? Number)?.toInt() ?: 0,
                        duration = (data["duration"] as? Number)?.toLong() ?: 0L,
                        timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L,
                        details = data["details"] as? Map<String, Any> ?: emptyMap()
                    )
                }
            }
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGameScores(userId: String): Result<Map<String, List<GameSession>>> {
        return try {
            val snapshot = sessionsCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val sessionsByGame = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { data ->
                    GameSession(
                        id = doc.id,
                        userId = data["userId"] as? String ?: "",
                        gameType = data["gameType"] as? String ?: "",
                        score = (data["score"] as? Number)?.toInt() ?: 0,
                        maxScore = (data["maxScore"] as? Number)?.toInt() ?: 100,
                        roundsPlayed = (data["roundsPlayed"] as? Number)?.toInt() ?: 0,
                        timestamp = (data["timestamp"] as? Number)?.toLong() ?: 0L
                    )
                }
            }.groupBy { it.gameType }

            Result.success(sessionsByGame)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun GameSession.toMap(): Map<String, Any> = mapOf(
        "userId" to userId,
        "gameType" to gameType,
        "score" to score,
        "maxScore" to maxScore,
        "roundsPlayed" to roundsPlayed,
        "duration" to duration,
        "timestamp" to timestamp,
        "details" to details
    )
}
