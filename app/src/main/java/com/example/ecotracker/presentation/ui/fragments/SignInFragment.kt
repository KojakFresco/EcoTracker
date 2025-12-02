package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecotracker.databinding.FragmentSignInBinding
import com.example.ecotracker.presentation.viewmodels.AuthState
import com.example.ecotracker.presentation.viewmodels.AuthViewModel
import com.google.android.material.R
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInFragment : Fragment() {

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.signInButton.isEnabled = false
                    }
                    is AuthState.Authenticated -> {
                        // Переход на главный экран
                        findNavController().navigate(R.id.action_to_main)
                    }
                    is AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.signInButton.isEnabled = true
                        showError(state.message)
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.signInButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(email, password)) {
                authViewModel.signIn(email, password)
            }
        }

        binding.signUpTextView.setOnClickListener {
            findNavController().navigate(R.id.action_to_signUp)
        }

        binding.forgotPasswordTextView.setOnClickListener {
            findNavController().navigate(R.id.action_to_resetPassword)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isBlank()) {
            binding.emailInputLayout.error = "Email is required"
            isValid = false
        } else {
            binding.emailInputLayout.error = null
        }

        if (password.isBlank()) {
            binding.passwordInputLayout.error = "Password is required"
            isValid = false
        } else {
            binding.passwordInputLayout.error = null
        }

        return isValid
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}