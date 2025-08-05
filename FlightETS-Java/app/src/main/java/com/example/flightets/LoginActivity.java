package com.example.flightets;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    TextView createAccountLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        createAccountLink = findViewById(R.id.createAccountLink);

        loginButton.setOnClickListener(v -> {
            Toast.makeText(this, "Login clicked", Toast.LENGTH_SHORT).show();
            // TODO: Replace with Retrofit login call
            startActivity(new Intent(this, MainActivity.class));
        });

        createAccountLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
