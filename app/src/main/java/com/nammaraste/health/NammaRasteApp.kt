package com.nammaraste.health

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.nammaraste.health.data.db.AppDatabase
import com.nammaraste.health.data.seed.SeedData
import javax.inject.Inject

/**
 * Application class for Namma-Raste Health.
 * Initializes Hilt DI and seeds demo data on first launch.
 */
@HiltAndroidApp
class NammaRasteApp : Application() {

    // Application-scoped coroutine scope for background work
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        seedDatabaseIfEmpty()
    }

    /**
     * Seeds the Room database with demo/dummy data if it is empty.
     * This runs once on first launch.
     */
    private fun seedDatabaseIfEmpty() {
        applicationScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            if (db.roadDao().getRoadCount() == 0) {
                SeedData.seedAll(db)
            }
        }
    }
}
