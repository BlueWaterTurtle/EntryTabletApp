package com.bluewaterturtle.entrytabletapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [GuestEntity::class, PersonToSeeEntity::class], version = 2, exportSchema = false)
abstract class GuestDatabase : RoomDatabase() {

    abstract fun guestDao(): GuestDao
    abstract fun personToSeeDao(): PersonToSeeDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `people_to_see` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `display_name` TEXT NOT NULL COLLATE NOCASE,
                        `active` INTEGER NOT NULL DEFAULT 1
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS `index_people_to_see_display_name`
                    ON `people_to_see` (`display_name`)
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: GuestDatabase? = null

        fun getDatabase(context: Context): GuestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GuestDatabase::class.java,
                    "guest_database"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
