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
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.HabitItem
import com.example.ecotracker.R
import com.google.android.material.internal.ViewUtils.dpToPx

data class RatingItem(var place: Int, var pictureId: Int, var name: String, var xp: Int, var level: Int)
class RatingRecyclerViewAdapter(var context: Context?, var ratingItems: ArrayList<RatingItem>) : RecyclerView.Adapter<RatingRecyclerViewAdapter.RatingViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_rating, parent, false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val item: RatingItem = ratingItems[position]

        holder.place.text = context?.getString(R.string.place_format, item.place)
        holder.picture.setImageResource(item.pictureId)
        holder.name.text = item.name
        holder.xp.text = context?.getString(R.string.xp_format, item.xp)
        holder.level.text = context?.getString(R.string.level_format, item.level)

        holder.place.setTextColor(ContextCompat.getColor(context!!, R.color.black))

    }

//    private fun dpToPx(dp: Int): Int {
//        val density = context?.resources?.displayMetrics?.density ?: 1.0f
//        return (dp * density).toInt()
//    }

    override fun getItemCount(): Int {
        return ratingItems.size
    }

    class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var card: CardView
        var place: TextView
        var picture: ImageView
        var name: TextView
        var xp: TextView
        var level: TextView

        init {
            card = itemView.findViewById<CardView>(R.id.cardView)
            place = itemView.findViewById<TextView>(R.id.position)
            picture = itemView.findViewById<ImageView>(R.id.avatar)
            name = itemView.findViewById<TextView>(R.id.username)
            xp = itemView.findViewById<TextView>(R.id.xp)
            level = itemView.findViewById<TextView>(R.id.level)
        }
    }

}