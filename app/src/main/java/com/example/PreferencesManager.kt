package com.example

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private const val PREFS_NAME = "scanner_prefs"
    private const val KEY_OCR_MODE = "ocr_mode"
    private const val KEY_ENDPOINT = "online_endpoint"
    
    fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getOcrMode(context: Context): String {
        return getPrefs(context).getString(KEY_OCR_MODE, "Offline Only") ?: "Offline Only"
    }

    fun setOcrMode(context: Context, mode: String) {
        getPrefs(context).edit().putString(KEY_OCR_MODE, mode).apply()
    }
}
