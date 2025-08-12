package com.example.flightets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flightets.model.Flight;
import com.example.flightets.model.ReservationRequest;
import com.example.flightets.model.ReservationResponse;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationActivity extends AppCompatActivity {

    public static final String EXTRA_FLIGHT_JSON = SearchFlightActivity.EXTRA_FLIGHT_JSON;

    private AuthApiService api;
    private String bearer;

    private Flight flight;

    private TextView tvTitle, tvRoute, tvDepart, tvArrivee, tvDuree, tvPriceClass;
    private EditText nomPassager;
    private Button btnConfirmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Guard: requires valid token; will redirect to LoginActivity and finish() if invalid
        if (!AuthGuard.requireLogin(this)) return;

        setContentView(R.layout.activity_reservation);

        // Header (first name if present) + highlight "Réservations"
        String name = AuthGuard.getNameFromJwt(this);
        HeaderNav.bind(this, name, R.id.headerReservations);

        bearer = AuthGuard.getBearer(this);
        api = ApiClient.get().create(AuthApiService.class);

        // Views
        tvTitle      = findViewById(R.id.tvTitle);
        tvRoute      = findViewById(R.id.tvRoutes);
        tvDepart     = findViewById(R.id.tvDepart);
        tvArrivee    = findViewById(R.id.tvArrivee);
        tvDuree      = findViewById(R.id.tvDuree);
        tvPriceClass = findViewById(R.id.tvPriceClass);
        nomPassager  = findViewById(R.id.nomPassagers);
        btnConfirmer = findViewById(R.id.btnConfirmers);

        // Flight from Intent
        String json = getIntent().getStringExtra(EXTRA_FLIGHT_JSON);
        if (json == null) {
            Toast.makeText(this, "Aucun vol sélectionné.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        flight = new Gson().fromJson(json, Flight.class);
        if (flight == null) {
            Toast.makeText(this, "Données de vol invalides.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fill UI
        bindFlightToUi(flight);

        // Fill passenger name from JWT
        if (name != null && !name.isEmpty()) {
            nomPassager.setText(name);
        }

        btnConfirmer.setOnClickListener(v -> submitReservation());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-check token (in case it expired while backgrounded)
        if (!AuthGuard.requireLogin(this)) return;
    }

    private void bindFlightToUi(Flight f) {
        if (tvTitle != null)
            tvTitle.setText(nz(f.getCompagnie()) + " - " + nz(f.getNumeroVol()));

        if (tvRoute != null)
            tvRoute.setText(
                    nz(f.getOrigineVille()) + " (" + nz(f.getOrigineCode()) + ") \u2192 " +
                            nz(f.getDestinationVille()) + " (" + nz(f.getDestinationCode()) + ")"
            );

        Date depart = parseFlexible(f.getHeureDepart());
        Date arrivee = parseFlexible(f.getHeureArrivee());
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());

        if (tvDepart != null)
            tvDepart.setText("Départ : " + (depart != null ? df.format(depart) : nz(f.getHeureDepart())));

        if (tvArrivee != null)
            tvArrivee.setText("Arrivée : " + (arrivee != null ? df.format(arrivee) : nz(f.getHeureArrivee())));

        if (tvDuree != null) {
            if (depart != null && arrivee != null) {
                long minutes = Math.max(0, (arrivee.getTime() - depart.getTime()) / 60000);
                tvDuree.setText(String.format(Locale.getDefault(), "Durée estimée : %dh %dm", minutes / 60, minutes % 60));
            } else {
                tvDuree.setText("Durée estimée : —");
            }
        }

        if (tvPriceClass != null)
            tvPriceClass.setText(stripTrailingZeros(f.getPrix()) + " $ - " + nz(f.getClasse()));
    }

    private void submitReservation() {
        btnConfirmer.setEnabled(false);

        // Random seat like the web (row 1–30, seat A–F)
        int row = 1 + (int) (Math.random() * 30);
        char col = (char) ('A' + (int) (Math.random() * 6));
        String seatNumber = row + String.valueOf(col);

        ReservationRequest req = new ReservationRequest(flight.getIdVol(), seatNumber);

        api.createReservation(bearer, req).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call, Response<ReservationResponse> response) {
                btnConfirmer.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ReservationActivity.this, "Erreur HTTP: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                ReservationResponse body = response.body();
                if (body.isSuccess()) {
                    // Store a few details (like the web flow does)
                    getSharedPreferences("reservation", MODE_PRIVATE).edit()
                            .putInt("id_reservation", body.getIdReservation())
                            .putString("nom", nomPassager.getText().toString())
                            .putString("seat", seatNumber)
                            .apply();

                    Toast.makeText(ReservationActivity.this,
                            "Réservation confirmée (#" + body.getIdReservation() + ")", Toast.LENGTH_LONG).show();

                    Intent pay = new Intent(ReservationActivity.this, PaiementActivity.class);
                    pay.putExtra(PaiementActivity.EXTRA_ID_RESERVATION, body.getIdReservation());
                    pay.putExtra(PaiementActivity.EXTRA_MONTANT, flight.getPrix()); // uses your Flight model price
                    startActivity(pay);
                    finish();
                } else {
                    Toast.makeText(ReservationActivity.this,
                            nz(body.getError(), "Erreur lors de la réservation"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable t) {
                btnConfirmer.setEnabled(true);
                Toast.makeText(ReservationActivity.this, "Erreur réseau lors de la réservation.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---- Helpers ----
    private static String nz(String s) { return s == null ? "" : s; }
    private static String nz(String s, String def) { return (s == null || s.isEmpty()) ? def : s; }

    // Accept "yyyy-MM-dd HH:mm[:ss]" and ISO "yyyy-MM-dd'T'HH:mm[:ss]"
    private static Date parseFlexible(String raw) {
        if (raw == null) return null;
        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm"
        };
        for (String p : patterns) {
            try {
                SimpleDateFormat f = new SimpleDateFormat(p, Locale.US);
                f.setLenient(true);
                return f.parse(raw);
            } catch (ParseException ignored) {}
        }
        return null;
    }

    private static String stripTrailingZeros(double value) {
        if (Math.rint(value) == value) return String.format(Locale.getDefault(), "%.0f", value);
        return String.format(Locale.getDefault(), "%.2f", value);
    }
}
