package com.nammaraste.health.domain.model

/**
 * Issue types that can be detected / reported.
 * Mapped from the AI classifier and stored as String in DB.
 */
enum class IssueType(val displayName: String, val emoji: String) {
    POTHOLE("Pothole", "🕳️"),
    CRACK("Road Crack", "🪨"),
    WATERLOGGING("Waterlogging", "💧"),
    DEBRIS("Debris / Blockage", "🪵"),
    UNKNOWN("Unknown", "❓")
}

/**
 * Severity levels for damage reports.
 * Used to weight health score calculation.
 */
enum class Severity(val displayName: String, val weight: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    CRITICAL("Critical", 5)
}

/**
 * Report status lifecycle.
 */
enum class ReportStatus(val displayName: String) {
    PENDING("Pending"),
    ACKNOWLEDGED("Acknowledged"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved")
}

/**
 * Road health classification based on complaint density.
 * Thresholds: complaints per km
 */
enum class RoadHealth(val displayName: String) {
    EXCELLENT("Excellent"),  // 0 complaints/km
    GOOD("Good"),            // < 0.5 complaints/km
    FAIR("Fair"),            // 0.5 – 1.0 complaints/km
    POOR("Poor"),            // 1.0 – 2.0 complaints/km
    CRITICAL("Critical")     // > 2.0 complaints/km
}
