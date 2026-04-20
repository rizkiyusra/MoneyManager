package com.example.moneymanager.presentation.security

import androidx.lifecycle.ViewModel
import com.example.moneymanager.common.utils.SecurityPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityPreferences: SecurityPreferences
) : ViewModel() {

    private val _isAppLockEnabled = MutableStateFlow(securityPreferences.isAppLockEnabled())
    val isAppLockEnabled: StateFlow<Boolean> = _isAppLockEnabled.asStateFlow()

    fun setAppLock(enabled: Boolean) {
        securityPreferences.setAppLockEnabled(enabled)
        _isAppLockEnabled.value = enabled
    }
}