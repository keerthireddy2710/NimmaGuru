package com.example.nimmaguru.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nimmaguru.model.Guru
import com.example.nimmaguru.model.Session
import com.example.nimmaguru.model.ThankYouNote
import com.example.nimmaguru.model.User

@Database(
    entities = [User::class, Guru::class, Session::class, ThankYouNote::class],
    version = 1,
    exportSchema = false
)
abstract class NimmaGuruDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun guruDao(): GuruDao
    abstract fun sessionDao(): SessionDao
    abstract fun thankYouDao(): ThankYouDao

    companion object {
        @Volatile private var INSTANCE: NimmaGuruDatabase? = null

        fun getDatabase(context: Context): NimmaGuruDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NimmaGuruDatabase::class.java,
                    "nimmaguru_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}