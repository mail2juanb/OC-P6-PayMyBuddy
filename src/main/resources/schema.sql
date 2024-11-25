-- Creation de la Database
CREATE DATABASE IF NOT EXISTS paymybuddy_test;
USE paymybuddy_test;


-- Creation de la table User
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,        -- Identifiant unique avec auto-incrementation
    username    VARCHAR(100) NOT NULL UNIQUE,              -- Nom d'utilisateur unique et non nul
    email       VARCHAR(100) NOT NULL UNIQUE,              -- Adresse e-mail unique et non nulle
    password    VARCHAR(255) NOT NULL,                     -- Mot de passe (hache) non nul
    balance     DECIMAL(65, 2) DEFAULT 0.00               -- Balance financiere avec precision etendue
);

-- Creation de la table Transaction
CREATE TABLE IF NOT EXISTS transactions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,        -- Identifiant unique avec auto-incrementation
    sender      BIGINT NOT NULL,                           -- Identifiant de l'expediteur, cle etrangere
    receiver    BIGINT NOT NULL,                           -- Identifiant du destinataire, cle etrangere
    description VARCHAR(255),                              -- Description de la transaction
    amount      DECIMAL(65, 2) NOT NULL CHECK (amount > 0), -- Montant de la transaction, doit etre positif

    FOREIGN KEY (sender) REFERENCES Users(id),           -- Contrainte de cle etrangere vers la table Users pour le champ sender
    FOREIGN KEY (receiver) REFERENCES Users(id)          -- Contrainte de cle etrangere vers la table Users pour le champ receiver
);

-- Creation de la table user_connections
CREATE TABLE IF NOT EXISTS users_connections (
    user_id             BIGINT NOT NULL,                           -- Identifiant de l'utilisateur, cle etrangere
    connections_id      BIGINT NOT NULL,                           -- Identifiant de l'ami, cle etrangere

    PRIMARY KEY (user_id, connections_id),                      -- Cle primaire composite pour eviter les doublons d'amitie
    FOREIGN KEY (user_id) REFERENCES Users(id),                 -- Cle etrangere vers la table Users pour le champ user_id
    FOREIGN KEY (connections_id) REFERENCES Users(id)          -- Cle etrangere vers la table Users pour le champ friend_id
);
