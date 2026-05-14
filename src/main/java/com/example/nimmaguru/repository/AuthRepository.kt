package com.example.nimmaguru.repository

import com.example.nimmaguru.data.UserDao
import com.example.nimmaguru.model.User

class AuthRepository(private val userDao: UserDao) {

    suspend fun register(email: String, name: String, password: String, role: String): Result<Unit> {
        return try {
            val existing = userDao.getUserByEmail(email)
            if (existing != null) return Result.failure(Exception("Email already registered"))
            userDao.register(User(email, name, password, role))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val user = userDao.login(email, password)
                ?: return Result.failure(Exception("Invalid email or password"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
}