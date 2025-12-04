package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecotracker.R
import com.example.ecotracker.databinding.FragmentForgotPasswordBinding
import com.example.ecotracker.presentation.viewmodels.AuthViewModel
import com.example.ecotracker.presentation.viewmodels.PasswordResetState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.passwordResetState.collect { state ->
                when (state) {
                    is PasswordResetState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.resetPasswordButton.isEnabled = false
                    }
                    is PasswordResetState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, getString(R.string.message_password_reset_sent), Snackbar.LENGTH_LONG).show()
                        findNavController().navigateUp() // Возвращаемся на экран входа
                    }
                    is PasswordResetState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.resetPasswordButton.isEnabled = true
                        showError(state.message)
                    }
                    is PasswordResetState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        binding.resetPasswordButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.resetPasswordButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            if (validateEmail(email)) {
                authViewModel.sendPasswordResetEmail(email)
            }
        }

        binding.backToSignInTextView.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = null
            true
        } else {
            binding.emailInputLayout.error = getString(R.string.error_invalid_email)
            false
        }
    }

    private fun showError(messageKey: String) {
        val resId = resources.getIdentifier(messageKey, "string", requireContext().packageName)
        val message = if (resId != 0) getString(resId) else messageKey
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        authViewModel.resetPasswordState() // Сбрасываем состояние, чтобы Snackbar не появился снова
        _binding = null
    }
}