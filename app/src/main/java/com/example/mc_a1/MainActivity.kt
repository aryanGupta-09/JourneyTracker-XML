package com.example.mc_a1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StopAdapter
    private lateinit var progressText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var switchUnitButton: Button
    private lateinit var nextStopButton: Button
    private lateinit var progressArrow: View

    private var isKm = true
    private lateinit var journeyManager: JourneyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView)
        progressText = findViewById(R.id.progressText)
        progressBar = findViewById(R.id.progressBar)
        switchUnitButton = findViewById(R.id.switchUnitButton)
        nextStopButton = findViewById(R.id.nextStopButton)
        progressArrow = findViewById(R.id.progressArrow)

        journeyManager = JourneyManager(this)

        // Set up RecyclerView
        adapter = StopAdapter(journeyManager.stops, isKm)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Initialize progress
        updateProgress()

        // Set up scroll listener to update arrow position
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateProgressArrow()
            }
        })

        // Switch Units button click listener
        switchUnitButton.setOnClickListener {
            isKm = !isKm
            adapter.updateUnit(isKm)
        }

        // Next Stop button click listener
        nextStopButton.setOnClickListener {
            journeyManager.markNextStop()
            updateProgress()
            adapter.updateProgress(journeyManager.getCurrentPosition())
            scrollToNextStop()
        }
    }

    private fun updateProgress() {
        val progress = journeyManager.getProgress()
        progressText.text = "Progress: $progress%"
        progressBar.progress = progress
        updateProgressArrow()
    }

    private fun updateProgressArrow() {
        recyclerView.post {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            val currentPosition = journeyManager.getCurrentPosition()

            if (currentPosition >= 0) {
                var visibleHeight = 0
                
                // If current position is visible, calculate height up to it
                if (currentPosition >= firstVisibleItem && currentPosition <= lastVisibleItem) {
                    val currentView = layoutManager.findViewByPosition(currentPosition)
                    currentView?.let { view ->
                        // Add extra padding to accommodate the circle
                        visibleHeight = view.bottom + 20  // Adding padding for circle
                    }
                } 
                // If current position is below visible area, use full recycler height
                else if (currentPosition > lastVisibleItem) {
                    visibleHeight = recyclerView.height
                }
                // If current position is above visible area, use zero height
                else {
                    visibleHeight = 0
                }

                progressArrow.layoutParams.height = visibleHeight
                progressArrow.requestLayout()
            } else {
                progressArrow.layoutParams.height = 0
                progressArrow.requestLayout()
            }
        }
    }
    
    private fun scrollToNextStop() {
        val currentPosition = journeyManager.getCurrentPosition()
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        
        // Scroll when reaching last visible item
        if (currentPosition == lastVisibleItem) {
            layoutManager.scrollToPositionWithOffset(currentPosition, 0)
        }
    }
}