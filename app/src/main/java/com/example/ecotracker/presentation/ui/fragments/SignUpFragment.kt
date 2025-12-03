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
import com.example.ecotracker.databinding.FragmentSignUpBinding
import com.example.ecotracker.presentation.viewmodels.AuthState
import com.example.ecotracker.presentation.viewmodels.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
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
                        binding.signUpButton.isEnabled = false
                    }
                    is AuthState.Authenticated -> {
                        findNavController().navigate(R.id.action_global_mainFragment)
                    }
                    is AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.signUpButton.isEnabled = true
                        showError(state.message)
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.signUpButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.signUpButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (validateInput(name, email, password, confirmPassword)) {
                authViewModel.signUp(name, email, password)
            }
        }

        binding.signInTextView.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateInput(name: String, email: String, password: String, confirm: String): Boolean {
        var isValid = true

        if (name.isBlank()) {
            binding.nameInputLayout.error = getString(R.string.error_name_required)
            isValid = false
        } else {
            binding.nameInputLayout.error = null
        }

        if (email.isBlank()) {
            binding.emailInputLayout.error = getString(R.string.error_email_required)
            isValid = false
        } else {
            binding.emailInputLayout.error = null
        }

        if (password.length < 6) {
            binding.passwordInputLayout.error = getString(R.string.error_password_too_short)
            isValid = false
        } else {
            binding.passwordInputLayout.error = null
        }

        if (password != confirm) {
            binding.confirmPasswordInputLayout.error = getString(R.string.error_passwords_do_not_match)
            isValid = false
        } else {
            binding.confirmPasswordInputLayout.error = null
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
