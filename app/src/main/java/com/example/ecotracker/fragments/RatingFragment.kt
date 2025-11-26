package com.example.ecotracker.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.LOG_LABEL
import com.example.ecotracker.R
import com.example.ecotracker.adapters.RatingItem
import com.example.ecotracker.adapters.RatingRecyclerViewAdapter
import com.example.ecotracker.databinding.FragmentRatingBinding
import kotlin.concurrent.timer
import kotlin.system.measureTimeMillis

class RatingFragment : Fragment() {
    private var _binding: FragmentRatingBinding? = null
    private val binding get() = _binding!!

    val ratingList: ArrayList<RatingItem> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.recycler
        setUpRatingList()

        val myCard = binding.myRatingCard
        myCard.position.text = context?.getString(R.string.place_format, 36)
        myCard.avatar.setImageResource(R.drawable.wolf_ava)
        myCard.username.text = "User 36"
        myCard.xp.text = context?.getString(R.string.xp_format, 1888)
        myCard.level.text = context?.getString(R.string.level_format, 12)





        val adapter = RatingRecyclerViewAdapter(activity, ratingList)
        recyclerView.adapter = adapter
        recyclerView.setLayoutManager(LinearLayoutManager(activity))
    }

    fun setUpRatingList() {
        for (i in 1..9) {
            ratingList.add(
                RatingItem(
                    i,
                    R.drawable.wolf_ava,
                    "User $i",
                    i * 100,
                    i
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}