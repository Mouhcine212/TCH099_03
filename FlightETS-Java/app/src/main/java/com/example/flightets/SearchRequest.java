package com.example.flightets;

public class SearchRequest {
    private String destination;
    private String date;

    public SearchRequest(String destination, String date) {
        this.destination = destination;
        this.date = date;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
