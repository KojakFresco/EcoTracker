package com.example.ecotracker.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ecotracker.data.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val preferencesRepo: PreferencesRepository
) : ViewModel() {

    fun getStreakCounter(): Int {
        return preferencesRepo.loadInt("counter")
    }

    fun getStreakRecord(): Int {
        return preferencesRepo.loadInt("streak_record") // Пример
    }
}