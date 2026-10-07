package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("leadflow_auth_prefs", Context.MODE_PRIVATE)

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _adminName = MutableStateFlow("Administrator")
    val adminName: StateFlow<String> = _adminName.asStateFlow()

    init {
        // Restore session if remember me was active
        val loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        _isAdminLoggedIn.value = loggedIn
        _adminName.value = prefs.getString(KEY_ADMIN_NAME, "Admin User") ?: "Admin User"
    }

    fun authenticate(identifier: String, secret: String): Boolean {
        val cleanIdentifier = identifier.trim().lowercase()
        val cleanSecret = secret.trim()

        val savedPin = prefs.getString(KEY_ADMIN_PIN, DEFAULT_ADMIN_PIN) ?: DEFAULT_ADMIN_PIN
        val savedPassword = prefs.getString(KEY_ADMIN_PASSWORD, DEFAULT_ADMIN_PASSWORD) ?: DEFAULT_ADMIN_PASSWORD

        val isPinMatch = cleanSecret == savedPin || cleanSecret == DEFAULT_ADMIN_PIN
        val isPasswordMatch = cleanSecret == savedPassword || cleanSecret == DEFAULT_ADMIN_PASSWORD

        val isIdentifierValid = cleanIdentifier.isEmpty() ||
                cleanIdentifier == "admin" ||
                cleanIdentifier == "admin@leadflow.io" ||
                cleanIdentifier == "admin@company.com"

        if ((isIdentifierValid && isPasswordMatch) || isPinMatch) {
            _isAdminLoggedIn.value = true
            _adminName.value = if (cleanIdentifier.isNotEmpty()) cleanIdentifier else "Admin Manager"
            prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_ADMIN_NAME, _adminName.value)
                .apply()
            return true
        }
        return false
    }

    fun logout() {
        _isAdminLoggedIn.value = false
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }

    fun getAdminPin(): String {
        return prefs.getString(KEY_ADMIN_PIN, DEFAULT_ADMIN_PIN) ?: DEFAULT_ADMIN_PIN
    }

    fun updatePin(newPin: String): Boolean {
        if (newPin.length >= 4) {
            prefs.edit().putString(KEY_ADMIN_PIN, newPin).apply()
            return true
        }
        return false
    }

    companion object {
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_ADMIN_NAME = "key_admin_name"
        private const val KEY_ADMIN_PIN = "key_admin_pin"
        private const val KEY_ADMIN_PASSWORD = "key_admin_password"

        const val DEFAULT_ADMIN_PIN = "1234"
        const val DEFAULT_ADMIN_PASSWORD = "admin123"
        const val DEFAULT_ADMIN_EMAIL = "admin@leadflow.io"
    }
}
