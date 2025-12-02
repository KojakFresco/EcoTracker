package com.example.ecotracker.domain.usecase

import com.example.ecotracker.data.model.AuthResult
import com.example.ecotracker.data.repository.AuthRepository
import javax.inject.Inject


class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): AuthResult {
        return authRepository.signOut()
    }
}