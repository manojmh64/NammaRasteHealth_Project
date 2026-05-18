package com.nammaraste.health.domain.usecase

import com.nammaraste.health.data.db.dao.RoadWithComplaintCount
import com.nammaraste.health.domain.model.RoadHealth
import com.nammaraste.health.domain.model.RoadHealthInfo
import com.nammaraste.health.data.repository.RoadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case: Calculates road health score dynamically.
 *
 * Algorithm:
 *   complaintsPerKm = totalComplaints / lengthKm
 *
 *   Health classification:
 *   ≤ 0.0  → EXCELLENT (100)
 *   ≤ 0.5  → GOOD      (75–100)
 *   ≤ 1.0  → FAIR      (50–75)
 *   ≤ 2.0  → POOR      (25–50)
 *   > 2.0  → CRITICAL  (0–25)
 */
class GetRoadHealthUseCase @Inject constructor(
    private val repository: RoadRepository
) {

    /**
     * Returns a Flow of all roads with their computed health scores,
     * ordered by health score ascending (worst roads first).
     */
    operator fun invoke(): Flow<List<RoadHealthInfo>> {
        return repository.getRoadsWithComplaintCount().map { roads ->
            roads.map { road -> road.toRoadHealthInfo() }
                .sortedBy { it.healthScore }
        }
    }

    /**
     * Returns health info for a single road by ID, from the full list.
     */
    fun forRoad(roadId: Long): Flow<RoadHealthInfo?> {
        return repository.getRoadsWithComplaintCount().map { roads ->
            roads.firstOrNull { it.id == roadId }?.toRoadHealthInfo()
        }
    }
}

// ─── Extension ────────────────────────────────────────────────────────────────

fun RoadWithComplaintCount.toRoadHealthInfo(): RoadHealthInfo {
    val perKm = if (lengthKm > 0) complaintCount.toDouble() / lengthKm else 0.0
    val (status, score) = calculateHealth(perKm)

    return RoadHealthInfo(
        roadId = id,
        roadName = name,
        taluka = taluka,
        district = district,
        lengthKm = lengthKm,
        surfaceType = surfaceType,
        contractorId = contractorId,
        warrantyExpiryMs = warrantyExpiryMs,
        constructionYear = constructionYear,
        startLat = startLat,
        startLng = startLng,
        endLat = endLat,
        endLng = endLng,
        complaintCount = complaintCount,
        complaintsPerKm = perKm,
        healthStatus = status,
        healthScore = score
    )
}

/**
 * Pure function: maps complaints-per-km to a health status and 0–100 score.
 */
fun calculateHealth(complaintsPerKm: Double): Pair<RoadHealth, Double> {
    return when {
        complaintsPerKm <= 0.0 -> RoadHealth.EXCELLENT to 100.0
        complaintsPerKm <= 0.5 -> {
            val score = 100.0 - (complaintsPerKm / 0.5) * 25.0
            RoadHealth.GOOD to score.coerceIn(75.0, 100.0)
        }
        complaintsPerKm <= 1.0 -> {
            val score = 75.0 - ((complaintsPerKm - 0.5) / 0.5) * 25.0
            RoadHealth.FAIR to score.coerceIn(50.0, 75.0)
        }
        complaintsPerKm <= 2.0 -> {
            val score = 50.0 - ((complaintsPerKm - 1.0) / 1.0) * 25.0
            RoadHealth.POOR to score.coerceIn(25.0, 50.0)
        }
        else -> {
            val score = (25.0 - ((complaintsPerKm - 2.0) / 2.0) * 25.0).coerceAtLeast(0.0)
            RoadHealth.CRITICAL to score
        }
    }
}
