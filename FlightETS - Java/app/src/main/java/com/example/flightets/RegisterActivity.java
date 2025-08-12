package com.example.flightets;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flightets.model.RegisterRequest;
import com.example.flightets.model.RegisterResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password, firstName, lastName, telephone;
    private TextView errorMsg, successMsg, backToLogin;
    private Button createAccountButton;

    private AuthApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        telephone = findViewById(R.id.telephone);

        errorMsg = findViewById(R.id.errorMsg);
        successMsg = findViewById(R.id.successMsg);
        backToLogin = findViewById(R.id.backToLogin);
        createAccountButton = findViewById(R.id.createAccountButton);

        api = ApiClient.get().create(AuthApiService.class);

        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        createAccountButton.setOnClickListener(v -> submit());
    }

    private void submit() {
        clearMessages();

        String e  = email.getText().toString().trim();
        String p  = password.getText().toString().trim();
        String fn = firstName.getText().toString().trim();
        String ln = lastName.getText().toString().trim();
        String tel = telephone.getText().toString().trim(); // optional, no format validation

        // Email mandatory + format
        if (e.isEmpty()) {
            showFieldError("Veuillez saisir votre adresse courriel.", email);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            showFieldError("Adresse courriel invalide.", email);
            return;
        }

        // Password min length
        if (p.isEmpty() || p.length() < 6) {
            showFieldError("Le mot de passe doit contenir au moins 6 caractères.", password);
            return;
        }

        // First/Last name required
        if (fn.isEmpty() || ln.isEmpty()) {
            showError("Veuillez remplir le prénom et le nom.");
            return;
        }

        // Build JSON: { email, motDePasse, prenom, nom, telephone }
        RegisterRequest body = new RegisterRequest(e, p, fn, ln, tel);

        createAccountButton.setEnabled(false);

        api.register(body).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> resp) {
                createAccountButton.setEnabled(true);

                if (resp.isSuccessful() && resp.body() != null) {
                    RegisterResponse data = resp.body();
                    if (data.isSuccess()) {
                        // Optional auto-login:
                        // if (data.getToken() != null) {
                        //     getSharedPreferences("auth", MODE_PRIVATE)
                        //         .edit().putString("token", data.getToken()).apply();
                        // }
                        showSuccess("Inscription réussie ! Redirection vers la connexion...");
                        new Handler().postDelayed(() -> {
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }, 1500);
                    } else {
                        String msg = data.getError() != null ? data.getError()
                                : (data.getDetails() != null ? data.getDetails()
                                : "Une erreur est survenue côté serveur.");
                        showError(msg);
                    }
                    return;
                }

                // Non-2xx or empty body → parse error JSON if possible
                try {
                    String raw = resp.errorBody() != null ? resp.errorBody().string() : "";
                    String msg;
                    try {
                        JSONObject obj = new JSONObject(raw);
                        msg = obj.optString("error",
                                obj.optString("details", "Une erreur est survenue côté serveur."));
                    } catch (Exception ignored) {
                        msg = "Réponse non JSON. Vérifie les logs serveur.";
                    }
                    showError(msg);
                } catch (Exception ex) {
                    showError("Erreur serveur");
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                createAccountButton.setEnabled(true);
                showError("Erreur de connexion au serveur.");
            }
        });
    }

    // --- UI helpers ---

    private void clearMessages() {
        errorMsg.setText("");
        successMsg.setText("");
        errorMsg.setVisibility(TextView.GONE);
        successMsg.setVisibility(TextView.GONE);
        resetTint(email, password, firstName, lastName, telephone);
    }

    private void showError(String msg) {
        errorMsg.setText(msg);
        errorMsg.setVisibility(TextView.VISIBLE);
        successMsg.setVisibility(TextView.GONE);
    }

    private void showSuccess(String msg) {
        successMsg.setText(msg);
        successMsg.setVisibility(TextView.VISIBLE);
        errorMsg.setVisibility(TextView.GONE);
    }

    private void showFieldError(String message, EditText field) {
        showError(message);
        field.requestFocus();
        try { field.setBackgroundTintList(getColorStateList(android.R.color.holo_red_dark)); }
        catch (Exception ignored) {}
    }

    private void resetTint(EditText... fields) {
        for (EditText f : fields) {
            try { f.setBackgroundTintList(null); } catch (Exception ignored) {}
        }
    }
}
