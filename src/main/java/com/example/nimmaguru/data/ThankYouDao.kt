package com.example.nimmaguru.data

import androidx.room.*
import com.example.nimmaguru.model.ThankYouNote
import kotlinx.coroutines.flow.Flow

@Dao
interface ThankYouDao {

    @Insert
    suspend fun postNote(note: ThankYouNote)

    @Query("SELECT * FROM thank_you_notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<ThankYouNote>>
}