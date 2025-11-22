package com.example.ecotracker.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.HabitItem
import com.example.ecotracker.adapters.EditHabitsRecyclerViewAdapter
import com.example.ecotracker.adapters.NewHabitItem
import com.example.ecotracker.databinding.FragmentEditHabitsBinding
import com.example.ecotracker.habitsDescriptions
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
        _binding = FragmentEditHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.habitsRecycler
        setUpHabitsList()

        val adapter = EditHabitsRecyclerViewAdapter(activity, habitsList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            // TODO: Добавить логику сохранения привычек
            dismiss()
        }



    }

    // Этот метод делает окно полноэкранным
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?

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
        if (habitsList.isEmpty()) {

            for (i in 0..2) {
                habitsList.add(NewHabitItem(i.toString(), habitsNames[i], false))
            }
        }
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
