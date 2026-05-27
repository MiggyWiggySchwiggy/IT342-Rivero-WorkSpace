package edu.cit.rivero.workspace

import android.content.Context

/**
 * Centralized session management utility.
 * Wraps SharedPreferences access to provide a clean, type-safe API
 * for storing and retrieving the user's JWT token, role, and basic info.
 */
object SessionManager {

    private const val PREFS_NAME = "WorkSpacePrefs"
    private const val KEY_TOKEN = "JWT_TOKEN"
    private const val KEY_ROLE = "USER_ROLE"
    private const val KEY_USER_ID = "USER_ID"
    private const val KEY_FIRST_NAME = "USER_FIRST_NAME"
    private const val KEY_LAST_NAME = "USER_LAST_NAME"
    private const val KEY_EMAIL = "USER_EMAIL"

    fun saveSession(
        context: Context,
        token: String,
        role: String,
        userId: Int,
        firstName: String,
        lastName: String,
        email: String
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_ROLE, role)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_FIRST_NAME, firstName)
            .putString(KEY_LAST_NAME, lastName)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getToken(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, null)

    fun getRole(context: Context): String? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ROLE, null)

    fun getUserId(context: Context): Int =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_USER_ID, -1)

    fun getFirstName(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_FIRST_NAME, "") ?: ""

    fun getLastName(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAST_NAME, "") ?: ""

    fun getEmail(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "") ?: ""

    fun isLoggedIn(context: Context): Boolean = getToken(context) != null

    fun isAdmin(context: Context): Boolean {
        val r = getRole(context)?.uppercase() ?: ""
        return r == "ADMIN" || r == "ROLE_ADMIN"
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
