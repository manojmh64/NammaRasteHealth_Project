package com.nammaraste.health.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a rural road in the system.
 * Health is calculated dynamically from complaint count / length.
 */
@Entity(tableName = "roads")
data class Road(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Official road name */
    val name: String,

    /** Taluka / administrative block this road belongs to */
    val taluka: String,

    /** District the road falls under */
    val district: String,

    /** Length of the road in kilometres */
    val lengthKm: Double,

    /** Surface type: "Asphalt", "Concrete", "Gravel", "Dirt" */
    val surfaceType: String,

    /** Foreign key reference to the assigned contractor */
    val contractorId: Long,

    /** Warranty expiry date stored as epoch millis */
    val warrantyExpiryMs: Long,

    /** Year the road was constructed */
    val constructionYear: Int,

    /** Latitude of the road's start point */
    val startLat: Double,

    /** Longitude of the road's start point */
    val startLng: Double,

    /** Latitude of the road's end point */
    val endLat: Double,

    /** Longitude of the road's end point */
    val endLng: Double,

    /** Timestamp when this record was last updated */
    val updatedAt: Long = System.currentTimeMillis()
)
