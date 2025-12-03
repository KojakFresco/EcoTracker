package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.model.User
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.ArrayList

sealed class ExperienceEvent {
    data class LevelUp(val newLevel: Int) : ExperienceEvent()
    object AllHabitsDone : ExperienceEvent()
    data class StreakSaved(val newStreak: Int) : ExperienceEvent()
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _experienceEvents = MutableSharedFlow<ExperienceEvent>()
    val experienceEvents = _experienceEvents.asSharedFlow()

    private var currentUserId: String? = null

    fun loadUser(userId: String) {
        if (_userState.value is UserState.Loading && this.currentUserId == userId) return

        this.currentUserId = userId

        viewModelScope.launch {
            _userState.value = UserState.Loading
            when (val result = userRepository.getUser(userId)) {
                is Result.Success -> _userState.value = UserState.Success(result.data)
                is Result.Error -> _userState.value = UserState.Error(result.exception.message ?: "Load failed")
            }
        }
    }

    fun completeHabit(habitId: String, habitBaseExp: Int) {
        val currentUser = (_userState.value as? UserState.Success)?.user ?: return
        if (currentUser.completedHabits.contains(habitId)) return

        val wasFirstHabitOfTheDay = currentUser.completedHabits.isEmpty()

        // Add experience and calculate new level with progressive difficulty
        val oldLevel = currentUser.level
        val newExperience = currentUser.experience + habitBaseExp
        val newLevel = calculateLevelForXp(newExperience)

        viewModelScope.launch {
            if (newLevel > oldLevel) {
                _experienceEvents.emit(ExperienceEvent.LevelUp(newLevel))
            }
        }

        // Update streak if it was the first habit
        val newStreak = if (wasFirstHabitOfTheDay) currentUser.streak + 1 else currentUser.streak
        if (wasFirstHabitOfTheDay && newStreak > 0) {
            viewModelScope.launch {
                _experienceEvents.emit(ExperienceEvent.StreakSaved(newStreak))
            }
        }
        val newRecord = if (newStreak > currentUser.record) newStreak else currentUser.record

        // Add to completed list
        val updatedCompletedHabits = currentUser.completedHabits + habitId

        val updatedUser = currentUser.copy(
            experience = newExperience,
            level = newLevel,
            completedHabits = ArrayList(updatedCompletedHabits),
            streak = newStreak,
            record = newRecord
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
        val userId = currentUserId ?: return

        viewModelScope.launch {
            _userState.value = UserState.Success(updatedUser)
            userRepository.updateUser(userId, updatedUser)
        }
    }
}

sealed class UserState {
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val message: String) : UserState()
}
