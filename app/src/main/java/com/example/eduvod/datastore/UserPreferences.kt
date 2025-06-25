package com.example.eduvod.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//Extension to create DataStore instance
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_ID_KEY = intPreferencesKey("user_id")
    }

    // Save token, email and ID
    suspend fun saveUserSession(token: String, email: String, userId: Int) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[EMAIL_KEY] =email
            prefs[USER_ID_KEY] = userId
        }
    }

    //Access token as Flow (reactive)
    val authToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[EMAIL_KEY] }
    val userId: Flow<Int?> = context.dataStore.data.map { it[USER_ID_KEY] }

    //Logout
    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }


}