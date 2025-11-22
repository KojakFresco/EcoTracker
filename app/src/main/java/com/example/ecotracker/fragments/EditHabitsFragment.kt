package com.example.ecotracker.fragments

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.HabitItem
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.adapters.EditHabitsRecyclerViewAdapter
import com.example.ecotracker.adapters.NewHabitItem
import com.example.ecotracker.databinding.FragmentEditHabitsBinding
import com.example.ecotracker.habitsDescriptions
import com.example.ecotracker.habitsIDs
import com.example.ecotracker.habitsNames
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditHabitsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditHabitsBinding? = null
    private val binding get() = _binding!!

    private var habitsList: ArrayList<NewHabitItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditHabitsBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.habitsRecycler
        setUpHabitsList()

        val adapter = EditHabitsRecyclerViewAdapter(activity, habitsList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget
            .LinearLayoutManager(activity)

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            for (item in habitsList) {
                saveHabitStateById(item.id, item.isAdded!!)
            }
            val result = Bundle().apply {
                putBoolean("habitsUpdated", true) // Передаем флаг, что данные обновлены
            }
            parentFragmentManager.setFragmentResult("requestKey", result)

            dismiss()
        }



    }

    // Этот метод делает окно полноэкранным
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?

            bottomSheet?.let {
                val layoutParams = it.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.layoutParams = layoutParams

                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    fun setUpHabitsList() {
        for (id in habitsIDs) {
            if (!habitsList.contains(NewHabitItem(id, habitsNames[id]!!, loadHabitById(id))))
                habitsList.add(
                    NewHabitItem(id, habitsNames[id]!!, loadHabitById(id)))
        }
    }

    fun saveHabitStateById(id : String, isDone : Boolean) {
        try {
            val sp: SharedPreferences? = context?.getSharedPreferences("HABITS", MODE_PRIVATE)
            sp?.edit {
                this.putBoolean(id, isDone)
            }
            Log.d(LOG_LABEL, "Save success, id: $id")
        } catch (e: Exception) {
            Log.e(LOG_LABEL, "Save Error " + e.message)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EditHabitsFragment"
        fun newInstance(): EditHabitsFragment {
            return EditHabitsFragment()
        }
    }
}
