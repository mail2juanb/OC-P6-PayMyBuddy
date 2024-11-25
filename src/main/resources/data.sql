-- Insertion d'utilisateurs dans la table User
INSERT INTO users (username, email, password, balance)
SELECT * FROM (SELECT 'alice123', 'alice@example.com', 'password_hashed_1', 5000.00) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'alice123' AND email = 'alice@example.com'
);
INSERT INTO users (username, email, password, balance)
SELECT * FROM (SELECT 'bob456', 'bob@example.com', 'password_hashed_2', 3000.50) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'bob456' AND email = 'bob@example.com'
);
INSERT INTO users (username, email, password, balance)
SELECT * FROM (SELECT 'charlie789', 'charlie@example.com', 'password_hashed_3', 1000.75) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'charlie789' AND email = 'charlie@example.com'
);
INSERT INTO users (username, email, password, balance)
SELECT * FROM (SELECT 'david321', 'david@example.com', 'password_hashed_4', 1500.00) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'david321' AND email = 'david@example.com'
);




-- Insertion de transactions dans la table Transaction
INSERT INTO transactions (sender, receiver, description, amount)
SELECT * FROM (SELECT 1, 2, 'Transfert pour services rendus', 200.00) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM transactions
    WHERE sender = 1 AND receiver = 2 AND description = 'Transfert pour services rendus' AND amount = 200.00
);
INSERT INTO transactions (sender, receiver, description, amount)
SELECT * FROM (SELECT 3, 1, 'Remboursement prêt', 50.00) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM transactions
    WHERE sender = 3 AND receiver = 1 AND description = 'Remboursement prêt' AND amount = 50.00
);
INSERT INTO transactions (sender, receiver, description, amount)
SELECT * FROM (SELECT 4, 2, 'Achat en ligne', 150.50) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM transactions
    WHERE sender = 4 AND receiver = 2 AND description = 'Achat en ligne' AND amount = 150.50
);
INSERT INTO transactions (sender, receiver, description, amount)
SELECT * FROM (SELECT 2, 3, 'Transfert de fonds personnel', 300.00) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM transactions
    WHERE sender = 2 AND receiver = 3 AND description = 'Transfert de fonds personnel' AND amount = 300.00
);




-- Insertion de connexions dans la table Connections (amis)
INSERT INTO users_connections (user_id, connections_id)
SELECT * FROM (SELECT 1, 2) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM users_connections WHERE user_id = 1 AND connections_id = 2
);
INSERT INTO users_connections (user_id, connections_id)
SELECT * FROM (SELECT 1, 3) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM users_connections WHERE user_id = 1 AND connections_id = 3
);
INSERT INTO users_connections (user_id, connections_id)
SELECT * FROM (SELECT 2, 3) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM users_connections WHERE user_id = 2 AND connections_id = 3
);
INSERT INTO users_connections (user_id, connections_id)
SELECT * FROM (SELECT 3, 4) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM users_connections WHERE user_id = 3 AND connections_id = 4
);
