package com.example.travelapp.user.favorit

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Favorit::class], version = 1, exportSchema = false)
abstract class FavoritRoomDatabase : RoomDatabase() {
    abstract fun favoritDao(): FavoritDao

    companion object {
        @Volatile
        private var INSTANCE: FavoritRoomDatabase? = null

        fun getDatabase(context: Context): FavoritRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoritRoomDatabase::class.java,
                    "favorit_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
