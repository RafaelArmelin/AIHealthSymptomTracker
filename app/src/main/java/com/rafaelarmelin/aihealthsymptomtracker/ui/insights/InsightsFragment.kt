package com.rafaelarmelin.aihealthsymptomtracker.ui.insights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.rafaelarmelin.aihealthsymptomtracker.databinding.FragmentInsightsBinding
import com.rafaelarmelin.aihealthsymptomtracker.viewmodel.SymptomViewModel

/**
 * AI Insights screen — lets the user pick a previously logged symptom from a spinner
 * and request an AI-generated informational insight via the Gemini API.
 * Handles loading state, error messages, and offline gracefully.
 */
class InsightsFragment : Fragment() {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SymptomViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSymptomList()
        observeInsightResult()
        observeLoadingState()
        observeErrors()

        binding.btnGetInsight.setOnClickListener {
            requestInsight()
        }
    }

    /** Populates the spinner with distinct symptom names from the database. */
    private fun observeSymptomList() {
        viewModel.allEntries.observe(viewLifecycleOwner) { entries ->
            val names = entries.map { it.symptomName }.distinct()
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            binding.spinnerSymptoms.adapter = spinnerAdapter

            val hasEntries = names.isNotEmpty()
            binding.tvNoSymptoms.visibility  = if (hasEntries) View.GONE else View.VISIBLE
            binding.btnGetInsight.isEnabled  = hasEntries
            binding.spinnerSymptoms.visibility = if (hasEntries) View.VISIBLE else View.GONE
        }
    }

    /** Displays the Gemini response text when it arrives. */
    private fun observeInsightResult() {
        viewModel.insightResult.observe(viewLifecycleOwner) { result ->
            binding.tvInsightResult.text = result
            binding.tvInsightResult.visibility = View.VISIBLE
        }
    }

    /** Shows or hides the progress bar and disables the button while loading. */
    private fun observeLoadingState() {
        viewModel.isLoadingInsight.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnGetInsight.isEnabled = !isLoading &&
                    (binding.spinnerSymptoms.adapter?.count ?: 0) > 0
        }
    }

    /** Shows a Snackbar if the API call fails. */
    private fun observeErrors() {
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let { Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show() }
        }
    }

    /** Finds the most recent entry matching the selected symptom name and requests insight. */
    private fun requestInsight() {
        val selectedName = binding.spinnerSymptoms.selectedItem?.toString() ?: return
        val entry = viewModel.allEntries.value?.firstOrNull { it.symptomName == selectedName }
        entry?.let { viewModel.getInsight(it.symptomName, it.severity, it.notes) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
