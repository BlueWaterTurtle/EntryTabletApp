package com.bluewaterturtle.entrytabletapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GuestEntity::class], version = 1, exportSchema = false)
abstract class GuestDatabase : RoomDatabase() {

    abstract fun guestDao(): GuestDao

    companion object {
        @Volatile
        private var INSTANCE: GuestDatabase? = null

        fun getDatabase(context: Context): GuestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GuestDatabase::class.java,
                    "guest_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
