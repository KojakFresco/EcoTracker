package com.example.ecotracker.data.repository

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ecotracker.data.model.User
import com.example.ecotracker.domain.util.Result
import com.example.ecotracker.workers.SyncUserWorker
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
    private val prefsRepository: PreferencesRepository
) {

    // Изменили: теперь вызываем отдельные методы сохранения
    suspend fun createUser(userId: String, user: User): Result<Unit> = try {
        firestore.collection("users").document(userId).set(user).await()
        prefsRepository.saveCachedUserId(userId)
        prefsRepository.saveCachedUserObject(user)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    suspend fun getUser(userId: String): Result<User> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                val user = document.toObject(User::class.java)!!
                // Сохраняем и ID, и объект
                prefsRepository.saveCachedUserId(userId)
                prefsRepository.saveCachedUserObject(user)
                Result.Success(user)
            } else {
                Result.Error(Exception("User not found online"))
            }
        } catch (e: Exception) {
            Log.w("UserRepository", "Failed to fetch user online, falling back to cache.")
            // Изменили логику работы с кэшем
            val cachedUserId = prefsRepository.loadCachedUserId()
            val cachedUserObject = prefsRepository.loadCachedUserObject()
            if (cachedUserId == userId && cachedUserObject != null) {
                Result.Success(cachedUserObject)
            } else {
                Result.Error(Exception("User not found in cache.", e))
            }
        }
    }

    // Изменили: теперь метод принимает ID и User
    suspend fun updateUser(userId: String, user: User): Result<Unit> {
        // Сохраняем в кэш и ID, и объект
        prefsRepository.saveCachedUserId(userId)
        prefsRepository.saveCachedUserObject(user)
        Log.i("UserRepository", "User updated locally. Enqueuing sync job.")
        enqueueUserSync()
        return Result.Success(Unit)
    }

    suspend fun updateLastLoginTimestamp(userId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .update("lastLogin", FieldValue.serverTimestamp())
                .await()
        } catch (e: Exception) {
            Log.w("UserRepository", "Failed to update last login timestamp", e)
        }
    }

    private fun enqueueUserSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncUserWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }

    suspend fun userExists(userId: String): Boolean = try {
        firestore.collection("users").document(userId).get().await().exists()
    } catch (e: Exception) {
        // Изменили логику работы с кэшем
        prefsRepository.loadCachedUserId() == userId
    }
}