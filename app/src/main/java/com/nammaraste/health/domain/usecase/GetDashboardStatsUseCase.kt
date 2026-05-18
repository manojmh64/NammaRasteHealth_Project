package com.nammaraste.health.domain.usecase

import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.domain.model.DashboardStats
import com.nammaraste.health.domain.model.RoadHealth
import com.nammaraste.health.domain.model.TalukaSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Use case: Produces the dashboard summary statistics.
 * Combines road health info, complaint counts, and taluka-level aggregation.
 */
class GetDashboardStatsUseCase @Inject constructor(
    private val repository: RoadRepository,
    private val getRoadHealthUseCase: GetRoadHealthUseCase
) {

    operator fun invoke(): Flow<DashboardStats> {
        return combine(
            getRoadHealthUseCase(),
            repository.getTotalReportCount(),
            repository.getComplaintsPerTaluka()
        ) { healthInfoList, totalReports, talukaComplaints ->

            val criticalRoads = healthInfoList.count {
                it.healthStatus == RoadHealth.CRITICAL || it.healthStatus == RoadHealth.POOR
            }
            val goodRoads = healthInfoList.count {
                it.healthStatus == RoadHealth.EXCELLENT || it.healthStatus == RoadHealth.GOOD
            }

            // Build taluka summaries
            val talukaGroups = healthInfoList.groupBy { it.taluka }
            val talukaSummaries = talukaGroups.map { (taluka, roads) ->
                val totalComplaints = roads.sumOf { it.complaintCount }
                val avgHealth = if (roads.isNotEmpty()) roads.sumOf { it.healthScore } / roads.size else 0.0
                val bestRoad = roads.maxByOrNull { it.healthScore }?.roadName ?: "-"
                val worstRoad = roads.minByOrNull { it.healthScore }?.roadName ?: "-"
                TalukaSummary(
                    taluka = taluka,
                    totalRoads = roads.size,
                    totalComplaints = totalComplaints,
                    averageHealthScore = avgHealth,
                    bestRoadName = bestRoad,
                    worstRoadName = worstRoad
                )
            }.sortedByDescending { it.totalComplaints }

            DashboardStats(
                totalRoads = healthInfoList.size,
                totalComplaints = totalReports,
                criticalRoads = criticalRoads,
                goodRoads = goodRoads,
                recentReports = minOf(totalReports, 10),
                talukaSummaries = talukaSummaries
            )
        }
    }
}
