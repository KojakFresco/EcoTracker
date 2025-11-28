package com.example.ecotracker.data.model

data class User(
    val id: String,
    val name: String,
    val level: Int = 1,
    val exp: Int = 0
)