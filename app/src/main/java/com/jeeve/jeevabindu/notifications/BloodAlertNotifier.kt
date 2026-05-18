package com.jeeve.jeevabindu.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.jeeve.jeevabindu.MainActivity
import com.jeeve.jeevabindu.R
import com.jeeve.jeevabindu.data.local.EmergencyPostEntity

object BloodAlertNotifier {
    private const val CHANNEL_ID = "blood_alerts"
    private var notificationId = 2000

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.default_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Urgent blood requirement alerts for nearby donors"
            enableVibration(true)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun showBloodAlert(context: Context, post: EmergencyPostEntity) {
        ensureChannel(context)
        if (!canNotify(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_OPEN_ALERTS, true)
            putExtra(EXTRA_POST_ID, post.id)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            post.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🩸 Emergency Blood Alert")
            .setContentText(post.message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("${post.message}\n📍 ${post.location}")
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setColor(ContextCompat.getColor(context, R.color.emergency_red))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId++, notification)
    }

    private fun canNotify(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    const val EXTRA_OPEN_ALERTS = "open_alerts"
    const val EXTRA_POST_ID = "post_id"
}
