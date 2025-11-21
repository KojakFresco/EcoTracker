package com.example.ecotracker.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.HabitItem
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.MyHabitsRecyclerViewAdapter
import com.example.ecotracker.databinding.FragmentMyHabitsBinding
import com.example.ecotracker.habitsDescriptions
import com.example.ecotracker.habitsNames
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs


class MyHabitsFragment : Fragment(), MyHabitsRecyclerViewAdapter.OnHabitStateChangedListener {
    private var _binding: FragmentMyHabitsBinding? = null
    private val binding get() = _binding!!

    private var doneHabits = 0
    private var habitsList: ArrayList<HabitItem> = ArrayList()
    private lateinit var myHabitsAdapter: MyHabitsRecyclerViewAdapter

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
    }

    override fun onCardStateChanged(habitId: String, isChecked: Boolean?) {
        //TODO: fix wrong amount of done habits (maybe cooldown)
        val ind = findHabitById(habitId)
        if (ind == -1) {
            return
        }

        if (isChecked!!) {
            doneHabits++
            for (i in habitsList.size - 1 downTo 0) {
                if (habitsList[ind].id > habitsList[i].id || !habitsList[i].isCompleted!! ||ind == i) {
                    moveHabitItem(ind, i)
                    break
                }
            }
        } else {
            doneHabits--
            for (i in 0 until habitsList.size) {
                if (habitsList[ind].id < habitsList[i].id || habitsList[i].isCompleted!! ||ind == i) {
                    moveHabitItem(ind, i)
                    break
                }
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

    private fun findHabitById(id: String): Int {
        for (i in 0 until habitsList.size) {
            val habit = habitsList[i]
            if (habit.id == id) {
                return i
            }
        }
        return -1
    }

    private fun setUpHabits() {
        if (habitsList.isEmpty()) {

            for (i in 0..2) {
                habitsList.add(HabitItem(i.toString(), habitsNames[i], habitsDescriptions[i], loadHabitById(i.toString())))
            }
        }
    }

    private fun addHabit(id: String, name: String, desc: String) {
        habitsList.add(HabitItem(id, name, desc, false))
    }

    fun loadHabitById(id : String) : Boolean? {
        try {
            val sp: SharedPreferences? = activity?.getSharedPreferences("HABITS", MODE_PRIVATE)
            return sp?.getBoolean(id, false)
        } catch (e: Exception) {
            Log.e(LOG_LABEL, "Load error " + e.message)
        }
        return false
    }
}