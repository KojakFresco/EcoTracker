package com.example.ecotracker.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ecotracker.data.repository.HabitRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ResetHabitsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val habitRepository: HabitRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            habitRepository.resetAllHabits()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
