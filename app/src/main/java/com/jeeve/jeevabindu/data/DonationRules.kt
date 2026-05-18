package com.jeeve.jeevabindu.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object DonationRules {
    const val COOLDOWN_DAYS = 90

    fun eligibilityDate(lastDonationDate: LocalDate): LocalDate =
        lastDonationDate.plusDays(COOLDOWN_DAYS.toLong())

    fun isEligible(lastDonationDate: LocalDate?, today: LocalDate = LocalDate.now()): Boolean {
        if (lastDonationDate == null) return true
        return !today.isBefore(eligibilityDate(lastDonationDate))
    }

    fun daysUntilEligible(lastDonationDate: LocalDate?, today: LocalDate = LocalDate.now()): Long {
        if (lastDonationDate == null) return 0
        val eligibleOn = eligibilityDate(lastDonationDate)
        return if (!today.isBefore(eligibleOn)) {
            0
        } else {
            ChronoUnit.DAYS.between(today, eligibleOn)
        }
    }
}
