package com.example.flightets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flightets.model.LoginRequest;
import com.example.flightets.model.LoginResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, motDePasseInput;
    private TextView errorMsg;
    private Button loginButton;
    private TextView createAccountLink;

    private AuthApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        motDePasseInput = findViewById(R.id.passwordInput);
        errorMsg = findViewById(R.id.errorMsg);
        loginButton = findViewById(R.id.loginButton);
        createAccountLink = findViewById(R.id.createAccountLink);

        api = ApiClient.get().create(AuthApiService.class);

        loginButton.setOnClickListener(v -> doLogin());
        createAccountLink.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void doLogin() {
        errorMsg.setText("");
        String email = emailInput.getText().toString().trim();
        String motDePasse = motDePasseInput.getText().toString();

        if (email.isEmpty() || motDePasse.isEmpty()) {
            errorMsg.setText("Veuillez remplir l’email et le mot de passe.");
            return;
        }

        loginButton.setEnabled(false);

        LoginRequest req = new LoginRequest(email, motDePasse);
        api.login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> resp) {
                loginButton.setEnabled(true);

                if (resp.isSuccessful() && resp.body() != null) {
                    LoginResponse data = resp.body();
                    if (data.isSuccess()) {
                        saveToken(data.getToken());
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        errorMsg.setText(data.getError() != null ? data.getError() : "Erreur inconnue");
                    }
                } else {
                    // Try to parse error JSON
                    try {
                        String errorJson = resp.errorBody() != null ? resp.errorBody().string() : "";
                        JSONObject obj = new JSONObject(errorJson);
                        String serverError = obj.optString("error", "Erreur inconnue");
                        errorMsg.setText(serverError);
                    } catch (Exception e) {
                        errorMsg.setText("Erreur serveur");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                errorMsg.setText("Erreur réseau : " + t.getMessage());
            }
        });

    }

    private void saveToken(String token) {
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        prefs.edit().putString("token", token).apply();
    }
}
