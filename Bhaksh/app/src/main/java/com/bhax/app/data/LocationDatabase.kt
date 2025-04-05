package com.bhax.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bhax.app.data.migrations.LocationDatabaseMigrations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [LocationData::class],
    version = 1,
    exportSchema = true
)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var INSTANCE: LocationDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ): LocationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocationDatabase::class.java,
                    "location_database"
                )
                .addMigrations(*LocationDatabaseMigrations.ALL_MIGRATIONS)
                .addCallback(LocationDatabaseCallback(scope))
                .build()
                
                INSTANCE = instance
                instance
            }
        }

        private class LocationDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        // Pre-populate database here if needed
                        val locationDao = database.locationDao()
                        locationDao.deleteAll() // Clear any existing data
                        
                        // Add some initial data if needed
                        val initialLocations = listOf(
                            LocationData(
                                pincode = "110001",
                                areaName = "Connaught Place",
                                latitude = 28.6304,
                                longitude = 77.2177
                            ),
                            LocationData(
                                pincode = "400001",
                                areaName = "Fort Mumbai",
                                latitude = 18.9345,
                                longitude = 72.8346
                            )
                        )
                        locationDao.insertAll(initialLocations)
                    }
                }
            }
        }
    }
}
