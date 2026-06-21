DROP INDEX idx_card_number ON transaction_log;
CREATE INDEX idx_card_number_status ON transaction_log (card_number, status);
