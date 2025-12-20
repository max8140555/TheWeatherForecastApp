package com.max.theweatherforecastapp.core.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.max.theweatherforecastapp.core.data.model.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class WeatherDao {

    @Query("SELECT * FROM weather_data WHERE name = :name LIMIT 1")
    abstract suspend fun getWeatherByName(name: String): WeatherEntity?

    @Query("UPDATE weather_data SET lastUsedAt = :timestamp WHERE name = :name")
    abstract suspend fun updateLastUsedTimestamp(name: String, timestamp: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather_data WHERE fetchedAt = (SELECT MIN(fetchedAt) FROM weather_data)")
    abstract suspend fun deleteOldest()

    @Query("SELECT * FROM weather_data ORDER BY lastUsedAt DESC")
    abstract fun getAllCacheWeather(): Flow<List<WeatherEntity>>

    @Query("DELETE FROM weather_data")
    abstract suspend fun clearWeather()

}
