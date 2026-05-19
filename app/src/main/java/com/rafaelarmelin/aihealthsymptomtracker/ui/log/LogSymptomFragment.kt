package com.rafaelarmelin.aihealthsymptomtracker.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rafaelarmelin.aihealthsymptomtracker.data.SymptomEntry
import com.rafaelarmelin.aihealthsymptomtracker.databinding.FragmentLogSymptomBinding
import com.rafaelarmelin.aihealthsymptomtracker.viewmodel.SymptomViewModel
import kotlin.getValue

/**
 * Log Symptom screen — allows the user to enter a symptom name,
 * choose a severity level (1–5), and add optional notes before saving.
 */
class LogSymptomFragment : Fragment() {

    private var _binding: FragmentLogSymptomBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SymptomViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogSymptomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update the severity label as the slider moves
        binding.severitySlider.addOnChangeListener { _, value, _ ->
            binding.tvSeverityLabel.text = severityLabel(value.toInt())
        }
        // Set initial label
        binding.tvSeverityLabel.text = severityLabel(binding.severitySlider.value.toInt())

        binding.btnSave.setOnClickListener { saveEntry() }
        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }
    }

    /** Validates inputs and persists the symptom entry via the ViewModel. */
    private fun saveEntry() {
        val name = binding.etSymptomName.text.toString().trim()
        if (name.isEmpty()) {
            binding.etSymptomName.error = "Please enter a symptom name"
            binding.etSymptomName.requestFocus()
            return
        }

        val entry = SymptomEntry(
            symptomName = name,
            severity    = binding.severitySlider.value.toInt(),
            notes       = binding.etNotes.text.toString().trim()
        )
        viewModel.insert(entry)
        Toast.makeText(requireContext(), "Symptom logged successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    /** Returns a human-readable description for each severity level. */
    private fun severityLabel(value: Int) = when (value) {
        1    -> "1 – Minimal"
        2    -> "2 – Mild"
        3    -> "3 – Moderate"
        4    -> "4 – Severe"
        5    -> "5 – Very Severe"
        else -> value.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
