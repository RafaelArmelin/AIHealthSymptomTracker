package com.rafaelarmelin.aihealthsymptomtracker.repository

import androidx.lifecycle.LiveData
import com.rafaelarmelin.aihealthsymptomtracker.data.SymptomDao
import com.rafaelarmelin.aihealthsymptomtracker.data.SymptomEntry

/**
 * Repository that abstracts access to the Room database.
 * The ViewModel interacts only with this class, not with the DAO directly.
 */
class SymptomRepository(private val dao: SymptomDao) {

    val allEntries: LiveData<List<SymptomEntry>>    = dao.getAllEntries()
    val recentEntries: LiveData<List<SymptomEntry>> = dao.getRecentEntries()

    fun getCountSince(startTime: Long): LiveData<Int>      = dao.getCountSince(startTime)
    fun getAverageSeveritySince(startTime: Long): LiveData<Float?> = dao.getAverageSeveritySince(startTime)

    // Severity distribution for the home screen chart
    fun getLowSeverityCount(startTime: Long): LiveData<Int>    = dao.getLowSeverityCount(startTime)
    fun getMediumSeverityCount(startTime: Long): LiveData<Int>  = dao.getMediumSeverityCount(startTime)
    fun getHighSeverityCount(startTime: Long): LiveData<Int>    = dao.getHighSeverityCount(startTime)

    suspend fun insert(entry: SymptomEntry)  = dao.insert(entry)
    suspend fun delete(entry: SymptomEntry)  = dao.delete(entry)
    suspend fun deleteAll()                  = dao.deleteAll()
}
