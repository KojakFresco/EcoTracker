package com.example.ecotracker.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.data.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("HabitsViewModel", "Coroutine Exception: ", throwable)
    }

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    private val _myHabits = MutableStateFlow<List<Habit>>(emptyList())
    val myHabits = _myHabits.asStateFlow()

    fun loadHabits() {
        viewModelScope.launch(coroutineExceptionHandler) {
            _habits.value = sortForEdit(habitRepository.getHabits())
        }
    }

    fun toggleHabitSelection(habitId: String) {
        val currentList = _habits.value
        val habitIndex = currentList.indexOfFirst { it.id == habitId }

        if (habitIndex != -1) {
            val habitToUpdate = currentList[habitIndex]
            val updatedHabit = habitToUpdate.copy(isAdded = !habitToUpdate.isAdded)

            val newList = currentList.toMutableList()
            newList[habitIndex] = updatedHabit
            _habits.value = sortForEdit(newList)
        }
    }

    fun loadMyHabits() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val allHabitsFromDb = habitRepository.getMyHabits()
            _myHabits.value = sortMyHabits(allHabitsFromDb)
        }
    }

    fun updateHabitState(id: String, isChecked: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            val currentList = _myHabits.value
            val habitIndex = currentList.indexOfFirst { it.id == id }

            if (habitIndex != -1) {
                val updatedHabit = currentList[habitIndex].copy(isCompleted = isChecked)
                val newList = currentList.toMutableList()
                newList[habitIndex] = updatedHabit
                _myHabits.value = sortMyHabits(newList)
            }
        }
    }

    fun saveSelectedHabits(selectedHabitIds: List<String>) {
        viewModelScope.launch(coroutineExceptionHandler) {
            habitRepository.saveSelectedHabits(selectedHabitIds)
        }
    }

    private fun sortMyHabits(habits: List<Habit>): List<Habit> {
        return habits.sortedWith(
            compareBy<Habit> { it.isCompleted }
                .thenBy { it.title }
        )
    }

    private fun sortForEdit(habits: List<Habit>): List<Habit> {
        return habits.sortedByDescending { it.isAdded }
    }
}