
-- Insert customers in users table
INSERT INTO users (username, email, password, balance)
VALUES
('user', 'user@user.com', '$2y$10$29P0CD/Fj79Gv5bHcJ2Mgu1LpQPmaFpBxEUexgRVnpEMdS7si1z4i', 5000.00),
('friend', 'friend@friend.com', '$2y$10$eC71IhYogX9u3gM6J8kk0ujpRVMRyAyqNT3uAPQ8XR9bTqPnKbw92', 5000.00),
('ami', 'ami@ami.com', '$2y$10$ugCkw3E91jHCchzI75HYTuky1Jc8LKtfQaro3YgqmtlF3EqKJidZW', 5000.00),
('copain', 'copain@copain.com', '$2y$10$3nM4.S74g.m4yEe4y.2U.e.2nJITJqtL6/sODeVi3/SdvAKeDLCS6', 100.00);



-- Insert transactions in transactions table
INSERT INTO transactions (sender, receiver, description, amount)
VALUES
(1, 2, 'Transfert pour services rendus -> 100', 100.00),
(1, 3, 'Transfert pour services rendus2 -> 200', 200.00);




-- Insert connections in users_connections table
INSERT INTO users_connections (user_id, connections_id)
VALUES
(1, 2),
(1, 3);

