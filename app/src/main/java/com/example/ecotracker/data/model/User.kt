package com.example.ecotracker.data.model

data class User(
    val id: String = "",
    var name: String = "",
    var level: Int = 1,
    var experience: Int = 0,
    var record: Int = 0,
    var selectedAvatar: Int = 0,
    var selectedHabits: ArrayList<String> = ArrayList(),
    var completedHabits: ArrayList<String> = ArrayList()
)