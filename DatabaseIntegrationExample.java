/**
 * Example: How to integrate DatabaseManager into StockMarketGame
 * This shows the key integration points
 */

// Add this import at the top of StockMarketGame.java
// import java.sql.*;

public class DatabaseIntegrationExample {
    
    // 1. ADD DATABASE MANAGER FIELD
    // Add this to your StockMarketGame class:
    /*
    private DatabaseManager dbManager;
    private int currentPlayerId = -1;
    */
    
    // 2. INITIALIZE IN CONSTRUCTOR
    // In your StockMarketGame() constructor, after initializeGameData():
    /*
    public StockMarketGame() {
        super("Stock Market Simulator - Advanced Trading Game");
        
        // Initialize database connection
        dbManager = new DatabaseManager();
        
        // Initialize game data
        initializeGameData();
        
        // Check if player wants to load saved game
        checkForSavedGame();
        
        // Setup GUI
        setupGUI();
        
        // Start simulation
        startSimulation();
        
        // Show welcome dialog
        showWelcomeDialog();
    }
    */
    
    // 3. CHECK FOR SAVED GAME
    /*
    private void checkForSavedGame() {
        List<String> savedPlayers = dbManager.getAllPlayerNames();
        
        if (!savedPlayers.isEmpty()) {
            String[] options = {"New Game", "Load Game"};
            int choice = JOptionPane.showOptionDialog(this,
                "Found saved games. What would you like to do?",
                "Stock Market Simulator",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
            
            if (choice == 1) { // Load Game
                String selectedPlayer = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a player to load:",
                    "Load Game",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    savedPlayers.toArray(),
                    savedPlayers.get(0)
                );
                
                if (selectedPlayer != null) {
                    loadGame(selectedPlayer);
                }
            }
        }
    }
    */
    
    // 4. LOAD GAME METHOD
    /*
    private void loadGame(String playerName) {
        Player loadedPlayer = dbManager.loadPlayer(playerName);
        
        if (loadedPlayer != null) {
            player = loadedPlayer;
            currentPlayerId = dbManager.getPlayerIdByName(playerName);
            
            // Load portfolio
            List<Stock> holdings = dbManager.loadPortfolio(currentPlayerId);
            for (Stock stock : holdings) {
                player.getPortfolio().addItem(stock);
            }
            
            // Load transaction history
            List<Transaction> transactions = dbManager.loadTransactions(currentPlayerId);
            player.setTransactionHistory(transactions);
            
            JOptionPane.showMessageDialog(this,
                "Game loaded successfully!\n" +
                "Player: " + player.getName() + "\n" +
                "Cash: $" + String.format("%.2f", player.getCash()) + "\n" +
                "Portfolio Value: $" + String.format("%.2f", player.getPortfolioValue()),
                "Load Successful",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    */
    
    // 5. SAVE GAME METHOD
    /*
    private void saveGame() {
        if (player != null) {
            if (currentPlayerId == -1) {
                currentPlayerId = dbManager.savePlayer(player);
            } else {
                dbManager.savePlayer(player);
            }
            
            dbManager.savePortfolio(currentPlayerId, player.getPortfolio().getHoldings());
            
            JOptionPane.showMessageDialog(this,
                "Game saved successfully!",
                "Save Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    */
    
    // 6. MODIFY BUY STOCK METHOD
    // In your TradingPanel or wherever you handle buying:
    /*
    private void handleBuyStock(Stock stock, int quantity) {
        if (player.buyStock(stock, quantity)) {
            // Update GUI
            updateDisplay();
            
            // Save to database
            saveToDatabase();
            
            JOptionPane.showMessageDialog(this,
                "Successfully purchased " + quantity + " shares of " + stock.getSymbol(),
                "Purchase Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    */
    
    // 7. MODIFY SELL STOCK METHOD
    /*
    private void handleSellStock(Stock stock, int quantity) {
        if (player.sellStock(stock, quantity)) {
            // Update GUI
            updateDisplay();
            
            // Save to database
            saveToDatabase();
            
            JOptionPane.showMessageDialog(this,
                "Successfully sold " + quantity + " shares of " + stock.getSymbol(),
                "Sale Complete",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    */
    
    // 8. SAVE TO DATABASE HELPER
    /*
    private void saveToDatabase() {
        if (currentPlayerId == -1) {
            currentPlayerId = dbManager.savePlayer(player);
        } else {
            dbManager.savePlayer(player);
        }
        
        dbManager.savePortfolio(currentPlayerId, player.getPortfolio().getHoldings());
        
        // Save last transaction
        List<Transaction> transactions = player.getTransactionHistory();
        if (!transactions.isEmpty()) {
            Transaction lastTransaction = transactions.get(transactions.size() - 1);
            dbManager.saveTransaction(currentPlayerId, lastTransaction);
        }
    }
    */
    
    // 9. AUTO-SAVE TIMER (OPTIONAL)
    // Add this to enable auto-save every 30 seconds:
    /*
    private void startAutoSave() {
        Timer autoSaveTimer = new Timer(30000, e -> {
            if (player != null && currentPlayerId != -1) {
                dbManager.savePlayer(player);
                dbManager.savePortfolio(currentPlayerId, player.getPortfolio().getHoldings());
                statusLabel.setText("Auto-saved at " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            }
        });
        autoSaveTimer.start();
    }
    */
    
    // 10. ADD MENU ITEMS
    // Add save/load buttons to your menu:
    /*
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem saveItem = new JMenuItem("Save Game");
        saveItem.addActionListener(e -> saveGame());
        
        JMenuItem loadItem = new JMenuItem("Load Game");
        loadItem.addActionListener(e -> {
            List<String> savedPlayers = dbManager.getAllPlayerNames();
            if (!savedPlayers.isEmpty()) {
                String selected = (String) JOptionPane.showInputDialog(
                    this, "Select player:", "Load Game",
                    JOptionPane.QUESTION_MESSAGE, null,
                    savedPlayers.toArray(), savedPlayers.get(0));
                if (selected != null) {
                    loadGame(selected);
                    updateDisplay();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No saved games found!");
            }
        });
        
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Start a new game? Current progress will be saved.",
                "New Game", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                saveGame();
                restartGame();
            }
        });
        
        gameMenu.add(newGameItem);
        gameMenu.add(saveItem);
        gameMenu.add(loadItem);
        gameMenu.addSeparator();
        gameMenu.add(new JMenuItem("Exit"));
        
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);
    }
    */
    
    // 11. CLOSE CONNECTION ON EXIT
    // Add this to your window closing handler:
    /*
    private void setupWindowClosing() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Save before closing
                if (player != null && currentPlayerId != -1) {
                    saveGame();
                }
                
                // Close database connection
                if (dbManager != null) {
                    dbManager.closeConnection();
                }
                
                System.exit(0);
            }
        });
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    */
    
    // SUMMARY OF CHANGES NEEDED:
    /*
    1. Add DatabaseManager field to StockMarketGame class
    2. Initialize dbManager in constructor
    3. Add currentPlayerId field to track player in database
    4. Add checkForSavedGame() method
    5. Add loadGame() method
    6. Add saveGame() method
    7. Call saveToDatabase() after each buy/sell operation
    8. Add menu items for Save/Load
    9. Close database connection when game closes
    10. (Optional) Add auto-save timer
    */
}
