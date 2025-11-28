package com.example.ecotracker.domain.managers

import androidx.fragment.app.FragmentManager
import com.example.ecotracker.R
import com.example.ecotracker.presentation.ui.fragments.MainFragment

class NavigationManager {

    fun navigateToMainFragment(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, MainFragment(), "main")
            .commit()
    }

    fun navigateToHabitsFragment(fragmentManager: FragmentManager) {

    }
}