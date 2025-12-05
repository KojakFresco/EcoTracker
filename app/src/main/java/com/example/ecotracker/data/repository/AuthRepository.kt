package com.example.ecotracker.data.repository

import com.example.ecotracker.data.model.AuthResult
import com.example.ecotracker.data.model.SignInData
import com.example.ecotracker.data.model.SignUpData
import com.example.ecotracker.data.model.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val prefsRepository: PreferencesRepository
) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    fun getAuthState() = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    suspend fun signUp(signUpData: SignUpData): AuthResult = try {
        val authResult = auth.createUserWithEmailAndPassword(
            signUpData.email,
            signUpData.password
        ).await()

        val userId = authResult.user?.uid ?: throw Exception("User ID is null")

        userRepository.createUser(
            User(
                name = signUpData.name,
                email = signUpData.email,
                experience = 0,
                level = 1,
                streak = 0,
                selectedAvatar = 1,
                selectedHabits = ArrayList(),
                completedHabits = ArrayList()
            )
        )

        AuthResult.Success(authResult.user)
    } catch (e: Exception) {
        val errorMessageKey = when (e) {
            is FirebaseAuthWeakPasswordException -> "error_weak_password"
            is FirebaseAuthUserCollisionException -> "error_email_already_in_use"
            is FirebaseAuthInvalidCredentialsException -> "error_invalid_email"
            is FirebaseNetworkException -> "error_network_request_failed"
            else -> "error_unknown"
        }
        AuthResult.Error(errorMessageKey)
    }

    suspend fun signIn(signInData: SignInData): AuthResult = try {
        val authResult = auth.signInWithEmailAndPassword(
            signInData.email,
            signInData.password
        ).await()

        authResult.user?.uid?.let { userRepository.updateLastLoginTimestamp() }

        AuthResult.Success(authResult.user)
    } catch (e: Exception) {
        val errorMessageKey = when (e) {
            is FirebaseAuthInvalidCredentialsException -> "error_invalid_credentials"
            is FirebaseNetworkException -> "error_network_request_failed"
            else -> "error_unknown"
        }
        AuthResult.Error(errorMessageKey)
    }

    suspend fun signOut(): AuthResult = try {
        auth.signOut()
        prefsRepository.clearUserCache()
        AuthResult.Success(null)
    } catch (e: Exception) {
        AuthResult.Error("error_unknown")
    }

    suspend fun resetPassword(email: String): AuthResult = try {
        auth.sendPasswordResetEmail(email).await()
        AuthResult.Success(null)
    } catch (e: Exception) {
        val errorMessageKey = when (e) {
            is FirebaseAuthInvalidCredentialsException -> "error_user_not_found"
            is FirebaseNetworkException -> "error_network_request_failed"
            else -> "error_unknown"
        }
        AuthResult.Error(errorMessageKey)
    }

    fun isUserAuthenticated(): Boolean = auth.currentUser != null
}