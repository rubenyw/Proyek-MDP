package com.example.project

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [], version = 1)
abstract class AppDatabase: RoomDatabase() {

    companion object{
        var db: AppDatabase? = null;

        fun getDatabase(context: Context): AppDatabase {
            if(db == null){
                db = Room.databaseBuilder(context,AppDatabase::class.java,"db_t7").fallbackToDestructiveMigration().build()
            }

            return db!!;
        }
    }
}