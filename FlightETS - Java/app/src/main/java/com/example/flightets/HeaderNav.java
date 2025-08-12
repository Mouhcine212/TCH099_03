package com.example.flightets;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

public final class HeaderNav {
    private HeaderNav() {}

    /** Simple bind: sets first name, wires clicks. */
    public static void bind(Activity a, String fullName) {
        bind(a, fullName, 0);
    }

    /**
     * Bind + highlight the active menu item.
     * Pass one of: R.id.headerHome, R.id.headerSearch, R.id.headerReservations (or 0 for none).
     */
    public static void bind(Activity a, String fullName, int activeMenuId) {
        TextView home = a.findViewById(R.id.headerHome);
        TextView search = a.findViewById(R.id.headerSearch);
        TextView reservations = a.findViewById(R.id.headerReservations);
        TextView logout = a.findViewById(R.id.headerLogoutButton);
        TextView user = a.findViewById(R.id.headerUserName);

        // First name only
        if (user != null && fullName != null) {
            user.setText(firstName(fullName));
        }

        if (home != null) home.setOnClickListener(v -> go(a, MainActivity.class));
        if (search != null) search.setOnClickListener(v -> go(a, SearchFlightActivity.class));
        if (reservations != null) reservations.setOnClickListener(v -> go(a, ReservationListActivity.class));
        if (user != null) user.setOnClickListener(v -> go(a, MonProfilActivity.class));
        if (logout != null) logout.setOnClickListener(v -> {
            a.getSharedPreferences("auth", Activity.MODE_PRIVATE).edit().clear().apply();
            Intent i = new Intent(a, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            a.startActivity(i);
        });

        highlightActive(a, activeMenuId);
    }

    private static void go(Activity a, Class<?> target) {
        if (a.getClass().equals(target)) return; // don't relaunch same screen
        a.startActivity(new Intent(a, target));
    }

    private static void highlightActive(Activity a, int activeId) {
        if (activeId == 0) return;

        int[] ids = { R.id.headerHome, R.id.headerSearch, R.id.headerReservations };
        for (int id : ids) {
            TextView tv = a.findViewById(id);
            if (tv == null) continue;

            if (id == activeId) {
                tv.setTextColor(Color.parseColor("#1976D2"));
                tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
            } else {
                tv.setTextColor(Color.parseColor("#666666"));
                tv.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
            }
        }
    }

    private static String firstName(String full) {
        if (full == null) return "";
        String t = full.trim();
        if (t.isEmpty()) return "";
        return t.split("\\s+")[0];
    }
}
