package com.example.ecotracker

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.edit
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.MyHabitsRecyclerViewAdapter.MyViewHolder
import com.example.ecotracker.fragments.MyHabitsFragment

class MyHabitsRecyclerViewAdapter(var context: Context?, var habitItems: ArrayList<HabitItem>) :
    RecyclerView.Adapter<MyViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // This is where you inflate the layout (giving a look to our rows)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Assigning values to the views we created in the recycler_view_row layout file
        // based on the position of the recycler view
        val item: HabitItem = habitItems.get(position)
        holder.name.setText(item.name)
        holder.description.setText(item.description)
        holder.checkBox.setChecked(item.isCompleted!!)
        //TODO: optimize and prettify this code

        if (item.isCompleted!!){
            holder.setAlpha(0.5f)
        } else {
            holder.setAlpha(1f)
        }

        holder.checkBox.setOnClickListener(View.OnClickListener {
            item.isCompleted = holder.checkBox.isChecked()
            saveHabitById(item.id, item.isCompleted!!)

            if (item.isCompleted!!){
                holder.setAlpha(0.5f)
            } else {
                holder.setAlpha(1f)
            }
        })
    }

    override fun getItemCount(): Int {
        // The recycler view just wants to know the number of items you want displayed
        return habitItems.size
    }

    fun saveHabitById(id : String, isDone : Boolean) {
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

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Grabbing the views from our recycler_view_row layout file
        // Kinda like in the onCreate method

        var name: TextView
        var description: TextView
        var checkBox: CheckBox
        var cardView: CardView


        init {
            name = itemView.findViewById<TextView?>(R.id.name)
            description = itemView.findViewById<TextView?>(R.id.description)
            checkBox = itemView.findViewById<CheckBox?>(R.id.checkBox)
            cardView = itemView.findViewById<CardView?>(R.id.cardView)
        }

        fun setAlpha(a: Float) {
            name.alpha = a
            description.alpha = a
            cardView.alpha = a
        }
    }
}