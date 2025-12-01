package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.HABIT_DONE_PREFIX
import com.example.ecotracker.HABIT_IN_USE_PREFIX
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.data.repository.HabitRepository
import com.example.ecotracker.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val preferencesRepo: PreferencesRepository
) : ViewModel() {

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

            _myHabits.value = sortHabits(filteredHabits)
        }
    }

    fun updateHabitState(id: String, isChecked: Boolean) {
        viewModelScope.launch {
            val habitKey = HABIT_DONE_PREFIX + id
            preferencesRepo.saveBoolean(habitKey, isChecked)

            // ОБНОВЛЯЕМ список, который уже лежит в StateFlow
            val currentList = _myHabits.value
            val habitIndex = currentList.indexOfFirst { it.id == id }

            if (habitIndex != -1) {
                val updatedHabit = currentList[habitIndex].copy(isCompleted = isChecked)

                val newList = currentList.toMutableList()
                newList[habitIndex] = updatedHabit

                // СОРТИРУЕМ и ПУБЛИКУЕМ новый список в StateFlow.
                _myHabits.value = sortHabits(newList)
            }
        }
    }

    fun saveIsHabitInUse(id : String, isInUse : Boolean) {
        val habitKey = HABIT_IN_USE_PREFIX + id
        preferencesRepo.saveBoolean(habitKey, isInUse)
    }

    fun isHabitInUse(id : String) : Boolean? {
        val habitKey = HABIT_IN_USE_PREFIX + id
        return preferencesRepo.loadBoolean(habitKey)
    }

    private fun sortHabits(habits: List<Habit>): List<Habit> {
        return habits.sortedWith(
            // Сначала сортируем по статусу: невыполненные (false) идут первыми.
            compareBy<Habit> { it.isCompleted }
                // Если статусы одинаковые, сортируем по title
                .thenBy { it.title }
        )
    }
}