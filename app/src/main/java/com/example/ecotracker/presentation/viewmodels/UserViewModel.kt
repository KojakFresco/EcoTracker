
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.model.User
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.domain.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log


@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _userState = MutableStateFlow<UserState>(UserState.Loading)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    fun initializeUser(userId: String, email: String, name: String) {
        viewModelScope.launch {
            _userState.value = UserState.Loading

            val userExists = userRepository.userExists(userId)

            if (!userExists) {
                val newUser = User(
                    id = userId,
                    name = name,
                    email = email,
                    experience = 0,
                    level = 1,
                    streak = 0,
                    selectedAvatar = 1,
                    selectedHabits = emptyList(),
                    completedHabits = emptyList()
                )

                val result = userRepository.createUser(newUser)

                when (result) {
                    is Result.Success -> {
                        _userState.value = UserState.Success(newUser)
                    }

                    is Result.Error -> {
                        _userState.value =
                            UserState.Error(result.exception.message ?: "Creation failed")
                    }
                }
            } else {
                loadUser(userId)
            }
        }
    }

    fun loadUser(userId: String) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            val result = userRepository.getUser(userId)

            when (result) {
                is Result.Success -> {
                    _userState.value = UserState.Success(result.data)
                }

                is Result.Error -> {
                    _userState.value = UserState.Error(result.exception.message ?: "Load failed")
                }
            }
        }
    }

    fun updateUserExperience(userId: String, experienceToAdd: Int) {
        viewModelScope.launch {
            val currentState = _userState.value
            if (currentState is UserState.Success) {
                val currentUser = currentState.user
                val newExperience = currentUser.experience + experienceToAdd
                val newLevel = calculateLevel(newExperience)

                val result = userRepository.updateUser(
                    userId, mapOf(
                        "experience" to newExperience,
                        "level" to newLevel,
                        "lastLogin" to FieldValue.serverTimestamp()
                    )
                )

                if (result is Result.Success) {
                    // Обновляем локальное состояние
                    _userState.value = UserState.Success(
                        currentUser.copy(
                            experience = newExperience,
                            level = newLevel
                        )
                    )
                } else if (result is Result.Error) {
                    Log.e("UserViewModel", "Failed to update experience", result.exception)
                }
            }
        }
    }

    fun updateUserAvatar(userId: String, avatarIndex: Int) {
        viewModelScope.launch {
            val result = userRepository.updateUser(
                userId, mapOf(
                    "selectedAvatar" to avatarIndex
                )
            )

            if (result is Result.Success) {
                val currentState = _userState.value
                if (currentState is UserState.Success) {
                    _userState.value = UserState.Success(
                        currentState.user.copy(selectedAvatar = avatarIndex)
                    )
                }
            }
        }
    }



    fun addCompletedHabit(userId: String, habitId: String) {
        viewModelScope.launch {
            val result = userRepository.updateUser(
                userId, mapOf(
                    "completedHabits" to FieldValue.arrayUnion(habitId)
                )
            )

            if (result is Result.Success) {
                val currentState = _userState.value
                if (currentState is UserState.Success) {
                    val updatedHabits = currentState.user.completedHabits + habitId
                    _userState.value = UserState.Success(
                        currentState.user.copy(completedHabits = updatedHabits)
                    )
                }

            }
        }
    }

    private fun calculateLevel(experience: Int): Int {
        return  (experience / 100) + 1

    }
}

sealed class UserState {
    object Loading : UserState()
    data class Success(val user : User) : UserState()
    data class Error(val message : String) : UserState()

}