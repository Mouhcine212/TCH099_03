# SkyBooker - Plateforme de Réservation de Billets d'Avion

# Liste Membres

- Mouhcine Imaoun/Pseudo: Mouhcine212
- Andrea Borges/Pseudo:Cvgirl
- Bogdan Alexandru/Pseudo:bogdan-b-alexandru
- Bijoy Dey/Pseudo: Pas encore inscrit
- Abdurrahmane/Zeguendri Pseudo:CodeAbdou
- Jonathan/St-Louis Pseudo:JonathanStLouis



## Description du Projet
Plateforme complète permettant aux voyageurs de rechercher, réserver et gérer leurs billets d'avion. Le système comprend une application mobile pour les passagers, une interface web pour les compagnies aériennes ainsi que pour les passagers, et une API REST pour la gestion centralisée des données.

## Choix technologiques pour l’API REST du *backend*

- **Langage** : PHP
- **Serveur** : Apache avec XAMPP
- **Format d’échange** : JSON
- **Sécurité** : Authentification par JWT

##  Interfaces et *stack* technologiques utilisées

### Interface Web (administrateur)
- **Frontend** : HTML, CSS, JavaScript
- **Librairie possible** : DataTables.js pour l'affichage des réservations, Retrofit (API calls), Glide (images), Room (cache local).

### Application Mobile (client)
- **Framework** : Android Framework
- Langage principal : Java
- SDK : Android SDK
- Moteur de build : Gradle
- **Plateformes ciblées** : Android (Java/Kotlin), iOS

##  Base de Données

### Technologie
- **SGBD** : MySQL 8.0 
- **ORM** : Laravel Eloquent 

### Tables Principales
- `users` - Utilisateurs (passagers + administrateurs compagnies)
- `airlines` - Compagnies aériennes
- `airports` - Aéroports et destinations
- `flights` - Vols disponibles
- `bookings` - Réservations
- `passengers` - Informations passagers
- `payments` - Transactions et paiements

## Gestion Multi-Utilisateurs

### Passagers (Application Mobile et interface web)
**Fonctionnalités :**
- Recherche de vols (origine, destination, dates)
- Réservation avec sélection de sièges
- Gestion du profil et historique des voyages
- Check-in mobile et cartes d'embarquement
- Notifications (retards, changements de porte)

**Données privées :**
- Historique de réservations personnel
- Préférences de voyage (sièges, repas)
- Informations de paiement
- Programme de fidélité et miles

### Administrateurs Compagnies (Interface Web)
**Fonctionnalités :**
- Gestion de la flotte et des vols
- Création/modification des horaires
- Gestion des réservations et overbooking ????
- Analytics et reporting (taux de remplissage, revenus)
- Communication avec les passagers

**Données d'entreprise :**
- Inventaire des sièges par vol
- Tarification qui change en temps réelle
- Statistiques de performance
- Gestion du personnel navigant

### Différenciation des Accès
- **Authentification** : JWT tokens avec rôles
- **Passagers** : Accès limité à leurs données personnelles
- **Compagnies** : Accès complet à leurs vols et réservations
- **Isolation** : Chaque compagnie voit uniquement ses données
