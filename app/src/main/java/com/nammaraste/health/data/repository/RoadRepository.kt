package com.nammaraste.health.data.repository

import com.nammaraste.health.data.db.dao.ContractorDao
import com.nammaraste.health.data.db.dao.ReportDao
import com.nammaraste.health.data.db.dao.RoadDao
import com.nammaraste.health.data.db.dao.RoadWithComplaintCount
import com.nammaraste.health.data.db.entity.Contractor
import com.nammaraste.health.data.db.entity.DamageReport
import com.nammaraste.health.data.db.entity.Road
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that aggregates Road, Report, and Contractor data.
 * Single source of truth for the domain layer.
 */
@Singleton
class RoadRepository @Inject constructor(
    private val roadDao: RoadDao,
    private val reportDao: ReportDao,
    private val contractorDao: ContractorDao
) {

    // ─── Roads ────────────────────────────────────────────────────────────────

    fun getAllRoads(): Flow<List<Road>> = roadDao.getAllRoads()

    fun getRoadById(id: Long): Flow<Road?> = roadDao.getRoadById(id)

    fun searchRoads(query: String): Flow<List<Road>> = roadDao.searchRoads(query)

    fun getRoadsByTaluka(taluka: String): Flow<List<Road>> = roadDao.getRoadsByTaluka(taluka)

    fun getAllTalukas(): Flow<List<String>> = roadDao.getAllTalukas()

    fun getRoadsWithComplaintCount(): Flow<List<RoadWithComplaintCount>> =
        roadDao.getRoadsWithComplaintCount()

    suspend fun insertRoad(road: Road): Long = roadDao.insertRoad(road)

    // ─── Reports ──────────────────────────────────────────────────────────────

    fun getAllReports(): Flow<List<DamageReport>> = reportDao.getAllReports()

    fun getReportsByRoad(roadId: Long): Flow<List<DamageReport>> =
        reportDao.getReportsByRoad(roadId)

    fun getComplaintCountForRoad(roadId: Long): Flow<Int> =
        reportDao.getComplaintCountForRoad(roadId)

    fun getRecentReports(): Flow<List<DamageReport>> = reportDao.getRecentReports()

    fun getTotalReportCount(): Flow<Int> = reportDao.getTotalReportCount()

    fun getComplaintsPerTaluka() = reportDao.getComplaintsPerTaluka()

    fun getReportsByIssueType() = reportDao.getReportsByIssueType()

    suspend fun submitReport(report: DamageReport): Long = reportDao.insertReport(report)

    suspend fun updateReportStatus(reportId: Long, status: String) =
        reportDao.updateStatus(reportId, status)

    // ─── Contractors ──────────────────────────────────────────────────────────

    fun getAllContractors(): Flow<List<Contractor>> = contractorDao.getAllContractors()

    fun getContractorById(id: Long): Flow<Contractor?> = contractorDao.getContractorById(id)

    fun searchContractors(query: String): Flow<List<Contractor>> =
        contractorDao.searchContractors(query)
}
