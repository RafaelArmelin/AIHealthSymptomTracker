package com.rafaelarmelin.aihealthsymptomtracker.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rafaelarmelin.aihealthsymptomtracker.R
import com.rafaelarmelin.aihealthsymptomtracker.data.SymptomEntry
import com.rafaelarmelin.aihealthsymptomtracker.databinding.ItemSymptomBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView adapter for the symptom history list.
 * Uses ListAdapter + DiffUtil for efficient, animated list updates.
 * Long-pressing an item shows a confirmation dialog to delete it.
 */
class SymptomAdapter(
    private val onDelete: (SymptomEntry) -> Unit
) : ListAdapter<SymptomEntry, SymptomAdapter.SymptomViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomViewHolder {
        val binding = ItemSymptomBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SymptomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SymptomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SymptomViewHolder(private val binding: ItemSymptomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.UK)

        fun bind(entry: SymptomEntry) {
            binding.tvSymptomName.text  = entry.symptomName
            binding.tvSeverity.text     = "Severity: ${entry.severity} / 5"
            binding.tvNotes.text        = entry.notes.ifBlank { "No notes" }
            binding.tvTimestamp.text    = dateFormat.format(Date(entry.timestamp))

            // Colour-code the left indicator bar by severity
            val colorRes = when (entry.severity) {
                1, 2 -> R.color.severity_low
                3    -> R.color.severity_medium
                else -> R.color.severity_high
            }
            binding.severityIndicator.setBackgroundColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )

            // Long-press to confirm and delete
            binding.root.setOnLongClickListener {
                MaterialAlertDialogBuilder(binding.root.context)
                    .setTitle("Delete Entry")
                    .setMessage("Delete '${entry.symptomName}'? This cannot be undone.")
                    .setPositiveButton("Delete") { _, _ -> onDelete(entry) }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SymptomEntry>() {
            override fun areItemsTheSame(old: SymptomEntry, new: SymptomEntry) =
                old.id == new.id
            override fun areContentsTheSame(old: SymptomEntry, new: SymptomEntry) =
                old == new
        }
    }
}
