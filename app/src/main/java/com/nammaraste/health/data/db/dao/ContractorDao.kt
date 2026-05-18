package com.nammaraste.health.data.db.dao

import androidx.room.*
import com.nammaraste.health.data.db.entity.Contractor
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Contractor entity.
 */
@Dao
interface ContractorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContractor(contractor: Contractor): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contractors: List<Contractor>)

    @Update
    suspend fun updateContractor(contractor: Contractor)

    @Delete
    suspend fun deleteContractor(contractor: Contractor)

    /** Returns all contractors ordered by rating descending */
    @Query("SELECT * FROM contractors ORDER BY rating DESC")
    fun getAllContractors(): Flow<List<Contractor>>

    /** Returns a single contractor by ID */
    @Query("SELECT * FROM contractors WHERE id = :id")
    fun getContractorById(id: Long): Flow<Contractor?>

    /** Searches contractors by name */
    @Query("""
        SELECT * FROM contractors
        WHERE name LIKE '%' || :query || '%'
           OR contactPerson LIKE '%' || :query || '%'
        ORDER BY rating DESC
    """)
    fun searchContractors(query: String): Flow<List<Contractor>>

    @Query("SELECT COUNT(*) FROM contractors")
    suspend fun getContractorCount(): Int
}
