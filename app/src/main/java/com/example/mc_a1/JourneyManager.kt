package com.example.mc_a1

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

class JourneyManager(context: Context) {
    val stops: List<Stop>
    private var currentPosition = -1

    init {
        stops = loadStopsFromFile(context)
    }

    private fun loadStopsFromFile(context: Context): List<Stop> {
        val stops = mutableListOf<Stop>()
        val inputStream = context.resources.openRawResource(R.raw.stops)
        val reader = BufferedReader(InputStreamReader(inputStream))
        
        reader.useLines { lines ->
            lines.forEach { line ->
                val parts = line.split(",")
                if (parts.size == 3) {
                    val name = parts[0].trim()
                    val distanceKm = parts[1].trim().toDoubleOrNull() ?: 0.0
                    val transitVisaRequired = parts[2].trim().toBoolean()
                    stops.add(Stop(name, distanceKm, transitVisaRequired))
                }
            }
        }
        return stops
    }

    fun markNextStop() {
        if (currentPosition < stops.size - 1) {
            currentPosition++
        }
    }

    fun getCurrentPosition(): Int {
        return currentPosition
    }

    fun getProgress(): Double {
        val totalDistance = getTotalDistance()
        if (totalDistance == 0.0) return 0.0
        
        val coveredDistance = getDistanceCovered()
        return ((coveredDistance * 100) / totalDistance)
    }

    fun getTotalDistance(): Double {
        return stops.sumOf { it.distanceKm }
    }

    fun getDistanceCovered(): Double {
        return if (currentPosition >= 0) {
            stops.take(currentPosition + 1).sumOf { it.distanceKm }
        } else 0.0
    }

    fun getRemainingDistance(): Double {
        val total = getTotalDistance()
        val covered = getDistanceCovered()
        return total - covered
    }

    fun resetJourney() {
        currentPosition = -1
    }
}