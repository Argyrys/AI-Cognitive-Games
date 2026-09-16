package com.example.cognigame.data.model

data class GameSession(
    val id: String = "",
    val userId: String = "",
    val gameType: String = "",
    val score: Int = 0,
    val maxScore: Int = 100,
    val roundsPlayed: Int = 0,
    val duration: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val details: Map<String, Any> = emptyMap()
)

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastActive: Long = System.currentTimeMillis()
)

data class CognitiveScore(
    val gameType: String,
    val score: Int,
    val maxScore: Int,
    val sessionsPlayed: Int,
    val lastPlayed: Long,
    val trend: String
)
