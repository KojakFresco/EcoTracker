package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.data.repository.HabitRepository
import com.example.ecotracker.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val preferencesRepo: PreferencesRepository
) : ViewModel() {

    private val HABIT_IN_USE_PREFIX = "is_habit_in_use_"

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    private val _myHabits = MutableStateFlow<List<Habit>>(emptyList())
    val myHabits: StateFlow<List<Habit>> = _myHabits.asStateFlow()

    fun loadHabits() {
        viewModelScope.launch {
            _habits.value = habitRepository.getHabits()
        }
    }

    fun loadMyHabits() {
        viewModelScope.launch {
            val allHabitsFromDb = habitRepository.getHabits()

            val filteredHabits = allHabitsFromDb.filter { habit ->
                preferencesRepo.loadBoolean(HABIT_IN_USE_PREFIX + habit.id)
            }

            _myHabits.value = filteredHabits
        }
    }

    fun saveHabitState(id : String, isInUse : Boolean) {
        val habitKey = HABIT_IN_USE_PREFIX + id
        preferencesRepo.saveBoolean(habitKey, isInUse)
    }

    fun loadHabitState(id : String) : Boolean? {
        val habitKey = HABIT_IN_USE_PREFIX + id
        return preferencesRepo.loadBoolean(habitKey)
    }
}