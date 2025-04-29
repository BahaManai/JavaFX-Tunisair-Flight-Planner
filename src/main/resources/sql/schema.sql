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
