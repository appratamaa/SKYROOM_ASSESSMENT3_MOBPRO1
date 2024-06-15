package org.d3if3156.skyroom.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.d3if3156.skyroom.model.User

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preference"
)

class UserDataStore(private val context: Context) {

    companion object {
        private val USER_NAME = stringPreferencesKey("name")
        private val USER_EMAIL = stringPreferencesKey("email")
        private val USER_PHOTO = stringPreferencesKey("photoUrl")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val userFlow: Flow<User> = context.dataStore.data.map { preferences ->
        User(
            name = preferences[USER_NAME] ?: "",
            email = preferences[USER_EMAIL] ?: "",
            photoUrl = preferences[USER_PHOTO] ?: ""
        )
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    suspend fun saveData(user: User, isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = user.name
            preferences[USER_EMAIL] = user.email
            preferences[USER_PHOTO] = user.photoUrl
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }
}
