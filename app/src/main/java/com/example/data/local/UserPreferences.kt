package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kids_review_prefs")

data class UserPrefsState(
    val studentName: String = "بطلنا المستقبلي",
    val studentAvatar: String = "🌟",
    val isProfileCompleted: Boolean = false,
    val soundEffectsEnabled: Boolean = true,
    val voiceReadAloudEnabled: Boolean = true,
    val dailyStreak: Int = 1,
    val lastActiveDate: String = "",
    val dailyChallengeCompletedDate: String = ""
)

class UserPreferences(private val context: Context) {

    private object Keys {
        val STUDENT_NAME = stringPreferencesKey("student_name")
        val STUDENT_AVATAR = stringPreferencesKey("student_avatar")
        val PROFILE_COMPLETED = booleanPreferencesKey("profile_completed")
        val SOUND_EFFECTS = booleanPreferencesKey("sound_effects_enabled")
        val VOICE_READ_ALOUD = booleanPreferencesKey("voice_read_aloud_enabled")
        val DAILY_STREAK = intPreferencesKey("daily_streak")
        val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
        val DAILY_CHALLENGE_DATE = stringPreferencesKey("daily_challenge_completed_date")
    }

    val userPrefsFlow: Flow<UserPrefsState> = context.dataStore.data.map { prefs ->
        UserPrefsState(
            studentName = prefs[Keys.STUDENT_NAME] ?: "بطلنا المستقبلي",
            studentAvatar = prefs[Keys.STUDENT_AVATAR] ?: "🌟",
            isProfileCompleted = prefs[Keys.PROFILE_COMPLETED] ?: false,
            soundEffectsEnabled = prefs[Keys.SOUND_EFFECTS] ?: true,
            voiceReadAloudEnabled = prefs[Keys.VOICE_READ_ALOUD] ?: true,
            dailyStreak = prefs[Keys.DAILY_STREAK] ?: 1,
            lastActiveDate = prefs[Keys.LAST_ACTIVE_DATE] ?: "",
            dailyChallengeCompletedDate = prefs[Keys.DAILY_CHALLENGE_DATE] ?: ""
        )
    }

    suspend fun saveProfile(name: String, avatar: String) {
        val cleanName = if (name.trim().isEmpty()) "بطلنا المستقبلي" else name.trim()
        context.dataStore.edit { prefs ->
            prefs[Keys.STUDENT_NAME] = cleanName
            prefs[Keys.STUDENT_AVATAR] = avatar
            prefs[Keys.PROFILE_COMPLETED] = true
        }
    }

    suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SOUND_EFFECTS] = enabled
        }
    }

    suspend fun setVoiceReadAloudEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.VOICE_READ_ALOUD] = enabled
        }
    }

    suspend fun recordActivityToday() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = formatter.format(Date())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = formatter.format(cal.time)

        context.dataStore.edit { prefs ->
            val lastDate = prefs[Keys.LAST_ACTIVE_DATE] ?: ""
            val currentStreak = prefs[Keys.DAILY_STREAK] ?: 0
            when (lastDate) {
                today -> {
                    if (currentStreak < 1) prefs[Keys.DAILY_STREAK] = 1
                }
                yesterday -> {
                    prefs[Keys.DAILY_STREAK] = (currentStreak + 1).coerceAtLeast(1)
                    prefs[Keys.LAST_ACTIVE_DATE] = today
                }
                else -> {
                    prefs[Keys.DAILY_STREAK] = 1
                    prefs[Keys.LAST_ACTIVE_DATE] = today
                }
            }
        }
    }

    suspend fun markDailyChallengeCompleted() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = formatter.format(Date())
        context.dataStore.edit { prefs ->
            prefs[Keys.DAILY_CHALLENGE_DATE] = today
        }
    }
}
