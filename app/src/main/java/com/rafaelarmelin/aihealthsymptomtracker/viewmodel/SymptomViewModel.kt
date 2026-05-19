package com.rafaelarmelin.aihealthsymptomtracker.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.work.*
import com.rafaelarmelin.aihealthsymptomtracker.data.SymptomDatabase
import com.rafaelarmelin.aihealthsymptomtracker.data.SymptomEntry
import com.rafaelarmelin.aihealthsymptomtracker.repository.GroqRepository
import com.rafaelarmelin.aihealthsymptomtracker.repository.SymptomRepository
import com.rafaelarmelin.aihealthsymptomtracker.worker.ReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Shared ViewModel scoped to the Activity lifecycle.
 * All five fragments access this single instance via activityViewModels().
 * Uses AndroidViewModel to safely access the application context for Room.
 */
class SymptomViewModel(application: Application) : AndroidViewModel(application) {

    private val symptomRepository: SymptomRepository
    private val groqRepository = GroqRepository()

    // ── Database LiveData ──────────────────────────────────────────────────────

    val allEntries: LiveData<List<SymptomEntry>>
    val recentEntries: LiveData<List<SymptomEntry>>

    // ── Groq API state ─────────────────────────────────────────────────────────

    private val _insightResult = MutableLiveData<String>()
    val insightResult: LiveData<String> = _insightResult

    private val _isLoadingInsight = MutableLiveData(false)
    val isLoadingInsight: LiveData<Boolean> = _isLoadingInsight

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        val dao = SymptomDatabase.getDatabase(application).symptomDao()
        symptomRepository = SymptomRepository(dao)
        allEntries   = symptomRepository.allEntries
        recentEntries = symptomRepository.recentEntries
    }

    // ── Database operations ────────────────────────────────────────────────────

    fun insert(entry: SymptomEntry) = viewModelScope.launch { symptomRepository.insert(entry) }
    fun delete(entry: SymptomEntry) = viewModelScope.launch { symptomRepository.delete(entry) }
    fun deleteAll()                  = viewModelScope.launch { symptomRepository.deleteAll() }

    /** Returns a LiveData count of entries logged in the past 7 days. */
    fun getWeeklyCount(): LiveData<Int> =
        symptomRepository.getCountSince(weekAgoMs())

    /** Returns a LiveData average severity of entries in the past 7 days. */
    fun getWeeklyAverageSeverity(): LiveData<Float?> =
        symptomRepository.getAverageSeveritySince(weekAgoMs())

    // ── Chart data — severity distribution for the past 7 days ────────────────

    fun getLowSeverityCount(): LiveData<Int>    = symptomRepository.getLowSeverityCount(weekAgoMs())
    fun getMediumSeverityCount(): LiveData<Int>  = symptomRepository.getMediumSeverityCount(weekAgoMs())
    fun getHighSeverityCount(): LiveData<Int>    = symptomRepository.getHighSeverityCount(weekAgoMs())

    // ── Notification scheduling ────────────────────────────────────────────────

    /**
     * Schedules a periodic daily reminder notification using WorkManager.
     * The interval is set to 15 minutes (WorkManager minimum) for demonstration
     * purposes; production would use 24 hours.
     */
    fun scheduleReminder() {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(getApplication()).enqueueUniquePeriodicWork(
            ReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /** Cancels any scheduled reminder notification. */
    fun cancelReminder() {
        WorkManager.getInstance(getApplication())
            .cancelUniqueWork(ReminderWorker.WORK_NAME)
    }

    // ── Groq API ───────────────────────────────────────────────────────────────

    fun getInsight(symptomName: String, severity: Int, notes: String) {
        _isLoadingInsight.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = groqRepository.getSymptomInsight(symptomName, severity, notes)
                _insightResult.value = result
            } catch (e: Exception) {
                _errorMessage.value = "Could not reach AI service. Check your internet connection."
            } finally {
                _isLoadingInsight.value = false
            }
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private fun weekAgoMs() = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
}
