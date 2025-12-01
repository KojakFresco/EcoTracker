package com.example.ecotracker.data.repository

import android.util.Log
import com.example.ecotracker.HABIT_DONE_PREFIX
import com.example.ecotracker.HABIT_IN_USE_PREFIX
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import jakarta.inject.Singleton
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Singleton
class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val preferencesRepo: PreferencesRepository
) {
    suspend fun getUser(): User? {
        try {
            // Отправляем запрос и ПРИОСТАНАВЛИВАЕМ функцию до получения результата
            val document = db.collection("users").document("user_test_1").get().await()
            val user = document.toObject<User>()!!.copy(id = document.id)

            Log.d(LOG_LABEL, "Успешно получен пользователь: $user")
            return user

        } catch (e: Exception) {
            Log.w(LOG_LABEL, "Ошибка при получении привычек", e)
            return null
        }

    }
}