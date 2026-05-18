package com.jeeve.jeevabindu.fcm

import android.content.Context
import com.jeeve.jeevabindu.data.DonorUiModel
import com.jeeve.jeevabindu.data.local.EmergencyPostEntity
import com.jeeve.jeevabindu.notifications.BloodAlertNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.jeeve.jeevabindu.data.JeevaBinduRepository

/**
 * Simulates FCM delivery to matching donors on-device within 5 seconds (internship demo).
 * Replace with server-side FCM topic/data messages when a backend is available.
 */
class AlertDispatchService(
    private val context: Context,
    private val repository: JeevaBinduRepository
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun dispatchAfterPost(post: EmergencyPostEntity, @Suppress("UNUSED_PARAMETER") excludeDonorId: Long?) {
        scope.launch {
            delay(2_000L) // Success criteria: alert on device within 5 seconds
            val current = repository.getCurrentUser()
            val matchesDonor = current != null &&
                current.bloodGroup == post.bloodGroup &&
                current.isReadyToDonate &&
                locationMatches(current, post.location)
            // Demo: always show notification so a single device can verify FCM-style delivery.
            // In production, send FCM only to other donors (exclude poster).
            if (matchesDonor || current != null) {
                BloodAlertNotifier.showBloodAlert(context, post)
            }
        }
    }

    private fun locationMatches(donor: DonorUiModel, alertLocation: String): Boolean {
        val donorLoc = donor.location.lowercase()
        val alertLoc = alertLocation.lowercase()
        return donorLoc == alertLoc ||
            donorLoc.contains(alertLoc) ||
            alertLoc.contains(donorLoc) ||
            alertLoc.split(" ").any { word -> word.length > 3 && donorLoc.contains(word) }
    }
}
