package com.ub.finanstics.presentation.preferencesManager

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.ub.finanstics.ui.theme.PREF_NAME

class EncryptedPreferencesManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
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
    ): Int = sharedPreferences.getInt(key, defaultValue)

    fun getString(
        key: String,
        defaultValue: String
    ): String = sharedPreferences.getString(key, defaultValue) ?: defaultValue

    fun getLong(
        key: String,
        defaultValue: Long
    ): Long = sharedPreferences.getLong(key, defaultValue)

    fun getFloat(
        key: String,
        defaultValue: Float
    ): Float = sharedPreferences.getFloat(key, defaultValue)

    fun getBoolean(
        key: String,
        defaultValue: Boolean
    ): Boolean = sharedPreferences.getBoolean(key, defaultValue)
}
