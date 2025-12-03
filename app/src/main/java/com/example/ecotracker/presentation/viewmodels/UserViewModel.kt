package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.model.User
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.ArrayList

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

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

    fun addExperience(points: Int) {
        val currentUser = (_userState.value as? UserState.Success)?.user ?: return

        val newExperience = currentUser.experience + points
        val newLevel = 1 + (newExperience / 100)

        val updatedUser = currentUser.copy(experience = newExperience, level = newLevel)
        updateUser(updatedUser)
    }

    fun updateUserAvatar(avatarIndex: Int) {
        val currentUser = (_userState.value as? UserState.Success)?.user ?: return
        val updatedUser = currentUser.copy(selectedAvatar = avatarIndex)
        updateUser(updatedUser)
    }

    fun addCompletedHabit(habitId: String) {
        val currentUser = (_userState.value as? UserState.Success)?.user ?: return
        if (!currentUser.completedHabits.contains(habitId)) {
            val updatedHabits = currentUser.completedHabits + habitId
            val updatedUser = currentUser.copy(completedHabits = ArrayList(updatedHabits))
            updateUser(updatedUser)
        }
    }

    fun updateUserStreak(newStreak: Int) {
        val currentUser = (_userState.value as? UserState.Success)?.user ?: return

        val newRecord = if (newStreak > currentUser.record) newStreak else currentUser.record

        val updatedUser = currentUser.copy(streak = newStreak, record = newRecord)
        updateUser(updatedUser)
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
