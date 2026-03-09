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
 * Transaction History panel — displays all buy/sell transactions
 * with color-coded type indicators and clean dark theme.
 */
public class TransactionHistoryPanelFX extends VBox {
    private Player player;
    private TableView<TxRow> txTable;
    private ObservableList<TxRow> txData;
    private Label summaryLabel;

    public TransactionHistoryPanelFX(Player player) {
        this.player = player;

        setSpacing(0);
        setStyle("-fx-background-color: #0b0e11;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Transaction History");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#eaecef"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        summaryLabel = new Label("0 transactions");
        summaryLabel.setFont(Font.font("Segoe UI", 13));
        summaryLabel.setTextFill(Color.web("#848e9c"));

        header.getChildren().addAll(title, spacer, summaryLabel);

        // Summary cards
        HBox summaryCards = createSummaryCards();

        // Table
        VBox tableSection = createTransactionTable();
        VBox.setVgrow(tableSection, Priority.ALWAYS);

        content.getChildren().addAll(header, summaryCards, tableSection);
        VBox.setVgrow(content, Priority.ALWAYS);
        getChildren().add(content);
    }

    // ========================== SUMMARY CARDS ==========================
    private HBox createSummaryCards() {
        HBox row = new HBox(16);

        List<Transaction> txList = player.getTransactionHistory();
        long buys = txList.stream().filter(t -> "BUY".equals(t.getType())).count();
        long sells = txList.stream().filter(t -> "SELL".equals(t.getType())).count();
        double totalSpent = txList.stream().filter(t -> "BUY".equals(t.getType()))
                .mapToDouble(Transaction::getTotalAmount).sum();
        double totalEarned = txList.stream().filter(t -> "SELL".equals(t.getType()))
                .mapToDouble(Transaction::getTotalAmount).sum();

        VBox card1 = miniCard("Total Buys", String.valueOf(buys), "#0ecb81");
        VBox card2 = miniCard("Total Sells", String.valueOf(sells), "#f6465d");
        VBox card3 = miniCard("Amount Spent", String.format("$%,.2f", totalSpent), "#fcd535");
        VBox card4 = miniCard("Amount Earned", String.format("$%,.2f", totalEarned), "#0ecb81");

        for (VBox c : new VBox[]{card1, card2, card3, card4}) {
            HBox.setHgrow(c, Priority.ALWAYS);
        }

        row.getChildren().addAll(card1, card2, card3, card4);
        return row;
    }

    private VBox miniCard(String title, String value, String color) {
        VBox card = new VBox(4);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("stat-label");

        Label valueLbl = new Label(value);
        valueLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        valueLbl.setTextFill(Color.web(color));

        card.getChildren().addAll(titleLbl, valueLbl);
        return card;
    }

    // ========================== TRANSACTION TABLE ==========================
    @SuppressWarnings("unchecked")
    private VBox createTransactionTable() {
        VBox box = new VBox();

        txTable = new TableView<>();
        txData = FXCollections.observableArrayList();

        TableColumn<TxRow, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeCol.setPrefWidth(160);

        TableColumn<TxRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(80);
        typeCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }

                Label badge = new Label(item);
                badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
                badge.setPadding(new Insets(3, 10, 3, 10));

                if ("BUY".equals(item)) {
                    badge.setTextFill(Color.web("#0ecb81"));
                    badge.setStyle("-fx-background-color: #0ecb8122; -fx-background-radius: 6;");
                } else {
                    badge.setTextFill(Color.web("#f6465d"));
                    badge.setStyle("-fx-background-color: #f6465d22; -fx-background-radius: 6;");
                }

                setText(null);
                setGraphic(badge);
            }
        });

        TableColumn<TxRow, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        symbolCol.setPrefWidth(90);
        symbolCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                setTextFill(Color.web("#fcd535"));
            }
        });

        TableColumn<TxRow, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(70);

        TableColumn<TxRow, String> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(110);

        TableColumn<TxRow, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalCol.setPrefWidth(130);
        totalCol.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                setText(item);
                setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
                setTextFill(Color.web("#eaecef"));
            }
        });

        txTable.getColumns().addAll(timeCol, typeCol, symbolCol, qtyCol, priceCol, totalCol);
        txTable.setItems(txData);
        txTable.setPlaceholder(new Label("No transactions yet. Start trading to see your history here!"));
        VBox.setVgrow(txTable, Priority.ALWAYS);

        box.getChildren().add(txTable);
        VBox.setVgrow(box, Priority.ALWAYS);

        updateTransactionTable();
        return box;
    }

    // ========================== UPDATE ==========================
    public void updateTransactionTable() {
        txData.clear();
        List<Transaction> history = player.getTransactionHistory();

        // Show newest first
        for (int i = history.size() - 1; i >= 0; i--) {
            Transaction t = history.get(i);
            txData.add(new TxRow(t));
        }

        summaryLabel.setText(history.size() + " transaction" + (history.size() != 1 ? "s" : ""));
    }

    // ========================== ROW CLASS ==========================
    public static class TxRow {
        private final String time;
        private final String type;
        private final String symbol;
        private final int quantity;
        private final String price;
        private final String totalAmount;

        public TxRow(Transaction t) {
            this.time = t.getFormattedTimestamp();
            this.type = t.getType();
            this.symbol = t.getSymbol();
            this.quantity = t.getQuantity();
            this.price = String.format("$%,.2f", t.getPrice());
            this.totalAmount = String.format("$%,.2f", t.getTotalAmount());
        }

        public String getTime() { return time; }
        public String getType() { return type; }
        public String getSymbol() { return symbol; }
        public int getQuantity() { return quantity; }
        public String getPrice() { return price; }
        public String getTotalAmount() { return totalAmount; }
    }
}
