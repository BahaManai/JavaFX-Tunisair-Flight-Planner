CREATE DATABASE IF NOT EXISTS tunisair_flight_planner;
USE tunisair_flight_planner;

CREATE TABLE AgentProgrammation (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    nom VARCHAR(255) NOT NULL,
                                    email VARCHAR(255) UNIQUE NOT NULL,
                                    mot_de_passe VARCHAR(255) NOT NULL
);

CREATE TABLE Avion (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       modele VARCHAR(255) NOT NULL,
                       capacite INT NOT NULL,
                       estDisponible BOOLEAN DEFAULT TRUE,
                       type_trajet ENUM('Court', 'Moyen', 'Long') NOT NULL
);

CREATE TABLE Equipage (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          nomEquipage VARCHAR(255) NOT NULL
);

CREATE TABLE Membre (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        cin VARCHAR(20) UNIQUE NOT NULL,
                        nom VARCHAR(255) NOT NULL,
                        prenom VARCHAR(255) NOT NULL,
                        role VARCHAR(100) NOT NULL,
                        estDisponible BOOLEAN DEFAULT TRUE
);

CREATE TABLE Equipage_Membre (
                                 equipage_id INT NOT NULL,
                                 membre_id INT NOT NULL,
                                 PRIMARY KEY (equipage_id, membre_id),
                                 FOREIGN KEY (equipage_id) REFERENCES Equipage(id) ON DELETE CASCADE,
                                 FOREIGN KEY (membre_id) REFERENCES Membre(id) ON DELETE CASCADE
);

CREATE TABLE Vol (
                     id INT AUTO_INCREMENT PRIMARY KEY,
                     numVol VARCHAR(100) UNIQUE NOT NULL,
                     destination VARCHAR(255) NOT NULL,
                     heure_depart DATETIME NOT NULL,
                     heure_arrivee DATETIME NOT NULL,
                     type_trajet ENUM('Court', 'Moyen', 'Long') NOT NULL,
                     statutVol ENUM('Planifié', 'Annulé', 'Terminé') NOT NULL,
                     avion_id INT,
                     equipage_id INT,
                     FOREIGN KEY (avion_id) REFERENCES Avion(id),
                     FOREIGN KEY (equipage_id) REFERENCES Equipage(id)
);
CREATE TABLE archiveVol (
    idVol INT PRIMARY KEY,
    numeroVol VARCHAR(50),
    destination VARCHAR(100),
    heureDepart TIMESTAMP,
    heureArrivee TIMESTAMP,
    typeTrajet VARCHAR(50),
    statut VARCHAR(50),
    dateArchivage TIMESTAMP
);
CREATE TABLE ArchiveAvion (
    id INT PRIMARY KEY,
    modele VARCHAR(255) NOT NULL,
    capacite INT NOT NULL,
    estDisponible BOOLEAN,
    type_trajet ENUM('Court', 'Moyen', 'Long') NOT NULL,
    date_archivage DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    cin VARCHAR(20) NOT NULL UNIQUE,
    matricule VARCHAR(20) NOT NULL UNIQUE,
    date_naissance DATE NOT NULL,
    nationalite VARCHAR(50) NOT NULL,
    departement VARCHAR(100) NOT NULL,
    poste VARCHAR(100) NOT NULL,
    base_affectation VARCHAR(100) NOT NULL,
    aeroport VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE CHECK (email LIKE '%@tunisair.com'),
    telephone VARCHAR(20) NOT NULL,
    encrypted_password VARCHAR(255) NOT NULL,
    salt VARCHAR(255) NOT NULL,
    is_approved BOOLEAN DEFAULT FALSE,
    is_admin BOOLEAN DEFAULT FALSE,
    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE Avion ADD COLUMN marque VARCHAR(100);
ALTER TABLE Membre MODIFY role ENUM('Pilote', 'Copilote', 'Chef_de_cabine', 'Hôtesse', 'Mécanicien');
ALTER TABLE archiveVol
ADD COLUMN avion_id INT,
ADD COLUMN equipage_id INT;
ALTER TABLE Vol
ADD COLUMN origine VARCHAR(255) NOT NULL AFTER numVol;

ALTER TABLE avion
DROP COLUMN type_trajet;
ALTER TABLE archiveAvion
DROP COLUMN type_trajet;
ALTER TABLE archiveAvion ADD COLUMN marque VARCHAR(100);
ALTER TABLE archivevol
ADD COLUMN origine VARCHAR(255) DEFAULT '';

-- à exécuter
ALTER TABLE Membre MODIFY role ENUM('Pilote', 'Copilote', 'Chef_de_cabine', 'Hôtesse', 'Mécanicien', 'Agent_de_sécurité') NOT NULL;