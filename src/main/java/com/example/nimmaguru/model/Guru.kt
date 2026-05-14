package com.example.nimmaguru.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gurus")
data class Guru(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userEmail: String = "",
    val name: String = "",
    val photoPath: String = "",
    val village: String = "",
    val bio: String = "",
    val skills: String = "",
    val freeHours: String = "",
    val thankYouCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)