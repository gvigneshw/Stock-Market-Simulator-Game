import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

/**
 * Portfolio panel — shows account summary, holdings table, and performance.
 * Dark themed matching the modern crypto exchange design.
 */
public class PortfolioPanelFX extends VBox {
    private Player player;
    private TableView<HoldingRow> holdingsTable;
    private ObservableList<HoldingRow> holdingsData;

    // Stat cards
    private Label netWorthValue, cashValue, portfolioValue, profitValue, profitPctValue;
    private Label levelValue;
    private ProgressBar levelBar;

    public PortfolioPanelFX(Player player) {
        this.player = player;

        setSpacing(0);
        setStyle("-fx-background-color: #0b0e11;");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: #0b0e11; -fx-background: #0b0e11;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));

        // 1. Title
        Label title = new Label("My Portfolio");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#eaecef"));

        // 2. Account summary cards
        HBox summaryRow = createSummaryCards();

        // 3. Level card
        HBox levelRow = createLevelCard();

        // 4. Holdings table
        VBox tableSection = createHoldingsTable();
        VBox.setVgrow(tableSection, Priority.ALWAYS);

        // 5. Quick analysis
        HBox analysisRow = createAnalysisCards();

        content.getChildren().addAll(title, summaryRow, levelRow, tableSection, analysisRow);
        scroll.setContent(content);
        VBox.setVgrow(scroll, Priority.ALWAYS);
        getChildren().add(scroll);

        // Refresh after all UI created
        refreshDisplay();
    }

    // ========================== SUMMARY CARDS ==========================
    private HBox createSummaryCards() {
        HBox row = new HBox(16);

        netWorthValue = new Label("$0.00");
        VBox card1 = createCard("Net Worth", netWorthValue, "#fcd535");

        cashValue = new Label("$0.00");
        VBox card2 = createCard("Available Cash", cashValue, "#0ecb81");

        portfolioValue = new Label("$0.00");
        VBox card3 = createCard("Portfolio Value", portfolioValue, "#3861fb");

        profitValue = new Label("$0.00");
        VBox card4 = createCard("Total P/L", profitValue, "#848e9c");

        profitPctValue = new Label("0.00%");
        VBox card5 = createCard("Return %", profitPctValue, "#848e9c");

        for (VBox c : new VBox[]{card1, card2, card3, card4, card5}) {
            HBox.setHgrow(c, Priority.ALWAYS);
        }

        row.getChildren().addAll(card1, card2, card3, card4, card5);
        return row;
    }

    private VBox createCard(String title, Label valueLabel, String accentColor) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(card.getStyle() + "-fx-border-color: " + accentColor + "44; -fx-border-width: 0 0 0 3;");

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("stat-label");

        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        valueLabel.setTextFill(Color.web("#eaecef"));

        card.getChildren().addAll(titleLbl, valueLabel);
        return card;
    }

    // ========================== LEVEL CARD ==========================
    private HBox createLevelCard() {
        HBox row = new HBox(16);
        row.getStyleClass().add("card-flat");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 20, 12, 20));

        Label levelTitle = new Label("Trader Level");
        levelTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        levelTitle.setTextFill(Color.web("#fcd535"));

        levelValue = new Label("Level 1");
        levelValue.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        levelValue.setTextFill(Color.web("#eaecef"));

        levelBar = new ProgressBar(0);
        levelBar.setPrefWidth(300);
        levelBar.setPrefHeight(8);

        Label nextLevelLabel = new Label("Next: $200,000");
        nextLevelLabel.setTextFill(Color.web("#848e9c"));
        nextLevelLabel.setFont(Font.font("Segoe UI", 12));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(levelTitle, levelValue, levelBar, nextLevelLabel, spacer);
        return row;
    }

    // ========================== HOLDINGS TABLE ==========================
    @SuppressWarnings("unchecked")
    private VBox createHoldingsTable() {
        VBox box = new VBox(8);

        Label tableTitle = new Label("Holdings");
        tableTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        tableTitle.setTextFill(Color.web("#eaecef"));

        holdingsTable = new TableView<>();
        holdingsData = FXCollections.observableArrayList();

        TableColumn<HoldingRow, String> symbolCol = col("Symbol", "symbol", 80);
        TableColumn<HoldingRow, String> companyCol = col("Company", "company", 180);
        TableColumn<HoldingRow, String> typeCol = col("Type", "type", 110);
        TableColumn<HoldingRow, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(70);
        TableColumn<HoldingRow, String> priceCol = col("Price", "currentPrice", 100);
        TableColumn<HoldingRow, String> valueCol = col("Value", "totalValue", 110);
        TableColumn<HoldingRow, String> riskCol = col("Risk", "risk", 70);
        TableColumn<HoldingRow, String> sectorCol = col("Sector", "sector", 120);

        // Color the value column
        valueCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                setTextFill(Color.web("#0ecb81"));
            }
        });

        // Color the risk column
        riskCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                try {
                    double val = Double.parseDouble(item.replace("%", ""));
                    if (val < 30) setTextFill(Color.web("#0ecb81"));
                    else if (val < 60) setTextFill(Color.web("#fcd535"));
                    else setTextFill(Color.web("#f6465d"));
                } catch (NumberFormatException e) {
                    setTextFill(Color.web("#eaecef"));
                }
            }
        });

        holdingsTable.getColumns().addAll(symbolCol, companyCol, typeCol, qtyCol,
                priceCol, valueCol, riskCol, sectorCol);
        holdingsTable.setItems(holdingsData);
        holdingsTable.setPlaceholder(new Label("No holdings yet. Start trading to build your portfolio!"));
        holdingsTable.setPrefHeight(350);

        box.getChildren().addAll(tableTitle, holdingsTable);
        VBox.setVgrow(holdingsTable, Priority.ALWAYS);

        updateHoldingsTable();
        return box;
    }

    private TableColumn<HoldingRow, String> col(String title, String prop, double width) {
        TableColumn<HoldingRow, String> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    // ========================== ANALYSIS CARDS ==========================
    private HBox createAnalysisCards() {
        HBox row = new HBox(16);
        row.getStyleClass().add("card-flat");
        row.setPadding(new Insets(16));

        // Weighted avg risk
        StockAnalyzer<Stock> analyzer = new StockAnalyzer<>(player.getPortfolio().getHoldings());

        VBox riskCard = new VBox(4);
        riskCard.setAlignment(Pos.CENTER);
        Label riskTitle = new Label("Weighted Avg Risk");
        riskTitle.setTextFill(Color.web("#848e9c"));
        riskTitle.setFont(Font.font("Segoe UI", 12));
        Label riskVal = new Label(String.format("%.1f%%", analyzer.calculateWeightedAverageRisk()));
        riskVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        double risk = analyzer.calculateWeightedAverageRisk();
        riskVal.setTextFill(risk < 30 ? Color.web("#0ecb81") : risk < 60 ? Color.web("#fcd535") : Color.web("#f6465d"));
        riskCard.getChildren().addAll(riskTitle, riskVal);

        VBox countCard = new VBox(4);
        countCard.setAlignment(Pos.CENTER);
        Label countTitle = new Label("Unique Stocks");
        countTitle.setTextFill(Color.web("#848e9c"));
        countTitle.setFont(Font.font("Segoe UI", 12));
        Label countVal = new Label(String.valueOf(player.getPortfolio().size()));
        countVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        countVal.setTextFill(Color.web("#eaecef"));
        countCard.getChildren().addAll(countTitle, countVal);

        VBox avgPriceCard = new VBox(4);
        avgPriceCard.setAlignment(Pos.CENTER);
        Label avgTitle = new Label("Avg Stock Price");
        avgTitle.setTextFill(Color.web("#848e9c"));
        avgTitle.setFont(Font.font("Segoe UI", 12));
        Label avgVal = new Label(String.format("$%,.2f", analyzer.calculateAveragePrice()));
        avgVal.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        avgVal.setTextFill(Color.web("#eaecef"));
        avgPriceCard.getChildren().addAll(avgTitle, avgVal);

        for (VBox c : new VBox[]{riskCard, countCard, avgPriceCard}) {
            HBox.setHgrow(c, Priority.ALWAYS);
        }

        row.getChildren().addAll(riskCard, countCard, avgPriceCard);
        return row;
    }

    // ========================== REFRESH ==========================
    public void refreshDisplay() {
        player.calculateTotalProfit();
        player.checkLevelUp();

        netWorthValue.setText(String.format("$%,.2f", player.getNetWorth()));
        cashValue.setText(String.format("$%,.2f", player.getCash()));
        portfolioValue.setText(String.format("$%,.2f", player.getPortfolioValue()));

        double profit = player.getTotalProfit();
        profitValue.setText(String.format("$%,.2f", profit));
        profitValue.setTextFill(profit >= 0 ? Color.web("#0ecb81") : Color.web("#f6465d"));

        double pct = player.getProfitPercentage();
        profitPctValue.setText(String.format("%+.2f%%", pct));
        profitPctValue.setTextFill(pct >= 0 ? Color.web("#0ecb81") : Color.web("#f6465d"));

        // Level
        levelValue.setText("Level " + player.getLevel());
        int nextThreshold = player.getLevel() * 100000;
        int prevThreshold = (player.getLevel() - 1) * 100000;
        double progress = (player.getNetWorth() - prevThreshold) / (double)(nextThreshold - prevThreshold);
        levelBar.setProgress(Math.max(0, Math.min(1.0, progress)));

        updateHoldingsTable();
    }

    private void updateHoldingsTable() {
        holdingsData.clear();
        List<Stock> holdings = player.getPortfolio().getHoldings();
        for (Stock stock : holdings) {
            holdingsData.add(new HoldingRow(stock));
        }
    }

    // ========================== ROW CLASS ==========================
    public static class HoldingRow {
        private final String symbol;
        private final String company;
        private final String type;
        private final int quantity;
        private final String currentPrice;
        private final String totalValue;
        private final String risk;
        private final String sector;

        public HoldingRow(Stock stock) {
            this.symbol = stock.getSymbol();
            this.company = stock.getCompanyName();
            this.type = stock.getStockType();
            this.quantity = stock.getQuantity();
            this.currentPrice = String.format("$%,.2f", stock.getCurrentPrice());
            this.totalValue = String.format("$%,.2f", stock.getTotalValue());
            this.risk = String.format("%.0f%%", stock.calculateRisk());
            this.sector = stock.getSector();
        }

        public String getSymbol() { return symbol; }
        public String getCompany() { return company; }
        public String getType() { return type; }
        public int getQuantity() { return quantity; }
        public String getCurrentPrice() { return currentPrice; }
        public String getTotalValue() { return totalValue; }
        public String getRisk() { return risk; }
        public String getSector() { return sector; }
    }
}
