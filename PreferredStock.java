// Preferred Stock - represents preferred equity shares with fixed dividends
public class PreferredStock extends Stock {
    private double fixedDividendRate;
    private double parValue;

    public PreferredStock(String symbol, String companyName, double currentPrice, 
                         int quantity, String sector, double fixedDividendRate, double parValue) {
        super(symbol, companyName, currentPrice, quantity, sector);
        this.fixedDividendRate = fixedDividendRate;
        this.parValue = parValue;
    }

    public double getFixedDividendRate() {
        return fixedDividendRate;
    }

    public void setFixedDividendRate(double fixedDividendRate) {
        this.fixedDividendRate = fixedDividendRate;
    }

    public double getParValue() {
        return parValue;
    }

    public void setParValue(double parValue) {
        this.parValue = parValue;
    }

    @Override
    public double calculateRisk() {
        // Preferred stocks have lower risk (typically 30-50% of common stock risk)
        return 35.0;
    }

    @Override
    public String getStockType() {
        return "Preferred Stock";
    }

    public double calculateFixedAnnualDividend() {
        return parValue * getQuantity() * (fixedDividendRate / 100);
    }
}
