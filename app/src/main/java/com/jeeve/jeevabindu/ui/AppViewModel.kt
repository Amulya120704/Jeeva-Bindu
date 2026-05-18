package com.jeeve.jeevabindu.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jeeve.jeevabindu.JeevaBinduApp
import com.jeeve.jeevabindu.data.BloodGroup
import com.jeeve.jeevabindu.data.DonorUiModel
import com.jeeve.jeevabindu.data.JeevaBinduRepository
import com.jeeve.jeevabindu.data.local.AlertResponseEntity
import com.jeeve.jeevabindu.data.local.EmergencyPostEntity
import com.jeeve.jeevabindu.fcm.AlertDispatchService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as JeevaBinduApp
    private val repository: JeevaBinduRepository = app.repository
    private val alertDispatch: AlertDispatchService = app.alertDispatch

    private val _hasProfile = MutableStateFlow<Boolean?>(null)
    val hasProfile: StateFlow<Boolean?> = _hasProfile.asStateFlow()

    val currentUser = repository.currentUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )

    private val _bloodGroupFilter = MutableStateFlow<String?>(null)
    val bloodGroupFilter: StateFlow<String?> = _bloodGroupFilter.asStateFlow()

    private val _showOnlyAvailable = MutableStateFlow(false)
    val showOnlyAvailable: StateFlow<Boolean> = _showOnlyAvailable.asStateFlow()

    val donors = combine(
        repository.observeDonors(),
        _bloodGroupFilter,
        _showOnlyAvailable
    ) { all, group, availableOnly ->
        all.filter { donor ->
            (group == null || donor.bloodGroup == group) &&
            (!availableOnly || donor.isReadyToDonate)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val emergencyPosts = repository.emergencyPosts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    private val _registryError = MutableStateFlow<String?>(null)
    val registryError: StateFlow<String?> = _registryError.asStateFlow()

    private val _phoneVerified = MutableStateFlow(false)
    val phoneVerified: StateFlow<Boolean> = _phoneVerified.asStateFlow()

    init {
        viewModelScope.launch {
            _hasProfile.value = repository.hasProfile()
        }
    }

    fun setBloodGroupFilter(group: String?) {
        _bloodGroupFilter.value = group
    }

    fun setShowOnlyAvailable(only: Boolean) {
        _showOnlyAvailable.value = only
    }

    fun sendSimulatedOtp(phone: String): Boolean {
        return phone.length >= 10
    }

    fun verifyOtp(otp: String): Boolean {
        val ok = otp == "123456"
        _phoneVerified.value = ok
        return ok
    }

    fun registerDonor(
        name: String,
        phone: String,
        bloodGroup: String,
        ageText: String,
        location: String
    ) {
        if (!_phoneVerified.value) {
            _registryError.value = "Please verify your phone number first (OTP: 123456)"
            return
        }
        val age = ageText.toIntOrNull()
        if (name.isBlank() || phone.length < 10 || bloodGroup.isBlank() || age == null || age < 18 || location.isBlank()) {
            _registryError.value = "Fill all fields. Age must be 18+."
            return
        }
        viewModelScope.launch {
            repository.registerDonor(name, phone, bloodGroup, age, location)
            _hasProfile.value = true
            _registryError.value = null
        }
    }

    fun postEmergency(hospital: String, location: String, bloodGroup: String) {
        if (hospital.isBlank() || location.isBlank() || bloodGroup.isBlank()) return
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            val post = repository.createEmergencyPost(
                bloodGroup = bloodGroup,
                hospitalName = hospital,
                location = location,
                posterDonorId = user?.id
            )
            alertDispatch.dispatchAfterPost(post, excludeDonorId = user?.id)
        }
    }

    fun respondToPost(postId: Long) {
        viewModelScope.launch {
            val user = repository.getCurrentUser() ?: return@launch
            repository.respondToPost(postId, user)
        }
    }

    fun recordDonation() {
        viewModelScope.launch {
            val user = repository.getCurrentUser() ?: return@launch
            repository.recordDonation(user.id)
        }
    }

    fun observeResponses(postId: Long) = repository.observeResponses(postId)

    fun clearRegistryError() {
        _registryError.value = null
    }

    fun eligibilityText(user: DonorUiModel?): String {
        if (user == null) return ""
        if (user.isReadyToDonate) return "Ready to donate"
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val date = user.eligibilityDate?.format(formatter) ?: "—"
        return "Eligible again on $date (${user.daysUntilEligible} days left)"
    }

    fun previewEligibilityAfterDonation(): String {
        val date = LocalDate.now().plusDays(90)
        return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    }
}
