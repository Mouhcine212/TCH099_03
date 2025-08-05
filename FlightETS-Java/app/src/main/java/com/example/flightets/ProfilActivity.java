package com.example.flightets;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfilActivity extends AppCompatActivity {

    private EditText nomInput, emailInput, telephoneInput;
    private EditText currentPasswordInput, newPasswordInput;
    private Button modifierButton, retourButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        nomInput = findViewById(R.id.nomInput);
        emailInput = findViewById(R.id.emailInput);
        telephoneInput = findViewById(R.id.telephoneInput);
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);

        modifierButton = findViewById(R.id.modifierButton);
        retourButton = findViewById(R.id.retourButton);

        //  Charger données utilisateur (mock)
        nomInput.setText("Jean Dupont");
        emailInput.setText("jean.dupont@example.com");
        telephoneInput.setText("514-123-4567");

        modifierButton.setOnClickListener(v -> {
            String nom = nomInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String tel = telephoneInput.getText().toString().trim();

            String currentPwd = currentPasswordInput.getText().toString().trim();
            String newPwd = newPasswordInput.getText().toString().trim();

            if (nom.isEmpty() || email.isEmpty() || tel.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir toutes les informations", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Send API call here
            Toast.makeText(this, "Profil mis à jour (mock)", Toast.LENGTH_SHORT).show();
        });

        retourButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilActivity.this, SearchFlightActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
