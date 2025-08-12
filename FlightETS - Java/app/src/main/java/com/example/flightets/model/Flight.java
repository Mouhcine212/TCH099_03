package com.example.flightets.model;

import com.google.gson.annotations.SerializedName;

public class Flight {
    @SerializedName("ID_VOL") private int idVol;

    @SerializedName("ORIGINE_VILLE") private String origineVille;
    @SerializedName("ORIGINE_CODE")  private String origineCode;

    @SerializedName("DESTINATION_VILLE") private String destinationVille;
    @SerializedName("DESTINATION_CODE")  private String destinationCode;

    @SerializedName("HEURE_DEPART")  private String heureDepart;   // e.g. "2025-08-12 14:05:00" or ISO
    @SerializedName("HEURE_ARRIVEE") private String heureArrivee;

    @SerializedName("COMPAGNIE")   private String compagnie;
    @SerializedName("NUMERO_VOL")  private String numeroVol;
    @SerializedName("PRIX")        private double prix;
    @SerializedName("CLASSE")      private String classe;
    @SerializedName("SIEGES_DISPONIBLES") private int siegesDisponibles;

    public int getIdVol() { return idVol; }
    public String getOrigineVille() { return origineVille; }
    public String getOrigineCode() { return origineCode; }
    public String getDestinationVille() { return destinationVille; }
    public String getDestinationCode() { return destinationCode; }
    public String getHeureDepart() { return heureDepart; }
    public String getHeureArrivee() { return heureArrivee; }
    public String getCompagnie() { return compagnie; }
    public String getNumeroVol() { return numeroVol; }
    public double getPrix() { return prix; }
    public String getClasse() { return classe; }
    public int getSiegesDisponibles() { return siegesDisponibles; }
}
