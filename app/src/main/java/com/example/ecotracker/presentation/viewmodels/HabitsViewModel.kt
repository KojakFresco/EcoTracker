package com.example.ecotracker.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.HABIT_DONE_PREFIX
import com.example.ecotracker.HABIT_IN_USE_PREFIX
import com.example.ecotracker.KEY_LAST_STREAK_TIME
import com.example.ecotracker.KEY_STREAK_COUNTER
import com.example.ecotracker.KEY_STREAK_RECORD
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.data.repository.HabitRepository
import com.example.ecotracker.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val preferencesRepo: PreferencesRepository
) : ViewModel() {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("HabitsViewModel", "Coroutine Exception: ", throwable)
    }

    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits = _habits.asStateFlow()

    private val _myHabits = MutableStateFlow<List<Habit>>(emptyList())
    val myHabits = _myHabits.asStateFlow()

    private val _showStreakToast = MutableSharedFlow<Unit>()
    val showStreakToast = _showStreakToast.asSharedFlow()

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
            val allHabitsFromDb = habitRepository.getHabits()

            val filteredHabits = allHabitsFromDb.filter { habit ->
                preferencesRepo.loadBoolean(HABIT_IN_USE_PREFIX + habit.id, false)
            }

            _myHabits.value = sortMyHabits(filteredHabits)
        }
    }

    fun updateHabitState(id: String, isChecked: Boolean) {
        viewModelScope.launch(coroutineExceptionHandler) {
            if (isChecked) {
                handleStreak()
            }

            val habitKey = HABIT_DONE_PREFIX + id
            preferencesRepo.saveBoolean(habitKey, isChecked)

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

    private fun handleStreak() {
        val currentTime = ZonedDateTime.now(ZoneId.systemDefault())
        val lastRewardTimeMillis = preferencesRepo.loadLong(KEY_LAST_STREAK_TIME, 0L)

        if (lastRewardTimeMillis == 0L || !isSameDay(currentTime, lastRewardTimeMillis)) {
            val lastRewardTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastRewardTimeMillis), ZoneId.systemDefault())
            var counter = preferencesRepo.loadInt(KEY_STREAK_COUNTER, 0)

            if (lastRewardTimeMillis != 0L && ChronoUnit.DAYS.between(lastRewardTime.toLocalDate(), currentTime.toLocalDate()) == 1L) {
                counter++
            } else {
                counter = 1
            }

            val currentRecord = preferencesRepo.loadInt(KEY_STREAK_RECORD, 0)
            if (counter > currentRecord) {
                preferencesRepo.saveInt(KEY_STREAK_RECORD, counter)
            }

            preferencesRepo.saveInt(KEY_STREAK_COUNTER, counter)
            preferencesRepo.saveLong(KEY_LAST_STREAK_TIME, currentTime.toInstant().toEpochMilli())

            viewModelScope.launch(coroutineExceptionHandler) {
                _showStreakToast.emit(Unit)
            }
        }
    }

    private fun isSameDay(time1: ZonedDateTime, time2Millis: Long): Boolean {
        if (time2Millis == 0L) return false
        val time2 = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time2Millis), ZoneId.systemDefault())
        return time1.toLocalDate().isEqual(time2.toLocalDate())
    }

    fun saveIsHabitInUse(id : String, isInUse : Boolean) {
        val habitKey = HABIT_IN_USE_PREFIX + id
        preferencesRepo.saveBoolean(habitKey, isInUse)
    }

    fun isHabitInUse(id : String) : Boolean {
        val habitKey = HABIT_IN_USE_PREFIX + id
        return preferencesRepo.loadBoolean(habitKey, false)
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