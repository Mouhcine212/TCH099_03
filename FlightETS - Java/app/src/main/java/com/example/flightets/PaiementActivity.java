package com.example.flightets;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flightets.model.PaymentRequest;
import com.example.flightets.model.PaymentResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaiementActivity extends AppCompatActivity {

    public static final String EXTRA_ID_RESERVATION = "id_reservation";
    public static final String EXTRA_MONTANT = "montant";

    private AuthApiService api;
    private String bearer;

    private int idReservation = 0;
    private double montant = 0.0;

    private TextView tvAmount;
    private EditText etCardNumber, etExpiry, etCvv;
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AuthGuard.requireLogin(this)) return;

        setContentView(R.layout.activity_payment);

        // Header
        String name = AuthGuard.getNameFromJwt(this);
        HeaderNav.bind(this, name, R.id.headerReservations);

        bearer = AuthGuard.getBearer(this);
        api = ApiClient.get().create(AuthApiService.class);

        tvAmount = findViewById(R.id.tvAmount);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCvv = findViewById(R.id.etCvv);
        btnPay = findViewById(R.id.btnPay);

        // Get reservation + amount (prefer Intent extras from ReservationActivity)
        idReservation = getIntent().getIntExtra(EXTRA_ID_RESERVATION, 0);
        montant = getIntent().getDoubleExtra(EXTRA_MONTANT, 0.0);

        // Fallback: SharedPreferences if needed
        if (idReservation == 0) {
            idReservation = getSharedPreferences("reservation", MODE_PRIVATE).getInt("id_reservation", 0);
        }

        if (montant == 0.0) {
            // If you saved the price elsewhere, load it; otherwise keep 0 (we still allow, but show unknown)
            // e.g., montant = Double.longBitsToDouble(getSharedPreferences("reservation", MODE_PRIVATE).getLong("montantBits", 0L));
        }

        tvAmount.setText(montant > 0 ? "Montant : " + strip(montant) + " $" : "Montant : —");

        // Input formatting like web
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private boolean self;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable e) {
                if (self) return;
                self = true;
                String digits = e.toString().replaceAll("\\D", "");
                String spaced = digits.replaceAll("(.{4})", "$1 ").trim();
                etCardNumber.setText(spaced);
                etCardNumber.setSelection(spaced.length());
                self = false;
            }
        });

        etExpiry.addTextChangedListener(new TextWatcher() {
            private boolean self;
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable e) {
                if (self) return;
                self = true;
                String digits = e.toString().replaceAll("\\D", "");
                if (digits.length() >= 3) {
                    digits = digits.substring(0, Math.min(4, digits.length()));
                    String mm = digits.substring(0, 2);
                    String yy = digits.length() > 2 ? digits.substring(2) : "";
                    String val = yy.isEmpty() ? mm : mm + "/" + yy;
                    etExpiry.setText(val);
                    etExpiry.setSelection(val.length());
                } else {
                    etExpiry.setText(digits);
                    etExpiry.setSelection(digits.length());
                }
                self = false;
            }
        });

        btnPay.setOnClickListener(v -> submitPayment());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AuthGuard.requireLogin(this)) return;
    }

    private void submitPayment() {
        if (idReservation <= 0) {
            Toast.makeText(this, "Aucune réservation à payer.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String card = etCardNumber.getText().toString().replace(" ", "").trim();
        String exp = etExpiry.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (!card.matches("^\\d{16}$")) {
            Toast.makeText(this, "Numéro de carte invalide (16 chiffres).", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!exp.matches("^(0[1-9]|1[0-2])\\/\\d{2}$")) {
            Toast.makeText(this, "Date d'expiration invalide (MM/AA).", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!cvv.matches("^\\d{3}$")) {
            Toast.makeText(this, "CVV invalide (3 chiffres).", Toast.LENGTH_SHORT).show();
            return;
        }

        double amountToSend = (montant > 0 ? montant : 0.0);
        PaymentRequest req = new PaymentRequest(idReservation, amountToSend, "Carte");

        btnPay.setEnabled(false);

        api.processPayment(bearer, req).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                btnPay.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(PaiementActivity.this, "Erreur HTTP: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                PaymentResponse body = response.body();
                if (body.isSuccess()) {
                    // Clear local temp reservation info
                    getSharedPreferences("reservation", MODE_PRIVATE).edit().clear().apply();

                    Toast.makeText(PaiementActivity.this, "Paiement réussi !", Toast.LENGTH_LONG).show();

                    // Go to history (or main)
                    startActivity(new Intent(PaiementActivity.this, ReservationListActivity.class));
                    finish();
                } else {
                    Toast.makeText(PaiementActivity.this, nz(body.getError(), "Erreur lors du paiement."), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                btnPay.setEnabled(true);
                Toast.makeText(PaiementActivity.this, "Erreur réseau lors du paiement.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String nz(String s, String def) { return (s == null || s.isEmpty()) ? def : s; }
    private static String strip(double d) {
        return (Math.rint(d) == d) ? String.format(java.util.Locale.getDefault(), "%.0f", d)
                : String.format(java.util.Locale.getDefault(), "%.2f", d);
    }
}
