package com.example.flightets;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flightets.model.ChangePasswordRequest;
import com.example.flightets.model.ChangePasswordResponse;
import com.example.flightets.model.UpdateUserRequest;
import com.example.flightets.model.UpdateUserResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonProfilActivity extends AppCompatActivity {

    private AuthApiService api;
    private String bearer;
    private String rawToken;

    // Profile views
    private EditText etFullName, etEmail, etTelephone;
    private TextView tvMessage;
    private Button btnEditSave, btnBack;

    // Password views
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private TextView tvPasswordMessage;
    private Button btnChangePassword;

    private boolean editMode = false; // like the web: "Modifier" -> fields enabled -> "Confirmer"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AuthGuard.requireLogin(this)) return;

        setContentView(R.layout.activity_profile);

        // Header (there's no "Profile" item in header_nav, so pass 0 for no highlight)
        String nameForHeader = AuthGuard.getNameFromJwt(this);
        HeaderNav.bind(this, nameForHeader, 0);

        api = ApiClient.get().create(AuthApiService.class);
        bearer = AuthGuard.getBearer(this);
        rawToken = AuthGuard.getToken(this); // if you have this helper; otherwise read from prefs

        // Bind views
        etFullName  = findViewById(R.id.etFullName);
        etEmail     = findViewById(R.id.etEmail);
        etTelephone = findViewById(R.id.etTelephone);
        tvMessage   = findViewById(R.id.tvMessage);
        btnEditSave = findViewById(R.id.btnEditSave);
        btnBack     = findViewById(R.id.btnBack);

        etCurrentPassword  = findViewById(R.id.etCurrentPassword);
        etNewPassword      = findViewById(R.id.etNewPassword);
        etConfirmPassword  = findViewById(R.id.etConfirmPassword);
        tvPasswordMessage  = findViewById(R.id.tvPasswordMessage);
        btnChangePassword  = findViewById(R.id.btnChangePassword);

        // Prefill from JWT
        PrefilledUser u = parseJwtUser(rawToken);
        if (u != null) {
            etFullName.setText(nz(u.nom));
            etEmail.setText(nz(u.email));
            etTelephone.setText(nz(u.telephone));
        }

        // Start disabled (web has disabled then toggle)
        setProfileEnabled(false);

        btnEditSave.setOnClickListener(v -> {
            if (!editMode) {
                // Switch to edit mode
                setProfileEnabled(true);
                btnEditSave.setText("Confirmer");
                editMode = true;
                tvMessage.setText("");
            } else {
                // Submit profile update
                submitProfile();
            }
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnChangePassword.setOnClickListener(v -> submitPasswordChange());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AuthGuard.requireLogin(this)) return;
    }

    private void setProfileEnabled(boolean enabled) {
        etFullName.setEnabled(enabled);
        etEmail.setEnabled(enabled);
        etTelephone.setEnabled(enabled);
    }

    private void submitProfile() {
        tvMessage.setText("");

        String nom = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telRaw = etTelephone.getText().toString().trim();

        if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(email)) {
            tvMessage.setTextColor(0xFFFF4D4D);
            tvMessage.setText("Nom et email requis.");
            return;
        }

        // Web validation: phone optional but if present must be 10 digits.
        String telDigits = telRaw.replaceAll("\\D", "");
        if (!TextUtils.isEmpty(telDigits) && telDigits.length() != 10) {
            tvMessage.setTextColor(0xFFFF4D4D);
            tvMessage.setText("Téléphone invalide (10 chiffres requis).");
            return;
        }

        UpdateUserRequest req = new UpdateUserRequest(nom, email, telDigits);

        api.updateUser(bearer, req).enqueue(new Callback<UpdateUserResponse>() {
            @Override
            public void onResponse(Call<UpdateUserResponse> call, Response<UpdateUserResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    tvMessage.setTextColor(0xFFFF4D4D);
                    tvMessage.setText("Erreur HTTP: " + response.code());
                    return;
                }
                UpdateUserResponse body = response.body();
                if (body.isSuccess()) {
                    // Save new token if provided (server returns refreshed JWT with updated claims)
                    if (!TextUtils.isEmpty(body.getToken())) {
                        saveToken(body.getToken());
                        rawToken = body.getToken();
                        PrefilledUser u = parseJwtUser(rawToken);
                        String headerName = (u != null ? u.nom : nom);
                        HeaderNav.bind(MonProfilActivity.this, headerName, 0);
                    }

                    tvMessage.setTextColor(0xFF14CA50);
                    tvMessage.setText(nz(body.getMessage(), "Profil mis à jour avec succès !"));

                    // Lock fields again and reset button text
                    setProfileEnabled(false);
                    btnEditSave.setText("Modifier");
                    editMode = false;

                    // (Optional) Navigate home like the web after a short delay
                    // startActivity(new Intent(MonProfilActivity.this, MainActivity.class));
                    // finish();
                } else {
                    tvMessage.setTextColor(0xFFFF4D4D);
                    tvMessage.setText(nz(body.getError(), "Erreur lors de la mise à jour."));
                }
            }

            @Override
            public void onFailure(Call<UpdateUserResponse> call, Throwable t) {
                tvMessage.setTextColor(0xFFFF4D4D);
                tvMessage.setText("Erreur serveur.");
            }
        });
    }

    private void submitPasswordChange() {
        tvPasswordMessage.setText("");

        String current = etCurrentPassword.getText().toString().trim();
        String nw = etNewPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(current) || TextUtils.isEmpty(nw) || TextUtils.isEmpty(confirm)) {
            tvPasswordMessage.setTextColor(0xFFFF4D4D);
            tvPasswordMessage.setText("Tous les champs sont obligatoires.");
            return;
        }
        if (nw.length() < 6) {
            tvPasswordMessage.setTextColor(0xFFFF4D4D);
            tvPasswordMessage.setText("Le nouveau mot de passe doit contenir au moins 6 caractères.");
            return;
        }
        if (!nw.equals(confirm)) {
            tvPasswordMessage.setTextColor(0xFFFF4D4D);
            tvPasswordMessage.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        ChangePasswordRequest req = new ChangePasswordRequest(current, nw);
        api.changePassword(bearer, req).enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    tvPasswordMessage.setTextColor(0xFFFF4D4D);
                    tvPasswordMessage.setText("Erreur HTTP: " + response.code());
                    return;
                }
                ChangePasswordResponse body = response.body();
                if (body.isSuccess()) {
                    tvPasswordMessage.setTextColor(0xFF14CA50);
                    tvPasswordMessage.setText(nz(body.getMessage(), "Mot de passe modifié avec succès"));
                    etCurrentPassword.setText("");
                    etNewPassword.setText("");
                    etConfirmPassword.setText("");

                    // Like web: force re-login
                    getSharedPreferences("auth", MODE_PRIVATE).edit().clear().apply();
                    Toast.makeText(MonProfilActivity.this, "Veuillez vous reconnecter.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MonProfilActivity.this, LoginActivity.class));
                    finish();
                } else {
                    tvPasswordMessage.setTextColor(0xFFFF4D4D);
                    tvPasswordMessage.setText(nz(body.getError(), "Erreur lors du changement de mot de passe."));
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                tvPasswordMessage.setTextColor(0xFFFF4D4D);
                tvPasswordMessage.setText("Erreur serveur.");
            }
        });
    }

    private void saveToken(String token) {
        getSharedPreferences("auth", MODE_PRIVATE).edit()
                .putString("token", token)
                .apply();
        bearer = "Bearer " + token;
    }

    private PrefilledUser parseJwtUser(String token) {
        try {
            if (TextUtils.isEmpty(token)) return null;
            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;
            byte[] payload = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            JSONObject obj = new JSONObject(new String(payload));
            PrefilledUser u = new PrefilledUser();
            u.nom = obj.optString("nom", "");
            u.email = obj.optString("email", "");
            u.telephone = obj.optString("telephone", "");
            return u;
        } catch (Throwable t) {
            return null;
        }
    }

    // Tiny holder for JWT claims
    private static class PrefilledUser {
        String nom, email, telephone;
    }

    private static String nz(String s) { return s == null ? "" : s; }
    private static String nz(String s, String def) { return s == null ? def : s; }
}
