package com.bhax.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations WHERE pincode = :pincode")
    suspend fun getByPincode(pincode: String): LocationData?
    
    @Query("SELECT * FROM locations WHERE area_name LIKE '%' || :query || '%'")
    suspend fun searchByAreaName(query: String): List<LocationData>
    
    @Query("SELECT * FROM locations")
    suspend fun getAllLocations(): List<LocationData>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<LocationData>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationData)
    
    @Update
    suspend fun update(location: LocationData)
    
    @Delete
    suspend fun delete(location: LocationData)
    
    @Query("DELETE FROM locations")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM locations")
    suspend fun getCount(): Int
    
    @Query("SELECT * FROM locations WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    suspend fun getLocationsInBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): List<LocationData>
}
