package com.example.flightets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView logoutButton;
    private TextView manageReservationsCard;
    private TextView viewFlightsCard;
    private TextView statisticsCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        logoutButton = findViewById(R.id.headerLogoutButton);
        manageReservationsCard = findViewById(R.id.manageReservationsCard);
        viewFlightsCard = findViewById(R.id.viewFlightsCard);
        statisticsCard = findViewById(R.id.statisticsCard);

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        manageReservationsCard.setOnClickListener(v ->
                Toast.makeText(this, "Gérer les réservations (bientôt disponible)", Toast.LENGTH_SHORT).show()
        );

        viewFlightsCard.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, SearchFlightActivity.class);
            startActivity(intent);
        });

        statisticsCard.setOnClickListener(v ->
                Toast.makeText(this, "Statistiques détaillées (à venir)", Toast.LENGTH_SHORT).show()
        );
    }
}
