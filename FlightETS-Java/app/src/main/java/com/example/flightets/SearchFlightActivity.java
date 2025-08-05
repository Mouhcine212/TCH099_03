package com.example.flightets;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.flightets.AuthApiService;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFlightActivity extends AppCompatActivity {

    private EditText destinationInput, dateInput;
    private Button searchButton;
    private RecyclerView resultsRecyclerView;
    private TextView noResultsText;

    private FlightAdapter flightAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_flight);

        destinationInput = findViewById(R.id.destinationInput);
        dateInput = findViewById(R.id.dateInput);
        searchButton = findViewById(R.id.searchButton);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        noResultsText = findViewById(R.id.noResultsText);

        flightAdapter = new FlightAdapter(List.of());
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(flightAdapter);

        searchButton.setOnClickListener(view -> {
            String destination = destinationInput.getText().toString();
            String date = dateInput.getText().toString();

            if (destination.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            searchFlights(destination, date);
        });
    }

    private void searchFlights(String destination, String date) {
        SearchRequest request = new SearchRequest(destination, date);

        Call<List<Flight>> call = RetrofitClient.getInstance()
                .searchFlights(request);

        call.enqueue(new Callback<List<Flight>>() {
            @Override
            public void onResponse(Call<List<Flight>> call, Response<List<Flight>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    flightAdapter = new FlightAdapter(response.body());
                    resultsRecyclerView.setAdapter(flightAdapter);
                    noResultsText.setVisibility(View.GONE);
                    resultsRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    noResultsText.setVisibility(View.VISIBLE);
                    resultsRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Flight>> call, Throwable t) {
                Toast.makeText(SearchFlightActivity.this, "Erreur lors de la recherche", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
