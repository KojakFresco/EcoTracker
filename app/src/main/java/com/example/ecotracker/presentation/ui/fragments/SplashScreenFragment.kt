package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.R
import androidx.navigation.fragment.findNavController
import com.example.ecotracker.presentation.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            // Ждем инициализацию AuthViewModel
            delay(1000)

            authViewModel.currentUser.collect { user ->
                if (user != null) {
                    // Пользователь авторизован - на главный экран
                    findNavController().navigate(R.id.action_to_main)
                } else {
                    // Не авторизован - на экран входа
                    findNavController().navigate(R.id.action_to_signIn)
                }
            }
        }
    }
}