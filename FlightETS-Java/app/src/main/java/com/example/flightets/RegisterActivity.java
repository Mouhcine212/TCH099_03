package com.example.flightets;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText emailInput, passwordInput, confirmPasswordInput;
    Button createAccountButton;

    TextView backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        createAccountButton = findViewById(R.id.createAccountButton);
        backToLogin = findViewById(R.id.backToLogin);

        createAccountButton.setOnClickListener(v -> {

            Toast.makeText(this, "Compte créé avec succès!", Toast.LENGTH_SHORT).show();

            //
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }


}
