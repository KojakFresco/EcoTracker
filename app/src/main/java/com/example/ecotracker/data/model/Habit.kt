package com.example.ecotracker.data.model

data class Habit(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val icon: String = "",
    val baseExp: Int = 0,
    val co2Reduction: Float = 0.0f,
    val difficulty: String = "",
    val frequency: String = "",
    var isCompleted: Boolean = false,
    var isAdded: Boolean = false
)
