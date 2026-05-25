package com.apulum.tenis.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session")

class SessionStore(private val context: Context) {
    private val tokenKey = stringPreferencesKey("token")
    private val displayNameKey = stringPreferencesKey("display_name")
    private val emailKey = stringPreferencesKey("email")
    private val userIdKey = stringPreferencesKey("user_id")
    private val roleKey = stringPreferencesKey("role")

    val sessionFlow: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        val token = prefs[tokenKey] ?: return@map null
        UserSession(
            token = token,
            userId = prefs[userIdKey]?.toLongOrNull() ?: return@map null,
            displayName = prefs[displayNameKey] ?: "",
            email = prefs[emailKey] ?: "",
            role = com.apulum.tenis.data.model.UserRole.fromApi(prefs[roleKey])
        )
    }

    suspend fun save(auth: com.apulum.tenis.data.api.AuthResponse) {
        context.dataStore.edit { prefs ->
            prefs[tokenKey] = auth.token
            prefs[displayNameKey] = auth.displayName
            prefs[emailKey] = auth.email
            prefs[userIdKey] = auth.userId.toString()
            prefs[roleKey] = auth.role
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}

data class UserSession(
    val token: String,
    val userId: Long,
    val displayName: String,
    val email: String,
    val role: com.apulum.tenis.data.model.UserRole
)
