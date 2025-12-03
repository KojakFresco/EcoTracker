// data/repository/PreferencesRepository.kt
package com.example.ecotracker.data.repository

import android.content.SharedPreferences
import com.example.ecotracker.KEY_USER_CACHE
import com.example.ecotracker.data.model.User
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val prefs: SharedPreferences,
    private val gson: Gson
) {
    fun getUserId(): String = prefs.getString("userId", "null") ?: "null"

    fun saveInt(name: String, value: Int) {
        prefs.edit().putInt(name, value).apply()
    }

    fun loadInt(name: String, defaultValue: Int): Int = prefs.getInt(name, defaultValue)

    fun saveLong(name: String, value: Long) {
        prefs.edit().putLong(name, value).apply()
    }

    fun loadLong(name: String, defaultValue: Long): Long = prefs.getLong(name, defaultValue)

    fun saveString(name: String, value: String) {
        prefs.edit().putString(name, value).apply()
    }

    fun saveBoolean(name: String, value: Boolean) {
        prefs.edit().putBoolean(name, value).apply()
    }

    fun loadBoolean(name: String, defaultValue: Boolean): Boolean = prefs.getBoolean(name, defaultValue)

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString(KEY_USER_CACHE, userJson).apply()
    }

    fun loadUser(): User? {
        val userJson = prefs.getString(KEY_USER_CACHE, null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    fun saveDate(name: String, date: ZonedDateTime) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
        prefs.edit().putString(name, formatter.format(date)).apply()
    }

    fun loadDate(name: String): ZonedDateTime {
        val dateString = prefs.getString(name, null)
        return if (dateString == null) {
            ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1)
        } else {
            LocalDateTime.parse(dateString.replace(" ", "T"))
                .atOffset(ZoneOffset.UTC)
                .atZoneSameInstant(ZoneId.systemDefault())
        }
    }
}