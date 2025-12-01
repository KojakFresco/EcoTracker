package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.presentation.ui.adapters.MyHabitsRecyclerViewAdapter
import com.example.ecotracker.databinding.FragmentMyHabitsBinding
import com.example.ecotracker.presentation.viewmodels.HabitsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
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

        myHabitsAdapter = MyHabitsRecyclerViewAdapter { habitId, isChecked ->
            // Просто передаем событие в ViewModel. ViewModel решит, что делать.
             viewModel.updateHabitState(habitId, isChecked)
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

                // АДАПТЕР САМ РАЗБЕРЕТСЯ, ЧТО ИЗМЕНИЛОСЬ!
                myHabitsAdapter.submitList(newHabitsList)

                // Обновляем счетчик
                val doneCount = newHabitsList.count { it.isCompleted }
                binding.infoLabel.text = "Сегодня: $doneCount из ${newHabitsList.size} привычек"
            }
        }

        childFragmentManager.setFragmentResultListener("requestKey", this) { requestKey, bundle ->
            val result = bundle.getBoolean("habitsUpdated")
            if (result) {
                Log.d(LOG_LABEL, "Получен сигнал от EditHabitsFragment. Обновляем список привычек...")
                viewModel.loadMyHabits()
            }
        }

        viewModel.loadMyHabits()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}