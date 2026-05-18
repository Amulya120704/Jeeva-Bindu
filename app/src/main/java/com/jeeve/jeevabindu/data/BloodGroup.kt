package com.jeeve.jeevabindu.data

enum class BloodGroup(val label: String) {
    A_POS("A+"),
    A_NEG("A-"),
    B_POS("B+"),
    B_NEG("B-"),
    AB_POS("AB+"),
    AB_NEG("AB-"),
    O_POS("O+"),
    O_NEG("O-");

    companion object {
        fun fromLabel(label: String): BloodGroup? =
            entries.firstOrNull { it.label.equals(label, ignoreCase = true) }
    }
}
