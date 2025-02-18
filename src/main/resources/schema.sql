DROP TABLE IF EXISTS users_connections;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(100) NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    balance     DECIMAL(38, 2) NOT NULL CHECK (balance >= 0)
);

CREATE TABLE IF NOT EXISTS transactions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender      BIGINT NOT NULL,
    receiver    BIGINT NOT NULL,
    description VARCHAR(255),
    amount      DECIMAL(38, 2) NOT NULL CHECK (amount > 0),

    FOREIGN KEY (sender) REFERENCES Users(id),
    FOREIGN KEY (receiver) REFERENCES Users(id)
);

CREATE TABLE IF NOT EXISTS users_connections (
    user_id             BIGINT NOT NULL,
    connections_id      BIGINT NOT NULL,

    PRIMARY KEY (user_id, connections_id),
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (connections_id) REFERENCES Users(id)
);
