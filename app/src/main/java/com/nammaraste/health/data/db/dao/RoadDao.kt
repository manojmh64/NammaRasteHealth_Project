package com.nammaraste.health.data.db.dao

import androidx.room.*
import com.nammaraste.health.data.db.entity.Road
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Road entity.
 * Provides reactive queries via Flow for real-time UI updates.
 */
@Dao
interface RoadDao {

    // ─── Inserts ─────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoad(road: Road): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(roads: List<Road>)

    // ─── Updates ─────────────────────────────────────────────────────────────

    @Update
    suspend fun updateRoad(road: Road)

    // ─── Deletes ─────────────────────────────────────────────────────────────

    @Delete
    suspend fun deleteRoad(road: Road)

    // ─── Queries ─────────────────────────────────────────────────────────────

    /** Returns all roads ordered by taluka and name */
    @Query("SELECT * FROM roads ORDER BY taluka ASC, name ASC")
    fun getAllRoads(): Flow<List<Road>>

    /** Returns a single road by its primary key */
    @Query("SELECT * FROM roads WHERE id = :roadId")
    fun getRoadById(roadId: Long): Flow<Road?>

    /** Searches roads by name or taluka (case-insensitive) */
    @Query("""
        SELECT * FROM roads 
        WHERE name LIKE '%' || :query || '%' 
           OR taluka LIKE '%' || :query || '%'
           OR district LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchRoads(query: String): Flow<List<Road>>

    /** Returns roads belonging to a specific taluka */
    @Query("SELECT * FROM roads WHERE taluka = :taluka ORDER BY name ASC")
    fun getRoadsByTaluka(taluka: String): Flow<List<Road>>

    /** Returns all unique talukas */
    @Query("SELECT DISTINCT taluka FROM roads ORDER BY taluka ASC")
    fun getAllTalukas(): Flow<List<String>>

    /** Returns total road count — used for seed check */
    @Query("SELECT COUNT(*) FROM roads")
    suspend fun getRoadCount(): Int

    /** Returns roads assigned to a specific contractor */
    @Query("SELECT * FROM roads WHERE contractorId = :contractorId")
    fun getRoadsByContractor(contractorId: Long): Flow<List<Road>>

    /**
     * Returns roads with their complaint counts joined.
     * Used to calculate health scores on the dashboard.
     */
    @Query("""
        SELECT r.*, COUNT(d.id) AS complaintCount
        FROM roads r
        LEFT JOIN damage_reports d ON r.id = d.roadId
        GROUP BY r.id
        ORDER BY complaintCount ASC
    """)
    fun getRoadsWithComplaintCount(): Flow<List<RoadWithComplaintCount>>
}

/** Projection for roads joined with their complaint count */
data class RoadWithComplaintCount(
    val id: Long,
    val name: String,
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
    val updatedAt: Long,
    val complaintCount: Int
)
