package com.example.cognigame.data.sync

import android.content.Context
import com.example.cognigame.data.local.GameDatabase
import com.example.cognigame.data.local.GameSessionEntity
import com.example.cognigame.data.repository.GameRepository
import com.example.cognigame.data.model.GameSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncManager(context: Context) {
    private val database = GameDatabase.getDatabase(context)
    private val dao = database.gameSessionDao()
    private val repository = GameRepository()

    suspend fun syncLocalToCloud() = withContext(Dispatchers.IO) {
        val unsynced = dao.getUnsyncedSessions()
        for (entity in unsynced) {
            val session = GameSession(
                id = entity.id,
                userId = entity.userId,
                gameType = entity.gameType,
                score = entity.score,
                maxScore = entity.maxScore,
                roundsPlayed = entity.roundsPlayed,
                duration = entity.duration,
                timestamp = entity.timestamp
            )
            val result = repository.saveGameSession(session)
            result.onSuccess { docId ->
                dao.markAsSynced(entity.id)
            }
        }
    }

    suspend fun saveSessionLocally(session: GameSession) = withContext(Dispatchers.IO) {
        val entity = GameSessionEntity(
            id = session.id.ifEmpty { java.util.UUID.randomUUID().toString() },
            userId = session.userId,
            gameType = session.gameType,
            score = session.score,
            maxScore = session.maxScore,
            roundsPlayed = session.roundsPlayed,
            duration = session.duration,
            timestamp = session.timestamp,
            isSynced = false
        )
        dao.insertSession(entity)
    }
}
