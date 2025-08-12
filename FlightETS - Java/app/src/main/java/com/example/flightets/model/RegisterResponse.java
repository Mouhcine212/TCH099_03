package com.example.flightets.model;

public class RegisterResponse {
    private boolean success; // true on success
    private String token;    // present on success
    private String error;    // present on failure
    private String details;  // optional extra info on failure

    public boolean isSuccess() { return success; }
    public String getToken() { return token; }
    public String getError() { return error; }
    public String getDetails() { return details; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setToken(String token) { this.token = token; }
    public void setError(String error) { this.error = error; }
    public void setDetails(String details) { this.details = details; }
}
