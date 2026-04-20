package com.example.moneymanager.common.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.content.edit

class SecurityPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)

    fun setAppLockEnabled(isEnabled: Boolean) {
        prefs.edit { putBoolean("is_app_lock_enabled", isEnabled) }
    }

    fun isAppLockEnabled(): Boolean {
        return prefs.getBoolean("is_app_lock_enabled", false)
    }
}