package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecotracker.R
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.databinding.FragmentMyHabitsBinding
import com.example.ecotracker.presentation.ui.adapters.MyHabitsRecyclerViewAdapter
import com.example.ecotracker.presentation.viewmodels.ExperienceEvent
import com.example.ecotracker.presentation.viewmodels.HabitsViewModel
import com.example.ecotracker.presentation.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyHabitsFragment : Fragment() {
    private var _binding: FragmentMyHabitsBinding? = null
    private val binding get() = _binding!!

    private lateinit var myHabitsAdapter: MyHabitsRecyclerViewAdapter
    private val habitsViewModel: HabitsViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModels()
    }

    private fun setupRecyclerView() {
        myHabitsAdapter = MyHabitsRecyclerViewAdapter { habit ->
            showConfirmationDialog(habit)
        }
        binding.recycler.adapter = myHabitsAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun setupClickListeners() {
        binding.fabAddHabit.setOnClickListener {
            EditHabitsFragment.newInstance().show(childFragmentManager, EditHabitsFragment.TAG)
        }
    }

    private fun observeViewModels() {
        viewLifecycleOwner.lifecycleScope.launch {
            habitsViewModel.myHabits.collect { newHabitsList ->
                myHabitsAdapter.submitList(newHabitsList)
                val doneCount = newHabitsList.count { it.isCompleted }
                binding.infoLabel.text = "Сегодня: $doneCount из ${newHabitsList.size} привычек"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.experienceEvents.collect {
                when (it) {
                    is ExperienceEvent.LevelUp -> showLevelUpDialog(it.newLevel)
                    is ExperienceEvent.AllHabitsDone -> showAllHabitsDoneSnackbar()
                    is ExperienceEvent.StreakSaved -> showStreakSavedSnackbar(it.newStreak)
                }
            }
        }

        childFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            if (bundle.getBoolean("habitsUpdated")) {
                habitsViewModel.loadMyHabits()
            }
        }
    }

    private fun showConfirmationDialog(habit: Habit) {
        if (!habit.isCompleted) {
            val dialogMessage = "Отметить привычку '${habit.title}' как выполненную?"
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Подтверждение")
                .setMessage(dialogMessage)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Да") { _, _ ->
                    habitsViewModel.updateHabitState(habit.id, true)
                    userViewModel.completeHabit(habit.id, habit.baseExp)
                }
                .show()
        }
    }

    private fun showLevelUpDialog(newLevel: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Новый уровень!")
            .setMessage("Поздравляем! Вы достигли ${newLevel}-го уровня.")
            .setPositiveButton("Отлично!", null)
            .show()
    }

    private fun showAllHabitsDoneSnackbar() {
        Snackbar.make(binding.root, "Отличная работа! Все привычки на сегодня выполнены!", Snackbar.LENGTH_LONG).show()
    }

    private fun showStreakSavedSnackbar(newStreak: Int) {
        val message = "Ваша серия из $newStreak дней сохранена! Так держать!"
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        habitsViewModel.loadMyHabits()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}