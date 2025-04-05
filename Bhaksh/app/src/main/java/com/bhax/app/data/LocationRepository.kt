package com.bhax.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class LocationRepository(context: Context) {
    private val database = LocationDatabase.getDatabase(context)
    private val locationDao = database.locationDao()

    suspend fun initializeData() {
        withContext(Dispatchers.IO) {
            if (locationDao.getCount() == 0) {
                val jsonString = context.assets.open("locations.json").bufferedReader().use { it.readText() }
                val locations: List<LocationData> = parseLocationsJson(jsonString)
                locationDao.insertAll(locations)
            }
        }
    }

    suspend fun searchByPincode(pincode: String): LocationData? =
        withContext(Dispatchers.IO) {
            locationDao.getByPincode(pincode)
        }

    suspend fun searchByAreaName(query: String): List<LocationData> =
        withContext(Dispatchers.IO) {
            if (query.length < 3) emptyList()
            else locationDao.searchByAreaName(query)
        }

    fun observeLocationsInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<List<LocationData>> = flow {
        while (true) {
            val locations = locationDao.getLocationsInBounds(minLat, maxLat, minLng, maxLng)
            emit(locations)
            kotlinx.coroutines.delay(5000) // Update every 5 seconds
        }
    }.flowOn(Dispatchers.IO)

    suspend fun addLocation(location: LocationData) =
        withContext(Dispatchers.IO) {
            locationDao.insert(location)
        }

    suspend fun updateLocation(location: LocationData) =
        withContext(Dispatchers.IO) {
            locationDao.update(location)
        }

    suspend fun deleteLocation(location: LocationData) =
        withContext(Dispatchers.IO) {
            locationDao.delete(location)
        }

    private fun parseLocationsJson(jsonString: String): List<LocationData> {
        return try {
            com.google.gson.Gson().fromJson(
                jsonString,
                com.google.gson.reflect.TypeToken.getParameterized(
                    List::class.java,
                    LocationData::class.java
                ).type
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LocationRepository? = null

        fun getInstance(context: Context): LocationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocationRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
