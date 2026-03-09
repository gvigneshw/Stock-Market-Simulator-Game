import javafx.application.Platform;
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
import javafx.collections.transformation.FilteredList;
import java.util.List;

/**
 * Trading panel — full stock table with search, filters, and quick trading.
 * Dark themed to match the modern crypto exchange UI.
 */
public class TradingPanelFX extends VBox {
    private TableView<StockRow> stockTable;
    private ObservableList<StockRow> stockData;
    private FilteredList<StockRow> filteredData;
    private List<Stock> availableStocks;
    private Player player;

    private Label cashLabel;
    private TextField searchField;
    private Spinner<Integer> quantitySpinner;
    private Label statusLabel;
    private String activeFilter = "All";

    private Runnable onTradeCallback;
    private Runnable onViewChart; // callback to navigate to dashboard with selected stock

    public TradingPanelFX(List<Stock> stocks, Player player) {
        this.availableStocks = stocks;
        this.player = player;

        setSpacing(0);
        setPadding(new Insets(0));
        setStyle("-fx-background-color: #0b0e11;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        // 1. Top bar: cash + search
        HBox topBar = createTopBar();
        // 2. Filter buttons
        HBox filterBar = createFilterBar();
        // 3. Stock table
        VBox tableSection = createTableSection();
        VBox.setVgrow(tableSection, Priority.ALWAYS);
        // 4. Trade controls
        HBox tradeBar = createTradeBar();

        content.getChildren().addAll(topBar, filterBar, tableSection, tradeBar);
        VBox.setVgrow(content, Priority.ALWAYS);
        getChildren().add(content);
    }

    public void setOnTradeCallback(Runnable callback) { this.onTradeCallback = callback; }
    public void setOnViewChart(Runnable callback) { this.onViewChart = callback; }

    // ========================== TOP BAR ==========================
    private HBox createTopBar() {
        HBox bar = new HBox(16);
        bar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Market");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#eaecef"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        cashLabel = new Label();
        cashLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        cashLabel.setTextFill(Color.web("#fcd535"));
        updateCashLabel();

        searchField = new TextField();
        searchField.setPromptText("Search stocks...");
        searchField.setPrefWidth(240);
        searchField.textProperty().addListener((obs, o, n) -> applyFilter());

        bar.getChildren().addAll(title, spacer, cashLabel, searchField);
        return bar;
    }

    // ========================== FILTER BAR ==========================
    private HBox createFilterBar() {
        HBox bar = new HBox(8);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 0, 4, 0));

        String[] filters = {"All", "Technology", "Banking", "Healthcare", "Automotive",
                            "Telecommunications", "Common Stock", "Preferred Stock", "Bond"};

        for (String filter : filters) {
            Button btn = new Button(filter);
            btn.getStyleClass().add("btn-outline");
            if (filter.equals("All")) btn.getStyleClass().add("btn-outline-active");

            btn.setOnAction(e -> {
                activeFilter = filter;
                // Update button styles
                bar.getChildren().forEach(node -> {
                    if (node instanceof Button) {
                        node.getStyleClass().remove("btn-outline-active");
                    }
                });
                btn.getStyleClass().add("btn-outline-active");
                applyFilter();
            });

            bar.getChildren().add(btn);
        }
        return bar;
    }

    private void applyFilter() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase().trim();
        filteredData.setPredicate(row -> {
            boolean matchSearch = search.isEmpty()
                    || row.getSymbol().toLowerCase().contains(search)
                    || row.getCompany().toLowerCase().contains(search)
                    || row.getSector().toLowerCase().contains(search);

            boolean matchFilter = activeFilter.equals("All")
                    || row.getSector().equalsIgnoreCase(activeFilter)
                    || row.getType().equalsIgnoreCase(activeFilter);

            return matchSearch && matchFilter;
        });
    }

    // ========================== TABLE ==========================
    @SuppressWarnings("unchecked")
    private VBox createTableSection() {
        VBox box = new VBox();
        box.getStyleClass().add("card-flat");
        box.setPadding(new Insets(0));

        stockTable = new TableView<>();
        stockData = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(stockData, p -> true);

        TableColumn<StockRow, String> symbolCol = createCol("Symbol", "symbol", 80);
        TableColumn<StockRow, String> companyCol = createCol("Company", "company", 180);
        TableColumn<StockRow, String> typeCol = createCol("Type", "type", 110);
        TableColumn<StockRow, String> sectorCol = createCol("Sector", "sector", 120);
        TableColumn<StockRow, String> riskCol = createCol("Risk", "risk", 70);
        TableColumn<StockRow, Integer> ownedCol = new TableColumn<>("Owned");
        ownedCol.setCellValueFactory(new PropertyValueFactory<>("owned"));
        ownedCol.setPrefWidth(70);

        // Price column with color
        TableColumn<StockRow, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(110);
        priceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    setTextFill(Color.web("#eaecef"));
                }
            }
        });

        // Change column with color coding
        TableColumn<StockRow, String> changeCol = new TableColumn<>("Change");
        changeCol.setCellValueFactory(new PropertyValueFactory<>("change"));
        changeCol.setPrefWidth(90);
        changeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                    if (item.startsWith("+") || item.startsWith("▲")) {
                        setTextFill(Color.web("#0ecb81"));
                    } else if (item.startsWith("-") || item.startsWith("▼")) {
                        setTextFill(Color.web("#f6465d"));
                    } else {
                        setTextFill(Color.web("#848e9c"));
                    }
                }
            }
        });

        // Owned column with color
        ownedCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(item));
                    if (item > 0) {
                        setTextFill(Color.web("#fcd535"));
                        setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                    } else {
                        setTextFill(Color.web("#848e9c"));
                    }
                }
            }
        });

        stockTable.getColumns().addAll(symbolCol, companyCol, typeCol, priceCol,
                changeCol, sectorCol, riskCol, ownedCol);
        stockTable.setItems(filteredData);
        stockTable.setPlaceholder(new Label("No stocks match your filter"));
        VBox.setVgrow(stockTable, Priority.ALWAYS);
        stockTable.setPrefHeight(500);

        box.getChildren().add(stockTable);
        updateStockTable();
        return box;
    }

    private TableColumn<StockRow, String> createCol(String title, String property, double width) {
        TableColumn<StockRow, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }

    // ========================== TRADE BAR ==========================
    private HBox createTradeBar() {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(16));
        bar.getStyleClass().add("card-flat");

        Label selectedInfo = new Label("Select a stock from the table above");
        selectedInfo.setTextFill(Color.web("#848e9c"));
        selectedInfo.setFont(Font.font("Segoe UI", 13));

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Label qtyLabel = new Label("Qty:");
        qtyLabel.setTextFill(Color.web("#848e9c"));

        quantitySpinner = new Spinner<>(1, 100000, 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(100);

        Button buyBtn = new Button("Buy");
        buyBtn.getStyleClass().add("btn-buy");
        buyBtn.setMinWidth(80);
        buyBtn.setPrefWidth(90);
        buyBtn.setMaxWidth(Double.MAX_VALUE);
        buyBtn.setOnAction(e -> handleTrade(true));

        Button sellBtn = new Button("Sell");
        sellBtn.getStyleClass().add("btn-sell");
        sellBtn.setMinWidth(80);
        sellBtn.setPrefWidth(90);
        sellBtn.setMaxWidth(Double.MAX_VALUE);
        sellBtn.setOnAction(e -> handleTrade(false));

        Button chartBtn = new Button("View Chart");
        chartBtn.getStyleClass().add("btn-outline");
        chartBtn.setOnAction(e -> {
            StockRow row = stockTable.getSelectionModel().getSelectedItem();
            if (row != null && onViewChart != null) {
                onViewChart.run();
            }
        });

        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        // Update info when selection changes
        stockTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedInfo.setText(String.format("%s — %s — %s",
                        newVal.getSymbol(), newVal.getCompany(), newVal.getPrice()));
                selectedInfo.setTextFill(Color.web("#eaecef"));
            }
        });

        bar.getChildren().addAll(selectedInfo, spacer1, qtyLabel, quantitySpinner,
                buyBtn, sellBtn, chartBtn, statusLabel);
        return bar;
    }

    // ========================== TRADE LOGIC ==========================
    private void handleTrade(boolean isBuy) {
        StockRow row = stockTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            showTradeStatus("Select a stock first", false);
            return;
        }

        Stock stock = availableStocks.stream()
                .filter(s -> s.getSymbol().equals(row.getSymbol()))
                .findFirst().orElse(null);
        if (stock == null) return;

        int quantity = quantitySpinner.getValue();

        if (isBuy) {
            double cost = stock.getCurrentPrice() * quantity;
            if (cost > player.getCash()) {
                showTradeStatus(String.format("Insufficient funds ($%,.2f needed)", cost), false);
                return;
            }
            if (player.buyStock(stock, quantity)) {
                showTradeStatus(String.format("Bought %d %s @ $%,.2f", quantity, stock.getSymbol(), stock.getCurrentPrice()), true);
                if (onTradeCallback != null) onTradeCallback.run();
            }
        } else {
            Stock owned = null;
            for (Stock s : player.getPortfolio().getHoldings()) {
                if (s.getSymbol().equals(stock.getSymbol())) { owned = s; break; }
            }
            if (owned == null || owned.getQuantity() < quantity) {
                int have = owned != null ? owned.getQuantity() : 0;
                showTradeStatus(String.format("Only own %d shares", have), false);
                return;
            }
            if (player.sellStock(stock, quantity)) {
                showTradeStatus(String.format("Sold %d %s @ $%,.2f", quantity, stock.getSymbol(), stock.getCurrentPrice()), true);
                if (onTradeCallback != null) onTradeCallback.run();
            }
        }
        updateStockTable();
        updateCashLabel();
    }

    private void showTradeStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setTextFill(success ? Color.web("#0ecb81") : Color.web("#f6465d"));
        new Thread(() -> {
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            Platform.runLater(() -> {
                if (statusLabel.getText().equals(msg)) statusLabel.setText("");
            });
        }).start();
    }

    // ========================== UPDATES ==========================
    public void updateStockTable() {
        stockData.clear();
        for (Stock stock : availableStocks) {
            int ownedQty = 0;
            for (Stock s : player.getPortfolio().getHoldings()) {
                if (s.getSymbol().equals(stock.getSymbol())) {
                    ownedQty = s.getQuantity();
                    break;
                }
            }
            stockData.add(new StockRow(stock, ownedQty));
        }
    }

    public void updateCashLabel() {
        cashLabel.setText(String.format("Cash: $%,.2f", player.getCash()));
    }

    public String getSelectedSymbol() {
        StockRow row = stockTable.getSelectionModel().getSelectedItem();
        return row != null ? row.getSymbol() : null;
    }

    // ========================== TABLE ROW CLASS ==========================
    public static class StockRow {
        private final String symbol;
        private final String company;
        private final String type;
        private final String price;
        private final String change;
        private final String sector;
        private final String risk;
        private final int owned;
        private final double riskValue;

        public StockRow(Stock stock, int ownedQty) {
            this.symbol = stock.getSymbol();
            this.company = stock.getCompanyName();
            this.type = stock.getStockType();
            this.price = String.format("$%,.2f", stock.getCurrentPrice());
            this.sector = stock.getSector();
            this.risk = String.format("%.0f%%", stock.calculateRisk());
            this.owned = ownedQty;
            this.riskValue = stock.calculateRisk();

            // Simulate a change for display (based on small random fluctuation shown in table)
            double pct = (Math.random() - 0.48) * stock.calculateRisk() * 0.02;
            if (pct >= 0) {
                this.change = String.format("▲ +%.2f%%", pct);
            } else {
                this.change = String.format("▼ %.2f%%", pct);
            }
        }

        public String getSymbol() { return symbol; }
        public String getCompany() { return company; }
        public String getType() { return type; }
        public String getPrice() { return price; }
        public String getChange() { return change; }
        public String getSector() { return sector; }
        public String getRisk() { return risk; }
        public int getOwned() { return owned; }
        public double getRiskValue() { return riskValue; }
    }
}
