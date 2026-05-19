package com.rafaelarmelin.aihealthsymptomtracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing a single symptom log entry.
 * Each entry stores the symptom name, a severity score (1–5),
 * optional notes, and an auto-generated timestamp.
 */
@Entity(tableName = "symptom_entries")
data class SymptomEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val symptomName: String,
    val severity: Int,          // 1 = minimal, 5 = very severe
    val notes: String,
    val timestamp: Long = System.currentTimeMillis()
)
