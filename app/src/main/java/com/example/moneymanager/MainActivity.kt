package com.example.moneymanager

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.moneymanager.common.utils.SecurityPreferences
import com.example.moneymanager.presentation.MoneyManagerApp
import com.example.moneymanager.presentation.security.LockScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var securityPreferences: SecurityPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isLockEnabled = remember { securityPreferences.isAppLockEnabled() }

            var isAuthenticated by remember { mutableStateOf(!isLockEnabled) }

            if (isAuthenticated) {
                MoneyManagerApp()
            } else {
                LockScreen(
                    onUnlockSuccess = {
                        isAuthenticated = true
                    }
                )
            }
        }
    }
}