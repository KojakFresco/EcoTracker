package com.example.ecotracker.data.repository

import android.util.Log
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.data.model.Habit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Singleton
class HabitRepository @Inject constructor(
    private val db: FirebaseFirestore
){

    suspend fun getHabits(): List<Habit> {
        try {
            // 2. Отправляем запрос и ПРИОСТАНАВЛИВАЕМ функцию до получения результата
            val documents = db.collection("habitPatterns").get().await()

            val habits = documents.map { document ->
                document.toObject<Habit>()
            }
            Log.d(LOG_LABEL, "Успешно получено ${habits.size} привычек")
            return habits

        } catch (e: Exception) {
            Log.w(LOG_LABEL, "Ошибка при получении привычек", e)
            return emptyList()
        }
    }
    suspend fun completeHabit(habitId: String) {
    }
}