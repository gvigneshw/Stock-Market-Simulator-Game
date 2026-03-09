import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

// Main Stock Market Simulator Game - Full GUI Application
public class StockMarketGame extends JFrame {
    // Game state
    private Player player;
    private List<Stock> availableStocks;
    private StockPriceSimulator priceSimulator;
    private MarketNewsGenerator newsGenerator;
    private javax.swing.Timer gameTimer;
    private javax.swing.Timer newsTimer;
    
    // Database management
    private DatabaseManager dbManager;
    private int currentPlayerId = -1;
    
    // GUI Components
    private TradingPanel tradingPanel;
    private PortfolioPanel portfolioPanel;
    private NewsPanel newsPanel;
    private TransactionHistoryPanel transactionPanel;
    private JTabbedPane mainTabbedPane;
    private JLabel statusLabel;
    
    public StockMarketGame() {
        super("Stock Market Simulator - Advanced Trading Game");
        
        // Initialize database connection
        dbManager = new DatabaseManager();
        
        // Check for saved game and initialize player
        boolean isLoadedGame = checkForSavedGame();
        
        // Initialize game data (stocks, simulators)
        initializeGameData(isLoadedGame);
        
        // Setup GUI
        setupGUI();
        
        // Start simulation
        startSimulation();
        
        // Show welcome dialog (only for new games)
        if (!isLoadedGame) {
            showWelcomeDialog();
        }
    }
    
    private boolean checkForSavedGame() {
        List<String> savedPlayers = dbManager.getAllPlayerNames();
        
        if (!savedPlayers.isEmpty()) {
            String[] options = {"Continue Previous Session", "Start New Game"};
            int choice = JOptionPane.showOptionDialog(this,
                "Welcome back to Stock Market Simulator!\n\n" +
                "Found saved game(s). What would you like to do?",
                "Stock Market Simulator",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
            
            if (choice == 0) { // Continue Previous Session
                String selectedPlayer = (String) JOptionPane.showInputDialog(
                    this,
                    "Select a player to continue:",
                    "Load Game",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    savedPlayers.toArray(),
                    savedPlayers.get(0)
                );
                
                if (selectedPlayer != null) {
                    loadGame(selectedPlayer);
                    return true;
                }
            }
        }
        
        // New game - ask for player name
        String playerName = JOptionPane.showInputDialog(this, 
            "Welcome to Stock Market Simulator!\n\nEnter your name:", 
            "New Game Setup", JOptionPane.QUESTION_MESSAGE);
        
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Trader";
        }
        
        player = new Player(playerName, 100000.0); // Start with $100,000
        return false;
    }
    
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
                String.format("Game loaded successfully!\n\n" +
                    "Player: %s\n" +
                    "Cash: $%,.2f\n" +
                    "Portfolio Value: $%,.2f\n" +
                    "Net Worth: $%,.2f\n" +
                    "Level: %d",
                    player.getName(), player.getCash(), player.getPortfolioValue(),
                    player.getNetWorth(), player.getLevel()),
                "Welcome Back!",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to load player data. Starting new game.",
                "Load Error",
                JOptionPane.ERROR_MESSAGE);
            player = new Player(playerName, 100000.0);
        }
    }
    
    private void saveGame() {
        if (player != null) {
            if (currentPlayerId == -1) {
                currentPlayerId = dbManager.savePlayer(player);
            } else {
                dbManager.savePlayer(player);
            }
            
            dbManager.savePortfolio(currentPlayerId, player.getPortfolio().getHoldings());
            
            statusLabel.setText("Game saved successfully!");
        }
    }
    
    private void saveToDatabase() {
        if (player != null) {
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
    }
    
    private void initializeGameData(boolean isLoadedGame) {
        // Only create player if not loaded from database
        if (player == null) {
            String playerName = JOptionPane.showInputDialog(this, 
                "Welcome to Stock Market Simulator!\n\nEnter your name:", 
                "Player Setup", JOptionPane.QUESTION_MESSAGE);
            
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Trader";
            }
            
            player = new Player(playerName, 100000.0); // Start with $100,000
        }
        
        // Create diverse stock portfolio using Generics
        availableStocks = new ArrayList<>();
        
        // Technology Stocks
        availableStocks.add(new CommonStock("AAPL", "Apple Inc.", 175.50, 0, "Technology", 0.5, 65.0));
        availableStocks.add(new CommonStock("MSFT", "Microsoft Corp.", 380.20, 0, "Technology", 0.8, 55.0));
        availableStocks.add(new CommonStock("GOOGL", "Alphabet Inc.", 142.30, 0, "Technology", 0.0, 70.0));
        availableStocks.add(new CommonStock("NVDA", "NVIDIA Corp.", 495.20, 0, "Technology", 0.03, 88.0));
        
        // Automotive
        availableStocks.add(new CommonStock("TSLA", "Tesla Inc.", 245.80, 0, "Automotive", 0.0, 85.0));
        availableStocks.add(new CommonStock("F", "Ford Motor Co.", 12.45, 0, "Automotive", 4.5, 62.0));
        
        // Finance
        availableStocks.add(new CommonStock("JPM", "JPMorgan Chase", 155.75, 0, "Banking", 2.5, 48.0));
        availableStocks.add(new CommonStock("BAC", "Bank of America", 32.80, 0, "Banking", 2.8, 52.0));
        
        // Healthcare
        availableStocks.add(new CommonStock("JNJ", "Johnson & Johnson", 158.90, 0, "Healthcare", 3.0, 35.0));
        availableStocks.add(new CommonStock("PFE", "Pfizer Inc.", 28.45, 0, "Healthcare", 5.8, 42.0));
        
        // Preferred Stocks
        availableStocks.add(new PreferredStock("VZ-P", "Verizon Preferred", 52.30, 0, "Telecommunications", 5.5, 50.0));
        availableStocks.add(new PreferredStock("BAC-P", "Bank of America Preferred", 25.75, 0, "Banking", 6.0, 25.0));
        availableStocks.add(new PreferredStock("T-P", "AT&T Preferred", 48.20, 0, "Telecommunications", 6.5, 50.0));
        
        // Bonds
        availableStocks.add(new Bond("T-10Y", "US Treasury 10Y", 98.50, 0, "Government", 4.5, 10, "AAA"));
        availableStocks.add(new Bond("CORP-5Y", "Corporate Bond 5Y", 102.00, 0, "Corporate", 5.8, 5, "BBB"));
        availableStocks.add(new Bond("MUNI-7Y", "Municipal Bond 7Y", 95.75, 0, "Municipal", 3.8, 7, "AA"));
        
        // Initialize price simulator and news generator
        priceSimulator = new StockPriceSimulator(availableStocks);
        newsGenerator = new MarketNewsGenerator(availableStocks);
        
        // Add price update listener
        priceSimulator.addPriceUpdateListener((stock, oldPrice, newPrice) -> {
            SwingUtilities.invokeLater(() -> {
                if (tradingPanel != null) {
                    tradingPanel.updateStockTable();
                }
                if (portfolioPanel != null) {
                    portfolioPanel.refreshDisplay();
                }
                updateStatusBar();
            });
        });
    }
    
    private void setupGUI() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitGame();
            }
        });
        
        setLayout(new BorderLayout(10, 10));
        
        // Menu bar
        createMenuBar();
        
        // Main tabbed pane
        mainTabbedPane = new JTabbedPane();
        mainTabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Trading tab
        tradingPanel = new TradingPanel(availableStocks, player);
        tradingPanel.setTradeListener(() -> saveToDatabase()); // Auto-save after trades
        mainTabbedPane.addTab("📊 Trading", tradingPanel);
        
        // Portfolio tab
        portfolioPanel = new PortfolioPanel(player);
        mainTabbedPane.addTab("💼 My Portfolio", portfolioPanel);
        
        // Transaction history tab
        transactionPanel = new TransactionHistoryPanel(player);
        mainTabbedPane.addTab("📜 Transactions", transactionPanel);
        
        add(mainTabbedPane, BorderLayout.CENTER);
        
        // Right panel with news
        newsPanel = new NewsPanel(newsGenerator);
        add(newsPanel, BorderLayout.EAST);
        
        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel("Game started! Good luck trading!");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.add(statusLabel);
        add(statusBar, BorderLayout.SOUTH);
        
        // Set window properties
        setSize(1400, 800);
        setLocationRelativeTo(null);
        
        // Set icon (if available)
        try {
            // You can add an icon here if you have one
        } catch (Exception e) {
            // Icon not available
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Game menu
        JMenu gameMenu = new JMenu("Game");
        
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> newGame());
        gameMenu.add(newGameItem);
        
        gameMenu.addSeparator();
        
        JMenuItem saveItem = new JMenuItem("Save Game");
        saveItem.addActionListener(e -> {
            saveGame();
            JOptionPane.showMessageDialog(this,
                "Game saved successfully!",
                "Save Complete",
                JOptionPane.INFORMATION_MESSAGE);
        });
        gameMenu.add(saveItem);
        
        JMenuItem loadItem = new JMenuItem("Load Game");
        loadItem.addActionListener(e -> {
            List<String> savedPlayers = dbManager.getAllPlayerNames();
            if (!savedPlayers.isEmpty()) {
                String selected = (String) JOptionPane.showInputDialog(
                    this, "Select player:", "Load Game",
                    JOptionPane.QUESTION_MESSAGE, null,
                    savedPlayers.toArray(), savedPlayers.get(0));
                if (selected != null) {
                    // Stop current game
                    priceSimulator.stop();
                    gameTimer.stop();
                    newsTimer.stop();
                    
                    // Load selected player
                    loadGame(selected);
                    
                    // Restart simulation
                    Thread simulatorThread = new Thread(priceSimulator);
                    simulatorThread.setDaemon(true);
                    simulatorThread.start();
                    gameTimer.start();
                    newsTimer.start();
                    
                    // Refresh all panels
                    tradingPanel.updateStockTable();
                    portfolioPanel.refreshDisplay();
                    transactionPanel.updateTransactionTable();
                    updateStatusBar();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No saved games found!",
                    "Load Game", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gameMenu.add(loadItem);
        
        gameMenu.addSeparator();
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> exitGame());
        gameMenu.add(exitItem);
        
        menuBar.add(gameMenu);
        
        // Market menu
        JMenu marketMenu = new JMenu("Market");
        
        JMenuItem refreshItem = new JMenuItem("Refresh Prices");
        refreshItem.addActionListener(e -> {
            tradingPanel.updateStockTable();
            statusLabel.setText("Prices refreshed!");
        });
        marketMenu.add(refreshItem);
        
        JMenuItem newsItem = new JMenuItem("Generate News Event");
        newsItem.addActionListener(e -> generateNewsEvent());
        marketMenu.add(newsItem);
        
        menuBar.add(marketMenu);
        
        // Analysis menu
        JMenu analysisMenu = new JMenu("Analysis");
        
        JMenuItem portfolioAnalysisItem = new JMenuItem("Portfolio Analysis");
        portfolioAnalysisItem.addActionListener(e -> showPortfolioAnalysis());
        analysisMenu.add(portfolioAnalysisItem);
        
        JMenuItem filterStocksItem = new JMenuItem("Filter Stocks");
        filterStocksItem.addActionListener(e -> showStockFilter());
        analysisMenu.add(filterStocksItem);
        
        menuBar.add(analysisMenu);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        JMenuItem howToPlayItem = new JMenuItem("How to Play");
        howToPlayItem.addActionListener(e -> showHowToPlay());
        helpMenu.add(howToPlayItem);
        
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void startSimulation() {
        // Start price simulation thread
        Thread simulatorThread = new Thread(priceSimulator);
        simulatorThread.setDaemon(true);
        simulatorThread.start();
        
        // Game update timer (for UI updates)
        gameTimer = new javax.swing.Timer(5000, e -> {
            portfolioPanel.refreshDisplay();
            transactionPanel.updateTransactionTable();
            updateStatusBar();
        });
        gameTimer.start();
        
        // News generation timer
        newsTimer = new javax.swing.Timer(15000, e -> {
            generateNewsEvent();
        });
        newsTimer.start();
    }
    
    private void generateNewsEvent() {
        MarketNewsGenerator.NewsEvent event = newsGenerator.generateNews();
        event.applyImpact();
        newsPanel.addNewsEvent(event);
        statusLabel.setText("📰 Breaking News: " + event.getHeadline());
    }
    
    private void updateStatusBar() {
        statusLabel.setText(String.format("💰 Cash: $%.2f | 💼 Net Worth: $%.2f | ⭐ Level: %d",
            player.getCash(), player.getNetWorth(), player.getLevel()));
    }
    
    private void showPortfolioAnalysis() {
        if (player.getPortfolio().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Your portfolio is empty!\nStart trading to see analysis.",
                "No Holdings", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Use bounded generic StockAnalyzer
        StockAnalyzer<Stock> analyzer = new StockAnalyzer<>(player.getPortfolio().getHoldings());
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("=== PORTFOLIO ANALYSIS ===\n\n");
        analysis.append(String.format("Total Value: $%.2f\n", analyzer.calculateTotalValue()));
        analysis.append(String.format("Average Price: $%.2f\n", analyzer.calculateAveragePrice()));
        analysis.append(String.format("Weighted Risk: %.2f%%\n\n", analyzer.calculateWeightedAverageRisk()));
        
        Stock highestValue = analyzer.findHighestValueStock();
        if (highestValue != null) {
            analysis.append(String.format("Highest Value: %s ($%.2f)\n", 
                highestValue.getSymbol(), highestValue.getTotalValue()));
        }
        
        Stock lowestRisk = analyzer.findLowestRiskStock();
        if (lowestRisk != null) {
            analysis.append(String.format("Lowest Risk: %s (%.2f%%)\n", 
                lowestRisk.getSymbol(), lowestRisk.calculateRisk()));
        }
        
        JTextArea textArea = new JTextArea(analysis.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
            "Portfolio Analysis", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showStockFilter() {
        String[] options = {"High Value (>$10,000)", "High Risk (>50%)", "Low Risk (<40%)", "Technology Sector"};
        String choice = (String) JOptionPane.showInputDialog(this,
            "Select a filter to apply:",
            "Stock Filter",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == null) return;
        
        StockFilter<Stock> filter = null;
        
        if (choice.equals(options[0])) {
            filter = new HighValueStockFilter(10000.0);
        } else if (choice.equals(options[1])) {
            filter = new HighRiskStockFilter(50.0);
        } else if (choice.equals(options[2])) {
            filter = new LowRiskStockFilter(40.0);
        } else if (choice.equals(options[3])) {
            filter = new SectorStockFilter("Technology");
        }
        
        if (filter != null) {
            List<Stock> filtered = new ArrayList<>();
            for (Stock stock : availableStocks) {
                if (filter.meetsCriteria(stock)) {
                    filtered.add(stock);
                }
            }
            
            StringBuilder result = new StringBuilder();
            result.append(filter.getFilterDescription()).append("\n\n");
            result.append("Matching Stocks:\n");
            
            if (filtered.isEmpty()) {
                result.append("No stocks match this criteria.");
            } else {
                for (Stock stock : filtered) {
                    result.append(String.format("• %s (%s) - $%.2f\n", 
                        stock.getSymbol(), stock.getCompanyName(), stock.getCurrentPrice()));
                }
            }
            
            JTextArea textArea = new JTextArea(result.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                "Filter Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void showWelcomeDialog() {
        String message = String.format(
            "Welcome, %s!\n\n" +
            "Starting Cash: $%.2f\n\n" +
            "Game Objective:\n" +
            "• Buy and sell stocks to maximize profit\n" +
            "• Reach higher levels by growing your net worth\n" +
            "• Watch market news that affects stock prices\n" +
            "• Manage risk across your portfolio\n\n" +
            "Good luck trading!",
            player.getName(), player.getCash());
        
        JOptionPane.showMessageDialog(this, message,
            "Welcome to Stock Market Simulator", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showAboutDialog() {
        String about = "Stock Market Simulator v2.0\n\n" +
                      "A comprehensive Java application demonstrating:\n" +
                      "• Object-Oriented Programming (OOP)\n" +
                      "• Inheritance & Polymorphism\n" +
                      "• Generics (Generic Classes, Methods, Interfaces)\n" +
                      "• GUI Programming with Swing\n" +
                      "• Multithreading & Concurrency\n" +
                      "• Event Handling\n" +
                      "• Collections Framework\n" +
                      "• Exception Handling\n\n" +
                      "Developed for Java Programming Lab\n" +
                      "Domain: Stock Market Trading Game";
        
        JOptionPane.showMessageDialog(this, about, "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showHowToPlay() {
        String howTo = "HOW TO PLAY:\n\n" +
                      "1. TRADING TAB:\n" +
                      "   • Select a stock from the table\n" +
                      "   • Choose quantity with spinner\n" +
                      "   • Click BUY to purchase or SELL to sell\n" +
                      "   • Green rows = Low risk, Red rows = High risk\n\n" +
                      "2. PORTFOLIO TAB:\n" +
                      "   • View your holdings and statistics\n" +
                      "   • Monitor net worth and profit/loss\n" +
                      "   • Track your level progression\n\n" +
                      "3. TRANSACTIONS TAB:\n" +
                      "   • Review complete transaction history\n\n" +
                      "4. MARKET NEWS:\n" +
                      "   • Events appear automatically\n" +
                      "   • News affects stock prices\n" +
                      "   • Watch for opportunities!\n\n" +
                      "5. LEVELING UP:\n" +
                      "   • Every $100,000 net worth = 1 level\n" +
                      "   • Higher levels = bragging rights!";
        
        JTextArea textArea = new JTextArea(howTo);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
            "How to Play", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void newGame() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Start a new game? Current progress will be saved automatically.",
            "New Game", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Save current game before starting new one
            saveGame();
            
            priceSimulator.stop();
            gameTimer.stop();
            newsTimer.stop();
            
            // Close database connection
            if (dbManager != null) {
                dbManager.closeConnection();
            }
            
            dispose();
            SwingUtilities.invokeLater(() -> {
                new StockMarketGame().setVisible(true);
            });
        }
    }
    
    private void exitGame() {
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Exit game? Your progress will be saved automatically.\n\n" +
                "Final Net Worth: $%,.2f\n" +
                "Profit/Loss: $%,.2f (%+.2f%%)",
                player.getNetWorth(), player.getTotalProfit(), player.getProfitPercentage()),
            "Exit Game", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Save game before exiting
            saveGame();
            
            priceSimulator.stop();
            gameTimer.stop();
            newsTimer.stop();
            
            // Close database connection
            if (dbManager != null) {
                dbManager.closeConnection();
            }
            
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show the game
        SwingUtilities.invokeLater(() -> {
            StockMarketGame game = new StockMarketGame();
            game.setVisible(true);
        });
    }
}
