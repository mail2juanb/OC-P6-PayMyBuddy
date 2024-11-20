-- Insertion d'utilisateurs dans la table User
INSERT INTO users (username, email, password, balance) VALUES
('alice123', 'alice@example.com', 'password_hashed_1', 5000.00),
('bob456', 'bob@example.com', 'password_hashed_2', 3000.50),
('charlie789', 'charlie@example.com', 'password_hashed_3', 1000.75),
('david321', 'david@example.com', 'password_hashed_4', 1500.00);

-- Insertion de transactions dans la table Transaction
INSERT INTO transactions (sender, receiver, description, amount) VALUES
(1, 2, 'Transfert pour services rendus', 200.00),
(3, 1, 'Remboursement prêt', 50.00),
(4, 2, 'Achat en ligne', 150.50),
(2, 3, 'Transfert de fonds personnel', 300.00);

-- Insertion de connexions dans la table Connections (amis)
INSERT INTO user_connections (user_id, connections_id) VALUES
(1, 2),  -- Alice et Bob sont amis
(1, 3),  -- Alice et Charlie sont amis
(2, 3),  -- Bob et Charlie sont amis
(3, 4);  -- Charlie et David sont amis
