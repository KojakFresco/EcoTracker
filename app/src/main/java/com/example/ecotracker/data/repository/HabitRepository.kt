package com.example.ecotracker.data.repository

import com.example.ecotracker.data.model.Habit

class HabitRepository {
    suspend fun getHabits(): List<Habit> {
        return emptyList()
    }
    suspend fun completeHabit(habitId: String) {

    }
}