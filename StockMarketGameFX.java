import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.io.File;
import java.util.*;

/**
 * StockSim — Modern Stock Market Simulator
 * JavaFX Application with JDBC Database Integration
 * 
 * Features:
 *  - Real-time stock price simulation with threading
 *  - Beautiful dark-themed UI inspired by crypto exchange dashboards
 *  - Sidebar navigation: Home / Portfolio / Trade / News / History
 *  - Interactive price charts (Canvas-based)
 *  - Buy/Sell trading with instant feedback
 *  - Market news events that impact stock prices
 *  - MySQL database persistence (save/load games, transactions)
 *  - Multiple stock types: Common Stock, Preferred Stock, Bonds
 *  - Portfolio analysis with risk metrics
 */
public class StockMarketGameFX extends Application {

    // ===================== GAME STATE =====================
    private Player player;
    private List<Stock> availableStocks;
    private StockPriceSimulator priceSimulator;
    private MarketNewsGenerator newsGenerator;
    private Timeline gameTimer;
    private Timeline newsTimer;

    // Database
    private DatabaseManager dbManager;
    private int currentPlayerId = -1;

    // ===================== UI COMPONENTS =====================
    private Stage primaryStage;
    private StackPane contentArea;
    private DashboardPanelFX dashboardPanel;
    private TradingPanelFX tradingPanel;
    private PortfolioPanelFX portfolioPanel;
    private NewsPanelFX newsPanel;
    private TransactionHistoryPanelFX transactionPanel;

    // Sidebar
    private Button[] navButtons;
    private Label walletBalance;
    private Label walletLevel;

    // Top bar value labels
    private Label topNetWorthVal, topCashVal, topProfitVal, topLevelVal;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("StockSim — Stock Market Simulator");

        // 1. Database connection
        dbManager = new DatabaseManager();

        // 2. Player setup (load or new)
        boolean isLoadedGame = initializePlayer();

        // 3. Initialize stocks, simulator, news
        initializeGameData();

        // 4. If loaded, restore portfolio
        if (isLoadedGame && currentPlayerId != -1) {
            List<Stock> savedHoldings = dbManager.loadPortfolio(currentPlayerId);
            for (Stock s : savedHoldings) {
                player.getPortfolio().addItem(s);
            }
            List<Transaction> savedTx = dbManager.loadTransactions(currentPlayerId);
            player.setTransactionHistory(savedTx);
        }

        // 5. Build UI
        Scene scene = buildScene();
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        // 6. Start simulation
        startSimulation();

        // 7. Handle close
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            exitGame();
        });

        primaryStage.show();

        // 8. Welcome message (new games only)
        if (!isLoadedGame) {
            Platform.runLater(() -> showStyledAlert("Welcome!", 
                String.format("Welcome to StockSim, %s!\n\nYou start with $100,000.\nBuy low, sell high, and build your fortune!\n\nGood luck!", player.getName()),
                Alert.AlertType.INFORMATION));
        }
    }

    // ===================== PLAYER INITIALIZATION =====================
    private boolean initializePlayer() {
        List<String> savedPlayers = dbManager.getAllPlayerNames();

        if (!savedPlayers.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("StockSim");
            alert.setHeaderText("Welcome to StockSim!");
            alert.setContentText("Saved sessions found. What would you like to do?");
            styleDialog(alert);

            ButtonType continueBtn = new ButtonType("Continue Previous");
            ButtonType newBtn = new ButtonType("New Game");
            ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(continueBtn, newBtn, cancelBtn);

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == continueBtn) {
                ChoiceDialog<String> choice = new ChoiceDialog<>(savedPlayers.get(0), savedPlayers);
                choice.setTitle("Load Game");
                choice.setHeaderText("Select a player:");
                choice.setContentText("Player:");
                styleDialog(choice);

                Optional<String> selected = choice.showAndWait();
                if (selected.isPresent()) {
                    return loadPlayerFromDB(selected.get());
                }
            }
        }

        // New game
        TextInputDialog nameDialog = new TextInputDialog("Trader");
        nameDialog.setTitle("New Game");
        nameDialog.setHeaderText("Create Your Trading Account");
        nameDialog.setContentText("Enter your name:");
        styleDialog(nameDialog);

        Optional<String> nameResult = nameDialog.showAndWait();
        String name = nameResult.orElse("Trader").trim();
        if (name.isEmpty()) name = "Trader";

        player = new Player(name, 100000.0);
        return false;
    }

    private boolean loadPlayerFromDB(String playerName) {
        Player loaded = dbManager.loadPlayer(playerName);
        if (loaded != null) {
            player = loaded;
            currentPlayerId = dbManager.getPlayerIdByName(playerName);
            return true;
        }
        player = new Player(playerName, 100000.0);
        return false;
    }

    // ===================== GAME DATA =====================
    private void initializeGameData() {
        availableStocks = new ArrayList<>();

        // Technology
        availableStocks.add(new CommonStock("AAPL", "Apple Inc.", 175.50, 0, "Technology", 0.5, 65.0));
        availableStocks.add(new CommonStock("MSFT", "Microsoft Corp.", 380.20, 0, "Technology", 0.8, 55.0));
        availableStocks.add(new CommonStock("GOOGL", "Alphabet Inc.", 142.30, 0, "Technology", 0.0, 70.0));
        availableStocks.add(new CommonStock("NVDA", "NVIDIA Corp.", 495.20, 0, "Technology", 0.03, 88.0));
        availableStocks.add(new CommonStock("META", "Meta Platforms", 370.50, 0, "Technology", 0.0, 72.0));

        // Automotive
        availableStocks.add(new CommonStock("TSLA", "Tesla Inc.", 245.80, 0, "Automotive", 0.0, 85.0));
        availableStocks.add(new CommonStock("F", "Ford Motor Co.", 12.45, 0, "Automotive", 4.5, 62.0));

        // Banking
        availableStocks.add(new CommonStock("JPM", "JPMorgan Chase", 155.75, 0, "Banking", 2.5, 48.0));
        availableStocks.add(new CommonStock("BAC", "Bank of America", 32.80, 0, "Banking", 2.8, 52.0));
        availableStocks.add(new CommonStock("GS", "Goldman Sachs", 385.60, 0, "Banking", 2.2, 58.0));

        // Healthcare
        availableStocks.add(new CommonStock("JNJ", "Johnson & Johnson", 158.90, 0, "Healthcare", 3.0, 35.0));
        availableStocks.add(new CommonStock("PFE", "Pfizer Inc.", 28.45, 0, "Healthcare", 5.8, 42.0));
        availableStocks.add(new CommonStock("UNH", "UnitedHealth Group", 520.30, 0, "Healthcare", 1.3, 40.0));

        // Preferred Stocks
        availableStocks.add(new PreferredStock("VZ-P", "Verizon Preferred", 52.30, 0, "Telecommunications", 5.5, 50.0));
        availableStocks.add(new PreferredStock("BAC-P", "BofA Preferred", 25.75, 0, "Banking", 6.0, 25.0));
        availableStocks.add(new PreferredStock("T-P", "AT&T Preferred", 48.20, 0, "Telecommunications", 6.5, 50.0));

        // Bonds
        availableStocks.add(new Bond("T-10Y", "US Treasury 10Y", 98.50, 0, "Government", 4.5, 10, "AAA"));
        availableStocks.add(new Bond("CORP-5Y", "Corporate Bond 5Y", 102.00, 0, "Corporate", 5.8, 5, "BBB"));
        availableStocks.add(new Bond("MUNI-7Y", "Municipal Bond 7Y", 95.75, 0, "Municipal", 3.8, 7, "AA"));

        // Initialize simulation & news
        priceSimulator = new StockPriceSimulator(availableStocks);
        newsGenerator = new MarketNewsGenerator(availableStocks);

        // Price update listener → refresh UI on JavaFX thread
        priceSimulator.addPriceUpdateListener((stock, oldPrice, newPrice) -> {
            Platform.runLater(() -> {
                if (dashboardPanel != null) dashboardPanel.refresh();
                if (tradingPanel != null) tradingPanel.updateStockTable();
                updateTopBar();
                updateWallet();
            });
        });
    }

    // ===================== BUILD SCENE =====================
    private Scene buildScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0b0e11;");

        // Sidebar
        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);

        // Top bar
        HBox topBar = buildTopBar();
        root.setTop(topBar);

        // Content area
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #0b0e11;");

        // Create all panels
        dashboardPanel = new DashboardPanelFX(availableStocks, player, priceSimulator, newsGenerator);
        dashboardPanel.setOnTradeCallback(this::onTradeCompleted);

        tradingPanel = new TradingPanelFX(availableStocks, player);
        tradingPanel.setOnTradeCallback(this::onTradeCompleted);
        tradingPanel.setOnViewChart(() -> {
            String sym = tradingPanel.getSelectedSymbol();
            if (sym != null) {
                dashboardPanel.selectStock(sym);
                navigateTo(0); // Switch to Home
            }
        });

        portfolioPanel = new PortfolioPanelFX(player);
        newsPanel = new NewsPanelFX(newsGenerator);
        transactionPanel = new TransactionHistoryPanelFX(player);

        contentArea.getChildren().addAll(
            dashboardPanel, portfolioPanel, tradingPanel, newsPanel, transactionPanel
        );

        // Show only dashboard initially
        for (int i = 0; i < contentArea.getChildren().size(); i++) {
            contentArea.getChildren().get(i).setVisible(i == 0);
            contentArea.getChildren().get(i).setManaged(i == 0);
        }

        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1400, 850);

        // Load CSS
        try {
            String cssPath = new File("style.css").toURI().toString();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Warning: Could not load style.css");
        }

        return scene;
    }

    // ===================== SIDEBAR =====================
    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);

        // Logo
        Label logo = new Label("\u25C6  StockSim");
        logo.getStyleClass().add("sidebar-logo");

        // Navigation
        String[] navItems = {
            "\u2302   Home",        // ⌂
            "\u25C8   Portfolio",    // ◈
            "\u21C5   Trade",       // ⇅
            "\u2630   News",        // ☰
            "\u2261   History"      // ≡
        };

        VBox navBox = new VBox(4);
        navBox.setPadding(new Insets(8, 12, 8, 12));

        navButtons = new Button[navItems.length];
        for (int i = 0; i < navItems.length; i++) {
            Button btn = new Button(navItems[i]);
            btn.getStyleClass().add("sidebar-btn");
            if (i == 0) btn.getStyleClass().add("sidebar-btn-active");
            btn.setMaxWidth(Double.MAX_VALUE);
            int idx = i;
            btn.setOnAction(e -> navigateTo(idx));
            navButtons[i] = btn;
            navBox.getChildren().add(btn);
        }

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Separator
        Separator sep = new Separator();
        sep.getStyleClass().add("sidebar-divider");

        // Game controls (mini buttons)
        VBox controlsBox = new VBox(4);
        controlsBox.setPadding(new Insets(8, 12, 4, 12));

        Button saveBtn = new Button("\u2B07   Save Game");
        saveBtn.getStyleClass().add("sidebar-btn");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> saveGame());

        Button loadBtn = new Button("\u2B06   Load Game");
        loadBtn.getStyleClass().add("sidebar-btn");
        loadBtn.setMaxWidth(Double.MAX_VALUE);
        loadBtn.setOnAction(e -> loadGameMenu());

        Button newBtn = new Button("\u271A   New Game");
        newBtn.getStyleClass().add("sidebar-btn");
        newBtn.setMaxWidth(Double.MAX_VALUE);
        newBtn.setOnAction(e -> newGame());

        controlsBox.getChildren().addAll(saveBtn, loadBtn, newBtn);

        // Wallet section
        VBox wallet = buildWalletSection();

        sidebar.getChildren().addAll(logo, navBox, spacer, sep, controlsBox, wallet);
        return sidebar;
    }

    private VBox buildWalletSection() {
        VBox wallet = new VBox(6);
        wallet.getStyleClass().add("wallet-section");
        VBox.setMargin(wallet, new Insets(12));

        Label title = new Label("Your Wallet");
        title.getStyleClass().add("wallet-title");

        // Colored dots representing portfolio diversity
        HBox dots = new HBox(6);
        dots.setAlignment(Pos.CENTER_LEFT);
        String[] colors = {"#f7931a", "#627eea", "#0ecb81", "#f6465d", "#fcd535", "#8247e5"};
        for (String c : colors) {
            Region dot = new Region();
            dot.setMinSize(12, 12);
            dot.setMaxSize(12, 12);
            dot.setStyle("-fx-background-color: " + c + "; -fx-background-radius: 6;");
            dots.getChildren().add(dot);
        }

        walletBalance = new Label("$100,000.00");
        walletBalance.getStyleClass().add("wallet-balance");

        walletLevel = new Label("Level 1 Trader");
        walletLevel.getStyleClass().add("wallet-sub");

        wallet.getChildren().addAll(title, dots, walletBalance, walletLevel);
        updateWallet();
        return wallet;
    }

    private void updateWallet() {
        if (walletBalance != null) {
            walletBalance.setText(String.format("$%,.2f", player.getCash()));
        }
        if (walletLevel != null) {
            player.checkLevelUp();
            walletLevel.setText("Level " + player.getLevel() + " Trader");
        }
    }

    // ===================== TOP BAR =====================
    private HBox buildTopBar() {
        HBox bar = new HBox(8);
        bar.getStyleClass().add("top-bar");
        bar.setAlignment(Pos.CENTER_LEFT);

        HBox topNetWorth = makeTopStat("Net Worth:", "$100,000.00");
        topNetWorthVal = (Label) topNetWorth.getChildren().get(1);
        HBox topCash = makeTopStat("Cash:", "$100,000.00");
        topCashVal = (Label) topCash.getChildren().get(1);
        HBox topProfit = makeTopStat("P/L:", "$0.00");
        topProfitVal = (Label) topProfit.getChildren().get(1);
        HBox topLevel = makeTopStat("Level:", "1");
        topLevelVal = (Label) topLevel.getChildren().get(1);

        Separator sep1 = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep1.setStyle("-fx-background-color: #2b3139;");
        Separator sep2 = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep2.setStyle("-fx-background-color: #2b3139;");
        Separator sep3 = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep3.setStyle("-fx-background-color: #2b3139;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label playerName = new Label(player.getName());
        playerName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        playerName.setTextFill(Color.web("#fcd535"));

        // Avatar circle
        Region avatar = new Region();
        avatar.setMinSize(32, 32);
        avatar.setMaxSize(32, 32);
        avatar.setStyle("-fx-background-color: #fcd535; -fx-background-radius: 16;");

        bar.getChildren().addAll(
            topNetWorth, sep1, topCash, sep2, topProfit, sep3, topLevel,
            spacer, playerName, avatar
        );

        updateTopBar();
        return bar;
    }

    private HBox makeTopStat(String label, String value) {
        HBox stat = new HBox(4);
        stat.setAlignment(Pos.CENTER_LEFT);
        stat.setPadding(new Insets(0, 8, 0, 8));

        Label lbl = new Label(label);
        lbl.getStyleClass().add("top-stat");

        Label val = new Label(value);
        val.getStyleClass().add("top-stat-value");

        stat.getChildren().addAll(lbl, val);
        return stat;
    }

    private void updateTopBar() {
        if (topNetWorthVal == null) return;
        player.calculateTotalProfit();
        player.checkLevelUp();

        topNetWorthVal.setText(String.format("$%,.2f", player.getNetWorth()));
        topCashVal.setText(String.format("$%,.2f", player.getCash()));

        double profit = player.getTotalProfit();
        topProfitVal.setText(String.format("$%,.2f (%+.1f%%)", profit, player.getProfitPercentage()));
        topProfitVal.getStyleClass().removeAll("top-stat-positive", "top-stat-negative", "top-stat-value");
        topProfitVal.getStyleClass().add(profit >= 0 ? "top-stat-positive" : "top-stat-negative");

        topLevelVal.setText(String.valueOf(player.getLevel()));
    }

    // ===================== NAVIGATION =====================
    private void navigateTo(int index) {
        // Update button styles
        for (Button btn : navButtons) {
            btn.getStyleClass().remove("sidebar-btn-active");
        }
        navButtons[index].getStyleClass().add("sidebar-btn-active");

        // Show/hide panels
        for (int i = 0; i < contentArea.getChildren().size(); i++) {
            contentArea.getChildren().get(i).setVisible(i == index);
            contentArea.getChildren().get(i).setManaged(i == index);
        }

        // Refresh the target panel
        switch (index) {
            case 0 -> dashboardPanel.refresh();
            case 1 -> portfolioPanel.refreshDisplay();
            case 2 -> { tradingPanel.updateStockTable(); tradingPanel.updateCashLabel(); }
            case 3 -> newsPanel.updateNews();
            case 4 -> transactionPanel.updateTransactionTable();
        }
    }

    // ===================== SIMULATION =====================
    private void startSimulation() {
        // Start price simulation thread
        Thread simThread = new Thread(priceSimulator);
        simThread.setDaemon(true);
        simThread.start();

        // Periodic UI refresh (every 5s)
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            portfolioPanel.refreshDisplay();
            transactionPanel.updateTransactionTable();
            updateTopBar();
            updateWallet();
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();

        // News generation (every 15s)
        newsTimer = new Timeline(new KeyFrame(Duration.seconds(15), e -> {
            MarketNewsGenerator.NewsEvent event = newsGenerator.generateRandomNews();
            newsPanel.addNewsEvent(event);
            dashboardPanel.refreshNews();
        }));
        newsTimer.setCycleCount(Timeline.INDEFINITE);
        newsTimer.play();
    }

    // ===================== TRADE CALLBACK =====================
    private void onTradeCompleted() {
        // Save to database
        saveToDatabase();
        // Refresh all panels
        updateTopBar();
        updateWallet();
        dashboardPanel.refresh();
        tradingPanel.updateStockTable();
        tradingPanel.updateCashLabel();
        portfolioPanel.refreshDisplay();
        transactionPanel.updateTransactionTable();
    }

    private void saveToDatabase() {
        if (player != null) {
            if (currentPlayerId == -1) {
                currentPlayerId = dbManager.savePlayer(player);
            } else {
                dbManager.savePlayer(player);
            }
            dbManager.savePortfolio(currentPlayerId, player.getPortfolio().getHoldings());

            // Save latest transaction
            List<Transaction> tx = player.getTransactionHistory();
            if (!tx.isEmpty()) {
                dbManager.saveTransaction(currentPlayerId, tx.get(tx.size() - 1));
            }
        }
    }

    // ===================== SAVE / LOAD / NEW GAME =====================
    private void saveGame() {
        saveToDatabase();
        showStyledAlert("Game Saved", 
            String.format("Progress saved successfully!\n\nPlayer: %s\nNet Worth: $%,.2f\nLevel: %d",
                player.getName(), player.getNetWorth(), player.getLevel()),
            Alert.AlertType.INFORMATION);
    }

    private void loadGameMenu() {
        List<String> savedPlayers = dbManager.getAllPlayerNames();
        if (savedPlayers.isEmpty()) {
            showStyledAlert("Load Game", "No saved games found!", Alert.AlertType.INFORMATION);
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(savedPlayers.get(0), savedPlayers);
        dialog.setTitle("Load Game");
        dialog.setHeaderText("Select a player:");
        dialog.setContentText("Player:");
        styleDialog(dialog);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Save current first
            saveToDatabase();
            // Stop simulation
            priceSimulator.stop();
            gameTimer.stop();
            newsTimer.stop();

            // Load
            loadPlayerFromDB(result.get());
            List<Stock> holdings = dbManager.loadPortfolio(currentPlayerId);
            for (Stock s : holdings) {
                player.getPortfolio().addItem(s);
            }
            player.setTransactionHistory(dbManager.loadTransactions(currentPlayerId));

            // Reinitialize simulation with existing stocks
            priceSimulator = new StockPriceSimulator(availableStocks);
            priceSimulator.addPriceUpdateListener((stock, oldPrice, newPrice) -> {
                Platform.runLater(() -> {
                    if (dashboardPanel != null) dashboardPanel.refresh();
                    if (tradingPanel != null) tradingPanel.updateStockTable();
                    updateTopBar();
                    updateWallet();
                });
            });

            startSimulation();

            // Refresh everything
            onTradeCompleted();
            navigateTo(0);

            showStyledAlert("Welcome Back!",
                String.format("Game loaded!\n\nPlayer: %s\nCash: $%,.2f\nNet Worth: $%,.2f",
                    player.getName(), player.getCash(), player.getNetWorth()),
                Alert.AlertType.INFORMATION);
        }
    }

    private void newGame() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("New Game");
        confirm.setHeaderText("Start a new game?");
        confirm.setContentText("Your current progress will be saved automatically.");
        styleDialog(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            saveToDatabase();
            priceSimulator.stop();
            gameTimer.stop();
            newsTimer.stop();
            if (dbManager != null) dbManager.closeConnection();

            primaryStage.close();
            Platform.runLater(() -> new StockMarketGameFX().start(new Stage()));
        }
    }

    private void exitGame() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Exit StockSim");
        confirm.setHeaderText("Exit the game?");
        confirm.setContentText(String.format(
            "Your progress will be saved.\n\nFinal Net Worth: $%,.2f\nProfit/Loss: $%,.2f (%+.1f%%)",
            player.getNetWorth(), player.getTotalProfit(), player.getProfitPercentage()));
        styleDialog(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            saveToDatabase();
            priceSimulator.stop();
            if (gameTimer != null) gameTimer.stop();
            if (newsTimer != null) newsTimer.stop();
            if (dbManager != null) dbManager.closeConnection();
            Platform.exit();
            System.exit(0);
        }
    }

    // ===================== DIALOG STYLING =====================
    private void styleDialog(Dialog<?> dialog) {
        try {
            DialogPane pane = dialog.getDialogPane();
            String cssPath = new File("style.css").toURI().toString();
            pane.getStylesheets().add(cssPath);
            pane.setStyle("-fx-background-color: #1e2329;");
        } catch (Exception ignored) {}
    }

    private void showStyledAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(content);
        styleDialog(alert);
        alert.showAndWait();
    }

    // ===================== MAIN =====================
    public static void main(String[] args) {
        launch(args);
    }
}
