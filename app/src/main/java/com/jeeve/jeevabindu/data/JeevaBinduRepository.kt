package com.jeeve.jeevabindu.data

import com.jeeve.jeevabindu.data.local.AlertResponseEntity
import com.jeeve.jeevabindu.data.local.AppDatabase
import com.jeeve.jeevabindu.data.local.DonorEntity
import com.jeeve.jeevabindu.data.local.EmergencyPostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class JeevaBinduRepository(private val database: AppDatabase) {

    private val donorDao = database.donorDao()
    private val postDao = database.emergencyPostDao()
    private val responseDao = database.alertResponseDao()

    val currentUser: Flow<DonorUiModel?> =
        donorDao.observeCurrentUser().map { it?.toUiModel() }

    val emergencyPosts: Flow<List<EmergencyPostEntity>> = postDao.observeAll()

    fun observeDonors(bloodGroupFilter: String? = null): Flow<List<DonorUiModel>> {
        val source = if (bloodGroupFilter.isNullOrBlank()) {
            donorDao.observeAll()
        } else {
            donorDao.observeByBloodGroup(bloodGroupFilter)
        }
        return source.map { donors -> donors.map { it.toUiModel() } }
    }

    fun observeAvailableDonors(bloodGroup: String): Flow<List<DonorUiModel>> =
        observeDonors(bloodGroup).map { list -> list.filter { it.isReadyToDonate } }

    fun observeResponses(postId: Long): Flow<List<AlertResponseEntity>> =
        responseDao.observeForPost(postId)

    suspend fun registerDonor(
        name: String,
        phone: String,
        bloodGroup: String,
        age: Int,
        location: String
    ): Long {
        donorDao.clearCurrentUserFlag()
        return donorDao.insert(
            DonorEntity(
                name = name.trim(),
                phone = phone.trim(),
                bloodGroup = bloodGroup,
                age = age,
                location = location.trim(),
                isCurrentUser = true
            )
        )
    }

    suspend fun recordDonation(donorId: Long, date: LocalDate = LocalDate.now()) {
        donorDao.updateLastDonation(donorId, date.toEpochDay())
    }

    suspend fun createEmergencyPost(
        bloodGroup: String,
        hospitalName: String,
        location: String,
        posterDonorId: Long?
    ): EmergencyPostEntity {
        val message = "Urgent $bloodGroup needed at $hospitalName"
        val id = postDao.insert(
            EmergencyPostEntity(
                bloodGroup = bloodGroup,
                hospitalName = hospitalName.trim(),
                location = location.trim(),
                message = message,
                createdAtMillis = System.currentTimeMillis(),
                posterDonorId = posterDonorId
            )
        )
        return postDao.getById(id)!!
    }

    suspend fun respondToPost(postId: Long, donor: DonorUiModel): Boolean {
        if (responseDao.hasResponded(postId, donor.id) > 0) return false
        responseDao.insert(
            AlertResponseEntity(
                postId = postId,
                donorId = donor.id,
                donorName = donor.name,
                respondedAtMillis = System.currentTimeMillis()
            )
        )
        return true
    }

    suspend fun hasProfile(): Boolean = donorDao.getCurrentUser() != null

    suspend fun getCurrentUser(): DonorUiModel? = donorDao.getCurrentUser()?.toUiModel()
}
