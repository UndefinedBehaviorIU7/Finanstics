package com.example.finanstics.presentation.preferencesManager

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.example.finanstics.ui.theme.PREF_NAME
import com.example.finanstics.ui.theme.TIME_INIT
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
        value: String
    ) {
        sharedPreferences.edit() {
            putString(key, value)
        }
    }

    fun getData(
        key: String,
        defaultValue: String
    ): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
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
        println(getData("time_update", TIME_INIT))
        return getData("time_update", TIME_INIT)
    }
}
