package com.nammaraste.health.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a road construction / maintenance contractor.
 * Contains contact information and company details.
 */
@Entity(tableName = "contractors")
data class Contractor(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** Company / contractor name */
    val name: String,

    /** Primary contact person */
    val contactPerson: String,

    /** Phone number for complaints/queries */
    val phone: String,

    /** Email address */
    val email: String,

    /** Registered office address */
    val address: String,

    /** GSTIN or registration number */
    val registrationNumber: String,

    /** Specialisation: "Road Construction", "Bridge Work", etc. */
    val specialisation: String,

    /** Rating out of 5.0 */
    val rating: Float,

    /** Number of projects completed */
    val projectsCompleted: Int
)
