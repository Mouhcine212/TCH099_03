package com.example.flightets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightets.model.ReservationItem;

import java.util.ArrayList;
import java.util.List;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.VH> {

    public interface OnCancelClick {
        void onCancel(int reservationId, int position);
    }

    private final List<ReservationItem> items = new ArrayList<>();
    private final OnCancelClick listener;

    public ReservationsAdapter(OnCancelClick listener) {
        this.listener = listener;
    }

    public void setItems(List<ReservationItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    public ReservationItem getItem(int pos) {
        if (pos < 0 || pos >= items.size()) return null;
        return items.get(pos);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        ReservationItem r = items.get(pos);

        h.tvTitle.setText(nz(r.compagnie) + " - " + nz(r.numeroVol));
        h.tvRoute.setText(nz(r.origine) + " (" + nz(r.origineCode) + ") \u2192 "
                + nz(r.destination) + " (" + nz(r.destinationCode) + ")");
        h.tvDepart.setText("Départ : " + nz(r.heureDepart));
        h.tvArrivee.setText("Arrivée : " + nz(r.heureArrivee));
        h.tvSiege.setText("Siège : " + (isEmpty(r.numeroSiege) ? "Non attribué" : r.numeroSiege));

        int color;
        if ("Confirmée".equalsIgnoreCase(r.statut))      color = 0xFF00FF88; // green
        else if ("Annulée".equalsIgnoreCase(r.statut))   color = 0xFFFF4D4D; // red
        else                                             color = 0xFFFFCC00; // yellow
        h.tvStatut.setText("Statut : " + nz(r.statut));
        h.tvStatut.setTextColor(color);

        h.tvPaiement.setText("Paiement : " + nz(r.statutPaiement, "Non payé"));

        boolean showCancel = !"Annulée".equalsIgnoreCase(r.statut);
        h.btnCancel.setVisibility(showCancel ? View.VISIBLE : View.GONE);
        h.btnCancel.setOnClickListener(v -> {
            if (listener == null) return;
            int adapterPos = h.getAdapterPosition(); // <-- compatible method
            if (adapterPos == RecyclerView.NO_POSITION) return;
            ReservationItem item = items.get(adapterPos);
            listener.onCancel(item.idReservation, adapterPos);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvRoute, tvDepart, tvArrivee, tvSiege, tvStatut, tvPaiement;
        Button btnCancel;
        VH(@NonNull View v) {
            super(v);
            tvTitle   = v.findViewById(R.id.tvTitle);
            tvRoute   = v.findViewById(R.id.tvRoute);
            tvDepart  = v.findViewById(R.id.tvDepart);
            tvArrivee = v.findViewById(R.id.tvArrivee);
            tvSiege   = v.findViewById(R.id.tvSiege);
            tvStatut  = v.findViewById(R.id.tvStatut);
            tvPaiement= v.findViewById(R.id.tvPaiement);
            btnCancel = v.findViewById(R.id.btnCancel);
        }
    }

    private static String nz(String s) { return s == null ? "" : s; }
    private static String nz(String s, String def) { return s == null ? def : s; }
    private static boolean isEmpty(String s) { return s == null || s.trim().isEmpty(); }
}
