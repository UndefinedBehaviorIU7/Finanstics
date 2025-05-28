package com.ub.finanstics.presentation.preferencesManager

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.ub.finanstics.ui.theme.PREF_NAME
import com.ub.finanstics.ui.theme.TIME_INIT
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    fun saveData(
        key: String,
        value: Any
    ) {
        with(sharedPreferences.edit()) {
            when (value) {
                is String -> putString(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is Float -> putFloat(key, value)
                is Long -> putLong(key, value)
                else -> throw IllegalArgumentException("Unsupported type: ${value::class.java}")
            }
            apply()
        }
    }

    fun getInt(
        key: String,
        defaultValue: Int
    ): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun getString(
        key: String,
        defaultValue: String
    ): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun getLong(
        key: String,
        defaultValue: Long
    ): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun getFloat(
        key: String,
        defaultValue: Float
    ): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    fun getBoolean(
        key: String,
        defaultValue: Boolean
    ): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveUpdateTime() {
        val date = LocalDate.now(ZoneOffset.UTC)
        val time = LocalTime.now(ZoneOffset.UTC)
        val dateTime = LocalDateTime.of(date, time)

        saveData("time_update", dateTime.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUpdateTime(): String {
        println(getString("time_update", TIME_INIT))
        return getString("time_update", TIME_INIT)
    }

    fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }
}
