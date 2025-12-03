package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.core.text.scale
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ecotracker.R
import com.example.ecotracker.presentation.ui.adapters.StatisticItem
import com.example.ecotracker.presentation.ui.adapters.StatisticsCardAdapter
import com.example.ecotracker.databinding.FragmentStatisticsBinding
import com.example.ecotracker.presentation.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val statisticItems = listOf(
            StatisticItem("Сэкономлено воды", R.drawable.image_splash, "525 литров", "Это 26 полных ванн"),
            StatisticItem("Сокращено выбросов CO2", R.drawable.icon_home, "150 кг", "Как если бы вы проехали 750 км на машине"),
            StatisticItem("Переработано мусора", R.drawable.icon_home, "35 кг", "Этого хватит на 1000 листов бумаги"),
        )

        binding.xpLabel.text = SpannableStringBuilder()
            .append(getString(R.string.your_level) + ": ")
            .bold {scale(1.2f) {color(ContextCompat.getColor(requireContext(), R.color.green_align))
            {append(12.toString())} }}
            .append("\n" + getString(R.string.xp_amount) + ": ")
            .scale(1.2f) {color(ContextCompat.getColor(requireContext(), R.color.light_green_align))
            {append(1142.toString() + " XP")} }

        binding.streakLabel.text = SpannableStringBuilder()
            .append(getString(R.string.current_streak) + ": ")
            .color(ContextCompat.getColor(requireContext(), R.color.red_align))
            {append(String.format(Locale.getDefault(), "%3d", viewModel.getStreakCounter()))}
            .append(" " + getString(R.string.days) + "\n" + getString(R.string.your_record) + ": ")
            .append(String.format(Locale.getDefault(), "%6d", viewModel.getStreakRecord()))
            .append(" " + getString(R.string.days))

        val adapter = StatisticsCardAdapter(statisticItems)
        binding.viewPagerStatistics.adapter = adapter

        binding.ivArrowLeft.setOnClickListener {
            val currentItem = binding.viewPagerStatistics.currentItem
            binding.viewPagerStatistics.currentItem = (currentItem - 1 + adapter.itemCount) % adapter.itemCount
        }

        binding.ivArrowRight.setOnClickListener {
            val currentItem = binding.viewPagerStatistics.currentItem
            binding.viewPagerStatistics.currentItem = (currentItem + 1) % adapter.itemCount
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}