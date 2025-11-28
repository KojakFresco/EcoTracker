package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HabitsViewModel : ViewModel() {
    private val repository = HabitRepository()

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()

    fun loadHabits() {
        viewModelScope.launch {
            _habits.value = repository.getHabits()
        }
    }
}