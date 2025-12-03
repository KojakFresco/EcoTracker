package com.example.ecotracker.data.model

import com.google.firebase.auth.FirebaseUser
sealed class AuthResult {
    data class Success(val user: FirebaseUser?) : AuthResult()
    data class Error(val errorMessage: String) : AuthResult()
    object Loading : AuthResult()
}