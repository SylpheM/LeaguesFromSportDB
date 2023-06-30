package com.sylphem.leaguesfromsportdb.di

import com.sylphem.leaguesfromsportdb.BuildConfig
import com.sylphem.leaguesfromsportdb.data.remote.SportsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun providesRetrofitSportsApi(): SportsApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.HOST_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SportsApiService::class.java)
    }
}