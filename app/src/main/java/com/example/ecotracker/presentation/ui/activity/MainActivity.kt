package com.example.ecotracker.presentation.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.ecotracker.R
import com.example.ecotracker.presentation.viewmodels.AuthViewModel
import com.example.ecotracker.presentation.viewmodels.AuthState
import com.example.ecotracker.presentation.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        splashScreen.setKeepOnScreenCondition {
            authViewModel.authState.value is AuthState.Idle || authViewModel.authState.value is AuthState.Loading
        }

        observeAuthState()
    }

    // ИСПРАВЛЕНИЕ: Вызываем loadUser() каждый раз, когда приложение становится видимым
    override fun onStart() {
        super.onStart()
        // Проверяем, авторизован ли пользователь, прежде чем загружать данные
        if (authViewModel.authState.value is AuthState.Authenticated) {
            userViewModel.loadUser()
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                if (state is AuthState.Authenticated) {
                    // Первоначальная загрузка данных пользователя при аутентификации
                    userViewModel.loadUser()
                }
            }
        }
    }
}