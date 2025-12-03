package com.example.ecotracker.presentation.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.ecotracker.R
import com.example.ecotracker.presentation.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // ViewModel здесь все еще нужна, чтобы SplashFragment мог к ней подключиться
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем сплеш-скрин. Это должно быть вызвано до super.onCreate()
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}