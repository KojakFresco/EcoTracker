package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecotracker.databinding.FragmentMainBinding
import com.example.ecotracker.R

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.inner_fragment, MyHabitsFragment(), TAG_MY_HABITS)
                .commit()
        }

        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.home_page -> {
                    childFragmentManager.findFragmentByTag(TAG_MY_HABITS)
                        ?: MyHabitsFragment()
                }
                R.id.stats_page -> {
                    childFragmentManager.findFragmentByTag(TAG_STATISTICS)
                        ?: StatisticsFragment()
                }
                R.id.rating_page -> {
                    childFragmentManager.findFragmentByTag(TAG_RATING)
                        ?: RatingFragment()
                }
                R.id.profile_page -> {
                    childFragmentManager.findFragmentByTag(TAG_PROFILE)
                        ?: UserProfileFragment()
                }
                else -> return@setOnItemSelectedListener false
            }

            childFragmentManager.beginTransaction()
                .replace(R.id.inner_fragment, fragment, getTagForMenuItem(menuItem.itemId))
                .commit()

            true
        }

        return binding.root
    }

    private fun getTagForMenuItem(itemId: Int): String {
        return when (itemId) {
            R.id.home_page -> TAG_MY_HABITS
            R.id.stats_page -> TAG_STATISTICS
            R.id.rating_page -> TAG_RATING
            R.id.profile_page -> TAG_PROFILE
            else -> ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG_MY_HABITS = "my_habits"
        private const val TAG_STATISTICS = "statistics"
        private const val TAG_RATING = "rating"
        private const val TAG_PROFILE = "profile"
    }
}