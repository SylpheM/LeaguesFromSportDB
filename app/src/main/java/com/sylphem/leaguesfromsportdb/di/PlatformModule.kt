package com.sylphem.leaguesfromsportdb.di

import com.sylphem.leaguesfromsportdb.core.CoroutineContextProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlatformModule {
    @Provides
    @Singleton
    fun provideCoroutineContexts() = object : CoroutineContextProvider {
        override val UI get() = Dispatchers.Main
        override val IO get() = Dispatchers.IO
        override val Default get() = Dispatchers.Default
    }
}