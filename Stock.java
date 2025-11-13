// Base class for all stock types
public abstract class Stock {
    private String symbol;
    private String companyName;
    private double currentPrice;
    private int quantity;
    private String sector;

    public Stock(String symbol, String companyName, double currentPrice, int quantity, String sector) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.currentPrice = currentPrice;
        this.quantity = quantity;
        this.sector = sector;
    }

    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    // Calculate total value of holdings
    public double getTotalValue() {
        return currentPrice * quantity;
    }

    // Abstract method to be implemented by subclasses
    public abstract double calculateRisk();

    public abstract String getStockType();

    @Override
    public String toString() {
        return String.format("%s [%s] - %s | Price: $%.2f | Qty: %d | Total: $%.2f | Sector: %s | Risk: %.2f%%",
                getStockType(), symbol, companyName, currentPrice, quantity, getTotalValue(), sector, calculateRisk());
    }
}
