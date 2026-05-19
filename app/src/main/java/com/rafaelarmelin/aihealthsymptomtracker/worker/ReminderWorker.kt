package com.rafaelarmelin.aihealthsymptomtracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rafaelarmelin.aihealthsymptomtracker.R

/**
 * WorkManager Worker that fires a daily reminder notification,
 * prompting the user to log their symptoms.
 */
class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        createNotificationChannel()
        showReminderNotification()
        return Result.success()
    }

    /** Creates the notification channel required on Android 8.0 and above. */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Daily Symptom Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to log your symptoms each day"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /** Builds and displays the reminder notification. */
    private fun showReminderNotification() {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle("AI Health Tracker")
            .setContentText("Don't forget to log your symptoms today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID     = "symptom_reminder_channel"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME      = "daily_symptom_reminder"
    }
}
