CREATE TABLE transaction_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_number VARCHAR(255) NOT NULL,
    previous_balance DECIMAL(15, 2) NOT NULL,
    new_balance DECIMAL(15, 2) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    timestamp DATETIME NOT NULL,
    INDEX idx_card_number (card_number)
);
