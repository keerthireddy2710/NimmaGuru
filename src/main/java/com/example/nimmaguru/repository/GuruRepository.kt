package com.example.nimmaguru.repository

import com.example.nimmaguru.data.GuruDao
import com.example.nimmaguru.model.Guru
import kotlinx.coroutines.flow.Flow

class GuruRepository(private val guruDao: GuruDao) {

    suspend fun saveGuru(guru: Guru) = guruDao.saveGuru(guru)

    fun getAllGurus(): Flow<List<Guru>> = guruDao.getAllGurus()

    suspend fun getGuruByEmail(email: String): Guru? = guruDao.getGuruByEmail(email)

    fun searchGurus(query: String): Flow<List<Guru>> = guruDao.searchGurus(query)

    fun getGurusBySkill(skill: String): Flow<List<Guru>> = guruDao.getGurusBySkill(skill)
}