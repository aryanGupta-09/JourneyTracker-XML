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
    private lateinit var journeyCompletedText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var switchUnitButton: Button
    private lateinit var nextStopButton: Button
    private lateinit var progressArrow: View
    private lateinit var distanceCovered: TextView
    private lateinit var distanceRemaining: TextView

    private var isKm = true
    private lateinit var journeyManager: JourneyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize JourneyManager first
        journeyManager = JourneyManager(this)

        // Initialize recyclerView before using it
        recyclerView = findViewById(R.id.recyclerView)

        // Initialize other views
        initializeViews()

        // Set up RecyclerView (now recyclerView is initialized)
        setupRecyclerView()

        // Initialize progress
        updateProgress()

        // Set up button listeners
        setupButtonListeners()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerView)
        progressText = findViewById(R.id.progressText)
        journeyCompletedText = findViewById(R.id.journeyCompletedText)
        progressBar = findViewById(R.id.progressBar)
        switchUnitButton = findViewById(R.id.switchUnitButton)
        nextStopButton = findViewById(R.id.nextStopButton)
        progressArrow = findViewById(R.id.progressArrow)
        distanceCovered = findViewById(R.id.distanceCovered)
        distanceRemaining = findViewById(R.id.distanceRemaining)
    }

    private fun setupRecyclerView() {
        adapter = StopAdapter(journeyManager.stops, isKm)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Set up scroll listener
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateProgressArrow()
            }
        })
    }

    private fun setupButtonListeners() {
        switchUnitButton.setOnClickListener {
            isKm = !isKm
            adapter.updateUnit(isKm)
            updateProgress()
        }

        nextStopButton.setOnClickListener {
            journeyManager.markNextStop()
            updateProgress()
            adapter.updateProgress(journeyManager.getCurrentPosition())
            scrollToNextStop()
        }
    }

    private fun updateProgress() {
        val progress = journeyManager.getProgress()
        progressText.text = String.format("Progress: %.1f%%", progress)
        progressBar.progress = progress.toInt()

        // Show/hide journey completed text
        journeyCompletedText.visibility = if (progress >= 99.9) View.VISIBLE else View.GONE

        // Update distance information
        val covered = if (isKm) {
            String.format("%.1f km", journeyManager.getDistanceCovered())
        } else {
            String.format("%.1f miles", journeyManager.getDistanceCovered() * 0.621371)
        }
        
        val remaining = if (isKm) {
            String.format("%.1f km", journeyManager.getRemainingDistance())
        } else {
            String.format("%.1f miles", journeyManager.getRemainingDistance() * 0.621371)
        }

        distanceCovered.text = "Covered: $covered"
        distanceRemaining.text = "Remaining: $remaining"
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
                
                if (currentPosition >= firstVisibleItem && currentPosition <= lastVisibleItem) {
                    val currentView = layoutManager.findViewByPosition(currentPosition)
                    currentView?.let { view ->
                        visibleHeight = view.bottom - view.height/4
                    }
                } else if (currentPosition > lastVisibleItem) {
                    visibleHeight = recyclerView.height
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
        
        if (currentPosition == lastVisibleItem) {
            layoutManager.scrollToPositionWithOffset(currentPosition, 0)
        }
    }
}