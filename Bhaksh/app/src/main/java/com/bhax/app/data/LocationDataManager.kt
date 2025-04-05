package com.bhax.app.data

import android.content.Context
import com.bhax.app.BhakshApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class LocationDataManager(private val context: Context) {
    private val database = (context.applicationContext as BhakshApplication).database
    private val locationDao = database.locationDao()
    private val gson = Gson()

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            if (locationDao.getCount() == 0) {
                try {
                    val jsonString = context.assets.open("locations.json").bufferedReader().use { it.readText() }
                    val listType = object : TypeToken<List<LocationData>>() {}.type
                    val locations: List<LocationData> = gson.fromJson(jsonString, listType)
                    locationDao.insertAll(locations)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun findLocationByPincode(pincode: String): LocationData? {
        return withContext(Dispatchers.IO) {
            locationDao.getByPincode(pincode)
        }
    }

    suspend fun searchLocationsByArea(query: String): List<LocationData> {
        if (query.length < 3) return emptyList()
        
        return withContext(Dispatchers.IO) {
            locationDao.searchByAreaName(query)
        }
    }

    suspend fun getLocationsInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<LocationData> = withContext(Dispatchers.IO) {
        locationDao.getLocationsInBounds(minLat, maxLat, minLng, maxLng)
    }

    suspend fun getAllLocations(): List<LocationData> = withContext(Dispatchers.IO) {
        locationDao.getAllLocations()
    }

    suspend fun addLocation(location: LocationData) = withContext(Dispatchers.IO) {
        locationDao.insert(location)
    }

    suspend fun updateLocation(location: LocationData) = withContext(Dispatchers.IO) {
        locationDao.update(location)
    }

    suspend fun deleteLocation(location: LocationData) = withContext(Dispatchers.IO) {
        locationDao.delete(location)
    }

    suspend fun clearAllLocations() = withContext(Dispatchers.IO) {
        locationDao.deleteAll()
    }

    fun observeLocations(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<List<LocationData>> = flow {
        while(true) {
            val locations = locationDao.getLocationsInBounds(minLat, maxLat, minLng, maxLng)
            emit(locations)
            kotlinx.coroutines.delay(5000) // Update every 5 seconds
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        @Volatile
        private var INSTANCE: LocationDataManager? = null

        fun getInstance(context: Context): LocationDataManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationDataManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
