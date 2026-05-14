package com.example.nimmaguru.data

import androidx.room.*
import com.example.nimmaguru.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert
    suspend fun addSession(session: Session)

    @Query("SELECT * FROM sessions WHERE dateMillis >= :now ORDER BY dateMillis ASC")
    fun getUpcomingSessions(now: Long): Flow<List<Session>>

    @Query("SELECT COUNT(*) FROM sessions WHERE dateMillis >= :now")
    fun getUpcomingSessionCount(now: Long): Flow<Int>
}