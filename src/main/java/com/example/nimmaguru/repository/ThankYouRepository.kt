package com.example.nimmaguru.repository

import com.example.nimmaguru.data.ThankYouDao
import com.example.nimmaguru.model.ThankYouNote
import kotlinx.coroutines.flow.Flow

class ThankYouRepository(private val thankYouDao: ThankYouDao) {

    suspend fun postNote(note: ThankYouNote) = thankYouDao.postNote(note)

    fun getAllNotes(): Flow<List<ThankYouNote>> = thankYouDao.getAllNotes()
}