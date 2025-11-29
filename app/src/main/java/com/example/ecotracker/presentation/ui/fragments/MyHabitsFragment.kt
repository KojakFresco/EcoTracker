package com.example.ecotracker.presentation.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.HabitItem
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.MyHabitsRecyclerViewAdapter
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.databinding.FragmentMyHabitsBinding
import com.example.ecotracker.habitsDescriptions
import com.example.ecotracker.habitsIDs
import com.example.ecotracker.habitsNames
import com.example.ecotracker.presentation.viewmodels.HabitsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs


@AndroidEntryPoint
class MyHabitsFragment : Fragment(), MyHabitsRecyclerViewAdapter.OnHabitStateChangedListener {
    private var _binding: FragmentMyHabitsBinding? = null
    private val binding get() = _binding!!

    private var doneHabits = 0
    private var habitsList: ArrayList<Habit> = ArrayList()
    private lateinit var myHabitsAdapter: MyHabitsRecyclerViewAdapter

    private val viewModel: HabitsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyHabitsBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.recycler
        setUpHabits()
        doneHabits = 0
        for (item in habitsList) {
            if (item.isCompleted!!) {
                doneHabits++
            }
        }
        binding.infoLabel.text = "Сегодня: $doneHabits из ${habitsList.size} привычек"

        myHabitsAdapter = MyHabitsRecyclerViewAdapter(activity, habitsList)
        myHabitsAdapter.setOnHabitStateChangedListener(this)
        recyclerView.adapter = myHabitsAdapter
        recyclerView.setLayoutManager(LinearLayoutManager(activity))

        val addButton: FloatingActionButton = binding.fabAddHabit
        addButton.setOnClickListener {
            EditHabitsFragment.newInstance().show(childFragmentManager, EditHabitsFragment.TAG)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.myHabits.collect { habitsToShow ->
                myHabitsAdapter.updateHabits(habitsToShow)
            }
        }


        childFragmentManager.setFragmentResultListener("requestKey", this) { requestKey, bundle ->

            val result = bundle.getBoolean("habitsUpdated")
            if (result) {
                Log.d(LOG_LABEL, "Получен сигнал от EditHabitsFragment. Обновляем список привычек...")
                viewModel.loadMyHabits()
                setUpHabits()
                updateDoneHabitsCount()
            }
        }
    }

    override fun onCardStateChanged(position: Int, isChecked: Boolean?) {
        //TODO: fix wrong amount of done habits (maybe cooldown)
        if (isChecked!!) {
            for (i in habitsList.size - 1 downTo 0) {
                if (habitsList[position].id > habitsList[i].id || !habitsList[i].isCompleted!! || position == i) {
                    moveHabitItem(position, i)
                    break
                }
            }
        } else {
            for (i in 0 until habitsList.size) {
                if (habitsList[position].id < habitsList[i].id || habitsList[i].isCompleted!! || position == i) {
                    moveHabitItem(position, i)
                    break
                }
            }
        }
        updateDoneHabitsCount()
    }

    private fun updateDoneHabitsCount() {
        doneHabits = 0
        for (item in habitsList) {
            if (item.isCompleted!!) {
                doneHabits++
            }
        }
        binding.infoLabel.text = "Сегодня: $doneHabits из ${habitsList.size} привычек"
    }

    fun moveHabitItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition < 0 || fromPosition >= habitsList.size || toPosition < 0 || toPosition >= habitsList.size) {
            Log.e(LOG_LABEL, "Невозможно переместить элемент: позиции вне диапазона.")
            return
        }

        val habitToMove = habitsList.removeAt(fromPosition)
        habitsList.add(toPosition, habitToMove)

        myHabitsAdapter.notifyItemMoved(fromPosition, toPosition)

        val start = minOf(fromPosition, toPosition)
        val count = abs(fromPosition - toPosition) + 1
        myHabitsAdapter.notifyItemRangeChanged(start, count)
    }

    private fun setUpHabits() {
        //TODO: add sort
        for (item in viewModel.myHabits.value) {
            if (!habitsList.contains(item) && viewModel.loadHabitState(item.id)!!) {
                habitsList.add(item)
            }
        }
    }

    fun loadHabitStateById(id : String) : Boolean? {
        try {
            val sp: SharedPreferences? = context?.getSharedPreferences("HABITS_IN_USE", MODE_PRIVATE)
            return sp?.getBoolean(id, false)
            Log.d(LOG_LABEL, "Load success, id: $id")
        } catch (e: Exception) {
            Log.e(LOG_LABEL, "Load Error " + e.message)
        }
        return false
    }

    override fun onStart() {
        setUpHabits()
        super.onStart()
    }
}