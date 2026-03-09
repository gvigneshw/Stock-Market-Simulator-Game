import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager - Handles all JDBC operations for the Stock Market Game
 * Manages connections to MySQL database and provides CRUD operations
 */
public class DatabaseManager {
    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stock_market";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Vignesh#1234567890";
    
    // Connection instance
    private Connection connection;
    
    /**
     * Constructor - Establishes database connection
     */
    public DatabaseManager() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish connection
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Database connected successfully!");
            
            // Initialize database schema if needed
            initializeDatabase();
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed!");
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize database tables if they don't exist
     */
    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            
            // Create players table
            String createPlayersTable = """
                CREATE TABLE IF NOT EXISTS players (
                    player_id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(100) NOT NULL,
                    cash DOUBLE NOT NULL,
                    initial_cash DOUBLE NOT NULL,
                    level INT DEFAULT 1,
                    total_profit DOUBLE DEFAULT 0.0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """;
            stmt.executeUpdate(createPlayersTable);
            
            // Create portfolio table
            String createPortfolioTable = """
                CREATE TABLE IF NOT EXISTS portfolio (
                    portfolio_id INT PRIMARY KEY AUTO_INCREMENT,
                    player_id INT NOT NULL,
                    stock_symbol VARCHAR(10) NOT NULL,
                    stock_name VARCHAR(100) NOT NULL,
                    quantity INT NOT NULL,
                    purchase_price DOUBLE NOT NULL,
                    stock_type VARCHAR(50) NOT NULL,
                    sector VARCHAR(50),
                    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE CASCADE,
                    UNIQUE KEY unique_player_stock (player_id, stock_symbol)
                )
            """;
            stmt.executeUpdate(createPortfolioTable);
            
            // Create transactions table
            String createTransactionsTable = """
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
                    player_id INT NOT NULL,
                    transaction_type VARCHAR(10) NOT NULL,
                    stock_symbol VARCHAR(10) NOT NULL,
                    quantity INT NOT NULL,
                    price DOUBLE NOT NULL,
                    total_amount DOUBLE NOT NULL,
                    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (player_id) REFERENCES players(player_id) ON DELETE CASCADE
                )
            """;
            stmt.executeUpdate(createTransactionsTable);
            
            System.out.println("✓ Database schema initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to initialize database schema!");
            e.printStackTrace();
        }
    }
    
    /**
     * Save or update player to database
     */
    public int savePlayer(Player player) {
        String checkSql = "SELECT player_id FROM players WHERE name = ?";
        String insertSql = "INSERT INTO players (name, cash, initial_cash, level, total_profit) VALUES (?, ?, ?, ?, ?)";
        String updateSql = "UPDATE players SET cash = ?, level = ?, total_profit = ? WHERE name = ?";
        
        try {
            // Check if player exists
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, player.getName());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Player exists - update
                int playerId = rs.getInt("player_id");
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setDouble(1, player.getCash());
                updateStmt.setInt(2, player.getLevel());
                updateStmt.setDouble(3, player.getTotalProfit());
                updateStmt.setString(4, player.getName());
                updateStmt.executeUpdate();
                System.out.println("✓ Player data updated in database");
                return playerId;
            } else {
                // New player - insert
                PreparedStatement insertStmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                insertStmt.setString(1, player.getName());
                insertStmt.setDouble(2, player.getCash());
                insertStmt.setDouble(3, player.getInitialCash());
                insertStmt.setInt(4, player.getLevel());
                insertStmt.setDouble(5, player.getTotalProfit());
                insertStmt.executeUpdate();
                
                ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int playerId = generatedKeys.getInt(1);
                    System.out.println("✓ New player saved to database with ID: " + playerId);
                    return playerId;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Failed to save player!");
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Load player from database
     */
    public Player loadPlayer(String playerName) {
        String sql = "SELECT * FROM players WHERE name = ?";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double cash = rs.getDouble("cash");
                double initialCash = rs.getDouble("initial_cash");
                Player player = new Player(playerName, initialCash);
                player.setCash(cash);
                player.setLevel(rs.getInt("level"));
                player.setTotalProfit(rs.getDouble("total_profit"));
                
                System.out.println("✓ Player loaded from database");
                return player;
            }
        } catch (SQLException e) {
            System.err.println("✗ Failed to load player!");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Save portfolio holdings to database
     */
    public void savePortfolio(int playerId, List<Stock> holdings) {
        // First, clear existing portfolio for this player
        String deleteSql = "DELETE FROM portfolio WHERE player_id = ?";
        String insertSql = """
            INSERT INTO portfolio (player_id, stock_symbol, stock_name, quantity, purchase_price, stock_type, sector)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), purchase_price = VALUES(purchase_price)
        """;
        
        try {
            // Clear existing entries
            PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
            deleteStmt.setInt(1, playerId);
            deleteStmt.executeUpdate();
            
            // Insert current holdings
            PreparedStatement insertStmt = connection.prepareStatement(insertSql);
            for (Stock stock : holdings) {
                insertStmt.setInt(1, playerId);
                insertStmt.setString(2, stock.getSymbol());
                insertStmt.setString(3, stock.getCompanyName());
                insertStmt.setInt(4, stock.getQuantity());
                insertStmt.setDouble(5, stock.getCurrentPrice());
                insertStmt.setString(6, stock.getStockType());
                insertStmt.setString(7, stock.getSector());
                insertStmt.executeUpdate();
            }
            
            System.out.println("✓ Portfolio saved to database (" + holdings.size() + " stocks)");
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to save portfolio!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load portfolio holdings from database
     */
    public List<Stock> loadPortfolio(int playerId) {
        List<Stock> holdings = new ArrayList<>();
        String sql = "SELECT * FROM portfolio WHERE player_id = ?";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String stockType = rs.getString("stock_type");
                String symbol = rs.getString("stock_symbol");
                String name = rs.getString("stock_name");
                double price = rs.getDouble("purchase_price");
                int quantity = rs.getInt("quantity");
                String sector = rs.getString("sector");
                
                // Create appropriate stock type based on stored type
                Stock stock = createStockByType(stockType, symbol, name, price, quantity, sector);
                if (stock != null) {
                    holdings.add(stock);
                }
            }
            
            System.out.println("✓ Portfolio loaded from database (" + holdings.size() + " stocks)");
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to load portfolio!");
            e.printStackTrace();
        }
        
        return holdings;
    }
    
    /**
     * Helper method to create stock instances based on type
     */
    private Stock createStockByType(String type, String symbol, String name, double price, int quantity, String sector) {
        return switch (type) {
            case "Common Stock" -> new CommonStock(symbol, name, price, quantity, sector, 0.0, 1.0);
            case "Preferred Stock" -> new PreferredStock(symbol, name, price, quantity, sector, 0.0, 100.0);
            default -> new CommonStock(symbol, name, price, quantity, sector, 0.0, 1.0);
        };
    }
    
    /**
     * Save transaction to database
     */
    public void saveTransaction(int playerId, Transaction transaction) {
        String sql = """
            INSERT INTO transactions (player_id, transaction_type, stock_symbol, quantity, price, total_amount)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, playerId);
            pstmt.setString(2, transaction.getType());
            pstmt.setString(3, transaction.getSymbol());
            pstmt.setInt(4, transaction.getQuantity());
            pstmt.setDouble(5, transaction.getPrice());
            pstmt.setDouble(6, transaction.getTotalAmount());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to save transaction!");
            e.printStackTrace();
        }
    }
    
    /**
     * Load transaction history from database
     */
    public List<Transaction> loadTransactions(int playerId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE player_id = ? ORDER BY transaction_date DESC";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, playerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getString("transaction_type"),
                    rs.getString("stock_symbol"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDouble("total_amount")
                );
                transactions.add(transaction);
            }
            
            System.out.println("✓ Transaction history loaded (" + transactions.size() + " transactions)");
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to load transactions!");
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Get all player names from database
     */
    public List<String> getAllPlayerNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT name FROM players ORDER BY name";
        
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Failed to retrieve player names!");
            e.printStackTrace();
        }
        
        return names;
    }
    
    /**
     * Get player ID by name
     */
    public int getPlayerIdByName(String playerName) {
        String sql = "SELECT player_id FROM players WHERE name = ?";
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("player_id");
            }
        } catch (SQLException e) {
            System.err.println("✗ Failed to get player ID!");
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("✗ Failed to close database connection!");
            e.printStackTrace();
        }
    }
    
    /**
     * Get connection instance (for advanced usage)
     */
    public Connection getConnection() {
        return connection;
    }
}
