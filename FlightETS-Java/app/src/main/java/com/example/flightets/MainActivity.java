package com.example.flightets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button reserverVolButton, historiqueButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reserverVolButton = findViewById(R.id.reserverVolButton);
        historiqueButton = findViewById(R.id.historiqueButton);
        loginButton = findViewById(R.id.loginButton);

        reserverVolButton.setOnClickListener(v ->
                startActivity(new Intent(this, SearchFlightActivity.class))
        );

        historiqueButton.setOnClickListener(v ->
                        Toast.makeText(this, "Historique des vols à venir", Toast.LENGTH_SHORT).show()
                // TODO: Navigate to ReservationHistoryActivity
        );

        loginButton.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }
}
