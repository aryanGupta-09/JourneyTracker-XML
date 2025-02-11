package com.example.mc_a1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StopAdapter(
    private var stops: List<Stop>,
    private var isKm: Boolean,
    private var currentPosition: Int = -1
) : RecyclerView.Adapter<StopAdapter.StopViewHolder>() {

    private val averageSpeedKmH = 900
    private val averageSpeedMilesH = 560

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stop, parent, false)
        return StopViewHolder(view)
    }

    override fun onBindViewHolder(holder: StopViewHolder, position: Int) {
        val stop = stops[position]
        val isVisited = position <= currentPosition && currentPosition >= 0
        val textColor = if (isVisited) {
            holder.itemView.context.getColor(android.R.color.holo_green_dark)
        } else {
            holder.itemView.context.getColor(android.R.color.black)
        }

        holder.stopName.apply {
            text = stop.name
            setTextColor(textColor)
        }
        
        holder.distance.apply {
            text = if (isKm) {
                String.format("%.1f km", stop.distanceKm)
            } else {
                String.format("%.1f miles", stop.distanceMiles())
            }
            setTextColor(textColor)
        }
        
        holder.visaRequirement.apply {
            text = if (stop.transitVisaRequired) "Visa Required" else "No Visa"
            setTextColor(textColor)
        }

        val timeRemaining = if (isKm) {
            stop.distanceKm / averageSpeedKmH.toDouble()
        } else {
            stop.distanceMiles() / averageSpeedMilesH.toDouble()
        }
        val hours = timeRemaining.toInt()
        val minutes = ((timeRemaining - hours) * 60).toInt()
        
        holder.timeRemaining.apply {
            text = String.format("%d hours %d minutes", hours, minutes)
            setTextColor(textColor)
        }
    }

    override fun getItemCount(): Int = stops.size

    fun updateUnit(isKm: Boolean) {
        this.isKm = isKm
        notifyDataSetChanged()
    }

    fun updateProgress(newPosition: Int) {
        currentPosition = newPosition
        notifyDataSetChanged()
    }

    class StopViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stopName: TextView = view.findViewById(R.id.stopName)
        val distance: TextView = view.findViewById(R.id.stopDistance)
        val visaRequirement: TextView = view.findViewById(R.id.stopVisaRequirement)
        val timeRemaining: TextView = view.findViewById(R.id.stopTimeRemaining)
    }
}