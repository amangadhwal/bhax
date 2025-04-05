package com.bhax.app.data

import android.content.Context
import android.provider.Settings
import java.util.UUID

class UserAgreementManager private constructor(private val context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun hasAgreed(): Boolean {
        return preferences.getBoolean(KEY_AGREEMENT_STATUS, false)
    }
    
    fun getToken(): String? {
        return preferences.getString(KEY_USER_TOKEN, null)
    }
    
    fun agreeAndGenerateToken() {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val uniqueId = UUID.nameUUIDFromBytes(androidId.toByteArray())
        val token = uniqueId.toString().take(7).uppercase()
        
        preferences.edit().apply {
            putBoolean(KEY_AGREEMENT_STATUS, true)
            putString(KEY_USER_TOKEN, token)
            apply()
        }
    }
    
    fun clearAgreement() {
        preferences.edit().apply {
            remove(KEY_AGREEMENT_STATUS)
            remove(KEY_USER_TOKEN)
            apply()
        }
    }
    
    companion object {
        private const val PREFS_NAME = "user_agreement_prefs"
        private const val KEY_AGREEMENT_STATUS = "agreement_status"
        private const val KEY_USER_TOKEN = "user_token"
        
        @Volatile
        private var INSTANCE: UserAgreementManager? = null
        
        fun getInstance(context: Context): UserAgreementManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserAgreementManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
package com.bhax.app.data

import android.content.Context
import android.provider.Settings
import java.util.UUID

class UserAgreementManager private constructor(private val context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    fun hasAgreed(): Boolean {
        return preferences.getBoolean(KEY_AGREEMENT_STATUS, false)
    }
    
    fun getToken(): String? {
        return preferences.getString(KEY_USER_TOKEN, null)
    }
    
    fun agreeAndGenerateToken() {
        val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val uniqueId = UUID.nameUUIDFromBytes(androidId.toByteArray())
        val token = uniqueId.toString().take(7).uppercase()
        
        preferences.edit().apply {
            putBoolean(KEY_AGREEMENT_STATUS, true)
            putString(KEY_USER_TOKEN, token)
            apply()
        }
    }
    
    fun clearAgreement() {
        preferences.edit().apply {
            remove(KEY_AGREEMENT_STATUS)
            remove(KEY_USER_TOKEN)
            apply()
        }
    }
    
    companion object {
        private const val PREFS_NAME = "user_agreement_prefs"
        private const val KEY_AGREEMENT_STATUS = "agreement_status"
        private const val KEY_USER_TOKEN = "user_token"
        
        @Volatile
        private var INSTANCE: UserAgreementManager? = null
        
        fun getInstance(context: Context): UserAgreementManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserAgreementManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}