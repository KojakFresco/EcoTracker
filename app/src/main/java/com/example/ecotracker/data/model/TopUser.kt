package com.example.ecotracker.data.model

data class TopUser(
    val id: String,
    val name: String,
    val experience: Int = 0,
    val level: Int = 1,
    val selectedAvatar: Int = 0
)