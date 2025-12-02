package com.example.ecotracker.data.repository

import com.example.ecotracker.data.model.User
import com.example.ecotracker.domain.util.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun createUser(user: User): Result<Unit> = try {
        firestore.collection("users")
            .document(user.id)
            .set(user)
            .await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun getUser(userId: String): Result<User> = try {
        val document = firestore.collection("users")
            .document(userId)
            .get()
            .await()

        if (document.exists()) {
            val user = document.toObject(User::class.java)
                ?: throw Exception("User data is null")
            Result.Success(user)
        } else {
            Result.Error(Exception("User not found"))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }


    suspend fun updateUser(userId: String, updates: Map<String, Any>): Result<Unit> = try {
        firestore.collection("users")
            .document(userId)
            .update(updates)
            .await()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    // ПРОВЕРКА существования пользователя
    suspend fun userExists(userId: String): Boolean = try {
        val document = firestore.collection("users")
            .document(userId)
            .get()
            .await()
        document.exists()
    } catch (e: Exception) {
        false
    }
}