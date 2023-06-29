package com.sylphem.leaguesfromsportdb.di;

import com.sylphem.leaguesfromsportdb.data.remote.SportsApiService
import com.sylphem.leaguesfromsportdb.data.repository.SportsRepositoryImpl
import com.sylphem.leaguesfromsportdb.domain.repository.SportsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideSportsRepository(sportsApiService: SportsApiService): SportsRepository {
        return SportsRepositoryImpl(sportsApiService)
    }
}