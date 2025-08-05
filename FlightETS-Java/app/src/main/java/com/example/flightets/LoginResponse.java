package com.example.flightets;

public class LoginResponse {
    private String status;
    private String email;
    private String role;

    public LoginResponse(String status, String email, String role) {
        this.status = status;
        this.email = email;
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
