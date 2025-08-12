package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class ReservationItem {
    @SerializedName("ID_RESERVATION") public int idReservation;
    @SerializedName("NUMERO_SIEGE")   public String numeroSiege;
    @SerializedName("STATUT")         public String statut;
    @SerializedName("DATE_RESERVATION") public String dateReservation;

    @SerializedName("COMPAGNIE")      public String compagnie;
    @SerializedName("NUMERO_VOL")     public String numeroVol;
    @SerializedName("HEURE_DEPART")   public String heureDepart;
    @SerializedName("HEURE_ARRIVEE")  public String heureArrivee;
    @SerializedName("CLASSE")         public String classe;
    @SerializedName("PRIX")           public double prix;

    @SerializedName("ORIGINE")        public String origine;
    @SerializedName("ORIGINE_CODE")   public String origineCode;
    @SerializedName("DESTINATION")    public String destination;
    @SerializedName("DESTINATION_CODE") public String destinationCode;

    @SerializedName("STATUT_PAIEMENT") public String statutPaiement;
}
