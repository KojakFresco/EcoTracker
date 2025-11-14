package com.example.ecotracker.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.HabitItem
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.MyHabitsRecyclerViewAdapter
import com.example.ecotracker.R
import com.example.ecotracker.databinding.FragmentMyHabitsBinding
import com.example.ecotracker.habitsDescriptions
import com.example.ecotracker.habitsNames
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MyHabitsFragment : Fragment() {

    private var columnCount = 1
    var habitsList: ArrayList<HabitItem> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMyHabitsBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.recycler
        setUpHabits()

        recyclerView.setAdapter(MyHabitsRecyclerViewAdapter(activity, habitsList))
        recyclerView.setLayoutManager(LinearLayoutManager(activity))

        val addButton: FloatingActionButton = binding.fabAddHabit
        addButton.setOnClickListener {
            EditHabitsFragment.newInstance().show(childFragmentManager, EditHabitsFragment.TAG)
        }

        return binding.root
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

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MyHabitsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
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

    override fun onDestroy() {
        Log.d(LOG_LABEL, "my habits on destroy")
        super.onDestroy()
    }
}