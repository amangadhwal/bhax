package com.bhax.app.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class LocationDatabaseTest {
    private lateinit var locationDao: LocationDao
    private lateinit var db: LocationDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            LocationDatabase::class.java
        ).build()
        locationDao = db.locationDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveLocation() = runBlocking {
        // Create sample location
        val location = LocationData(
            pincode = "110001",
            areaName = "Test Area",
            latitude = 28.6139,
            longitude = 77.2090
        )

        // Insert location
        locationDao.insertAll(listOf(location))

        // Retrieve by pincode
        val retrieved = locationDao.getByPincode("110001")
        assertNotNull(retrieved)
        assertEquals(location.areaName, retrieved?.areaName)
        assertEquals(location.latitude, retrieved?.latitude)
        assertEquals(location.longitude, retrieved?.longitude)
    }

    @Test
    fun searchByAreaName() = runBlocking {
        // Create sample locations
        val locations = listOf(
            LocationData("110001", "Central Delhi", 28.6139, 77.2090),
            LocationData("110002", "North Delhi", 28.7041, 77.1025),
            LocationData("110003", "South Delhi", 28.5244, 77.1855)
        )

        // Insert all locations
        locationDao.insertAll(locations)

        // Search for "Delhi"
        val results = locationDao.searchByAreaName("Delhi")
        assertEquals(3, results.size)

        // Search for "South"
        val southResults = locationDao.searchByAreaName("South")
        assertEquals(1, southResults.size)
        assertEquals("110003", southResults[0].pincode)
    }

    @Test
    fun getEmptyResults() = runBlocking {
        val result = locationDao.getByPincode("999999")
        assertNull(result)

        val searchResults = locationDao.searchByAreaName("NonExistent")
        assertTrue(searchResults.isEmpty())
    }
}
