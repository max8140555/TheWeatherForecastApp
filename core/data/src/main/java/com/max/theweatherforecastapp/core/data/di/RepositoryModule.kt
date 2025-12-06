package com.max.theweatherforecastapp.core.data.di

import com.max.theweatherforecastapp.core.data.repository.UserPreferencesRepositoryImpl
import com.max.theweatherforecastapp.core.data.repository.WeatherRepositoryImpl
import com.max.theweatherforecastapp.core.domain.repository.UserPreferencesRepository
import com.max.theweatherforecastapp.core.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserPreferencesRepository(
        userPreferencesRepositoryImpl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository
}
