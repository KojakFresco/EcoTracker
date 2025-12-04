package com.example.ecotracker.data.repository

import com.example.ecotracker.data.model.AuthResult
import com.example.ecotracker.data.model.SignInData
import com.example.ecotracker.data.model.SignUpData
import com.example.ecotracker.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
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

        // Изменили вызов userRepository.createUser
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
        AuthResult.Error(e.message ?: "Registration failed")
    }


    suspend fun signIn(signInData: SignInData): AuthResult = try {
        val authResult = auth.signInWithEmailAndPassword(
            signInData.email,
            signInData.password
        ).await()

        authResult.user?.uid?.let { userId ->
            userRepository.updateLastLoginTimestamp()
        }

        AuthResult.Success(authResult.user)
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "Sign in failed")
    }

    suspend fun signOut(): AuthResult = try {
        auth.signOut()
        AuthResult.Success(null)
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "Sign out failed")
    }

    suspend fun resetPassword(email: String): AuthResult = try {
        auth.sendPasswordResetEmail(email).await()
        AuthResult.Success(null)
    } catch (e: Exception) {
        AuthResult.Error(e.message ?: "Password reset failed")
    }

    fun isUserAuthenticated(): Boolean = auth.currentUser != null
}