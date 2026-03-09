import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.List;

/**
 * Dashboard / Home panel — the main view of the application.
 * Shows selected stock info, price chart, buy/sell panel, market stats, and news.
 * Design inspired by modern crypto exchange dashboards.
 */
public class DashboardPanelFX extends VBox {
    private List<Stock> stocks;
    private Player player;
    private StockPriceSimulator priceSimulator;
    private MarketNewsGenerator newsGenerator;

    // UI Components
    private ComboBox<String> stockSelector;
    private Label stockNameLabel;
    private Label priceLabel;
    private Label changeLabel;
    private StockChartCanvas chartCanvas;
    private Spinner<Integer> quantitySpinner;
    private Label totalCostLabel;
    private Label cashLabel;
    private Label buySellPriceLabel;
    private Label exchangeInfoLabel;
    private Button actionButton;
    private boolean isBuyMode = true;
    private Button buyTabBtn, sellTabBtn;
    private VBox recentNewsBox;
    private Label statusMessage;

    // Stats labels
    private Label statNetWorth, statPortfolio, statStocks, statProfit;

    private Runnable onTradeCallback;
    private Stock selectedStock;

    public DashboardPanelFX(List<Stock> stocks, Player player,
                            StockPriceSimulator priceSimulator,
                            MarketNewsGenerator newsGenerator) {
        this.stocks = stocks;
        this.player = player;
        this.priceSimulator = priceSimulator;
        this.newsGenerator = newsGenerator;

        setSpacing(0);
        setPadding(new Insets(0));
        setStyle("-fx-background-color: #0b0e11;");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: #0b0e11; -fx-background: #0b0e11;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));
        content.setStyle("-fx-background-color: #0b0e11;");

        // 1. Stock header
        HBox header = createStockHeader();
        // 2. Chart + Buy/Sell
        HBox mainContent = createMainContent();
        // 3. Market Stats
        HBox statsRow = createStatsRow();
        // 4. Recent News
        VBox newsSection = createNewsSection();

        content.getChildren().addAll(header, mainContent, statsRow, newsSection);
        scroll.setContent(content);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        getChildren().add(scroll);

        // Default selection
        if (!stocks.isEmpty()) {
            stockSelector.getSelectionModel().selectFirst();
        }
    }

    public void setOnTradeCallback(Runnable callback) {
        this.onTradeCallback = callback;
    }

    // ========================== STOCK HEADER ==========================
    private HBox createStockHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 8, 0));

        // Stock selector
        stockSelector = new ComboBox<>();
        for (Stock s : stocks) {
            stockSelector.getItems().add(s.getSymbol() + " - " + s.getCompanyName());
        }
        stockSelector.setPrefWidth(280);
        stockSelector.setOnAction(e -> onStockSelected());

        // Stock info
        VBox infoBox = new VBox(2);
        stockNameLabel = new Label("Select a stock");
        stockNameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        stockNameLabel.setTextFill(Color.web("#eaecef"));

        HBox priceRow = new HBox(12);
        priceRow.setAlignment(Pos.CENTER_LEFT);
        priceLabel = new Label("$0.00");
        priceLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        priceLabel.setTextFill(Color.web("#eaecef"));
        changeLabel = new Label("");
        changeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        priceRow.getChildren().addAll(priceLabel, changeLabel);

        infoBox.getChildren().addAll(stockNameLabel, priceRow);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Status message (toast)
        statusMessage = new Label("");
        statusMessage.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        statusMessage.setTextFill(Color.web("#0ecb81"));

        header.getChildren().addAll(stockSelector, infoBox, statusMessage);
        return header;
    }

    // ========================== MAIN CONTENT ==========================
    private HBox createMainContent() {
        HBox main = new HBox(20);
        main.setPrefHeight(380);

        // Left: Chart
        VBox chartBox = createChartSection();
        HBox.setHgrow(chartBox, Priority.ALWAYS);

        // Right: Buy/Sell card
        VBox tradeCard = createTradeCard();
        tradeCard.setMinWidth(300);
        tradeCard.setMaxWidth(320);

        main.getChildren().addAll(chartBox, tradeCard);
        return main;
    }

    private VBox createChartSection() {
        VBox box = new VBox(12);
        box.getStyleClass().add("card");
        box.setPadding(new Insets(16));

        // Tab-like buttons (Chart | Details)
        HBox tabRow = new HBox(2);
        Button chartTabBtn = new Button("Chart");
        chartTabBtn.getStyleClass().addAll("btn-tab", "btn-tab-active");
        Button detailsTabBtn = new Button("Details");
        detailsTabBtn.getStyleClass().add("btn-tab");
        tabRow.getChildren().addAll(chartTabBtn, detailsTabBtn);

        // Chart canvas
        chartCanvas = new StockChartCanvas();
        chartCanvas.setMinHeight(280);
        VBox.setVgrow(chartCanvas, Priority.ALWAYS);

        box.getChildren().addAll(tabRow, chartCanvas);
        return box;
    }

    private VBox createTradeCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(20));

        // Buy / Sell tabs
        HBox tabRow = new HBox(4);
        tabRow.setAlignment(Pos.CENTER);

        buyTabBtn = new Button("Buy");
        buyTabBtn.getStyleClass().addAll("btn-buy");
        buyTabBtn.setPrefWidth(120);
        buyTabBtn.setOnAction(e -> switchToMode(true));

        sellTabBtn = new Button("Sell");
        sellTabBtn.getStyleClass().addAll("btn-outline");
        sellTabBtn.setPrefWidth(120);
        sellTabBtn.setOnAction(e -> switchToMode(false));

        tabRow.getChildren().addAll(buyTabBtn, sellTabBtn);

        // Price display
        buySellPriceLabel = new Label("$0.00 USD");
        buySellPriceLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        buySellPriceLabel.setTextFill(Color.web("#eaecef"));

        exchangeInfoLabel = new Label("");
        exchangeInfoLabel.setFont(Font.font("Segoe UI", 11));
        exchangeInfoLabel.setTextFill(Color.web("#848e9c"));

        // Separator
        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #2b3139;");

        // Quantity
        Label qtyLabel = new Label("Quantity (shares)");
        qtyLabel.setTextFill(Color.web("#848e9c"));
        qtyLabel.setFont(Font.font("Segoe UI", 12));

        quantitySpinner = new Spinner<>(1, 100000, 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.setMaxWidth(Double.MAX_VALUE);
        quantitySpinner.setPrefHeight(40);
        quantitySpinner.valueProperty().addListener((obs, o, n) -> updateTotalCost());

        // Total cost
        Label totalLabel = new Label("Total");
        totalLabel.setTextFill(Color.web("#848e9c"));
        totalLabel.setFont(Font.font("Segoe UI", 12));

        totalCostLabel = new Label("$0.00");
        totalCostLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        totalCostLabel.setTextFill(Color.web("#eaecef"));

        // Cash available
        cashLabel = new Label(String.format("Cash: $%,.2f", player.getCash()));
        cashLabel.setFont(Font.font("Segoe UI", 12));
        cashLabel.setTextFill(Color.web("#848e9c"));

        // Separator
        Separator sep2 = new Separator();

        // Action button
        actionButton = new Button("Buy");
        actionButton.getStyleClass().add("btn-buy");
        actionButton.setMaxWidth(Double.MAX_VALUE);
        actionButton.setPrefHeight(48);
        actionButton.setOnAction(e -> executeTrade());

        card.getChildren().addAll(tabRow,
                buySellPriceLabel, exchangeInfoLabel,
                sep1,
                qtyLabel, quantitySpinner,
                totalLabel, totalCostLabel,
                cashLabel,
                sep2,
                actionButton);

        return card;
    }

    // ========================== STATS ROW ==========================
    private HBox createStatsRow() {
        HBox row = new HBox(16);

        statNetWorth = new Label("$0.00");
        VBox card1 = makeStatCard("Net Worth", statNetWorth);

        statPortfolio = new Label("$0.00");
        VBox card2 = makeStatCard("Portfolio Value", statPortfolio);

        statStocks = new Label("0");
        VBox card3 = makeStatCard("Holdings", statStocks);

        statProfit = new Label("$0.00");
        VBox card4 = makeStatCard("Profit / Loss", statProfit);

        HBox.setHgrow(card1, Priority.ALWAYS);
        HBox.setHgrow(card2, Priority.ALWAYS);
        HBox.setHgrow(card3, Priority.ALWAYS);
        HBox.setHgrow(card4, Priority.ALWAYS);

        row.getChildren().addAll(card1, card2, card3, card4);
        updateStats();
        return row;
    }

    private VBox makeStatCard(String title, Label valueLabel) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("stat-label");

        valueLabel.getStyleClass().add("stat-value");

        card.getChildren().addAll(titleLbl, valueLabel);
        return card;
    }

    // ========================== NEWS SECTION ==========================
    private VBox createNewsSection() {
        VBox section = new VBox(12);
        section.getStyleClass().add("card");
        section.setPadding(new Insets(16));

        Label title = new Label("Top Stories");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#eaecef"));

        recentNewsBox = new VBox(8);
        refreshNews();

        section.getChildren().addAll(title, recentNewsBox);
        return section;
    }

    // ========================== TRADE LOGIC ==========================
    private void switchToMode(boolean buy) {
        isBuyMode = buy;
        if (buy) {
            buyTabBtn.getStyleClass().removeAll("btn-outline");
            if (!buyTabBtn.getStyleClass().contains("btn-buy"))
                buyTabBtn.getStyleClass().add("btn-buy");
            sellTabBtn.getStyleClass().removeAll("btn-sell");
            if (!sellTabBtn.getStyleClass().contains("btn-outline"))
                sellTabBtn.getStyleClass().add("btn-outline");

            actionButton.setText(selectedStock != null ? "Buy " + selectedStock.getSymbol() : "Buy");
            actionButton.getStyleClass().removeAll("btn-sell");
            if (!actionButton.getStyleClass().contains("btn-buy"))
                actionButton.getStyleClass().add("btn-buy");
        } else {
            sellTabBtn.getStyleClass().removeAll("btn-outline");
            if (!sellTabBtn.getStyleClass().contains("btn-sell"))
                sellTabBtn.getStyleClass().add("btn-sell");
            buyTabBtn.getStyleClass().removeAll("btn-buy");
            if (!buyTabBtn.getStyleClass().contains("btn-outline"))
                buyTabBtn.getStyleClass().add("btn-outline");

            actionButton.setText(selectedStock != null ? "Sell " + selectedStock.getSymbol() : "Sell");
            actionButton.getStyleClass().removeAll("btn-buy");
            if (!actionButton.getStyleClass().contains("btn-sell"))
                actionButton.getStyleClass().add("btn-sell");
        }
        updateTotalCost();
    }

    private void executeTrade() {
        if (selectedStock == null) {
            showStatus("Please select a stock first", false);
            return;
        }

        int quantity = quantitySpinner.getValue();
        if (quantity <= 0) {
            showStatus("Invalid quantity", false);
            return;
        }

        if (isBuyMode) {
            double totalCost = selectedStock.getCurrentPrice() * quantity;
            if (totalCost > player.getCash()) {
                showStatus(String.format("Insufficient funds! Need $%,.2f", totalCost), false);
                return;
            }
            if (player.buyStock(selectedStock, quantity)) {
                showStatus(String.format("Bought %d shares of %s for $%,.2f",
                        quantity, selectedStock.getSymbol(), totalCost), true);
                if (onTradeCallback != null) onTradeCallback.run();
            }
        } else {
            Stock owned = findOwnedStock(selectedStock.getSymbol());
            if (owned == null || owned.getQuantity() < quantity) {
                int ownedQty = owned != null ? owned.getQuantity() : 0;
                showStatus(String.format("Not enough shares! You own %d", ownedQty), false);
                return;
            }
            double totalRevenue = selectedStock.getCurrentPrice() * quantity;
            if (player.sellStock(selectedStock, quantity)) {
                showStatus(String.format("Sold %d shares of %s for $%,.2f",
                        quantity, selectedStock.getSymbol(), totalRevenue), true);
                if (onTradeCallback != null) onTradeCallback.run();
            }
        }
        updateTradeCard();
        updateStats();
    }

    private Stock findOwnedStock(String symbol) {
        for (Stock s : player.getPortfolio().getHoldings()) {
            if (s.getSymbol().equals(symbol)) return s;
        }
        return null;
    }

    private void showStatus(String message, boolean success) {
        statusMessage.setText(message);
        statusMessage.setTextFill(success ? Color.web("#0ecb81") : Color.web("#f6465d"));
        // Auto-clear after 4 seconds
        new Thread(() -> {
            try { Thread.sleep(4000); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                if (statusMessage.getText().equals(message)) {
                    statusMessage.setText("");
                }
            });
        }).start();
    }

    // ========================== UPDATE METHODS ==========================
    private void onStockSelected() {
        int idx = stockSelector.getSelectionModel().getSelectedIndex();
        if (idx >= 0 && idx < stocks.size()) {
            selectedStock = stocks.get(idx);
            updateStockDisplay();
            updateChart();
            updateTradeCard();
        }
    }

    private void updateStockDisplay() {
        if (selectedStock == null) return;
        stockNameLabel.setText(selectedStock.getCompanyName() + "  " + selectedStock.getStockType());

        double price = selectedStock.getCurrentPrice();
        priceLabel.setText(String.format("$%,.2f", price));

        // Calculate change from price history
        List<Double> history = priceSimulator.getPriceHistory(selectedStock.getSymbol());
        if (history != null && !history.isEmpty()) {
            double openPrice = history.get(0);
            double change = price - openPrice;
            double changePct = (change / openPrice) * 100;
            if (change >= 0) {
                changeLabel.setText(String.format("▲ %.2f%%", changePct));
                changeLabel.setTextFill(Color.web("#0ecb81"));
            } else {
                changeLabel.setText(String.format("▼ %.2f%%", Math.abs(changePct)));
                changeLabel.setTextFill(Color.web("#f6465d"));
            }
        }
    }

    private void updateChart() {
        if (selectedStock == null) return;
        List<Double> history = priceSimulator.getPriceHistory(selectedStock.getSymbol());
        chartCanvas.updateData(history, selectedStock.getSymbol());
    }

    private void updateTradeCard() {
        if (selectedStock == null) return;
        buySellPriceLabel.setText(String.format("$%,.2f USD", selectedStock.getCurrentPrice()));

        Stock owned = findOwnedStock(selectedStock.getSymbol());
        int ownedQty = owned != null ? owned.getQuantity() : 0;
        exchangeInfoLabel.setText(String.format("You own %d shares  •  Sector: %s  •  Risk: %.0f%%",
                ownedQty, selectedStock.getSector(), selectedStock.calculateRisk()));

        cashLabel.setText(String.format("Available Cash: $%,.2f", player.getCash()));

        actionButton.setText((isBuyMode ? "Buy " : "Sell ") + selectedStock.getSymbol());
        updateTotalCost();
    }

    private void updateTotalCost() {
        if (selectedStock == null) {
            totalCostLabel.setText("$0.00");
            return;
        }
        int qty = quantitySpinner.getValue();
        double total = selectedStock.getCurrentPrice() * qty;
        totalCostLabel.setText(String.format("$%,.2f", total));

        if (isBuyMode && total > player.getCash()) {
            totalCostLabel.setTextFill(Color.web("#f6465d"));
        } else {
            totalCostLabel.setTextFill(Color.web("#eaecef"));
        }
    }

    public void updateStats() {
        player.calculateTotalProfit();
        player.checkLevelUp();

        statNetWorth.setText(String.format("$%,.2f", player.getNetWorth()));
        statPortfolio.setText(String.format("$%,.2f", player.getPortfolioValue()));
        statStocks.setText(String.valueOf(player.getPortfolio().size()));

        double profit = player.getTotalProfit();
        statProfit.setText(String.format("$%,.2f", profit));
        statProfit.setTextFill(profit >= 0 ? Color.web("#0ecb81") : Color.web("#f6465d"));
    }

    public void refreshNews() {
        recentNewsBox.getChildren().clear();
        List<MarketNewsGenerator.NewsEvent> news = newsGenerator.getRecentNews();
        int count = Math.min(news.size(), 5);
        if (count == 0) {
            Label empty = new Label("No market news yet. Events will appear as the market moves...");
            empty.setTextFill(Color.web("#848e9c"));
            empty.setFont(Font.font("Segoe UI", 13));
            recentNewsBox.getChildren().add(empty);
        } else {
            for (int i = news.size() - 1; i >= Math.max(0, news.size() - count); i--) {
                MarketNewsGenerator.NewsEvent event = news.get(i);
                recentNewsBox.getChildren().add(createNewsItem(event));
            }
        }
    }

    private HBox createNewsItem(MarketNewsGenerator.NewsEvent event) {
        HBox item = new HBox(12);
        item.getStyleClass().add("news-card");

        String sentiment = event.getSentiment();
        if ("POSITIVE".equals(sentiment)) {
            item.getStyleClass().add("news-card-positive");
        } else if ("NEGATIVE".equals(sentiment)) {
            item.getStyleClass().add("news-card-negative");
        } else {
            item.getStyleClass().add("news-card-neutral");
        }

        // Sentiment indicator
        Label dot = new Label(sentiment.equals("POSITIVE") ? "▲" : sentiment.equals("NEGATIVE") ? "▼" : "●");
        dot.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        dot.setTextFill(sentiment.equals("POSITIVE") ? Color.web("#0ecb81") :
                sentiment.equals("NEGATIVE") ? Color.web("#f6465d") : Color.web("#848e9c"));
        dot.setMinWidth(20);

        VBox textBox = new VBox(2);
        Label headline = new Label(event.getHeadline());
        headline.setTextFill(Color.web("#eaecef"));
        headline.setFont(Font.font("Segoe UI", 13));
        headline.setWrapText(true);

        Label impact = new Label(String.format("%s — Impact: %+.2f%% on %s",
                event.getSentiment(), event.getPriceImpact() * 100, event.getAffectedStock().getSymbol()));
        impact.setTextFill(Color.web("#848e9c"));
        impact.setFont(Font.font("Segoe UI", 11));

        textBox.getChildren().addAll(headline, impact);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        item.getChildren().addAll(dot, textBox);
        return item;
    }

    /** Called by the main game loop to refresh all dynamic content */
    public void refresh() {
        if (selectedStock != null) {
            updateStockDisplay();
            updateChart();
            updateTradeCard();
        }
        updateStats();
    }

    /** Select a stock programmatically (e.g., from Trading panel) */
    public void selectStock(String symbol) {
        for (int i = 0; i < stocks.size(); i++) {
            if (stocks.get(i).getSymbol().equals(symbol)) {
                stockSelector.getSelectionModel().select(i);
                return;
            }
        }
    }
}
