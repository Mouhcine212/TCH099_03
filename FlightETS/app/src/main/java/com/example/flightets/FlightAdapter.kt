package com.example.flightets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlightAdapter(private var flights: List<Flight>) :
    RecyclerView.Adapter<FlightAdapter.FlightViewHolder>() {

    class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val destinationText: TextView = itemView.findViewById(R.id.destinationText)
        val departureText: TextView = itemView.findViewById(R.id.departureText)
        val arrivalText: TextView = itemView.findViewById(R.id.arrivalText)
        val airlineText: TextView = itemView.findViewById(R.id.airlineText)
        val priceText: TextView = itemView.findViewById(R.id.priceText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val flight = flights[position]
        holder.destinationText.text = "Vers ${flight.DESTINATION}"
        holder.departureText.text = "Départ: ${flight.HEURE_DEPART}"
        holder.arrivalText.text = "Arrivée: ${flight.HEURE_ARRIVEE}"
        holder.airlineText.text = "Compagnie: ${flight.COMPAGNIE}"
        holder.priceText.text = "Prix: ${flight.PRIX}$"
    }

    override fun getItemCount() = flights.size

    // 🔧 ADD THIS METHOD
    fun setFlights(newFlights: List<Flight>) {
        flights = newFlights
        notifyDataSetChanged()
    }
}
