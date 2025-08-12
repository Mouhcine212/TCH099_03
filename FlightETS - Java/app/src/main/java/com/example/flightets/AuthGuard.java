package com.example.flightets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public final class AuthGuard {
    private AuthGuard() {}

    /** Returns the stored token (or null). Looks in "auth" then fallback "token". */
    public static String getToken(Context ctx) {
        SharedPreferences auth = ctx.getSharedPreferences("auth", Context.MODE_PRIVATE);
        String t = auth.getString("token", null);
        if (t == null) {
            t = ctx.getSharedPreferences("token", Context.MODE_PRIVATE).getString("token", null);
        }
        return (t == null || t.trim().isEmpty()) ? null : t;
    }

    /** Returns "Bearer <token>" or null. */
    public static String getBearer(Context ctx) {
        String t = getToken(ctx);
        return t == null ? null : "Bearer " + t;
    }

    /** Decode JWT payload and return the "nom" claim if present. */
    public static String getNameFromJwt(Context ctx) {
        String t = getToken(ctx);
        if (t == null) return null;
        try {
            String[] parts = t.split("\\.");
            if (parts.length < 2) return null;
            byte[] decoded = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP);
            JwtPayload payload = new Gson().fromJson(new String(decoded), JwtPayload.class);
            return payload != null ? payload.nom : null;
        } catch (Throwable ignored) { return null; }
    }

    /** True if token is missing or exp (seconds) is in the past. */
    public static boolean isTokenInvalid(Context ctx) {
        String t = getToken(ctx);
        if (t == null) return true;
        try {
            String[] parts = t.split("\\.");
            if (parts.length < 2) return true;
            byte[] decoded = Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP);
            JwtPayload payload = new Gson().fromJson(new String(decoded), JwtPayload.class);
            if (payload == null || payload.exp == null) return true; // treat missing exp as invalid
            long now = System.currentTimeMillis() / 1000L;
            return payload.exp <= now;
        } catch (Throwable e) {
            return true;
        }
    }

    /** If not logged-in (or token expired), redirect to Login and finish. Returns false if redirected. */
    public static boolean requireLogin(Activity a) {
        if (isTokenInvalid(a)) {
            // clear and redirect
            a.getSharedPreferences("auth", Activity.MODE_PRIVATE).edit().clear().apply();
            Intent i = new Intent(a, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            a.startActivity(i);
            a.finish();
            return false;
        }
        return true;
    }

    // JWT payload we expect
    static class JwtPayload {
        @SerializedName("exp") Long exp;
        @SerializedName("nom") String nom;
        @SerializedName("email") String email;
        @SerializedName("id") Integer id;
        @SerializedName("telephone") String telephone;
    }
}
