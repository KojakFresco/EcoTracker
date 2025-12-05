package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecotracker.R
import com.example.ecotracker.presentation.viewmodels.AuthViewModel
import com.example.ecotracker.presentation.viewmodels.AuthState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            // Ждем первого определенного состояния (не Loading и не Idle)
            val state = authViewModel.authState.first { it !is AuthState.Loading && it !is AuthState.Idle }

            if (state is AuthState.Authenticated) {
                // Пользователь авторизован, идем на главный экран
                findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
            } else {
                // Пользователь не авторизован, идем на экран входа
                findNavController().navigate(R.id.action_splashFragment_to_authFragment)
            }
        }
    }
}