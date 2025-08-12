package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class UpdateUserResponse {
    @SerializedName("success") private boolean success;
    @SerializedName("message") private String message;
    @SerializedName("error")   private String error;
    @SerializedName("token")   private String token;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getError()   { return error; }
    public String getToken()   { return token; }
}
