package com.example.ecotracker.data.repository

import android.util.Log
import com.example.ecotracker.HABIT_DONE_PREFIX
import com.example.ecotracker.HABIT_IN_USE_PREFIX
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.data.model.Habit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Singleton
class HabitRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val preferencesRepo: PreferencesRepository
){

    suspend fun getHabits(): List<Habit> {
        try {
            // Отправляем запрос и ПРИОСТАНАВЛИВАЕМ функцию до получения результата
            val documents = db.collection("habitPatterns").get().await()

            val habits = documents.map { document ->
                val habitFromDb = document.toObject<Habit>()

                val isHabitAdded = preferencesRepo.loadBoolean(HABIT_IN_USE_PREFIX + document.id)
                val isHabitDone = preferencesRepo.loadBoolean(HABIT_DONE_PREFIX + document.id)

                habitFromDb.copy(id = document.id, isAdded = isHabitAdded, isCompleted = isHabitDone)
            }
            Log.d(LOG_LABEL, "Успешно получено ${habits.size} привычек")
            return habits

        } catch (e: Exception) {
            Log.w(LOG_LABEL, "Ошибка при получении привычек", e)
            return emptyList()
        }
    }

    suspend fun resetAllHabits() {
        try {
            val documents = db.collection("habitPatterns").get().await()
            for (document in documents) {
                preferencesRepo.saveBoolean(HABIT_DONE_PREFIX + document.id, false)
            }
            Log.d(LOG_LABEL, "Все привычки были сброшены")
        } catch (e: Exception) {
            Log.w(LOG_LABEL, "Ошибка при сбросе привычек", e)
        }
    }
}