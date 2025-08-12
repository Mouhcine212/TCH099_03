package com.example.flightets;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightets.model.CancelRequest;
import com.example.flightets.model.CancelResponse;
import com.example.flightets.model.ReservationItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationListActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TextView emptyState;
    private View progress;

    private ReservationsAdapter adapter;
    private AuthApiService api;
    private String bearer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AuthGuard.requireLogin(this)) return;

        setContentView(R.layout.activity_reservation_list);

        // Header (first name) + highlight "Réservations"
        String name = AuthGuard.getNameFromJwt(this);
        HeaderNav.bind(this, name, R.id.headerReservations);

        recycler    = findViewById(R.id.recyclerReservations);
        emptyState  = findViewById(R.id.emptyState);
        progress    = findViewById(R.id.progress);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ReservationsAdapter((reservationId, position) -> cancelReservation(reservationId, position));
        recycler.setAdapter(adapter);

        bearer = AuthGuard.getBearer(this);
        api    = ApiClient.get().create(AuthApiService.class);

        loadReservations();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!AuthGuard.requireLogin(this)) return;
    }

    private void loadReservations() {
        emptyState.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        api.getReservations(bearer).enqueue(new Callback<List<ReservationItem>>() {
            @Override
            public void onResponse(Call<List<ReservationItem>> call, Response<List<ReservationItem>> response) {
                progress.setVisibility(View.GONE);

                if (response.code() == 401) { AuthGuard.requireLogin(ReservationListActivity.this); return; }

                if (response.isSuccessful() && response.body() != null) {
                    List<ReservationItem> list = response.body();
                    adapter.setItems(list);
                    emptyState.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(ReservationListActivity.this, "Erreur: " + response.code(), Toast.LENGTH_SHORT).show();
                    adapter.setItems(new ArrayList<>());
                    emptyState.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<ReservationItem>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(ReservationListActivity.this, "Erreur réseau.", Toast.LENGTH_SHORT).show();
                adapter.setItems(new ArrayList<>());
                emptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void cancelReservation(int idReservation, int position) {
        api.cancelReservation(bearer, new CancelRequest(idReservation)).enqueue(new Callback<CancelResponse>() {
            @Override
            public void onResponse(Call<CancelResponse> call, Response<CancelResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ReservationListActivity.this, "Erreur d'annulation: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                CancelResponse body = response.body();
                if (body.success) {
                    ReservationItem item = adapter.getItem(position);
                    if (item != null) {
                        item.statut = "Annulée";
                        if (body.statut_paiement != null) item.statutPaiement = body.statut_paiement;
                        adapter.notifyItemChanged(position);
                    }
                    Toast.makeText(ReservationListActivity.this, "Réservation annulée.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ReservationListActivity.this, body.error != null ? body.error : "Échec de l'annulation", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CancelResponse> call, Throwable t) {
                Toast.makeText(ReservationListActivity.this, "Erreur réseau lors de l'annulation.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
