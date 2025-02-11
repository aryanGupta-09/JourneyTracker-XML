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

    fun getProgress(): Int {
        return if (stops.isNotEmpty() && currentPosition >= 0) {
            ((currentPosition + 1) * 100) / stops.size
        } else 0
    }
}