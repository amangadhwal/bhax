package com.bhax.app.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object LocationDatabaseMigrations {
    // Example migration from version 1 to 2 (for future use)
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add new migrations here when needed
            // Example:
            // database.execSQL("ALTER TABLE locations ADD COLUMN description TEXT DEFAULT NULL")
        }
    }

    // List of all migrations
    val ALL_MIGRATIONS = arrayOf(
        MIGRATION_1_2
    )
}
