package com.app.appellas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.appellas.data.dao.UserDao
import com.app.appellas.data.models.entities.User

@Database(version = 1, entities = [User::class])
abstract class AppEllasDB: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppEllasDB? = null
        fun getDatabase(context: Context) = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context,
                AppEllasDB::class.java,
                "appellas_db"
            ).fallbackToDestructiveMigration().build()

            INSTANCE = instance
            instance
        }
    }

}