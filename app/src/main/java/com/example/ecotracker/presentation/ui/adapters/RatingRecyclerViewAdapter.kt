package com.example.ecotracker.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.R

data class RatingItem(
    val id: String,
    val currentUserId: String,
    var place: Int,
    var avatarId: Int,
    var name: String,
    var xp: Int,
    var level: Int
)

class RatingRecyclerViewAdapter(
    private var ratingItems: List<RatingItem>
) : RecyclerView.Adapter<RatingRecyclerViewAdapter.RatingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_rating, parent, false)
        return RatingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.bind(ratingItems[position])
    }

    override fun getItemCount(): Int = ratingItems.size

    class RatingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val place: TextView = itemView.findViewById(R.id.position)
        private val picture: ImageView = itemView.findViewById(R.id.avatar)
        private val name: TextView = itemView.findViewById(R.id.username)
        private val xp: TextView = itemView.findViewById(R.id.xp)
        private val level: TextView = itemView.findViewById(R.id.level)

        fun bind(item: RatingItem) {
            val context = itemView.context

            // ИСПРАВЛЕНИЕ: Если это текущий пользователь, меняем имя на "Вы"
            name.text = if (item.id == item.currentUserId) context.getString(R.string.you) else item.name

            place.text = context.getString(R.string.place_format, item.place)
            picture.setImageResource(item.avatarId)
            xp.text = context.getString(R.string.xp_format, item.xp)
            level.text = context.getString(R.string.level_format, item.level)

            // ИСПРАВЛЕНИЕ: Логика раскраски только для текста
            val placeColor = when {
                item.id == item.currentUserId -> R.color.green_align
                item.place == 1 -> R.color.gold
                item.place == 2 -> R.color.silver
                item.place == 3 -> R.color.bronze
                else -> R.color.black // Стандартный цвет
            }
            place.setTextColor(ContextCompat.getColor(context, placeColor))
        }
    }
}