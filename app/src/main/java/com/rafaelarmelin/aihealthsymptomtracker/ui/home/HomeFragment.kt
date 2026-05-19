package com.rafaelarmelin.aihealthsymptomtracker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rafaelarmelin.aihealthsymptomtracker.R
import com.rafaelarmelin.aihealthsymptomtracker.databinding.FragmentHomeBinding
import com.rafaelarmelin.aihealthsymptomtracker.viewmodel.SymptomViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Home screen — shows a greeting, today's date, weekly stats,
 * a severity distribution chart, and a Log New Symptom button.
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SymptomViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setGreeting()
        setDate()
        observeStats()
        observeChart()

        binding.btnLogSymptom.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_logSymptomFragment)
        }
    }

    private fun setGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        binding.tvGreeting.text = when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else      -> "Good evening"
        }
    }

    private fun setDate() {
        val fmt = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.UK)
        binding.tvDate.text = fmt.format(Date())
    }

    private fun observeStats() {
        viewModel.getWeeklyCount().observe(viewLifecycleOwner) { count ->
            binding.tvWeeklyCount.text = "$count symptom(s) logged this week"
        }
        viewModel.getWeeklyAverageSeverity().observe(viewLifecycleOwner) { avg ->
            binding.tvAverageSeverity.text = if (avg != null && avg > 0f)
                "Average severity: ${"%.1f".format(avg)} / 5"
            else
                "No symptoms logged this week"
        }
    }

    /**
     * Observes the three severity bands and updates the distribution chart bars.
     * Each bar's width is proportional to its count relative to the total.
     */
    private fun observeChart() {
        var low = 0; var mid = 0; var high = 0

        fun updateBars() {
            val total = (low + mid + high).coerceAtLeast(1)
            binding.barLow.progress    = (low  * 100 / total)
            binding.barMedium.progress = (mid  * 100 / total)
            binding.barHigh.progress   = (high * 100 / total)
            binding.tvCountLow.text    = low.toString()
            binding.tvCountMedium.text = mid.toString()
            binding.tvCountHigh.text   = high.toString()
        }

        viewModel.getLowSeverityCount().observe(viewLifecycleOwner)    { low    = it; updateBars() }
        viewModel.getMediumSeverityCount().observe(viewLifecycleOwner) { mid    = it; updateBars() }
        viewModel.getHighSeverityCount().observe(viewLifecycleOwner)   { high   = it; updateBars() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
