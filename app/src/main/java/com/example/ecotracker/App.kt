package com.example.ecotracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ecotracker.workers.ResetHabitsWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        setupDailyReset()
    }

    override val workManagerConfiguration get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun setupDailyReset() {
        val workManager = WorkManager.getInstance(this)

        val resetRequest = PeriodicWorkRequestBuilder<ResetHabitsWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "daily_habit_reset",
            ExistingPeriodicWorkPolicy.KEEP,
            resetRequest
        )
    }

    private fun calculateInitialDelay(): Long {
        val currentTime = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1)
        }
        return midnight.timeInMillis - currentTime.timeInMillis
    }
}