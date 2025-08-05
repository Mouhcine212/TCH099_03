package com.example.flightets

data class Flight(
    val ID_VOL: Int,
    val ORIGINE: String,
    val DESTINATION: String,
    val HEURE_DEPART: String,
    val HEURE_ARRIVEE: String,
    val COMPAGNIE: String,
    val NUMERO_VOL: String,
    val PRIX: Double,
    val CLASSE: String,
    val SIEGES_DISPONIBLES: Int
)
