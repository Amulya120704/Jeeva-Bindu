package com.jeeve.jeevabindu.fcm

/*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jeeve.jeevabindu.data.local.EmergencyPostEntity
import com.jeeve.jeevabindu.notifications.BloodAlertNotifier

class JeevaBinduMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        val data = message.data
        val bloodGroup = data["bloodGroup"] ?: return
        val hospital = data["hospitalName"] ?: return
        val location = data["location"] ?: ""
        val postId = data["postId"]?.toLongOrNull() ?: System.currentTimeMillis()

        val post = EmergencyPostEntity(
            id = postId,
            bloodGroup = bloodGroup,
            hospitalName = hospital,
            location = location,
            message = "Urgent $bloodGroup needed at $hospital",
            createdAtMillis = System.currentTimeMillis(),
            posterDonorId = null
        )
        BloodAlertNotifier.showBloodAlert(this, post)
    }

    override fun onNewToken(token: String) {
        // In production, send token to your backend for targeted FCM broadcasts.
    }
}
*/
