package com.example.ecotracker.adapters

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.HabitItem
import com.example.ecotracker.R

data class RatingItem(var place: Int, var picture: Bitmap, var name: String, var xp: Int, var level: Int)
class RatingRecyclerViewAdapter(var context: Context?, var ratingItems: ArrayList<RatingItem>) : RecyclerView.Adapter<RatingRecyclerViewAdapter.RatingViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_rating, parent, false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val item: RatingItem = ratingItems[position]

        holder.place.text = item.place.toString()
        holder.picture.setImageBitmap(item.picture)
        holder.name.text = item.name
        holder.xp.text = item.xp.toString()
        holder.level.text = item.level.toString()

//        if (position == 1 && item.place > 8) {
//            holder.card.margin = 10dp
//        }


    }

    override fun getItemCount(): Int {
        return ratingItems.size
    }

    class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // var card: CardView
        var place: TextView
        var picture: ImageView
        var name: TextView
        var xp: TextView
        var level: TextView

        init {
            place = itemView.findViewById<TextView>(R.id.position)
            picture = itemView.findViewById<ImageView>(R.id.avatar)
            name = itemView.findViewById<TextView>(R.id.username)
            xp = itemView.findViewById<TextView>(R.id.xp)
            level = itemView.findViewById<TextView>(R.id.level)
        }
    }

}