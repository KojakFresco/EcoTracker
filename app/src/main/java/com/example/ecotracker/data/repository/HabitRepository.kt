package com.example.ecotracker.data.repository

import android.util.Log
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.domain.util.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val userRepository: UserRepository // Правильная зависимость
) {

    // Загружает ВСЕ привычки и отмечает, какие из них УЖЕ ДОБАВЛЕНЫ пользователем
    suspend fun getHabits(): List<Habit> {
        return try {
            val allHabitsResult = db.collection("habitPatterns").get().await()
            val allHabits = allHabitsResult.documents.map { document ->
                document.toObject(Habit::class.java)!!.copy(id = document.id)
            }

            val userResult = userRepository.getUser()
            if (userResult is Result.Success) {
                val selectedHabitIds = userResult.data.selectedHabits
                // Возвращаем все привычки, отмечая, какие из них выбраны
                allHabits.map { it.copy(isAdded = selectedHabitIds.contains(it.id)) }
            } else {
                allHabits
            }
        } catch (e: Exception) {
            Log.w("HabitRepository", "Error getting all habits", e)
            emptyList()
        }
    }

    // Загружает ТОЛЬКО ВЫБРАННЫЕ пользователем привычки
    suspend fun getMyHabits(): List<Habit> {
        return try {
            val userResult = userRepository.getUser()
            if (userResult is Result.Success) {
                val user = userResult.data
                val selectedHabitIds = user.selectedHabits
                val completedHabitIds = user.completedHabits

                if (selectedHabitIds.isEmpty()) {
                    return emptyList()
                }

                // Запрашиваем с сервера только те документы, которые нам нужны
                val habitsResult = db.collection("habitPatterns")
                    .whereIn("__name__", selectedHabitIds)
                    .get()
                    .await()

                // Превращаем документы в привычки и отмечаем выполненные
                habitsResult.documents.map { document ->
                    val habit = document.toObject(Habit::class.java)!!
                    habit.copy(
                        id = document.id,
                        isAdded = true,
                        isCompleted = completedHabitIds.contains(document.id)
                    )
                }
            } else {
                Log.w("HabitRepository", "Could not get user to load my habits")
                emptyList()
            }
        } catch (e: Exception) {
            Log.w("HabitRepository", "Error getting my habits", e)
            emptyList()
        }
    }

    // Сохраняет обновленный список ВЫБРАННЫХ привычек в профиль пользователя
    suspend fun saveSelectedHabits(selectedHabitIds: List<String>) {
        try {
            val userResult = userRepository.getUser()
            if (userResult is Result.Success) {
                val user = userResult.data
                val updatedUser = user.copy(selectedHabits = ArrayList(selectedHabitIds))
                userRepository.updateUser(updatedUser) // Обновляем весь объект User
            } else {
                Log.w("HabitRepository", "Could not get user to save habits")
            }
        } catch (e: Exception) {
            Log.w("HabitRepository", "Error saving selected habits", e)
        }
    }
}