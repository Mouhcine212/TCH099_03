package com.example.flightets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private View header;
    private Button reserverVolButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        header = findViewById(R.id.header);
        reserverVolButton = findViewById(R.id.reserverVolButton);
        loginButton = findViewById(R.id.loginButton);

        updateUiForAuth();

        reserverVolButton.setOnClickListener(v -> {
            // Guard again in case token expired while app was open
            if (!AuthGuard.requireLogin(this)) return;
            startActivity(new Intent(this, SearchFlightActivity.class));
        });

        loginButton.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUiForAuth(); // reflect login/logout changes when returning
    }

    private void updateUiForAuth() {
        boolean loggedIn = !AuthGuard.isTokenInvalid(this);

        // Header visible ONLY when logged in (and bind first name + highlight Home)
        if (loggedIn) {
            header.setVisibility(View.VISIBLE);
            String name = AuthGuard.getNameFromJwt(this);
            HeaderNav.bind(this, name, R.id.headerHome);
        } else {
            header.setVisibility(View.GONE);
        }

        // Show “Réserver un vol” only when logged in
        reserverVolButton.setVisibility(loggedIn ? View.VISIBLE : View.GONE);

        // Show “Se connecter” only when logged out (header has logout already)
        loginButton.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
    }
}
