package com.example.ecotracker.presentation.ui.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.presentation.ui.adapters.EditHabitsRecyclerViewAdapter
import com.example.ecotracker.databinding.FragmentEditHabitsBinding
import com.example.ecotracker.presentation.viewmodels.HabitsViewModel
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditHabitsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditHabitsBinding? = null
    private val binding get() = _binding!!

    private val habitsViewModel: HabitsViewModel by viewModels()

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

        val adapter = EditHabitsRecyclerViewAdapter(ArrayList()) { habitId ->
            habitsViewModel.toggleHabitSelection(habitId)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        viewLifecycleOwner.lifecycleScope.launch {
            habitsViewModel.habits.collect { newHabitsList ->
                adapter.updateHabits(newHabitsList)
            }
        }
        habitsViewModel.loadHabits()

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            val selectedHabitIds = habitsViewModel.habits.value
                .filter { it.isAdded }
                .map { it.id }

            habitsViewModel.saveSelectedHabits(selectedHabitIds)

            val result = Bundle().apply {
                putBoolean("habitsUpdated", true)
            }
            parentFragmentManager.setFragmentResult("requestKey", result)

            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(
                R.id.design_bottom_sheet) as FrameLayout?

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