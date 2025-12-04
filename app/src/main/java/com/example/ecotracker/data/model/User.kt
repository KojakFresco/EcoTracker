package com.example.ecotracker.data.model

data class User(
    var name: String = "",
    var email: String = "",
    var level: Int = 1,
    var experience: Int = 0,
    var streak: Int = 0,
    var record: Int = 0,
    var selectedAvatar: Int = 0,
    var selectedHabits: ArrayList<String> = ArrayList(),
    var completedHabits: ArrayList<String> = ArrayList(),
    var co2Reduction: Float = 0.0f,
    var wasteDisposal: Float = 0.0f,
    var waterRescue: Float = 0.0f
)
