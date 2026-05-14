package com.example.nimmaguru.repository

import com.example.nimmaguru.data.SessionDao
import com.example.nimmaguru.model.Session
import kotlinx.coroutines.flow.Flow

class SessionRepository(private val sessionDao: SessionDao) {

    suspend fun addSession(session: Session) = sessionDao.addSession(session)

    fun getUpcomingSessions(): Flow<List<Session>> =
        sessionDao.getUpcomingSessions(System.currentTimeMillis())

    fun getUpcomingSessionCount(): Flow<Int> =
        sessionDao.getUpcomingSessionCount(System.currentTimeMillis())
}