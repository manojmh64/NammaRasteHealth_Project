package com.nammaraste.health.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a citizen-submitted damage report for a specific road.
 * GPS coordinates and timestamp are captured automatically at submission time.
 */
@Entity(
    tableName = "damage_reports",
    foreignKeys = [
        ForeignKey(
            entity = Road::class,
            parentColumns = ["id"],
            childColumns = ["roadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("roadId")]
)
data class DamageReport(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** The road this report belongs to */
    val roadId: Long,

    /** Absolute path to the photo stored on device */
    val photoPath: String?,

    /**
     * AI-classified issue type.
     * Values: "POTHOLE", "CRACK", "WATERLOGGING", "DEBRIS", "UNKNOWN"
     */
    val issueType: String,

    /**
     * AI-classified severity level.
     * Values: "LOW", "MEDIUM", "HIGH", "CRITICAL"
     */
    val severity: String,

    /** Reporter's description (optional, free text) */
    val description: String,

    /** GPS latitude where the issue was spotted */
    val latitude: Double,

    /** GPS longitude where the issue was spotted */
    val longitude: Double,

    /** Epoch millis when the report was submitted */
    val timestamp: Long = System.currentTimeMillis(),

    /**
     * Current status of the report.
     * Values: "PENDING", "ACKNOWLEDGED", "IN_PROGRESS", "RESOLVED"
     */
    val status: String = "PENDING"
)
