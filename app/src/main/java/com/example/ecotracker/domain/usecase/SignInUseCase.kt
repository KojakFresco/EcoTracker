package com.example.ecotracker.domain.usecase

import com.example.ecotracker.data.model.AuthResult
import com.example.ecotracker.data.model.SignInData
import com.example.ecotracker.data.repository.AuthRepository
import javax.inject.Inject


class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(signInData: SignInData): AuthResult {
        if (signInData.email.isBlank() || signInData.password.isBlank()) {
            return AuthResult.Error("Email and password are required")
        }

        return authRepository.signIn(signInData)
    }
}