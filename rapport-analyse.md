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

![Diagramme de cas d'utilisation](https://github.com/Mouhcine212/TCH099_03/issues/3)

### Description des acteurs
- **Passager** : Utilisateur cherchant à réserver des billets d'avion
- **Admin Compagnie** : Gestionnaire d'une compagnie aérienne
- **SkyBooker** : Système central gérant la logique métier

### Cas d'utilisation principaux
- **Passager** : S'inscrire, rechercher vols, réserver, gérer profil
- **Admin Compagnie** : Gérer vols, voir réservations, analytics, tarification
- **Système** : Calculer prix, envoyer notifications, traiter paiements

## Schéma initial de la base de données

![Schéma de base de données](https://github.com/Mouhcine212/TCH099_03/issues/4)

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




