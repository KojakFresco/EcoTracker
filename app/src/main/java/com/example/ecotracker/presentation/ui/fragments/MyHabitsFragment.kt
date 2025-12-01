package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.databinding.FragmentMyHabitsBinding
import com.example.ecotracker.presentation.ui.adapters.MyHabitsRecyclerViewAdapter
import com.example.ecotracker.presentation.viewmodels.HabitsViewModel
import com.example.ecotracker.workers.ResetHabitsWorker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyHabitsFragment : Fragment() {
    private var _binding: FragmentMyHabitsBinding? = null
    private val binding get() = _binding!!

    private lateinit var myHabitsAdapter: MyHabitsRecyclerViewAdapter
    private val viewModel: HabitsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyHabitsBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myHabitsAdapter = MyHabitsRecyclerViewAdapter { habit ->
            showConfirmationDialog(habit)
        }
        binding.recycler.adapter = myHabitsAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireActivity())

        val addButton: FloatingActionButton = binding.fabAddHabit
        addButton.setOnClickListener {
            EditHabitsFragment.newInstance().show(childFragmentManager, EditHabitsFragment.TAG)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myHabits.collect { newHabitsList ->
                Log.d(LOG_LABEL, "Получено обновление. Новый список: ${newHabitsList.size} элементов.")
                myHabitsAdapter.submitList(newHabitsList)
                val doneCount = newHabitsList.count { it.isCompleted }
                binding.infoLabel.text = "Сегодня: $doneCount из ${newHabitsList.size} привычек"
            }
        }

        childFragmentManager.setFragmentResultListener("requestKey", this) { requestKey, bundle ->
            val result = bundle.getBoolean("habitsUpdated")
            if (result) {
                viewModel.loadMyHabits()
            }
        }

        // --- ВРЕМЕННЫЙ КОД ДЛЯ ТЕСТИРОВАНИЯ --- //
        // TODO: Удалите этот блок после проверки
        binding.infoLabel.setOnClickListener {
            Toast.makeText(requireContext(), "Принудительный сброс привычек...", Toast.LENGTH_SHORT).show()
            val workManager = WorkManager.getInstance(requireContext())
            val oneTimeResetRequest = OneTimeWorkRequestBuilder<ResetHabitsWorker>().build()
            workManager.enqueueUniqueWork("oneTimeReset", ExistingWorkPolicy.REPLACE, oneTimeResetRequest)

            // Обновляем UI с небольшой задержкой, чтобы воркер успел отработать
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000)
                viewModel.loadMyHabits()
            }
        }
        // --- КОНЕЦ ВРЕМЕННОГО КОДА --- //

        viewModel.loadMyHabits()
    }

    private fun showConfirmationDialog(habit: Habit) {
        if (!habit.isCompleted) {
            val dialogMessage = "Отметить привычку '${habit.title}' как выполненную?"
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Подтверждение")
                .setMessage(dialogMessage)
                .setNegativeButton("Отмена", null)
                .setPositiveButton("Да") { dialog, which ->
                    viewModel.updateHabitState(habit.id, true)
                }
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}