// Common Stock - represents regular equity shares
public class CommonStock extends Stock {
    private double dividendYield;
    private double volatilityIndex;

    public CommonStock(String symbol, String companyName, double currentPrice, 
                      int quantity, String sector, double dividendYield, double volatilityIndex) {
        super(symbol, companyName, currentPrice, quantity, sector);
        this.dividendYield = dividendYield;
        this.volatilityIndex = volatilityIndex;
    }

    public double getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(double dividendYield) {
        this.dividendYield = dividendYield;
    }

    public double getVolatilityIndex() {
        return volatilityIndex;
    }

    public void setVolatilityIndex(double volatilityIndex) {
        this.volatilityIndex = volatilityIndex;
    }

    @Override
    public double calculateRisk() {
        // Risk based on volatility index
        return volatilityIndex;
    }

    @Override
    public String getStockType() {
        return "Common Stock";
    }

    public double calculateAnnualDividend() {
        return getCurrentPrice() * getQuantity() * (dividendYield / 100);
    }
}
