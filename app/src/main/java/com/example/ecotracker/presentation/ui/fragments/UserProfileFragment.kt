package com.example.ecotracker.presentation.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecotracker.R
import com.example.ecotracker.data.model.User
import com.example.ecotracker.databinding.FragmentUserProfileBinding
import com.example.ecotracker.presentation.ui.adapters.AvatarAdapter
import com.example.ecotracker.presentation.viewmodels.AuthViewModel
import com.example.ecotracker.presentation.viewmodels.AuthState
import com.example.ecotracker.presentation.viewmodels.UserState
import com.example.ecotracker.presentation.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()

    private val avatarResources: List<Int> by lazy { loadAvatarResources() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserState()
        observeAuthState()

        binding.editProfileButton.setOnClickListener {
            val currentUser = (userViewModel.userState.value as? UserState.Success)?.user
            currentUser?.let { showEditProfileDialog(it) }
        }

        binding.signOutButton.setOnClickListener {
            showSignOutConfirmationDialog()
        }
    }

    private fun observeUserState() {
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.userState.collect { state ->
                when (state) {
                    is UserState.Success -> updateUi(state.user)
                    is UserState.Loading -> { /* Можно показать ProgressBar */ }
                    is UserState.Error -> { /* Можно показать ошибку */ }
                }
            }
        }
    }

    private fun showSignOutConfirmationDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выход из аккаунта")
            .setMessage("Вы уверены, что хотите выйти?")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Выйти") { _, _ ->
                authViewModel.signOut()
            }
            .show()

        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton?.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_align))
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.authState.collect { state ->
                if (state is AuthState.Unauthenticated) {
                    findNavController().navigate(R.id.action_global_authFragment)
                }
            }
        }
    }

    private fun updateUi(user: User) {
        binding.profileName.text = user.name
        binding.profileLevel.text = user.level.toString()
        binding.profileXp.text = "${user.experience} XP"
        binding.profileAvatar.setImageResource(getAvatarResourceId(user.selectedAvatar))
    }

    private fun showEditProfileDialog(currentUser: User) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.editNameEditText)
        val avatarsRecyclerView = dialogView.findViewById<RecyclerView>(R.id.avatarsRecyclerView)

        nameEditText.setText(currentUser.name)
        var selectedAvatarId = currentUser.selectedAvatar

        val currentAvatarRes = getAvatarResourceId(currentUser.selectedAvatar)

        val adapter = AvatarAdapter(avatarResources, currentAvatarRes) { selectedResId ->
            selectedAvatarId = getAvatarId(selectedResId)
        }

        avatarsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        avatarsRecyclerView.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Сохранить") { dialog, _ ->
                val newName = nameEditText.text.toString()
                userViewModel.updateUserProfile(newName, selectedAvatarId)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun getAvatarResourceId(avatarId: Int): Int {
        val index = avatarId - 1
        return avatarResources.getOrNull(index) ?: R.mipmap.ic_launcher
    }

    private fun getAvatarId(resourceId: Int): Int {
        val index = avatarResources.indexOf(resourceId)
        return if (index != -1) index + 1 else 1
    }

    private fun loadAvatarResources(): List<Int> {
        return R.drawable::class.java.fields
            .filter { it.name.startsWith("avatar_") }
            .sortedBy { it.name }
            .map { it.getInt(null) }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
