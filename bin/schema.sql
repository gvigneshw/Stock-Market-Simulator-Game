-- Stock Market Game Database Schema
-- Database: stock_market
-- MySQL 8.0+

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS stock_market;
USE stock_market;

-- Table: players
-- Stores player account information
CREATE TABLE IF NOT EXISTS players (
    player_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    cash DOUBLE NOT NULL,
    initial_cash DOUBLE NOT NULL,
    level INT DEFAULT 1,
    total_profit DOUBLE DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: portfolio
-- Stores player's stock holdings
CREATE TABLE IF NOT EXISTS portfolio (
    portfolio_id INT PRIMARY KEY AUTO_INCREMENT,
    player_id INT NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    stock_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    purchase_price DOUBLE NOT NULL,
    stock_type VARCHAR(50) NOT NULL,
    sector VARCHAR(50),
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE CASCADE,
    UNIQUE KEY unique_player_stock (player_id, stock_symbol),
    INDEX idx_player_id (player_id),
    INDEX idx_stock_symbol (stock_symbol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: transactions
-- Records all buy/sell transactions
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    player_id INT NOT NULL,
    transaction_type ENUM('BUY', 'SELL') NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    quantity INT NOT NULL,
    price DOUBLE NOT NULL,
    total_amount DOUBLE NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE CASCADE,
    INDEX idx_player_id (player_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_stock_symbol (stock_symbol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Optional: Create view for player statistics
CREATE OR REPLACE VIEW player_statistics AS
SELECT 
    p.player_id,
    p.name,
    p.cash,
    p.initial_cash,
    p.level,
    p.total_profit,
    COUNT(DISTINCT po.stock_symbol) as total_stocks,
    COALESCE(SUM(po.quantity * po.purchase_price), 0) as portfolio_value,
    COUNT(t.transaction_id) as total_transactions
FROM players p
LEFT JOIN portfolio po ON p.player_id = po.player_id
LEFT JOIN transactions t ON p.player_id = t.player_id
GROUP BY p.player_id, p.name, p.cash, p.initial_cash, p.level, p.total_profit;

-- Sample query to view player statistics
-- SELECT * FROM player_statistics;

-- Query to get recent transactions for a player
-- SELECT * FROM transactions WHERE player_id = ? ORDER BY transaction_date DESC LIMIT 10;

-- Query to get portfolio summary
-- SELECT stock_symbol, stock_name, quantity, purchase_price, (quantity * purchase_price) as total_value
-- FROM portfolio WHERE player_id = ?;
