package com.example.mc_a1

data class Stop(
    val name: String,
    val distanceKm: Double,
    val transitVisaRequired: Boolean,
    var reached: Boolean = false
) {
    fun distanceMiles(): Double {
        return distanceKm * 0.621371
    }
}
