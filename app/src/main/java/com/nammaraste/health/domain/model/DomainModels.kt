package com.nammaraste.health.domain.model

/**
 * Domain model for a road with its computed health score.
 * Combines data from the Road entity and aggregated complaint count.
 */
data class RoadHealthInfo(
    val roadId: Long,
    val roadName: String,
    val taluka: String,
    val district: String,
    val lengthKm: Double,
    val surfaceType: String,
    val contractorId: Long,
    val warrantyExpiryMs: Long,
    val constructionYear: Int,
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double,
    val complaintCount: Int,

    /** Calculated as complaintCount / lengthKm */
    val complaintsPerKm: Double,

    /** Enum based on complaintsPerKm thresholds */
    val healthStatus: RoadHealth,

    /** 0.0 (worst) to 100.0 (best) */
    val healthScore: Double
)

/**
 * Domain model for dashboard taluka summary.
 */
data class TalukaSummary(
    val taluka: String,
    val totalRoads: Int,
    val totalComplaints: Int,
    val averageHealthScore: Double,
    val bestRoadName: String,
    val worstRoadName: String
)

/**
 * Result from the AI damage classifier.
 */
data class ClassificationResult(
    val issueType: IssueType,
    val severity: Severity,
    val confidence: Float,
    val reasoning: String
)

/**
 * Dashboard summary model.
 */
data class DashboardStats(
    val totalRoads: Int,
    val totalComplaints: Int,
    val criticalRoads: Int,
    val goodRoads: Int,
    val recentReports: Int,
    val talukaSummaries: List<TalukaSummary>
)
