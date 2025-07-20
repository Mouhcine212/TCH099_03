-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Hôte : database:3306
-- Généré le : dim. 20 juil. 2025 à 23:28
-- Version du serveur : 8.4.5
-- Version de PHP : 8.2.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `skybooker_dev`
--

-- --------------------------------------------------------

--
-- Structure de la table `airlines`
--

CREATE TABLE `airlines` (
  `id` int NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL,
  `invitation_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `airlines`
--

INSERT INTO `airlines` (`id`, `name`, `code`, `invitation_code`, `created_at`, `updated_at`) VALUES
(1, 'Air Canada', 'AC', 'AC2025', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(2, 'WestJet', 'WS', 'WS2025', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(3, 'Air France', 'AF', 'AF2025', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(4, 'Lufthansa', 'LH', 'LH2025', '2025-07-19 14:10:11', '2025-07-19 14:10:11');

-- --------------------------------------------------------

--
-- Structure de la table `airports`
--

CREATE TABLE `airports` (
  `id` int NOT NULL,
  `code` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `country` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `airports`
--

INSERT INTO `airports` (`id`, `code`, `name`, `city`, `country`, `created_at`, `updated_at`) VALUES
(1, 'YUL', 'Pierre Elliott Trudeau International Airport', 'Montreal', 'Canada', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(2, 'YYZ', 'Toronto Pearson International Airport', 'Toronto', 'Canada', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(3, 'CDG', 'Charles de Gaulle Airport', 'Paris', 'France', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(4, 'JFK', 'John F. Kennedy International Airport', 'New York', 'USA', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(5, 'FRA', 'Frankfurt Airport', 'Frankfurt', 'Germany', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(6, 'LHR', 'Heathrow Airport', 'London', 'United Kingdom', '2025-07-19 14:10:11', '2025-07-19 14:10:11');

-- --------------------------------------------------------

--
-- Structure de la table `bookings`
--

CREATE TABLE `bookings` (
  `id` int NOT NULL,
  `user_id` int NOT NULL,
  `flight_id` int NOT NULL,
  `booking_reference` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `payment_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `booking_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `flights`
--

CREATE TABLE `flights` (
  `id` int NOT NULL,
  `airline_id` int NOT NULL,
  `flight_number` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `origin_airport_id` int NOT NULL,
  `destination_airport_id` int NOT NULL,
  `departure_time` datetime NOT NULL,
  `arrival_time` datetime NOT NULL,
  `base_price` decimal(10,2) NOT NULL,
  `total_seats` int NOT NULL,
  `available_seats` int NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'scheduled',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `flights`
--

INSERT INTO `flights` (`id`, `airline_id`, `flight_number`, `origin_airport_id`, `destination_airport_id`, `departure_time`, `arrival_time`, `base_price`, `total_seats`, `available_seats`, `status`, `created_at`, `updated_at`) VALUES
(1, 1, 'AC123', 1, 3, '2025-08-15 14:30:00', '2025-08-16 07:45:00', 899.99, 200, 150, 'scheduled', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(2, 1, 'AC456', 2, 4, '2025-08-20 10:15:00', '2025-08-20 16:30:00', 1299.99, 180, 120, 'scheduled', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(3, 2, 'WS789', 1, 2, '2025-08-25 08:00:00', '2025-08-25 10:30:00', 299.99, 150, 100, 'scheduled', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(4, 3, 'AF447', 3, 1, '2025-08-19 13:20:00', '2025-08-19 20:50:00', 1050.99, 250, 200, 'scheduled', '2025-07-19 14:10:11', '2025-07-19 14:10:11'),
(5, 4, 'LH441', 5, 1, '2025-08-24 11:00:00', '2025-08-24 18:30:00', 1250.99, 280, 180, 'scheduled', '2025-07-19 14:10:11', '2025-07-19 14:10:11');

-- --------------------------------------------------------

--
-- Structure de la table `passengers`
--

CREATE TABLE `passengers` (
  `id` int NOT NULL,
  `booking_id` int NOT NULL,
  `first_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `passport_number` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nationality` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `seat_number` varchar(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `payments`
--

CREATE TABLE `payments` (
  `id` int NOT NULL,
  `booking_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_method` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `transaction_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `payment_date` datetime DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `first_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'passenger',
  `airline_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `email`, `password`, `first_name`, `last_name`, `phone`, `role`, `airline_id`, `created_at`, `updated_at`) VALUES
(1, 'jean.dupont@email.com', '$moneybagyo', 'Jean', 'Dupont', NULL, 'passenger', NULL, '2025-07-19 14:10:11', '2025-07-19 14:10:11');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `airlines`
--
ALTER TABLE `airlines`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD UNIQUE KEY `invitation_code` (`invitation_code`),
  ADD KEY `idx_airline_code` (`code`);

--
-- Index pour la table `airports`
--
ALTER TABLE `airports`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code` (`code`),
  ADD KEY `idx_airport_code` (`code`),
  ADD KEY `idx_airport_city` (`city`);

--
-- Index pour la table `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `booking_reference` (`booking_reference`),
  ADD UNIQUE KEY `idx_booking_ref` (`booking_reference`),
  ADD KEY `idx_user_bookings` (`user_id`),
  ADD KEY `idx_flight_bookings` (`flight_id`),
  ADD KEY `idx_booking_date` (`booking_date`);

--
-- Index pour la table `flights`
--
ALTER TABLE `flights`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_flight_search` (`origin_airport_id`,`destination_airport_id`,`departure_time`),
  ADD KEY `idx_airline_flights` (`airline_id`),
  ADD KEY `idx_flight_number` (`flight_number`),
  ADD KEY `idx_departure` (`departure_time`),
  ADD KEY `destination_airport_id` (`destination_airport_id`);

--
-- Index pour la table `passengers`
--
ALTER TABLE `passengers`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_booking_passengers` (`booking_id`),
  ADD KEY `idx_passport` (`passport_number`);

--
-- Index pour la table `payments`
--
ALTER TABLE `payments`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `transaction_id` (`transaction_id`),
  ADD KEY `idx_booking_payments` (`booking_id`),
  ADD KEY `idx_transaction` (`transaction_id`),
  ADD KEY `idx_payment_status` (`status`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `idx_email` (`email`),
  ADD KEY `idx_role` (`role`),
  ADD KEY `idx_airline` (`airline_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `airlines`
--
ALTER TABLE `airlines`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `airports`
--
ALTER TABLE `airports`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `bookings`
--
ALTER TABLE `bookings`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `flights`
--
ALTER TABLE `flights`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `passengers`
--
ALTER TABLE `passengers`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `payments`
--
ALTER TABLE `payments`
  MODIFY `id` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`flight_id`) REFERENCES `flights` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `flights`
--
ALTER TABLE `flights`
  ADD CONSTRAINT `flights_ibfk_1` FOREIGN KEY (`airline_id`) REFERENCES `airlines` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `flights_ibfk_2` FOREIGN KEY (`origin_airport_id`) REFERENCES `airports` (`id`) ON DELETE RESTRICT,
  ADD CONSTRAINT `flights_ibfk_3` FOREIGN KEY (`destination_airport_id`) REFERENCES `airports` (`id`) ON DELETE RESTRICT;

--
-- Contraintes pour la table `passengers`
--
ALTER TABLE `passengers`
  ADD CONSTRAINT `passengers_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `payments`
--
ALTER TABLE `payments`
  ADD CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`airline_id`) REFERENCES `airlines` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
