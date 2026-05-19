package com.rafaelarmelin.aihealthsymptomtracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Data Access Object (DAO) for all symptom entry database operations.
 * Suspend functions run on a background thread via coroutines.
 * LiveData queries are observed automatically on the main thread.
 */
@Dao
interface SymptomDao {

    /** Insert a new symptom entry, replacing any duplicate if it exists. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: SymptomEntry)

    /** Delete a specific symptom entry. */
    @Delete
    suspend fun delete(entry: SymptomEntry)

    /** Return all entries ordered by most recent first, as a live stream. */
    @Query("SELECT * FROM symptom_entries ORDER BY timestamp DESC")
    fun getAllEntries(): LiveData<List<SymptomEntry>>

    /** Return the 5 most recent entries for the Home dashboard. */
    @Query("SELECT * FROM symptom_entries ORDER BY timestamp DESC LIMIT 5")
    fun getRecentEntries(): LiveData<List<SymptomEntry>>

    /** Count entries logged since a given epoch timestamp. */
    @Query("SELECT COUNT(*) FROM symptom_entries WHERE timestamp >= :startTime")
    fun getCountSince(startTime: Long): LiveData<Int>

    /** Calculate the average severity of entries since a given timestamp. */
    @Query("SELECT AVG(severity) FROM symptom_entries WHERE timestamp >= :startTime")
    fun getAverageSeveritySince(startTime: Long): LiveData<Float?>

    /** Count low-severity entries (1–2) since a given timestamp — used for chart. */
    @Query("SELECT COUNT(*) FROM symptom_entries WHERE severity IN (1,2) AND timestamp >= :startTime")
    fun getLowSeverityCount(startTime: Long): LiveData<Int>

    /** Count medium-severity entries (3) since a given timestamp — used for chart. */
    @Query("SELECT COUNT(*) FROM symptom_entries WHERE severity = 3 AND timestamp >= :startTime")
    fun getMediumSeverityCount(startTime: Long): LiveData<Int>

    /** Count high-severity entries (4–5) since a given timestamp — used for chart. */
    @Query("SELECT COUNT(*) FROM symptom_entries WHERE severity IN (4,5) AND timestamp >= :startTime")
    fun getHighSeverityCount(startTime: Long): LiveData<Int>

    /** Delete every entry — used by the Clear Data option in Settings. */
    @Query("DELETE FROM symptom_entries")
    suspend fun deleteAll()
}
