package com.example.ecotracker.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.presentation.ui.adapters.MyHabitsRecyclerViewAdapter.MyViewHolder
import com.example.ecotracker.data.model.Habit
import com.example.ecotracker.databinding.ListItemBinding

typealias OnHabitCheckedChange = (habitId: String, isChecked: Boolean) -> Unit

class MyHabitsRecyclerViewAdapter(
    private val onHabitCheckedChange: OnHabitCheckedChange
) : ListAdapter<Habit, MyViewHolder>(HabitDiffCallback()) {

    inner class MyViewHolder(private val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Grabbing the views from our recycler_view_row layout file
        // Kinda like in the onCreate method

        fun bind(habit: Habit) {
            binding.name.text = habit.title
            binding.description.text = habit.description

            updateCompletedState(habit.isCompleted)

            binding.checkBox.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    updateCompletedState(binding.checkBox.isChecked)

                    val currentHabitId = getItem(bindingAdapterPosition).id
                    onHabitCheckedChange(currentHabitId, binding.checkBox.isChecked)
                }
            }
        }

        fun updateCompletedState(isCompleted: Boolean) {
            binding.checkBox.isChecked = isCompleted
            binding.cardView.alpha = if (isCompleted) 0.5f else 1.0f
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // This is where you inflate the layout (giving a look to our rows)
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    // Этот метод onBindViewHolder будет вызываться для ПОЛНОЙ перерисовки
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // А ЭТОТ МЕТОД будет вызываться, когда DiffUtil найдет ИЗМЕНЕНИЯ с PAYLOAD
    override fun onBindViewHolder(holder: MyViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (payloads.contains(HabitDiffCallback.HabitPayload.COMPLETED_STATE_CHANGED)) {
                val item = getItem(position)
                holder.updateCompletedState(item.isCompleted)
            }
        }
    }
}


// DiffUtil — это мозг ListAdapter. Он вычисляет разницу между старым и новым списком
// и анимирует только те элементы, которые изменились.
class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
    override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
        // Элементы — это один и тот же объект, если у них одинаковый ID
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
        // Содержимое одинаковое, если все поля равны. Data class делает это за нас.
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Habit, newItem: Habit): Any? {
        if (oldItem.isCompleted != newItem.isCompleted) {
            return HabitPayload.COMPLETED_STATE_CHANGED
        }
        return null
    }

    object HabitPayload {
        const val COMPLETED_STATE_CHANGED = "COMPLETED_STATE_CHANGED"
    }
}