package com.example.ecotracker.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecotracker.R
import com.example.ecotracker.data.repository.PublicUser
import com.example.ecotracker.databinding.FragmentRatingBinding
import com.example.ecotracker.presentation.ui.adapters.RatingItem
import com.example.ecotracker.presentation.ui.adapters.RatingRecyclerViewAdapter
import com.example.ecotracker.presentation.viewmodels.RatingState
import com.example.ecotracker.presentation.viewmodels.RatingViewModel
import com.example.ecotracker.presentation.viewmodels.UserState
import com.example.ecotracker.presentation.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RatingFragment : Fragment() {
    private var _binding: FragmentRatingBinding? = null
    private val binding get() = _binding!!

    private val ratingViewModel: RatingViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())

        observeViewModels()

        ratingViewModel.loadLeaderboard()
    }

    private fun observeViewModels() {
        viewLifecycleOwner.lifecycleScope.launch {
            ratingViewModel.ratingState.collect { state ->
                binding.recycler.visibility = if (state is RatingState.Loading) View.GONE else View.VISIBLE

                if (state is RatingState.Success) {
                    val currentUserId = userViewModel.currentUserId
                    if (currentUserId != null) {
                        val ratingItems = mapPublicUsersToRatingItems(state.users, currentUserId)
                        binding.recycler.adapter = RatingRecyclerViewAdapter(ratingItems)
                        updateUserCard(state.users, currentUserId)
                    }
                }
            }
        }
    }

    private fun updateUserCard(leaderboard: List<PublicUser>, currentUserId: String) {
        val userState = userViewModel.userState.value
        if (userState is UserState.Success) {
            val currentUser = userState.user
            val myCard = binding.myRatingCard

            val userRank = leaderboard.indexOfFirst { it.id == currentUserId } + 1

            myCard.position.text = if (userRank > 0) {
                getString(R.string.place_format, userRank)
            } else {
                "#>20"
            }

            // ИСПРАВЛЕНИЕ: Заменяем имя на "Вы"
            myCard.username.text = getString(R.string.you)
            myCard.xp.text = getString(R.string.xp_format, currentUser.experience)
            myCard.level.text = getString(R.string.level_format, currentUser.level)
            myCard.avatar.setImageResource(getAvatarResourceId(currentUser.selectedAvatar - 1))

            myCard.position.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_align))
        }
    }

    private fun mapPublicUsersToRatingItems(users: List<PublicUser>, currentUserId: String): List<RatingItem> {
        return users.mapIndexed { index, user ->
            RatingItem(
                id = user.id,
                currentUserId = currentUserId,
                place = index + 1,
                avatarId = getAvatarResourceId(user.selectedAvatar - 1),
                name = user.name,
                xp = user.experience,
                level = user.level
            )
        }
    }

    private fun getAvatarResourceId(index: Int): Int {
        return try {
            val avatars = resources.obtainTypedArray(R.array.avatars)
            val safeIndex = if (index >= 0 && index < avatars.length()) index else 0
            val resourceId = avatars.getResourceId(safeIndex, R.drawable.avatar_1)
            avatars.recycle()
            resourceId
        } catch (e: Exception) {
            R.drawable.avatar_1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}