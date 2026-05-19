package com.rafaelarmelin.aihealthsymptomtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Singleton Room database class.
 * Holds the symptom_entries table and exposes the DAO.
 * The @Volatile INSTANCE ensures thread-safe initialisation.
 */
@Database(entities = [SymptomEntry::class], version = 1, exportSchema = false)
abstract class SymptomDatabase : RoomDatabase() {

    abstract fun symptomDao(): SymptomDao

    companion object {
        @Volatile
        private var INSTANCE: SymptomDatabase? = null

        fun getDatabase(context: Context): SymptomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SymptomDatabase::class.java,
                    "symptom_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
