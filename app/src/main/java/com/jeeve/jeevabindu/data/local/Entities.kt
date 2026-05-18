package com.jeeve.jeevabindu.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donors")
data class DonorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val bloodGroup: String,
    val age: Int,
    val location: String,
    val lastDonationEpochDay: Long? = null,
    val isCurrentUser: Boolean = false
)

@Entity(tableName = "emergency_posts")
data class EmergencyPostEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bloodGroup: String,
    val hospitalName: String,
    val location: String,
    val message: String,
    val createdAtMillis: Long,
    val posterDonorId: Long?
)

@Entity(tableName = "alert_responses")
data class AlertResponseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Long,
    val donorId: Long,
    val donorName: String,
    val respondedAtMillis: Long
)
