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
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.databinding.FragmentMyHabitsBinding
import com.example.ecotracker.presentation.ui.adapters.MyHabitsRecyclerViewAdapter
import com.example.ecotracker.presentation.viewmodels.HabitsViewModel
import com.example.ecotracker.workers.ResetHabitsWorker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
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

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
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

        // --- ВРЕМЕННЫЙ КОД ДЛЯ ТЕСТИРОВАНИЯ --- //
        // TODO: Удалите этот блок после проверки
        binding.infoLabel.setOnClickListener {
            Toast.makeText(requireContext(), "Принудительный сброс привычек...", Toast.LENGTH_SHORT).show()
            val workManager = WorkManager.getInstance(requireContext())
            val oneTimeResetRequest = OneTimeWorkRequestBuilder<ResetHabitsWorker>().build()

            workManager.enqueueUniqueWork("oneTimeReset", ExistingWorkPolicy.REPLACE, oneTimeResetRequest)

            viewLifecycleOwner.lifecycleScope.launch {
                val workInfo = workManager.getWorkInfoByIdFlow(oneTimeResetRequest.id).first { it.state.isFinished }
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Log.d(LOG_LABEL, "Воркер успешно завершил работу. Обновляем UI.")
                    viewModel.loadMyHabits()
                } else {
                    Log.d(LOG_LABEL, "Воркер завершился с ошибкой или был отменен.")
                    Toast.makeText(requireContext(), "Не удалось сбросить привычки", Toast.LENGTH_SHORT).show()
                }
            }
        }
        // --- КОНЕЦ ВРЕМЕННОГО КОДА --- //
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myHabits.collect { newHabitsList ->
                Log.d(LOG_LABEL, "Получено обновление. Новый список: ${newHabitsList.size} элементов.")
                myHabitsAdapter.submitList(newHabitsList)
                val doneCount = newHabitsList.count { it.isCompleted }
                binding.infoLabel.text = "Сегодня: $doneCount из ${newHabitsList.size} привычек"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showStreakToast.collectLatest {
                Toast.makeText(requireContext(), "Вы молодец!", Toast.LENGTH_SHORT).show()
            }
        }

        childFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            if (bundle.getBoolean("habitsUpdated")) {
                viewModel.loadMyHabits()
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
                    viewModel.updateHabitState(habit.id, true)
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadMyHabits()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}