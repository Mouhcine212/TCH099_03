## Liste des membres de l'équipe avec identifiant Github et Discord
- **Mouhcine Imaoun** (GitHub: @Mouhcine212, Discord: mou._.)
- **Andrea Borges** (GitHub: @Cvgirl, Discord: gros_chef_bandit.)
- **Bogdan Alexandru** (GitHub: @bogdan-b-alexandru, Discord: buggy4637)
- **Bijoy Dey** (GitHub: @xxx, Discord: double_happiness)
- **Abdurrahmane Zeguendri** (GitHub: @CodeAbdou, Discord: abdouedit)
- **Jonathan St-Louis** (GitHub: @JonathanStLouis, Discord: .pino09)

## Description l'objectif principal de l'application

Plateforme complète permettant aux voyageurs de rechercher, réserver et gérer leurs billets d'avion. Le système comprend une interface web responsive pour les passagers et les administrateurs de compagnies aériennes, ainsi qu'une API REST pour la gestion centralisée des données.

### Fonctionnalités principales
- **Pour les passagers** : Recherche de vols, réservation, gestion du profil
- **Pour les compagnies** : Gestion des vols, analytics, tarification dynamique
- **Système complet** : Paiements, notifications, historique

## Choix technologique pour l'application

### Choix des deux interfaces et les technologies/framework/librairies utilisés

#### Interface Web Responsive (Passagers + Administrateurs)
- **Framework Frontend** : Vue.js 3
- **CSS Framework** : Bootstrap 5 (design responsive)
- **Communication API** : Axios pour requêtes HTTP
- **Build Tool** : Vite (inclus avec Vue.js)
- **Authentification** : JWT tokens gérés côté client

#### Justification du choix
- **Vue.js** : Courbe d'apprentissage plus douce que React
- **Bootstrap** : Interface responsive immédiate
- **Une seule interface** : Simplification du développement et maintenance

### Choix de technologies/framework/librairies pour l'API REST

#### Backend API REST
- **Framework** : Laravel (PHP)
- **API Architecture** : RESTful avec routes API Laravel
- **Authentification** : Laravel Sanctum (JWT tokens)
- **Base de données** : MySQL avec Eloquent ORM
- **Serveur** : Apache avec XAMPP (développement)

#### Justification du choix
- **Laravel** : Framework mature avec excellente documentation
- **Eloquent ORM** : Gestion simplifiée des relations entre tables
- **Sanctum** : Authentification API sécurisée et simple
- **MySQL** : Base de données relationnelle robuste et bien documentée

---

# Planification du Sprint 1

## Diagramme de cas d'utilisation

![Diagramme de cas d'utilisation](https://private-user-images.githubusercontent.com/58708075/461751867-56d32180-ce94-4320-a9d8-0f3279bc9c8e.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTE1MTA2NzAsIm5iZiI6MTc1MTUxMDM3MCwicGF0aCI6Ii81ODcwODA3NS80NjE3NTE4NjctNTZkMzIxODAtY2U5NC00MzIwLWE5ZDgtMGYzMjc5YmM5YzhlLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTA3MDMlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwNzAzVDAyMzkzMFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWNjYmU3MWFkNmViOGUxMGFiYTQ2OWM4MWQwMTY0MDg0NWZkYjUzMzI0YWJiOTg0M2MxZjg3ZGZiZjExZWViZmUmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.GAk__bl172nRcBbiOzIbCRB22O7SWCLO1fB_EsPYaNk)

### Description des acteurs
- **Passager** : Utilisateur cherchant à réserver des billets d'avion
- **Admin Compagnie** : Gestionnaire d'une compagnie aérienne
- **SkyBooker** : Système central gérant la logique métier

### Cas d'utilisation principaux
- **Passager** : S'inscrire, rechercher vols, réserver, gérer profil
- **Admin Compagnie** : Gérer vols, voir réservations, analytics, tarification
- **Système** : Calculer prix, envoyer notifications, traiter paiements

## Schéma initial de la base de données

![Schéma de base de données](https://private-user-images.githubusercontent.com/58708075/461752017-4a1a91b9-3f6c-49e4-9567-65d36ce147b9.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTE1MTA3MjQsIm5iZiI6MTc1MTUxMDQyNCwicGF0aCI6Ii81ODcwODA3NS80NjE3NTIwMTctNGExYTkxYjktM2Y2Yy00OWU0LTk1NjctNjVkMzZjZTE0N2I5LnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTA3MDMlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwNzAzVDAyNDAyNFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTU4NTYyY2EwY2Y2MzEzNDFmNDUzMjE5MGVmZjY0YzgxZmQ0OGExMDYwMWYwODI3MWY3MjZiNWIxZmFhYjQ0OTYmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.AnWnRv7jolPacN73OnqQWOoP84kYv2umChBQRVIJg8U)

### Description du modèle logique
Le schéma comprend 7 tables principales avec leurs relations :
- **users** : Comptes utilisateurs (passagers + admins)
- **airlines** : Compagnies aériennes
- **airports** : Aéroports
- **flights** : Vols avec origine/destination
- **bookings** : Réservations
- **passengers** : Informations détaillées des voyageurs
- **payments** : Transactions financières

### Relations clés
- Un vol a deux relations avec airports (origine + destination)
- Un booking peut avoir plusieurs passagers
- Chaque user peut avoir plusieurs bookings
- Les admins sont liés à leur compagnie via airline_id

## Liste d'user stories

### User Stories Fonctionnelles
- [Issue #5: US01 - Recherche de vols]
- [Issue #5: US02 - Réservation d’un vol]
- [Issue #5: US03 - Choix du siège]
- [Issue #5: US04 - Paiement en ligne]
- [Issue #5: US05 - Annulation d’une réservation]
- [Issue #5: US06 - Réception d’un courriel de confirmation]
- [Issue #5: US07 - Consultation de l’historique de voyages]
- [Issue #5: US08 - Modification des informations personnelles]
- [Issue #5: US09 - Gestion des vols]
- [Issue #5: US10 - Consultation des statistiques]
(https://github.com/Mouhcine212/TCH099_03/issues/5)

### Liste de requis technologiques
- [Issue #2: RT01 - Base de données relationnelle]
- [Issue #2: RT02 - ORM ]
- [Issue #2: RT03 - Serveur et backend]
- [Issue #2: RT04 - Sécurité des échanges]
- [Issue #2: RT05 - Format d’échange des données]
- [Issue #2: RT06 - Mobile (client)]
- [Issue #2: RT07 - Frontend web (compagnies)]
- [Issue #2: RT07 - Build Android]
(https://github.com/Mouhcine212/TCH099_03/issues/2)

### Liste de requis non fonctionnels
- [Issue #2: RNF01 - Performance]
- [Issue #2: RNF02 - Fiabilité]
- [Issue #2: RNF03 - Sécurité]
- [Issue #2: RNF04 - Maintenabilité]
- [Issue #2: RNF05 - Convivialité]
(https://github.com/Mouhcine212/TCH099_03/issues/2)




