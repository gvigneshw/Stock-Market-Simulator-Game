import java.util.List;
import java.util.ArrayList;

// Generic class with bounded type parameter for performing operations on Stock objects
public class StockAnalyzer<T extends Stock> {
    private List<T> stocks;

    public StockAnalyzer() {
        this.stocks = new ArrayList<>();
    }

    public StockAnalyzer(List<T> stocks) {
        this.stocks = new ArrayList<>(stocks);
    }

    // Add stock for analysis
    public void addStock(T stock) {
        stocks.add(stock);
    }

    // Calculate total portfolio value
    public double calculateTotalValue() {
        double total = 0.0;
        for (T stock : stocks) {
            total += stock.getTotalValue();
        }
        return total;
    }

    // Calculate average price
    public double calculateAveragePrice() {
        if (stocks.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (T stock : stocks) {
            sum += stock.getCurrentPrice();
        }
        return sum / stocks.size();
    }

    // Calculate weighted average risk
    public double calculateWeightedAverageRisk() {
        if (stocks.isEmpty()) {
            return 0.0;
        }
        double totalValue = calculateTotalValue();
        if (totalValue == 0) {
            return 0.0;
        }
        double weightedRisk = 0.0;
        for (T stock : stocks) {
            double weight = stock.getTotalValue() / totalValue;
            weightedRisk += stock.calculateRisk() * weight;
        }
        return weightedRisk;
    }

    // Find stock with highest value
    public T findHighestValueStock() {
        if (stocks.isEmpty()) {
            return null;
        }
        T highestValueStock = stocks.get(0);
        for (T stock : stocks) {
            if (stock.getTotalValue() > highestValueStock.getTotalValue()) {
                highestValueStock = stock;
            }
        }
        return highestValueStock;
    }

    // Find stock with lowest risk
    public T findLowestRiskStock() {
        if (stocks.isEmpty()) {
            return null;
        }
        T lowestRiskStock = stocks.get(0);
        for (T stock : stocks) {
            if (stock.calculateRisk() < lowestRiskStock.calculateRisk()) {
                lowestRiskStock = stock;
            }
        }
        return lowestRiskStock;
    }

    // Compare two stocks
    public String compareStocks(T stock1, T stock2) {
        StringBuilder comparison = new StringBuilder();
        comparison.append("\n=== Stock Comparison ===\n");
        comparison.append("Stock 1: ").append(stock1.getSymbol()).append("\n");
        comparison.append("Stock 2: ").append(stock2.getSymbol()).append("\n\n");
        
        comparison.append("Price Comparison:\n");
        if (stock1.getCurrentPrice() > stock2.getCurrentPrice()) {
            comparison.append("  ").append(stock1.getSymbol()).append(" is more expensive\n");
        } else if (stock1.getCurrentPrice() < stock2.getCurrentPrice()) {
            comparison.append("  ").append(stock2.getSymbol()).append(" is more expensive\n");
        } else {
            comparison.append("  Both have equal price\n");
        }

        comparison.append("\nValue Comparison:\n");
        if (stock1.getTotalValue() > stock2.getTotalValue()) {
            comparison.append("  ").append(stock1.getSymbol()).append(" has higher total value\n");
        } else if (stock1.getTotalValue() < stock2.getTotalValue()) {
            comparison.append("  ").append(stock2.getSymbol()).append(" has higher total value\n");
        } else {
            comparison.append("  Both have equal total value\n");
        }

        comparison.append("\nRisk Comparison:\n");
        if (stock1.calculateRisk() > stock2.calculateRisk()) {
            comparison.append("  ").append(stock1.getSymbol()).append(" is riskier\n");
        } else if (stock1.calculateRisk() < stock2.calculateRisk()) {
            comparison.append("  ").append(stock2.getSymbol()).append(" is riskier\n");
        } else {
            comparison.append("  Both have equal risk\n");
        }

        return comparison.toString();
    }

    // Display analysis report
    public void displayAnalysisReport() {
        System.out.println("\n========================================");
        System.out.println("STOCK ANALYSIS REPORT");
        System.out.println("========================================");
        System.out.println("Number of stocks analyzed: " + stocks.size());
        System.out.printf("Total Portfolio Value: $%.2f\n", calculateTotalValue());
        System.out.printf("Average Stock Price: $%.2f\n", calculateAveragePrice());
        System.out.printf("Weighted Average Risk: %.2f%%\n", calculateWeightedAverageRisk());
        
        T highestValue = findHighestValueStock();
        if (highestValue != null) {
            System.out.printf("\nHighest Value Stock: %s ($%.2f)\n", 
                highestValue.getSymbol(), highestValue.getTotalValue());
        }
        
        T lowestRisk = findLowestRiskStock();
        if (lowestRisk != null) {
            System.out.printf("Lowest Risk Stock: %s (%.2f%% risk)\n", 
                lowestRisk.getSymbol(), lowestRisk.calculateRisk());
        }
        System.out.println("========================================\n");
    }

    public List<T> getStocks() {
        return new ArrayList<>(stocks);
    }
}
