package com.example.flightets.model;

public class LoginResponse {
    private String token; // present on success
    private String error; // present on error

    public String getToken() { return token; }
    public String getError() { return error; }
    public boolean isSuccess() { return token != null && !token.isEmpty(); }
}
