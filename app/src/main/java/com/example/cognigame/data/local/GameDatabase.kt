package com.example.cognigame.data.local

import android.content.Context
import androidx.room.*

@Entity(tableName = "game_sessions")
data class GameSessionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val gameType: String,
    val score: Int,
    val maxScore: Int,
    val roundsPlayed: Int,
    val duration: Long,
    val timestamp: Long,
    val isSynced: Boolean = false
)

@Dao
interface GameSessionDao {
    @Query("SELECT * FROM game_sessions ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentSessions(limit: Int = 50): List<GameSessionEntity>

    @Query("SELECT * FROM game_sessions WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getUserSessions(userId: String): List<GameSessionEntity>

    @Query("SELECT * FROM game_sessions WHERE isSynced = 0")
    suspend fun getUnsyncedSessions(): List<GameSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSessionEntity)

    @Update
    suspend fun updateSession(session: GameSessionEntity)

    @Query("UPDATE game_sessions SET isSynced = 1 WHERE id = :sessionId")
    suspend fun markAsSynced(sessionId: String)
}

@Database(entities = [GameSessionEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameSessionDao(): GameSessionDao

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "cognigame_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
