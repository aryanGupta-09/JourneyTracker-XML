package com.example.mc_a1

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

object FileReader {
    fun readRawResource(context: Context, resId: Int): String {
        val inputStream = context.resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.readText()
    }

    fun parseStops(text: String): MutableList<Stop> {
        return text.lines().mapNotNull {
            val parts = it.split(",")
            if (parts.size == 3) {
                Stop(parts[0].trim(), parts[1].trim().toDouble(), parts[2].trim().toBooleanStrictOrNull() ?: false)
            } else null
        }.toMutableList()
    }
}
