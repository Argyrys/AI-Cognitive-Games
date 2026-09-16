package com.example.cognigame.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognigame.data.model.CognitiveScore
import com.example.cognigame.data.model.GameSession
import com.example.cognigame.data.repository.GameRepository
import com.example.cognigame.data.sync.SyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository()
    private val syncManager = SyncManager(application)

    private val _sessions = MutableStateFlow<List<GameSession>>(emptyList())
    val sessions: StateFlow<List<GameSession>> = _sessions.asStateFlow()

    private val _cognitiveScores = MutableStateFlow<List<CognitiveScore>>(emptyList())
    val cognitiveScores: StateFlow<List<CognitiveScore>> = _cognitiveScores.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun saveGameSession(session: GameSession) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                syncManager.saveSessionLocally(session)
                val result = repository.saveGameSession(session)
                result.onSuccess {
                    _error.value = null
                    loadUserSessions(session.userId)
                }
                result.onFailure {
                    _error.value = "Saved locally. Will sync when online."
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun loadUserSessions(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getUserSessions(userId)
            result.onSuccess { sessions ->
                _sessions.value = sessions
                updateCognitiveScores(sessions)
            }
            result.onFailure {
                _error.value = it.message
            }
            _isLoading.value = false
        }
    }

    fun syncOfflineData() {
        viewModelScope.launch {
            try {
                syncManager.syncLocalToCloud()
            } catch (e: Exception) {
                _error.value = "Sync failed: ${e.message}"
            }
        }
    }

    private fun updateCognitiveScores(sessions: List<GameSession>) {
        val scoresByGame = sessions.groupBy { it.gameType }
        val cognitiveScores = scoresByGame.map { (gameType, gameSessions) ->
            val latestScore = gameSessions.maxByOrNull { it.timestamp }
            val avgScore = gameSessions.map { it.score }.average().toInt()
            val trend = calculateTrend(gameSessions)

            CognitiveScore(
                gameType = gameType,
                score = latestScore?.score ?: 0,
                maxScore = latestScore?.maxScore ?: 100,
                sessionsPlayed = gameSessions.size,
                lastPlayed = latestScore?.timestamp ?: 0L,
                trend = trend
            )
        }
        _cognitiveScores.value = cognitiveScores
    }

    private fun calculateTrend(sessions: List<GameSession>): String {
        if (sessions.size < 2) return "New"

        val recentScores = sessions.sortedByDescending { it.timestamp }.take(5)
        val olderScores = sessions.sortedByDescending { it.timestamp }.drop(5).take(5)

        if (olderScores.isEmpty()) return "New"

        val recentAvg = recentScores.map { it.score }.average()
        val olderAvg = olderScores.map { it.score }.average()

        return when {
            recentAvg > olderAvg + 5 -> "Improving"
            recentAvg < olderAvg - 5 -> "Declining"
            else -> "Stable"
        }
    }
}
