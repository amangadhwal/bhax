package com.bhax.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "locations")
data class LocationData(
    @PrimaryKey
    @ColumnInfo(name = "pincode")
    val pincode: String,

    @ColumnInfo(name = "area_name")
    val areaName: String,

    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double
)
