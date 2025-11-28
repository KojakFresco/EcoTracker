// data/repository/PreferencesRepository.kt
package com.example.ecotracker.data.repository

import android.content.SharedPreferences
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun getUserId(): String = sharedPreferences.getString("userId", "null") ?: "null"

    fun saveInt(name: String, value: Int) {
        sharedPreferences.edit().putInt(name, value).apply()
    }

    fun getInt(name: String): Int = sharedPreferences.getInt(name, 0)

    fun saveString(name: String, value: String) {
        sharedPreferences.edit().putString(name, value).apply()
    }

    fun saveDate(name: String, date: ZonedDateTime) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
        sharedPreferences.edit().putString(name, formatter.format(date)).apply()
    }

    fun getDate(name: String): ZonedDateTime {
        val dateString = sharedPreferences.getString(name, null)
        return if (dateString == null) {
            ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1)
        } else {
            LocalDateTime.parse(dateString.replace(" ", "T"))
                .atOffset(ZoneOffset.UTC)
                .atZoneSameInstant(ZoneId.systemDefault())
        }
    }
}