package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.model.SignInData
import com.example.ecotracker.data.model.SignUpData
import com.example.ecotracker.data.model.AuthResult
import com.example.ecotracker.data.repository.AuthRepository
import com.example.ecotracker.domain.usecase.SignInUseCase
import com.example.ecotracker.domain.usecase.SignOutUseCase
import com.example.ecotracker.domain.usecase.SignUpUseCase
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val signInUseCase: SignInUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getAuthState().collect { user ->
                _currentUser.value = user
                _authState.value = if (user != null) {
                    AuthState.Authenticated(user)
                } else {
                    AuthState.Unauthenticated
                }
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = signUpUseCase(
                SignUpData(
                    name = name,
                    email = email,
                    password = password
                )
            )

            _authState.value = when (result) {
                is AuthResult.Success -> AuthState.Authenticated(result.user)
                is AuthResult.Error -> AuthState.Error(result.errorMessage)
                else -> AuthState.Idle
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = signInUseCase(
                SignInData(
                    email = email,
                    password = password
                )
            )

            _authState.value = when (result) {
                is AuthResult.Success -> AuthState.Authenticated(result.user)
                is AuthResult.Error -> AuthState.Error(result.errorMessage)
                else -> AuthState.Idle
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = signOutUseCase()

            _authState.value = when (result) {
                is AuthResult.Success -> AuthState.Unauthenticated
                is AuthResult.Error -> AuthState.Error(result.errorMessage)
                else -> AuthState.Idle
            }
        }
    }

    fun isAuthenticated(): Boolean = authRepository.isUserAuthenticated()
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}