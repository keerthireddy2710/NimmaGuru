package com.example.nimmaguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thank_you_notes")
data class ThankYouNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val studentName: String = "",
    val guruName: String = "",
    val message: String = "",
    val subject: String = "",
    val createdAt: Long = System.currentTimeMillis()
)