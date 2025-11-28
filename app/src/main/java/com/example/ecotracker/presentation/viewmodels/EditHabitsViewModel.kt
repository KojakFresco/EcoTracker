package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ecotracker.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditHabitsViewModel @Inject constructor(
    private val preferencesRepo: PreferencesRepository
) : ViewModel() {

    private val HABIT_IN_USE_PREFIX = "is_habit_in_use_"

    fun saveHabitState(id : String, isInUse : Boolean) {
        val habitKey = HABIT_IN_USE_PREFIX + id
        preferencesRepo.saveBoolean(habitKey, isInUse)
    }

    fun loadHabitState(id : String) : Boolean? {
        val habitKey = HABIT_IN_USE_PREFIX + id
        return preferencesRepo.loadBoolean(habitKey)
    }
}