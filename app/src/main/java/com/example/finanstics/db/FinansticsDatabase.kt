package com.example.finanstics.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Action::class, Category::class],
    version = 5,
    exportSchema = false
)
abstract class FinansticsDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: FinansticsDatabase? = null

        fun getDatabase(context: Context): FinansticsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinansticsDatabase::class.java,
                    "finanstics_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
