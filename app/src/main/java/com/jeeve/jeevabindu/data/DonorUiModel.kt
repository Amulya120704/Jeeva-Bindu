package com.jeeve.jeevabindu.data

import com.jeeve.jeevabindu.data.local.DonorEntity
import java.time.LocalDate

data class DonorUiModel(
    val id: Long,
    val name: String,
    val phone: String,
    val bloodGroup: String,
    val age: Int,
    val location: String,
    val isEligible: Boolean,
    val eligibilityDate: LocalDate?,
    val daysUntilEligible: Long,
    val isCurrentUser: Boolean
) {
    val isReadyToDonate: Boolean get() = isEligible
}

fun DonorEntity.toUiModel(today: LocalDate = LocalDate.now()): DonorUiModel {
    val lastDonation = lastDonationEpochDay?.let { LocalDate.ofEpochDay(it) }
    val eligible = DonationRules.isEligible(lastDonation, today)
    val eligibility = lastDonation?.let { DonationRules.eligibilityDate(it) }
    return DonorUiModel(
        id = id,
        name = name,
        phone = phone,
        bloodGroup = bloodGroup,
        age = age,
        location = location,
        isEligible = eligible,
        eligibilityDate = if (eligible) null else eligibility,
        daysUntilEligible = DonationRules.daysUntilEligible(lastDonation, today),
        isCurrentUser = isCurrentUser
    )
}
