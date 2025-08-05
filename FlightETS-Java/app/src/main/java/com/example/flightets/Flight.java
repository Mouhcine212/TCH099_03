package com.example.flightets;

public class Flight {
    private int ID_VOL;
    private String ORIGINE;
    private String DESTINATION;
    private String HEURE_DEPART;
    private String HEURE_ARRIVEE;
    private String COMPAGNIE;
    private String NUMERO_VOL;
    private double PRIX;
    private String CLASSE;
    private int SIEGES_DISPONIBLES;

    // Constructor
    public Flight(int ID_VOL, String ORIGINE, String DESTINATION, String HEURE_DEPART,
                  String HEURE_ARRIVEE, String COMPAGNIE, String NUMERO_VOL,
                  double PRIX, String CLASSE, int SIEGES_DISPONIBLES) {
        this.ID_VOL = ID_VOL;
        this.ORIGINE = ORIGINE;
        this.DESTINATION = DESTINATION;
        this.HEURE_DEPART = HEURE_DEPART;
        this.HEURE_ARRIVEE = HEURE_ARRIVEE;
        this.COMPAGNIE = COMPAGNIE;
        this.NUMERO_VOL = NUMERO_VOL;
        this.PRIX = PRIX;
        this.CLASSE = CLASSE;
        this.SIEGES_DISPONIBLES = SIEGES_DISPONIBLES;
    }

    // Getters
    public int getID_VOL() { return ID_VOL; }
    public String getORIGINE() { return ORIGINE; }
    public String getDESTINATION() { return DESTINATION; }
    public String getHEURE_DEPART() { return HEURE_DEPART; }
    public String getHEURE_ARRIVEE() { return HEURE_ARRIVEE; }
    public String getCOMPAGNIE() { return COMPAGNIE; }
    public String getNUMERO_VOL() { return NUMERO_VOL; }
    public double getPRIX() { return PRIX; }
    public String getCLASSE() { return CLASSE; }
    public int getSIEGES_DISPONIBLES() { return SIEGES_DISPONIBLES; }

    // Setters
    public void setID_VOL(int ID_VOL) { this.ID_VOL = ID_VOL; }
    public void setORIGINE(String ORIGINE) { this.ORIGINE = ORIGINE; }
    public void setDESTINATION(String DESTINATION) { this.DESTINATION = DESTINATION; }
    public void setHEURE_DEPART(String HEURE_DEPART) { this.HEURE_DEPART = HEURE_DEPART; }
    public void setHEURE_ARRIVEE(String HEURE_ARRIVEE) { this.HEURE_ARRIVEE = HEURE_ARRIVEE; }
    public void setCOMPAGNIE(String COMPAGNIE) { this.COMPAGNIE = COMPAGNIE; }
    public void setNUMERO_VOL(String NUMERO_VOL) { this.NUMERO_VOL = NUMERO_VOL; }
    public void setPRIX(double PRIX) { this.PRIX = PRIX; }
    public void setCLASSE(String CLASSE) { this.CLASSE = CLASSE; }
    public void setSIEGES_DISPONIBLES(int SIEGES_DISPONIBLES) { this.SIEGES_DISPONIBLES = SIEGES_DISPONIBLES; }
}
