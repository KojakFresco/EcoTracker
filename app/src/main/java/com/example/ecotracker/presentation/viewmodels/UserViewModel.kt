package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.data.model.User
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.domain.util.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.Calendar
import javax.inject.Inject

sealed class ExperienceEvent {
    data class LevelUp(val newLevel: Int) : ExperienceEvent()
    object AllHabitsDone : ExperienceEvent()
    data class StreakSaved(val newStreak: Int) : ExperienceEvent()
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _experienceEvents = MutableSharedFlow<ExperienceEvent>()
    val experienceEvents = _experienceEvents.asSharedFlow()

    val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    fun loadUser() {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            when (val result = userRepository.getUser()) {
                is Result.Success -> {
                    var user = result.data
                    var wasChanged = false

                    // Проверяем, наступил ли новый день
                    if (user.lastActiveDate != null && !isSameDay(user.lastActiveDate!!, Timestamp.now())) {

                        // Проверяем, не прервана ли серия
                        if (isStreakBroken(user.lastActiveDate!!, Timestamp.now())) {
                            user = user.copy(streak = 0)
                        }

                        // Сбрасываем выполненные привычки для нового дня
                        user = user.copy(completedHabits = ArrayList())
                        wasChanged = true
                    }

                    if (wasChanged) {
                        // Сохраняем изменения (сброс) и обновляем состояние
                        updateUser(user)
                    } else {
                        // Просто обновляем состояние без изменений
                        _userState.value = UserState.Success(user)
                    }
                }
                is Result.Error -> _userState.value = UserState.Error(result.exception.message ?: "Load failed")
            }
        }
    }

    fun completeHabit(habit: Habit) {
        val currentUser = (_userState.value as? UserState.Success)?.user ?: return
        if (currentUser.completedHabits.contains(habit.id)) return

        val wasFirstHabitOfTheDay = currentUser.completedHabits.isEmpty()

        val oldLevel = currentUser.level
        val newExperience = currentUser.experience + habit.baseExp
        val newLevel = calculateLevelForXp(newExperience)

        viewModelScope.launch {
            if (newLevel > oldLevel) {
                _experienceEvents.emit(ExperienceEvent.LevelUp(newLevel))
            }
        }

        val newStreak = if (wasFirstHabitOfTheDay) currentUser.streak + 1 else currentUser.streak
        if (wasFirstHabitOfTheDay && newStreak > 0) {
            viewModelScope.launch {
                _experienceEvents.emit(ExperienceEvent.StreakSaved(newStreak))
            }
        }
        val newRecord = if (newStreak > currentUser.record) newStreak else currentUser.record

        val updatedCompletedHabits = currentUser.completedHabits + habit.id

        val updatedUser = currentUser.copy(
            experience = newExperience,
            level = newLevel,
            completedHabits = ArrayList(updatedCompletedHabits),
            streak = newStreak,
            record = newRecord,
            co2Reduction = currentUser.co2Reduction + habit.co2Reduction,
            wasteDisposal = currentUser.wasteDisposal + habit.wasteDisposal,
            waterRescue = currentUser.waterRescue + habit.waterRescue,
            lastActiveDate = Timestamp.now() // Обновляем дату последней активности
        )

        updateUser(updatedUser)
        checkAllHabitsDone(updatedUser)
    }

    fun updateUserProfile(newName: String, newAvatarId: Int) {
        val currentUser = (_userState.value as? UserState.Success)?.user ?: return
        val updatedUser = currentUser.copy(name = newName, selectedAvatar = newAvatarId)
        updateUser(updatedUser)
    }

    fun updateSelectedHabits(habitIds: List<String>) {
        val currentUser = (_userState.value as? UserState.Success)?.user ?: return
        val updatedUser = currentUser.copy(selectedHabits = ArrayList(habitIds))
        updateUser(updatedUser)
    }

    private fun isSameDay(date1: Timestamp, date2: Timestamp): Boolean {
        val cal1 = Calendar.getInstance()
        cal1.time = date1.toDate()
        val cal2 = Calendar.getInstance()
        cal2.time = date2.toDate()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isStreakBroken(lastDate: Timestamp, currentDate: Timestamp): Boolean {
        val lastCal = Calendar.getInstance()
        lastCal.time = lastDate.toDate()
        lastCal.add(Calendar.DAY_OF_YEAR, 1)

        val currentCal = Calendar.getInstance()
        currentCal.time = currentDate.toDate()

        // Если "сегодня" не является "следующим днем" после последнего, серия прервана
        return !(lastCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                lastCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR))
    }

    private fun checkAllHabitsDone(user: User) {
        if (user.selectedHabits.isNotEmpty() && user.completedHabits.containsAll(user.selectedHabits)) {
            viewModelScope.launch {
                _experienceEvents.emit(ExperienceEvent.AllHabitsDone)
            }
        }
    }

    private fun calculateLevelForXp(experience: Int): Int {
        var level = 1
        var totalXpRequired = 0
        while (true) {
            val xpForThisLevel = 100 + (level - 1) * 20
            if (experience < totalXpRequired + xpForThisLevel) {
                return level
            }
            totalXpRequired += xpForThisLevel
            level++
        }
    }

    private fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            _userState.value = UserState.Success(updatedUser)
            userRepository.updateUser(updatedUser)
        }
    }
}

sealed class UserState {
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}
