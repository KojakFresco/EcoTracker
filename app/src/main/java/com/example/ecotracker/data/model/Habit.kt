package com.example.ecotracker.data.model

data class Habit(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val baseExp: Int = 0,
    val co2Reduction: Float = 0.0f,
    val difficulty: String = "",
    val frequency: String = "",
    val isCompleted: Boolean = false
)
