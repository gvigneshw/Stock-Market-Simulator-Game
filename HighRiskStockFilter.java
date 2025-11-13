// Filter for high-risk stocks
public class HighRiskStockFilter implements StockFilter<Stock> {
    private double riskThreshold;

    public HighRiskStockFilter(double riskThreshold) {
        this.riskThreshold = riskThreshold;
    }

    @Override
    public boolean meetsCriteria(Stock stock) {
        return stock.calculateRisk() >= riskThreshold;
    }

    @Override
    public String getFilterDescription() {
        return String.format("High Risk Stocks (Risk >= %.2f%%)", riskThreshold);
    }
}
