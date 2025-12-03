package com.example.ecotracker.presentation.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ecotracker.R
import com.example.ecotracker.presentation.viewmodels.AuthViewModel
import com.example.ecotracker.presentation.viewmodels.AuthState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем сплеш-скрин. Это должно быть вызвано до super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Удерживаем сплеш-скрин на экране до тех пор, пока ViewModel не определит состояние авторизации.
        // Это предотвращает "гонку состояний" и креш при запуске.
        splashScreen.setKeepOnScreenCondition {
            authViewModel.authState.value is AuthState.Idle || authViewModel.authState.value is AuthState.Loading
        }
    }
}