package com.example.ecotracker.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.R
import com.example.ecotracker.data.model.Habit

class EditHabitsRecyclerViewAdapter(
    private var habits: List<Habit> = emptyList(),
    private val onHabitToggled: (habitId: String) -> Unit
) : RecyclerView.Adapter<EditHabitsRecyclerViewAdapter.EditHabitsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditHabitsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.habit_edit_item, parent, false)
        return EditHabitsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditHabitsViewHolder, position: Int) {
        val habit = habits[position]
        holder.name.text = habit.title

        if (habit.isAdded) {
            holder.addButton.setImageResource(R.drawable.icon_close)
        } else {
            holder.addButton.setImageResource(R.drawable.icon_plus)
        }

        holder.addButton.setOnClickListener {
            onHabitToggled(habit.id)
        }
    }

    override fun getItemCount(): Int {
        return habits.size
    }

    fun updateHabits(newHabits: List<Habit>) {
        this.habits = newHabits
        notifyDataSetChanged()
    }

    class EditHabitsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val addButton: ImageButton = itemView.findViewById(R.id.addButton)
    }
}