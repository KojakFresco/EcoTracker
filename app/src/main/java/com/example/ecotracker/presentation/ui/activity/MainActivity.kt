package com.example.ecotracker.presentation.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.ecotracker.R
import com.example.ecotracker.presentation.viewmodels.AuthViewModel
import com.example.ecotracker.presentation.viewmodels.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем сплеш-скрин. Это должно быть вызвано до super.onCreate()
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Удерживаем сплеш-скрин на экране, пока мы проверяем авторизацию
        splashScreen.setKeepOnScreenCondition {
            authViewModel.authState.value is AuthState.Loading
        }

        // Как только состояние изменится, переходим на нужный экран
        lifecycleScope.launch {
            // Ждем первого состояния, которое не является Loading
            val initialState = authViewModel.authState.first { it !is AuthState.Loading }

            val navGraph = navController.navInflater.inflate(R.navigation.main_nav)

            if (initialState is AuthState.Authenticated) {
                // Если пользователь авторизован, стартовый экран - MainFragment
                navGraph.setStartDestination(R.id.mainFragment)
            } else {
                // Если нет - SignInFragment
                navGraph.setStartDestination(R.id.signInFragment)
            }
            navController.graph = navGraph
        }
    }
}