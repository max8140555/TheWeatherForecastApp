package com.max.theweatherforecastapp.core.data.database

import androidx.room.TypeConverter
import com.max.theweatherforecastapp.core.data.model.CurrentWeather
import com.max.theweatherforecastapp.core.data.model.DailyWeather
import com.max.theweatherforecastapp.core.data.model.FeelsLike
import com.max.theweatherforecastapp.core.data.model.HourlyWeather
import com.max.theweatherforecastapp.core.data.model.Temp
import com.max.theweatherforecastapp.core.data.model.WeatherDescription
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class DatabaseConverters {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val hourlyWeatherListAdapter = moshi.adapter<List<HourlyWeather>>(Types.newParameterizedType(List::class.java, HourlyWeather::class.java))
    private val dailyWeatherListAdapter = moshi.adapter<List<DailyWeather>>(Types.newParameterizedType(List::class.java, DailyWeather::class.java))
    private val weatherDescriptionListAdapter = moshi.adapter<List<WeatherDescription>>(Types.newParameterizedType(List::class.java, WeatherDescription::class.java))
    private val currentWeatherAdapter = moshi.adapter(CurrentWeather::class.java)
    private val tempAdapter = moshi.adapter(Temp::class.java)
    private val feelsLikeAdapter = moshi.adapter(FeelsLike::class.java)

    @TypeConverter
    fun fromCurrentWeather(value: CurrentWeather?): String? {
        return value?.let { currentWeatherAdapter.toJson(it) }
    }

    @TypeConverter
    fun toCurrentWeather(value: String?): CurrentWeather? {
        return value?.let { currentWeatherAdapter.fromJson(it) }
    }

    @TypeConverter
    fun fromHourlyWeatherList(value: List<HourlyWeather>?): String? {
        return value?.let { hourlyWeatherListAdapter.toJson(it) }
    }

    @TypeConverter
    fun toHourlyWeatherList(value: String?): List<HourlyWeather>? {
        return value?.let { hourlyWeatherListAdapter.fromJson(it) }
    }

    @TypeConverter
    fun fromDailyWeatherList(value: List<DailyWeather>?): String? {
        return value?.let { dailyWeatherListAdapter.toJson(it) }
    }

    @TypeConverter
    fun toDailyWeatherList(value: String?): List<DailyWeather>? {
        return value?.let { dailyWeatherListAdapter.fromJson(it) }
    }

    @TypeConverter
    fun fromWeatherDescriptionList(value: List<WeatherDescription>?): String? {
        return value?.let { weatherDescriptionListAdapter.toJson(it) }
    }

    @TypeConverter
    fun toWeatherDescriptionList(value: String?): List<WeatherDescription>? {
        return value?.let { weatherDescriptionListAdapter.fromJson(it) }
    }

    @TypeConverter
    fun fromTemp(value: Temp?): String? {
        return value?.let { tempAdapter.toJson(it) }
    }

    @TypeConverter
    fun toTemp(value: String?): Temp? {
        return value?.let { tempAdapter.fromJson(it) }
    }

    @TypeConverter
    fun fromFeelsLike(value: FeelsLike?): String? {
        return value?.let { feelsLikeAdapter.toJson(it) }
    }

    @TypeConverter
    fun toFeelsLike(value: String?): FeelsLike? {
        return value?.let { feelsLikeAdapter.fromJson(it) }
    }
}
