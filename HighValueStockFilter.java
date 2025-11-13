// Filter for high-value stocks
public class HighValueStockFilter implements StockFilter<Stock> {
    private double threshold;

    public HighValueStockFilter(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public boolean meetsCriteria(Stock stock) {
        return stock.getTotalValue() >= threshold;
    }

    @Override
    public String getFilterDescription() {
        return String.format("High Value Stocks (>= $%.2f)", threshold);
    }
}
