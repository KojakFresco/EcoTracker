package com.example.ecotracker.domain.usecase

import android.util.Patterns
import com.example.ecotracker.data.model.AuthResult
import com.example.ecotracker.data.model.SignUpData
import com.example.ecotracker.data.repository.AuthRepository
import javax.inject.Inject


class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(signUpData: SignUpData): AuthResult {
        if (!isValidEmail(signUpData.email)) {
            return AuthResult.Error("Invalid email format")
        }

        if (signUpData.password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters")
        }

        return authRepository.signUp(signUpData)
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}