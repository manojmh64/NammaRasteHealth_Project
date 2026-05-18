package com.nammaraste.health.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nammaraste.health.data.db.dao.ContractorDao
import com.nammaraste.health.data.db.dao.ReportDao
import com.nammaraste.health.data.db.dao.RoadDao
import com.nammaraste.health.data.db.entity.Contractor
import com.nammaraste.health.data.db.entity.DamageReport
import com.nammaraste.health.data.db.entity.Road

/**
 * Room Database for the Namma-Raste Health application.
 *
 * Schema:
 * - roads          : Road segments with contractor reference and geo bounds
 * - damage_reports : Citizen-submitted road damage reports with GPS + AI tags
 * - contractors    : Construction/maintenance contractor directory
 *
 * Version history:
 * 1 → Initial schema
 */
@Database(
    entities = [Road::class, DamageReport::class, Contractor::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun roadDao(): RoadDao
    abstract fun reportDao(): ReportDao
    abstract fun contractorDao(): ContractorDao

    companion object {
        private const val DB_NAME = "namma_raste_health.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns a singleton instance of the database.
         * Thread-safe via double-checked locking.
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration() // OK for demo; use migrations in production
                .build()
        }
    }
}
