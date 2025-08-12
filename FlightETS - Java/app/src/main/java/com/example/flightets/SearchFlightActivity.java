package com.example.flightets;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightets.model.Airport;
import com.example.flightets.model.Flight;
import com.example.flightets.model.SearchRequest;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFlightActivity extends AppCompatActivity {

    public static final String EXTRA_FLIGHT_JSON = "EXTRA_FLIGHT_JSON";

    // Form
    private AutoCompleteTextView originInput, destinationInput;
    private EditText dateInput;
    private Button searchButton;

    // Results card (second white box)
    private View resultsCard;
    private TextView noResultsText;
    private RecyclerView resultsRecyclerView;

    private AuthApiService api;
    private String bearer; // "Bearer <token>"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ---- Auth guard BEFORE any UI work ----
        if (!AuthGuard.requireLogin(this)) return;

        setContentView(R.layout.activity_search_flight);

        // Header (use JWT name if available, else fallback to stored profile)
        String nameFromJwt = AuthGuard.getNameFromJwt(this);
        if (nameFromJwt == null) {
            SharedPreferences prof = getSharedPreferences("profile", MODE_PRIVATE);
            nameFromJwt = prof.getString("fullName", "");
        }
        HeaderNav.bind(this, nameFromJwt, R.id.headerSearch);

        bearer = AuthGuard.getBearer(this);

        // Bind views
        originInput = findViewById(R.id.originInput);
        destinationInput = findViewById(R.id.destinationInput);
        dateInput = findViewById(R.id.dateInput);
        searchButton = findViewById(R.id.searchButton);

        resultsCard = findViewById(R.id.resultsCard);
        noResultsText = findViewById(R.id.noResultsText);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        if (resultsCard != null) resultsCard.setVisibility(View.GONE);

        // Retrofit
        api = ApiClient.get().create(AuthApiService.class);

        // Suggestions
        loadAirportSuggestions();

        // Date picker
        dateInput.setOnClickListener(v -> showDatePicker());

        // Results list
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(new FlightAdapter(Collections.emptyList()));

        // Search
        searchButton.setOnClickListener(v -> {
            String origine = originInput.getText().toString().trim();
            String destination = destinationInput.getText().toString().trim();
            String dateDepart = dateInput.getText().toString().trim(); // optional

            if (origine.isEmpty() || destination.isEmpty()) {
                Toast.makeText(this, "Veuillez renseigner l’origine et la destination.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (resultsCard != null) resultsCard.setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);
            resultsRecyclerView.setVisibility(View.GONE);
            searchButton.setEnabled(false);

            searchFlights(origine, destination, dateDepart.isEmpty() ? null : dateDepart);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-check in case token expired while app was backgrounded
        if (!AuthGuard.requireLogin(this)) return;
    }

    private void loadAirportSuggestions() {
        api.getAirports(bearer).enqueue(new Callback<List<Airport>>() {
            @Override
            public void onResponse(Call<List<Airport>> call, Response<List<Airport>> response) {
                if (response.code() == 401) { AuthGuard.requireLogin(SearchFlightActivity.this); return; }
                if (!response.isSuccessful() || response.body() == null) return;

                List<String> items = new ArrayList<>();
                for (Airport a : response.body()) {
                    items.add(a.getVille() + " (" + a.getCodeIata() + ")");
                }

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<>(SearchFlightActivity.this, android.R.layout.simple_list_item_1, items);
                originInput.setAdapter(adapter);
                destinationInput.setAdapter(adapter);
                originInput.setThreshold(1);
                destinationInput.setThreshold(1);

                originInput.setOnClickListener(v -> originInput.showDropDown());
                destinationInput.setOnClickListener(v -> destinationInput.showDropDown());
            }

            @Override
            public void onFailure(Call<List<Airport>> call, Throwable t) {
                // silent; user can type manually
            }
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, (view, year, month, day) -> {
            String mm = String.format(Locale.US, "%02d", month + 1);
            String dd = String.format(Locale.US, "%02d", day);
            dateInput.setText(year + "-" + mm + "-" + dd);
        }, y, m, d).show();
    }

    private void searchFlights(String origine, String destination, @Nullable String dateDepart) {
        SearchRequest request = new SearchRequest(origine, destination, dateDepart);

        api.searchFlights(bearer, request).enqueue(new Callback<List<Flight>>() {
            @Override
            public void onResponse(Call<List<Flight>> call, Response<List<Flight>> response) {
                searchButton.setEnabled(true);

                if (response.code() == 401) { AuthGuard.requireLogin(SearchFlightActivity.this); return; }

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    resultsRecyclerView.setAdapter(new FlightAdapter(response.body(), flight -> {
                        String json = new Gson().toJson(flight);
                        startActivity(
                                new android.content.Intent(SearchFlightActivity.this, ReservationActivity.class)
                                        .putExtra(EXTRA_FLIGHT_JSON, json)
                        );
                    }));
                    noResultsText.setVisibility(View.GONE);
                    resultsRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    noResultsText.setVisibility(View.VISIBLE);
                    resultsRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<Flight>> call, Throwable t) {
                searchButton.setEnabled(true);
                Toast.makeText(SearchFlightActivity.this, "Erreur lors de la recherche", Toast.LENGTH_SHORT).show();
                noResultsText.setVisibility(View.VISIBLE);
                resultsRecyclerView.setVisibility(View.GONE);
                if (resultsCard != null) resultsCard.setVisibility(View.VISIBLE);
            }
        });
    }
}
