package com.example.ecotracker.data.model

data class Habit(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val baseExp: Int,
    val co2Reduction: Float,
    val difficulty: String,
    val frequency: String,
    val isCompleted: Boolean = false
)