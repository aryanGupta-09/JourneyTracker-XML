package com.example.mc_a1

import android.content.Context

class JourneyManager(context: Context) {
    val stops: MutableList<Stop>

    init {
        val rawText = FileReader.readRawResource(context, R.raw.stops)
        stops = FileReader.parseStops(rawText)
    }

    fun markNextStop() {
        stops.find { !it.reached }?.reached = true
    }

    fun getProgress(): Int {
        val reached = stops.count { it.reached }
        return (reached.toDouble() / stops.size * 100).toInt()
    }
}
