package com.example.project

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.coroutines.coroutineContext
@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun userDAO() : UserDao

    companion object {
        private var _database: AppDatabase? = null

        fun build(context:Context?): AppDatabase {
            if(_database == null) {
                _database = Room.databaseBuilder(context!!, AppDatabase::class.java, "commovedb")
                    .fallbackToDestructiveMigration().build()
            }
            return _database!!
        }
    }
}

