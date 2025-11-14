package com.example.ecotracker.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.ecotracker.databinding.FragmentEditHabitsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditHabitsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentEditHabitsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем слушатели
        binding.closeButton.setOnClickListener {
            // Закрываем окно при нажатии на крестик
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            // Здесь будет логика сохранения
            // ...
            // После сохранения закрываем окно
            dismiss()
        }



        // TODO: Настроить RecyclerView, адаптер и логику добавления/удаления привычек
    }

    // Этот метод делает окно полноэкранным
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.let {
                // --- НАЧАЛО ИЗМЕНЕНИЙ ---
                // 1. Устанавливаем высоту, чтобы он мог занять весь экран
                val layoutParams = it.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                it.layoutParams = layoutParams

                // 2. Устанавливаем состояние "полностью раскрыт"
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true // Пропускаем свернутое состояние
                // --- КОНЕЦ ИЗМЕНЕНИЙ ---
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
