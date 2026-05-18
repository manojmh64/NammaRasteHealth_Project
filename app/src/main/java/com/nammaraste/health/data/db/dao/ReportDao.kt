package com.nammaraste.health.data.db.dao

import androidx.room.*
import com.nammaraste.health.data.db.entity.DamageReport
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for DamageReport entity.
 */
@Dao
interface ReportDao {

    // ─── Inserts ─────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: DamageReport): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<DamageReport>)

    // ─── Updates ─────────────────────────────────────────────────────────────

    @Update
    suspend fun updateReport(report: DamageReport)

    /** Update only the status field of a report */
    @Query("UPDATE damage_reports SET status = :status WHERE id = :reportId")
    suspend fun updateStatus(reportId: Long, status: String)

    // ─── Deletes ─────────────────────────────────────────────────────────────

    @Delete
    suspend fun deleteReport(report: DamageReport)

    // ─── Queries ─────────────────────────────────────────────────────────────

    /** Returns all reports ordered by most recent first */
    @Query("SELECT * FROM damage_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<DamageReport>>

    /** Returns all reports for a specific road */
    @Query("SELECT * FROM damage_reports WHERE roadId = :roadId ORDER BY timestamp DESC")
    fun getReportsByRoad(roadId: Long): Flow<List<DamageReport>>

    /** Returns total complaint count for a road — used for health score */
    @Query("SELECT COUNT(*) FROM damage_reports WHERE roadId = :roadId")
    fun getComplaintCountForRoad(roadId: Long): Flow<Int>

    /** Returns count of complaints per taluka by joining with roads table */
    @Query("""
        SELECT r.taluka, COUNT(d.id) AS reportCount
        FROM damage_reports d
        INNER JOIN roads r ON d.roadId = r.id
        GROUP BY r.taluka
        ORDER BY reportCount DESC
    """)
    fun getComplaintsPerTaluka(): Flow<List<TalukaComplaintCount>>

    /** Returns recent 10 reports across all roads */
    @Query("SELECT * FROM damage_reports ORDER BY timestamp DESC LIMIT 10")
    fun getRecentReports(): Flow<List<DamageReport>>

    /** Returns reports by severity level */
    @Query("SELECT * FROM damage_reports WHERE severity = :severity ORDER BY timestamp DESC")
    fun getReportsBySeverity(severity: String): Flow<List<DamageReport>>

    /** Returns total number of reports */
    @Query("SELECT COUNT(*) FROM damage_reports")
    fun getTotalReportCount(): Flow<Int>

    /** Returns counts grouped by issue type */
    @Query("""
        SELECT issueType, COUNT(*) AS count
        FROM damage_reports
        GROUP BY issueType
    """)
    fun getReportsByIssueType(): Flow<List<IssueTypeCount>>
}

/** Projection for taluka-level complaint aggregation */
data class TalukaComplaintCount(
    val taluka: String,
    val reportCount: Int
)

/** Projection for issue type breakdown */
data class IssueTypeCount(
    val issueType: String,
    val count: Int
)
