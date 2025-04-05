package com.bhax.app.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class ThemeManager private constructor(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun isAuroraTheme(): Boolean {
        return preferences.getBoolean(KEY_AURORA_THEME, false)
    }
    
    fun setTheme(activity: AppCompatActivity) {
        if (isAuroraTheme()) {
            activity.setTheme(com.bhax.app.R.style.Theme_BharatChan_Aurora)
        } else {
            activity.setTheme(com.bhax.app.R.style.Theme_BharatChan)
        }
    }
    
    fun toggleTheme(activity: AppCompatActivity) {
        val isAurora = isAuroraTheme()
        preferences.edit().putBoolean(KEY_AURORA_THEME, !isAurora).apply()
        setTheme(activity)
        activity.recreate()
    }
    
    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_AURORA_THEME = "is_aurora_theme"
        
        @Volatile
        private var INSTANCE: ThemeManager? = null
        
        fun getInstance(context: Context): ThemeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
package com.bhax.app.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

class ThemeManager private constructor(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun isAuroraTheme(): Boolean {
        return preferences.getBoolean(KEY_AURORA_THEME, false)
    }
    
    fun setTheme(activity: AppCompatActivity) {
        if (isAuroraTheme()) {
            activity.setTheme(com.bhax.app.R.style.Theme_BharatChan_Aurora)
        } else {
            activity.setTheme(com.bhax.app.R.style.Theme_BharatChan)
        }
    }
    
    fun toggleTheme(activity: AppCompatActivity) {
        val isAurora = isAuroraTheme()
        preferences.edit().putBoolean(KEY_AURORA_THEME, !isAurora).apply()
        setTheme(activity)
        activity.recreate()
    }
    
    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_AURORA_THEME = "is_aurora_theme"
        
        @Volatile
        private var INSTANCE: ThemeManager? = null
        
        fun getInstance(context: Context): ThemeManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ThemeManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}