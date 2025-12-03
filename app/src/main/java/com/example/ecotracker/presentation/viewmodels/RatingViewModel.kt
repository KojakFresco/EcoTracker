package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecotracker.data.repository.PublicUser
import com.example.ecotracker.data.repository.UserRepository
import com.example.ecotracker.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RatingState {
    object Loading : RatingState()
    data class Success(val users: List<PublicUser>) : RatingState()
    data class Error(val message: String) : RatingState()
}

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _ratingState = MutableStateFlow<RatingState>(RatingState.Loading)
    val ratingState: StateFlow<RatingState> = _ratingState.asStateFlow()

    fun loadLeaderboard() {
        viewModelScope.launch {
            _ratingState.value = RatingState.Loading
            when (val result = userRepository.getLeaderboard()) {
                is Result.Success -> {
                    _ratingState.value = RatingState.Success(result.data)
                }
                is Result.Error -> {
                    _ratingState.value = RatingState.Error(result.exception.message ?: "Failed to load leaderboard")
                }
            }
        }
    }
}