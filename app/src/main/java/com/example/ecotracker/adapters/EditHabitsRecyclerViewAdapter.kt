package com.example.ecotracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.R
import com.example.ecotracker.adapters.EditHabitsRecyclerViewAdapter.EditHabitsViewHolder



data class NewHabitItem(var id : String, var name : String, var isAdded : Boolean?)

class EditHabitsRecyclerViewAdapter(var context: Context?, var habitItems: ArrayList<NewHabitItem>) :
    RecyclerView.Adapter<EditHabitsViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditHabitsViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.habit_edit_item, parent, false)
        return EditHabitsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditHabitsViewHolder, position: Int) {
        holder.name.text = habitItems[position].name

        if (habitItems[position].isAdded!!) {
            holder.addButton.setImageResource(R.drawable.icon_close)
        } else {
            holder.addButton.setImageResource(R.drawable.icon_plus)
        }

        holder.addButton.setOnClickListener(View.OnClickListener {
            if (habitItems[position].isAdded!!) {
                habitItems[position].isAdded = false
                holder.addButton.setImageResource(R.drawable.icon_plus)
            } else {
                habitItems[position].isAdded = true
                holder.addButton.setImageResource(R.drawable.icon_close)
            }
        })
    }

    override fun getItemCount(): Int {
        return habitItems.size
    }

    class EditHabitsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var addButton: ImageButton

        init {
            name = itemView.findViewById<TextView>(R.id.name)
            addButton = itemView.findViewById<ImageButton>(R.id.addButton)
        }
    }
}
