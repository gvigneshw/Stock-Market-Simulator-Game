// Filter for low-risk stocks
public class LowRiskStockFilter implements StockFilter<Stock> {
    private double riskThreshold;

    public LowRiskStockFilter(double riskThreshold) {
        this.riskThreshold = riskThreshold;
    }

    @Override
    public boolean meetsCriteria(Stock stock) {
        return stock.calculateRisk() <= riskThreshold;
    }

    @Override
    public String getFilterDescription() {
        return String.format("Low Risk Stocks (Risk <= %.2f%%)", riskThreshold);
    }
}
