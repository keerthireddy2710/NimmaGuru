package com.example.nimmaguru.data

import androidx.room.*
import com.example.nimmaguru.model.Guru
import kotlinx.coroutines.flow.Flow

@Dao
interface GuruDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGuru(guru: Guru)

    @Query("SELECT * FROM gurus ORDER BY createdAt DESC")
    fun getAllGurus(): Flow<List<Guru>>

    @Query("SELECT * FROM gurus WHERE userEmail = :email LIMIT 1")
    suspend fun getGuruByEmail(email: String): Guru?

    @Query("SELECT * FROM gurus WHERE skills LIKE '%' || :skill || '%' ORDER BY createdAt DESC")
    fun getGurusBySkill(skill: String): Flow<List<Guru>>

    @Query("SELECT * FROM gurus WHERE name LIKE '%' || :query || '%' OR village LIKE '%' || :query || '%' OR skills LIKE '%' || :query || '%'")
    fun searchGurus(query: String): Flow<List<Guru>>

    @Update
    suspend fun updateGuru(guru: Guru)
}