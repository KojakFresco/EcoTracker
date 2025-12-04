package com.example.ecotracker.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.R
import com.google.android.material.imageview.ShapeableImageView

class AvatarAdapter(
    private val avatars: List<Int>,
    initialSelection: Int,
    private val onAvatarSelected: (Int) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private var selectedAvatarResId = initialSelection

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_avatar, parent, false)
        return AvatarViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatarResId = avatars[position]
        holder.avatarImageView.setImageResource(avatarResId)

        if (avatarResId == selectedAvatarResId) {
            holder.avatarImageView.strokeColor = ContextCompat.getColorStateList(holder.itemView.context, R.color.green_align)
            holder.avatarImageView.strokeWidth = 12f
        } else {
            holder.avatarImageView.strokeWidth = 0f
        }

        holder.itemView.setOnClickListener {
            val clickedPosition = holder.bindingAdapterPosition
            if (clickedPosition == RecyclerView.NO_POSITION) {
                return@setOnClickListener
            }
            val clickedAvatarResId = avatars[clickedPosition]

            if (selectedAvatarResId != clickedAvatarResId) {
                val oldSelectedIndex = avatars.indexOf(selectedAvatarResId)
                selectedAvatarResId = clickedAvatarResId
                val newSelectedIndex = avatars.indexOf(selectedAvatarResId)

                if (oldSelectedIndex != -1) {
                    notifyItemChanged(oldSelectedIndex)
                }
                if (newSelectedIndex != -1) {
                    notifyItemChanged(newSelectedIndex)
                }

                onAvatarSelected(selectedAvatarResId)
            }
        }
    }

    override fun getItemCount(): Int = avatars.size

    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ShapeableImageView = itemView.findViewById(R.id.avatarImageView)
    }
}