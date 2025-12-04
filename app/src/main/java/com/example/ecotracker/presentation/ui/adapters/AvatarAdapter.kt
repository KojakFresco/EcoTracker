package com.example.ecotracker.presentation.ui.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.R

class AvatarAdapter(
    private val avatars: List<Int>,
    initialSelection: Int,
    private val onAvatarSelected: (Int) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private var selectedPosition = avatars.indexOf(initialSelection)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_avatar, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatarResId = avatars[position]
        holder.avatarImageView.setImageResource(avatarResId)

        if (position == selectedPosition) {
            holder.itemView.background = ColorDrawable(Color.LTGRAY)
        } else {
            holder.itemView.background = null
        }

        holder.itemView.setOnClickListener {
            if (selectedPosition != holder.adapterPosition) {
                val oldPosition = selectedPosition
                selectedPosition = holder.adapterPosition
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
                onAvatarSelected(avatars[selectedPosition])
            }
        }
    }

    override fun getItemCount(): Int = avatars.size

    fun getSelectedAvatarResId(): Int {
        return avatars[selectedPosition]
    }

    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
    }
}