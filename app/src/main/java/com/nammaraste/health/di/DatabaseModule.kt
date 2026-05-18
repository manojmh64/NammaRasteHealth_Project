package com.nammaraste.health.di

import android.content.Context
import com.nammaraste.health.data.db.AppDatabase
import com.nammaraste.health.data.db.dao.ContractorDao
import com.nammaraste.health.data.db.dao.ReportDao
import com.nammaraste.health.data.db.dao.RoadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing database and DAO dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideRoadDao(db: AppDatabase): RoadDao = db.roadDao()

    @Provides
    @Singleton
    fun provideReportDao(db: AppDatabase): ReportDao = db.reportDao()

    @Provides
    @Singleton
    fun provideContractorDao(db: AppDatabase): ContractorDao = db.contractorDao()
}
