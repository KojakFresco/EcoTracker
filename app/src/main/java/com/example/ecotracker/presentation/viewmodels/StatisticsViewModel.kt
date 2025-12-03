package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ecotracker.KEY_EXPERIENCE
import com.example.ecotracker.KEY_STREAK_COUNTER
import com.example.ecotracker.KEY_STREAK_RECORD
import com.example.ecotracker.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val preferencesRepo: PreferencesRepository
): ViewModel() {

    fun getStreakCounter(): Int {
        return preferencesRepo.loadInt(KEY_STREAK_COUNTER, 0)
    }

    fun getStreakRecord(): Int {
        return preferencesRepo.loadInt(KEY_STREAK_RECORD, 0)
    }

    fun getExperience(): Int {
        return preferencesRepo.loadInt(KEY_EXPERIENCE, 0)
    }

}