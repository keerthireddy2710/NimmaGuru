package com.example.nimmaguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val guruName: String = "",
    val subject: String = "",
    val venue: String = "Samudaya Bhavana",
    val dateMillis: Long = 0L,
    val timeSlot: String = "",
    val village: String = "",
    val description: String = ""
)