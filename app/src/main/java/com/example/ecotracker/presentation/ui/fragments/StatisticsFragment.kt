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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecotracker.R
import com.example.ecotracker.data.model.User
import com.example.ecotracker.presentation.ui.adapters.StatisticItem
import com.example.ecotracker.presentation.ui.adapters.StatisticsCardAdapter
import com.example.ecotracker.databinding.FragmentStatisticsBinding
import com.example.ecotracker.presentation.viewmodels.UserState
import com.example.ecotracker.presentation.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserState()

        binding.ivArrowLeft.setOnClickListener {
            val adapter = binding.viewPagerStatistics.adapter ?: return@setOnClickListener
            val currentItem = binding.viewPagerStatistics.currentItem
            binding.viewPagerStatistics.currentItem = (currentItem - 1 + adapter.itemCount) % adapter.itemCount
        }

        binding.ivArrowRight.setOnClickListener {
            val adapter = binding.viewPagerStatistics.adapter ?: return@setOnClickListener
            val currentItem = binding.viewPagerStatistics.currentItem
            binding.viewPagerStatistics.currentItem = (currentItem + 1) % adapter.itemCount
        }
    }

    private fun observeUserState() {
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.userState.collect { state ->
                when (state) {
                    is UserState.Success -> {
                        updateUi(state.user)
                        binding.progressBar.visibility = View.GONE
                    }
                    is UserState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is UserState.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun updateUi(user: User) {
        // Обновляем текстовые поля с уровнем и серией
        updateLabels(user)
        
        // Создаем карточки статистики
        val statisticItems = createStatisticItems(user)
        val adapter = StatisticsCardAdapter(statisticItems)
        binding.viewPagerStatistics.adapter = adapter
    }

    private fun createStatisticItems(user: User): List<StatisticItem> {
        // Предполагаемые средние значения для расчетов
        val avgShowerLiters = 20
        val avgCarKmPerKgCo2 = 4.4
        val avgPaperSheetsPerKg = 1

        val waterSaved = user.waterRescue.roundToInt()
        val co2Reduced = user.co2Reduction.roundToInt()
        val wasteRecycled = user.wasteDisposal.roundToInt()

        val bathsSaved = (user.waterRescue / avgShowerLiters).roundToInt()
        val carKmSaved = (co2Reduced * avgCarKmPerKgCo2).roundToInt()
        val paperSaved = (wasteRecycled * avgPaperSheetsPerKg)

        return listOf(
            StatisticItem(
                "Сэкономлено воды",
                R.drawable.image_splash,
                "$waterSaved литров",
                "Это примерно $bathsSaved полных ванн"
            ),
            StatisticItem(
                "Сокращено выбросов CO2",
                R.drawable.icon_co2,
                "$co2Reduced кг",
                "Как если бы вы проехали $carKmSaved км на машине"
            ),
            StatisticItem(
                "Переработано мусора",
                R.drawable.icon_recycle,
                "$wasteRecycled кг",
                "Этого хватит на $paperSaved листов бумаги"
            ),
        )
    }

    private fun updateLabels(user: User) {
        binding.xpLabel.text = SpannableStringBuilder()
            .append(getString(R.string.your_level) + ": ")
            .bold {scale(1.2f) {color(ContextCompat.getColor(requireContext(), R.color.green_align))
            {append(user.level.toString())} }}
            .append("\n" + getString(R.string.xp_amount) + ": ")
            .scale(1.2f) {color(ContextCompat.getColor(requireContext(), R.color.light_green_align))
            {append(user.experience.toString() + " XP")} }

        binding.streakLabel.text = SpannableStringBuilder()
            .append(getString(R.string.current_streak) + ": ")
            .color(ContextCompat.getColor(requireContext(), R.color.red_align))
            {append(String.format(Locale.getDefault(), "%3d", user.streak))}
            .append(" " + getString(R.string.days) + "\n" + getString(R.string.your_record) + ": ")
            .append(String.format(Locale.getDefault(), "%6d", user.record))
            .append(" " + getString(R.string.days))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}