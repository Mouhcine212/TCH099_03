package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    @SerializedName("currentPassword") public String currentPassword;
    @SerializedName("newPassword")     public String newPassword;

    public ChangePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}
