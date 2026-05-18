package com.jeeve.jeevabindu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Query("SELECT * FROM donors ORDER BY location ASC, name ASC")
    fun observeAll(): Flow<List<DonorEntity>>

    @Query("SELECT * FROM donors WHERE bloodGroup = :bloodGroup ORDER BY location ASC")
    fun observeByBloodGroup(bloodGroup: String): Flow<List<DonorEntity>>

    @Query("SELECT * FROM donors WHERE isCurrentUser = 1 LIMIT 1")
    fun observeCurrentUser(): Flow<DonorEntity?>

    @Query("SELECT * FROM donors WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUser(): DonorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(donor: DonorEntity): Long

    @Query("UPDATE donors SET isCurrentUser = 0")
    suspend fun clearCurrentUserFlag()

    @Query("UPDATE donors SET lastDonationEpochDay = :epochDay WHERE id = :donorId")
    suspend fun updateLastDonation(donorId: Long, epochDay: Long)

    @Query("SELECT COUNT(*) FROM donors")
    suspend fun count(): Int
}

@Dao
interface EmergencyPostDao {
    @Query("SELECT * FROM emergency_posts ORDER BY createdAtMillis DESC")
    fun observeAll(): Flow<List<EmergencyPostEntity>>

    @Insert
    suspend fun insert(post: EmergencyPostEntity): Long

    @Query("SELECT * FROM emergency_posts WHERE id = :id")
    suspend fun getById(id: Long): EmergencyPostEntity?
}

@Dao
interface AlertResponseDao {
    @Query("SELECT * FROM alert_responses WHERE postId = :postId ORDER BY respondedAtMillis ASC")
    fun observeForPost(postId: Long): Flow<List<AlertResponseEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(response: AlertResponseEntity): Long

    @Query("SELECT COUNT(*) FROM alert_responses WHERE postId = :postId AND donorId = :donorId")
    suspend fun hasResponded(postId: Long, donorId: Long): Int
}
