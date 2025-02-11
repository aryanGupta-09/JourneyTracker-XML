package com.example.mc_a1

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StopAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var switchUnitButton: Button
    private lateinit var nextStopButton: Button

    private var isKm = true
    private lateinit var journeyManager: JourneyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progressText)
        switchUnitButton = findViewById(R.id.switchUnitButton)
        nextStopButton = findViewById(R.id.nextStopButton)

        journeyManager = JourneyManager(this)

        adapter = StopAdapter(journeyManager.stops, isKm)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        updateProgress()

        switchUnitButton.setOnClickListener {
            isKm = !isKm
            adapter.updateUnit(isKm)
        }

        nextStopButton.setOnClickListener {
            journeyManager.markNextStop()
            updateProgress()
            adapter.notifyDataSetChanged()
        }
    }

    private fun updateProgress() {
        val progress = journeyManager.getProgress()
        progressBar.progress = progress
        progressText.text = "Progress: $progress%"
    }
}
