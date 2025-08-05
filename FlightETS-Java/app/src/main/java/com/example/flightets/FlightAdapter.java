package com.example.flightets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {

    private List<Flight> flights;

    public FlightAdapter(List<Flight> flights) {
        this.flights = flights;
    }

    public static class FlightViewHolder extends RecyclerView.ViewHolder {
        TextView destinationText, departureText, arrivalText, airlineText, priceText;

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            destinationText = itemView.findViewById(R.id.destinationText);
            departureText = itemView.findViewById(R.id.departureText);
            arrivalText = itemView.findViewById(R.id.arrivalText);
            airlineText = itemView.findViewById(R.id.airlineText);
            priceText = itemView.findViewById(R.id.priceText);
        }
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight flight = flights.get(position);
        holder.destinationText.setText("Vers " + flight.getDESTINATION());
        holder.departureText.setText("Départ: " + flight.getHEURE_DEPART());
        holder.arrivalText.setText("Arrivée: " + flight.getHEURE_ARRIVEE());
        holder.airlineText.setText("Compagnie: " + flight.getCOMPAGNIE());
        holder.priceText.setText("Prix: " + flight.getPRIX() + "$");
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    // 🔧 Update flight list
    public void setFlights(List<Flight> newFlights) {
        this.flights = newFlights;
        notifyDataSetChanged();
    }
}
