-- ==============================
-- Base de données FlightETS v2
-- Avec gestion des aéroports
-- ==============================

DROP DATABASE IF EXISTS flightets;
CREATE DATABASE flightets;
USE flightets;

-- ==============================
-- TABLES
-- ==============================

-- Aéroports
CREATE TABLE AEROPORTS (
  ID_AEROPORT INT AUTO_INCREMENT PRIMARY KEY,
  NOM_AEROPORT VARCHAR(150) NOT NULL,
  VILLE VARCHAR(100) NOT NULL,
  CODE_IATA CHAR(3) UNIQUE NOT NULL
);

-- Utilisateurs
CREATE TABLE UTILISATEURS (
  ID_UTILISATEUR INT AUTO_INCREMENT PRIMARY KEY,
  NOM VARCHAR(100) NOT NULL,
  COURRIEL VARCHAR(100) UNIQUE NOT NULL,
  MOT_DE_PASSE_HASH VARCHAR(255) NOT NULL,
  TELEPHONE VARCHAR(20),
  DATE_CREATION TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vols
CREATE TABLE VOLS (
  ID_VOL INT AUTO_INCREMENT PRIMARY KEY,
  ID_AEROPORT_ORIGINE INT NOT NULL,
  ID_AEROPORT_DESTINATION INT NOT NULL,
  HEURE_DEPART DATETIME NOT NULL,
  HEURE_ARRIVEE DATETIME NOT NULL,
  COMPAGNIE VARCHAR(100),
  NUMERO_VOL VARCHAR(50),
  PRIX DECIMAL(10,2),
  CLASSE ENUM('Économie', 'Affaires', 'Première'),
  SIEGES_DISPONIBLES INT,
  FOREIGN KEY (ID_AEROPORT_ORIGINE) REFERENCES AEROPORTS(ID_AEROPORT),
  FOREIGN KEY (ID_AEROPORT_DESTINATION) REFERENCES AEROPORTS(ID_AEROPORT)
);

-- Réservations
CREATE TABLE RESERVATIONS (
  ID_RESERVATION INT AUTO_INCREMENT PRIMARY KEY,
  ID_UTILISATEUR INT NOT NULL,
  ID_VOL INT NOT NULL,
  NUMERO_SIEGE VARCHAR(10),
  DATE_RESERVATION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  STATUT ENUM('Confirmée', 'En attente', 'Annulée') DEFAULT 'En attente',
  FOREIGN KEY (ID_UTILISATEUR) REFERENCES UTILISATEURS(ID_UTILISATEUR),
  FOREIGN KEY (ID_VOL) REFERENCES VOLS(ID_VOL)
);

-- Passagers
CREATE TABLE PASSAGERS (
  ID_PASSAGER INT AUTO_INCREMENT PRIMARY KEY,
  ID_RESERVATION INT,
  NOM_COMPLET VARCHAR(100), 
  DATE_NAISSANCE DATE,
  NUMERO_PASSEPORT VARCHAR(50),
  FOREIGN KEY (ID_RESERVATION) REFERENCES RESERVATIONS(ID_RESERVATION)
);

-- Paiements
CREATE TABLE PAIEMENTS (
  ID_PAIEMENT INT AUTO_INCREMENT PRIMARY KEY,
  ID_RESERVATION INT NOT NULL,
  DATE_PAIEMENT DATETIME NOT NULL,
  MONTANT DECIMAL(10,2) NOT NULL,
  METHODE ENUM('Carte', 'PayPal', 'VirementBancaire'),
  STATUT ENUM('Payé', 'Refusé', 'Remboursé') DEFAULT 'Payé',
  FOREIGN KEY (ID_RESERVATION) REFERENCES RESERVATIONS(ID_RESERVATION)
);

-- ==============================
-- DONNÉES DE TEST
-- ==============================

-- Aéroports
INSERT INTO AEROPORTS (NOM_AEROPORT, VILLE, CODE_IATA) VALUES
('Montréal-Trudeau', 'Montréal', 'YUL'),
('Toronto Pearson', 'Toronto', 'YYZ'),
('Paris Charles-de-Gaulle', 'Paris', 'CDG'),
('Vancouver International', 'Vancouver', 'YVR'),
('John F. Kennedy International', 'New York', 'JFK'),
('Narita International', 'Tokyo', 'NRT'),
('Heathrow Airport', 'Londres', 'LHR'),
('Dubai International', 'Dubaï', 'DXB'),
('Frankfurt Airport', 'Francfort', 'FRA'),
('São Paulo–Guarulhos International', 'São Paulo', 'GRU'),
('Los Angeles International', 'Los Angeles', 'LAX'),
('Sydney Kingsford Smith', 'Sydney', 'SYD'),
('Hong Kong International', 'Hong Kong', 'HKG'),
('Singapore Changi Airport', 'Singapour', 'SIN'),
('Amsterdam Schiphol Airport', 'Amsterdam', 'AMS'),
('Madrid-Barajas Adolfo Suárez Airport', 'Madrid', 'MAD'),
('Rome Fiumicino Airport', 'Rome', 'FCO');

-- Vols
INSERT INTO VOLS (ID_AEROPORT_ORIGINE, ID_AEROPORT_DESTINATION, HEURE_DEPART, HEURE_ARRIVEE, COMPAGNIE, NUMERO_VOL, PRIX, CLASSE, SIEGES_DISPONIBLES) VALUES
(1, 2, '2025-08-01 08:00:00', '2025-08-01 09:30:00', 'Air Canada', 'AC101', 199.99, 'Économie', 50),
(1, 3, '2025-08-05 18:00:00', '2025-08-06 06:30:00', 'Air France', 'AF351', 799.50, 'Affaires', 10),
(2, 4, '2025-08-03 13:00:00', '2025-08-03 16:30:00', 'WestJet', 'WS200', 350.00, 'Première', 5),
(2, 5, '2025-08-01 11:30:00', '2025-08-01 19:55:00', 'Lufthansa', 'LU2000', 454.22, 'Affaires', 36),
(1, 11, '2025-08-01 12:00:00', '2025-08-01 20:26:00', 'Air Canada', 'AI2001', 298.77, 'Économie', 52),
(4, 17, '2025-08-02 20:00:00', '2025-08-03 04:22:00', 'Air Canada', 'AI2002', 811.62, 'Économie', 17),
(2, 13, '2025-08-02 21:30:00', '2025-08-03 08:07:00', 'American Airlines', 'AM2003', 434.37, 'Affaires', 29),
(3, 10, '2025-08-03 09:30:00', '2025-08-03 18:22:00', 'American Airlines', 'AM2004', 863.96, 'Économie', 20),
(3, 8, '2025-08-03 13:00:00', '2025-08-03 15:51:00', 'Delta Airlines', 'DE2005', 461.88, 'Première', 27),
(4, 13, '2025-08-04 22:30:00', '2025-08-05 03:36:00', 'Lufthansa', 'LU2006', 379.92, 'Affaires', 8),
(3, 8, '2025-08-04 12:30:00', '2025-08-04 18:34:00', 'Delta Airlines', 'DE2007', 515.21, 'Première', 10),
(2, 15, '2025-08-05 19:00:00', '2025-08-06 00:36:00', 'Emirates', 'EM2008', 299.59, 'Première', 54),
(1, 7, '2025-08-05 06:00:00', '2025-08-05 11:16:00', 'Emirates', 'EM2009', 1000.16, 'Première', 14),
(4, 5, '2025-08-06 14:30:00', '2025-08-06 17:10:00', 'Air Canada', 'AI2010', 242.34, 'Affaires', 33),
(2, 14, '2025-08-06 20:00:00', '2025-08-06 21:59:00', 'Delta Airlines', 'DE2011', 243.97, 'Première', 52),
(3, 12, '2025-08-07 10:00:00', '2025-08-07 12:43:00', 'Emirates', 'EM2012', 514.05, 'Économie', 21);

-- Utilisateurs
INSERT INTO UTILISATEURS (NOM, COURRIEL, MOT_DE_PASSE_HASH, TELEPHONE) VALUES
('Jean Dupont', 'jean.dupont@example.com', 'hash123', '5141234567'),
('Marie Tremblay', 'marie.tremblay@example.com', 'hash456', '4389876543'),
('Ali Ben Salah', 'ali.salah@example.com', 'hash789', '5145551122');

-- Réservations
INSERT INTO RESERVATIONS (ID_UTILISATEUR, ID_VOL, STATUT) VALUES
(1, 1, 'Confirmée'),
(2, 2, 'En attente'),
(3, 3, 'Annulée');

-- Passagers
INSERT INTO PASSAGERS (ID_RESERVATION, NOM_COMPLET, DATE_NAISSANCE, NUMERO_PASSEPORT) VALUES
(1, 'Jean Dupont', '1990-04-15', 'X1234567'),
(2, 'Marie Tremblay', '1985-12-30', 'Y9876543'),
(3, 'Ali Ben Salah', '1995-07-08', 'Z4567890');

-- Paiements
INSERT INTO PAIEMENTS (ID_RESERVATION, DATE_PAIEMENT, MONTANT, METHODE, STATUT) VALUES
(1, '2025-07-25 10:00:00', 199.99, 'Carte', 'Payé'),
(2, '2025-07-26 14:30:00', 799.50, 'Carte', 'Payé'),
(3, '2025-07-27 09:15:00', 350.00, 'Carte', 'Remboursé');