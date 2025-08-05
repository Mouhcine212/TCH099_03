package com.example.flightets;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class FlightListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noResultsText;
    private FlightAdapter flightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list); // Make sure this XML exists

        recyclerView = findViewById(R.id.recyclerViewFlights);
        noResultsText = findViewById(R.id.noResultsText);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get destination + date from intent
        String destination = getIntent().getStringExtra("destination");
        String date = getIntent().getStringExtra("date");

        // Dummy data
        List<Flight> dummyFlights = Arrays.asList(
                new Flight(1, "Montréal", destination, date + " 08:00", date + " 10:00", "Air Canada", "AC123", 199.99, "Économie", 45),
                new Flight(2, "Montréal", destination, date + " 12:30", date + " 14:20", "Air Transat", "TS456", 179.50, "Économie", 38),
                new Flight(3, "Montréal", destination, date + " 18:00", date + " 20:00", "Air France", "AF789", 220.75, "Affaires", 12)
        );

        if (dummyFlights.isEmpty()) {
            noResultsText.setText("Aucun vol trouvé.");
            noResultsText.setVisibility(TextView.VISIBLE);
        } else {
            flightAdapter = new FlightAdapter(dummyFlights);
            recyclerView.setAdapter(flightAdapter);
            noResultsText.setVisibility(TextView.GONE);
        }
    }
}
