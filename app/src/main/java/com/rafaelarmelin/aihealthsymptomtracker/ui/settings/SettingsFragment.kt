package com.rafaelarmelin.aihealthsymptomtracker.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.rafaelarmelin.aihealthsymptomtracker.databinding.FragmentSettingsBinding
import com.rafaelarmelin.aihealthsymptomtracker.viewmodel.SymptomViewModel

/**
 * Settings screen — allows the user to toggle daily reminders and clear all stored data.
 * Displays the app version and a medical disclaimer.
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SymptomViewModel by activityViewModels()

    // Runtime notification permission launcher (Android 13+)
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                viewModel.scheduleReminder()
                saveReminderPreference(true)
                Snackbar.make(binding.root, "Daily reminder enabled", Snackbar.LENGTH_SHORT).show()
            } else {
                binding.switchReminder.isChecked = false
                Snackbar.make(binding.root, "Notification permission denied", Snackbar.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvVersion.text    = "AI Health Symptom Tracker  v1.0"
        binding.tvDisclaimer.text =
            "Disclaimer: This application is intended for personal informational use only. " +
            "It is not a substitute for professional medical advice, diagnosis, or treatment. " +
            "Always consult a qualified healthcare professional for any health concerns."

        // Restore saved toggle state
        binding.switchReminder.isChecked = getReminderPreference()

        // Notification toggle
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestNotificationPermissionAndSchedule()
            } else {
                viewModel.cancelReminder()
                saveReminderPreference(false)
                Snackbar.make(binding.root, "Daily reminder disabled", Snackbar.LENGTH_SHORT).show()
            }
        }

        // Clear all data
        binding.btnClearData.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clear All Data")
                .setMessage("This will permanently delete all your symptom entries. This action cannot be undone.")
                .setPositiveButton("Clear") { _, _ ->
                    viewModel.deleteAll()
                    Snackbar.make(binding.root, "All data has been cleared", Snackbar.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    /**
     * On Android 13+, requests POST_NOTIFICATIONS permission before scheduling.
     * On older versions, schedules immediately.
     */
    private fun requestNotificationPermissionAndSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                == PackageManager.PERMISSION_GRANTED) {
                viewModel.scheduleReminder()
                saveReminderPreference(true)
                Snackbar.make(binding.root, "Daily reminder enabled", Snackbar.LENGTH_SHORT).show()
            } else {
                notificationPermissionLauncher.launch(permission)
            }
        } else {
            viewModel.scheduleReminder()
            saveReminderPreference(true)
            Snackbar.make(binding.root, "Daily reminder enabled", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun saveReminderPreference(enabled: Boolean) {
        requireContext()
            .getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            .edit()
            .putBoolean("reminder_enabled", enabled)
            .apply()
    }

    private fun getReminderPreference(): Boolean =
        requireContext()
            .getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            .getBoolean("reminder_enabled", false)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
