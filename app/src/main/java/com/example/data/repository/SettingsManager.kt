package com.example.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "aether_settings")

class SettingsManager(private val context: Context) {
    companion object {
        private val API_KEY_PREF = stringPreferencesKey("custom_api_key")
        private val USER_NAME_PREF = stringPreferencesKey("user_name")
        private val THEME_PREF = stringPreferencesKey("app_theme") // "cyberpunk", "hologram_grid", "slate_luxury"
        private val LANGUAGE_PREF = stringPreferencesKey("app_language")
        private val IS_PREMIUM_PREF = androidx.datastore.preferences.core.booleanPreferencesKey("is_premium")
        private val REQS_TODAY_PREF = androidx.datastore.preferences.core.intPreferencesKey("requests_today")
        private val LAST_REQ_DATE_PREF = stringPreferencesKey("last_request_date")

        // Secure Authentication Preferences
        private val IS_LOGGED_IN_PREF = androidx.datastore.preferences.core.booleanPreferencesKey("is_logged_in")
        private val AUTH_EMAIL_OR_PHONE_PREF = stringPreferencesKey("auth_email_or_phone")
        private val AUTH_METHOD_PREF = stringPreferencesKey("auth_method")
        private val AUTH_SECURE_TOKEN_PREF = stringPreferencesKey("auth_secure_token")
    }

    val customApiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[API_KEY_PREF]
    }

    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME_PREF] ?: "Nexus Operator"
    }

    val appTheme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_PREF] ?: "nebula_solaris"
    }

    val appLanguage: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_PREF] ?: "en"
    }

    val isPremium: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_PREMIUM_PREF] ?: false
    }

    val requestsToday: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REQS_TODAY_PREF] ?: 0
    }

    val lastRequestDate: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LAST_REQ_DATE_PREF] ?: ""
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_PREF] ?: false
    }

    val authEmailOrPhone: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AUTH_EMAIL_OR_PHONE_PREF] ?: ""
    }

    val authMethod: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AUTH_METHOD_PREF] ?: ""
    }

    val authSecureToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AUTH_SECURE_TOKEN_PREF] ?: ""
    }

    suspend fun saveCustomApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[API_KEY_PREF] = key.trim()
        }
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME_PREF] = name
        }
    }

    suspend fun saveAppTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_PREF] = theme
        }
    }

    suspend fun saveAppLanguage(lang: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_PREF] = lang
        }
    }

    suspend fun saveIsPremium(premium: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_PREMIUM_PREF] = premium
        }
    }

    suspend fun saveRequestsToday(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[REQS_TODAY_PREF] = count
        }
    }

    suspend fun saveLastRequestDate(dateStr: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_REQ_DATE_PREF] = dateStr
        }
    }

    suspend fun saveAuthData(isLoggedIn: Boolean, emailOrPhone: String, authMethod: String, secureToken: String) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_PREF] = isLoggedIn
            preferences[AUTH_EMAIL_OR_PHONE_PREF] = emailOrPhone
            preferences[AUTH_METHOD_PREF] = authMethod
            preferences[AUTH_SECURE_TOKEN_PREF] = secureToken
        }
    }

    suspend fun clearAuthData() {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN_PREF] = false
            preferences[AUTH_EMAIL_OR_PHONE_PREF] = ""
            preferences[AUTH_METHOD_PREF] = ""
            preferences[AUTH_SECURE_TOKEN_PREF] = ""
        }
    }
}
