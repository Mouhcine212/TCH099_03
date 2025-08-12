package com.example.flightets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flightets.model.Flight;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.VH> {

    public interface OnReserveClick {
        void onReserve(Flight flight);
    }

    private final List<Flight> flights;
    private final OnReserveClick onReserveClick;

    // You can pass null for onReserveClick if you just want a list with no button action
    public FlightAdapter(List<Flight> flights) {
        this(flights, null);
    }

    public FlightAdapter(List<Flight> flights, OnReserveClick onReserveClick) {
        this.flights = flights;
        this.onReserveClick = onReserveClick;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Flight f = flights.get(position);

        h.tvTitle.setText(s("%s - %s", nz(f.getCompagnie()), nz(f.getNumeroVol())));
        h.tvRouteLeft.setText(s("%s (%s)", nz(f.getOrigineVille()), nz(f.getOrigineCode())));
        h.tvRouteRight.setText(s("%s (%s)", nz(f.getDestinationVille()), nz(f.getDestinationCode())));

        Date depart = parseFlexible(f.getHeureDepart());
        Date arrivee = parseFlexible(f.getHeureArrivee());
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());

        h.tvDepart.setText("Départ : " + (depart != null ? df.format(depart) : nz(f.getHeureDepart())));
        h.tvArrivee.setText("Arrivée : " + (arrivee != null ? df.format(arrivee) : nz(f.getHeureArrivee())));

        if (depart != null && arrivee != null) {
            long minutes = Math.max(0, (arrivee.getTime() - depart.getTime()) / 60000);
            long hDur = minutes / 60;
            long mDur = minutes % 60;
            h.tvDuree.setText(s("Durée estimée : %dh %dm", hDur, mDur));
        } else {
            h.tvDuree.setText("Durée estimée : —");
        }

        h.tvPriceClass.setText(s("%s $ - %s", stripTrailingZeros(f.getPrix()), nz(f.getClasse())));

        h.btnReserver.setOnClickListener(v -> {
            if (onReserveClick != null) onReserveClick.onReserve(f);
        });
    }

    @Override public int getItemCount() { return flights == null ? 0 : flights.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvRouteLeft, tvRouteRight, tvDepart, tvArrivee, tvDuree, tvPriceClass;
        Button btnReserver;
        VH(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvRouteLeft = v.findViewById(R.id.tvRouteLeft);
            tvRouteRight = v.findViewById(R.id.tvRouteRight);
            tvDepart = v.findViewById(R.id.tvDepart);
            tvArrivee = v.findViewById(R.id.tvArrivee);
            tvDuree = v.findViewById(R.id.tvDuree);
            tvPriceClass = v.findViewById(R.id.tvPriceClass);
            btnReserver = v.findViewById(R.id.btnReserver);
        }
    }

    // ---- Helpers ----

    private static String s(String fmt, Object... args) { return String.format(Locale.getDefault(), fmt, args); }
    private static String nz(String s) { return s == null ? "" : s; }

    // Accept both "yyyy-MM-dd HH:mm:ss" and ISO "yyyy-MM-dd'T'HH:mm[:ss]" forms
    private static Date parseFlexible(String raw) {
        if (raw == null) return null;
        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm"
        };
        for (String p : patterns) {
            try {
                SimpleDateFormat f = new SimpleDateFormat(p, Locale.US);
                f.setLenient(true);
                return f.parse(raw);
            } catch (ParseException ignored) {}
        }
        return null;
    }

    private static String stripTrailingZeros(double value) {
        if (Math.rint(value) == value) {
            return String.format(Locale.getDefault(), "%.0f", value);
        } else {
            return String.format(Locale.getDefault(), "%.2f", value);
        }
    }
}
