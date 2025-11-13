import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Transaction class to record buy/sell activities
public class Transaction {
    private String type; // BUY or SELL
    private String symbol;
    private int quantity;
    private double price;
    private double totalAmount;
    private LocalDateTime timestamp;

    public Transaction(String type, String symbol, int quantity, double price, double totalAmount) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotalAmount() { return totalAmount; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return timestamp.format(formatter);
    }

    @Override
    public String toString() {
        return String.format("%s | %s %d shares of %s @ $%.2f | Total: $%.2f | %s",
            getFormattedTimestamp(), type, quantity, symbol, price, totalAmount, 
            type.equals("BUY") ? "Spent" : "Gained");
    }
}
