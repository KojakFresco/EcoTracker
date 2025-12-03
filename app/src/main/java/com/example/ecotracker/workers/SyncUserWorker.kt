package com.example.ecotracker.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ecotracker.data.repository.PreferencesRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class SyncUserWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore,
    private val prefsRepository: PreferencesRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Изменили логику: теперь загружаем ID и объект отдельно
        val userIdToSync = prefsRepository.loadCachedUserId()
        val userToSync = prefsRepository.loadCachedUserObject()

        if (userIdToSync == null || userToSync == null) {
            Log.e("SyncUserWorker", "No user ID or user object found in cache to sync. Stopping.")
            return Result.failure()
        }

        return try {
            Log.i("SyncUserWorker", "Starting user sync for $userIdToSync")
            firestore.collection("users")
                .document(userIdToSync)
                .set(userToSync) // Перезаписываем весь объект целиком
                .await()

            Log.i("SyncUserWorker", "Sync successful!")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncUserWorker", "Sync failed. Will retry later.", e)
            Result.retry()
        }
    }
}