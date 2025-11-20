import java.util.*;
import java.util.stream.*;

/**
 * LAB2: Comprehensive Stream API Demonstration
 * Showcases ALL required Stream API operations on Stock Market Domain
 * 
 * Stream Operations Demonstrated:
 * - Intermediate: filter(), map(), sorted(), distinct(), limit(), skip()
 * - Terminal: forEach(), count(), collect(), reduce(), max(), anyMatch(), allMatch()
 */
public class StreamAPIDemo {
    
    private static List<Stock> stocks;
    
    public static void main(String[] args) {
        initializeStocks();
        demonstrateAllStreamOperations();
    }
    
    /**
     * Initialize sample stock data
     */
    private static void initializeStocks() {
        stocks = new ArrayList<>();
        stocks.add(new CommonStock("AAPL", "Apple Inc.", 175.50, 100, "Technology", 0.52, 25.5));
        stocks.add(new CommonStock("GOOGL", "Alphabet Inc.", 142.30, 50, "Technology", 0.0, 28.3));
        stocks.add(new CommonStock("MSFT", "Microsoft Corp.", 380.75, 75, "Technology", 0.68, 22.1));
        stocks.add(new CommonStock("TSLA", "Tesla Inc.", 245.60, 30, "Automotive", 0.0, 65.8));
        stocks.add(new CommonStock("JPM", "JPMorgan Chase", 155.20, 60, "Finance", 2.92, 18.7));
        stocks.add(new PreferredStock("BAC-PL", "Bank of America", 25.50, 200, "Finance", 5.5, 25.0));
        stocks.add(new CommonStock("JNJ", "Johnson & Johnson", 162.30, 80, "Healthcare", 2.98, 12.4));
        stocks.add(new CommonStock("PFE", "Pfizer Inc.", 28.75, 150, "Healthcare", 3.88, 15.2));
        stocks.add(new Bond("US10Y", "US Treasury 10Y", 95.50, 10, "Government", 4.25, 10, "AAA"));
        stocks.add(new CommonStock("XOM", "Exxon Mobil", 110.40, 90, "Energy", 3.12, 20.5));
    }
    
    /**
     * Main demonstration method
     */
    public static void demonstrateAllStreamOperations() {
        System.out.println("=".repeat(80));
        System.out.println("LAB2: COMPREHENSIVE STREAM API DEMONSTRATION");
        System.out.println("Stock Market Domain");
        System.out.println("=".repeat(80));
        
        demonstrateFilter();
        demonstrateMap();
        demonstrateSorted();
        demonstrateDistinct();
        demonstrateLimit();
        demonstrateSkip();
        demonstrateForEach();
        demonstrateCount();
        demonstrateCollect();
        demonstrateReduce();
        demonstrateMax();
        demonstrateAnyMatch();
        demonstrateAllMatch();
        demonstrateComplexPipelines();
    }
    
    /**
     * LAB2: filter() - Filter stocks by criteria
     */
    private static void demonstrateFilter() {
        System.out.println("\n[1] FILTER OPERATION");
        System.out.println("-".repeat(80));
        
        // Filter high-value stocks (> $10,000)
        List<Stock> highValueStocks = stocks.stream()
                .filter(stock -> stock.getTotalValue() > 10000)
                .collect(Collectors.toList());
        
        System.out.println("Stocks with Total Value > $10,000:");
        highValueStocks.forEach(stock -> 
                System.out.printf("  %s: $%.2f (Qty: %d)\n", 
                        stock.getSymbol(), stock.getTotalValue(), stock.getQuantity()));
        
        // Filter by sector
        List<Stock> techStocks = stocks.stream()
                .filter(stock -> stock.getSector().equals("Technology"))
                .collect(Collectors.toList());
        
        System.out.println("\nTechnology Sector Stocks:");
        techStocks.forEach(stock -> 
                System.out.printf("  %s: $%.2f\n", stock.getSymbol(), stock.getCurrentPrice()));
    }
    
    /**
     * LAB2: map() - Transform data
     */
    private static void demonstrateMap() {
        System.out.println("\n[2] MAP OPERATION");
        System.out.println("-".repeat(80));
        
        // Map to stock symbols
        List<String> symbols = stocks.stream()
                .map(Stock::getSymbol)
                .collect(Collectors.toList());
        
        System.out.println("All Stock Symbols:");
        System.out.println("  " + symbols);
        
        // Map to total values
        List<Double> values = stocks.stream()
                .map(Stock::getTotalValue)
                .collect(Collectors.toList());
        
        System.out.println("\nAll Stock Total Values:");
        System.out.println("  " + values.stream()
                .map(v -> String.format("$%.2f", v))
                .collect(Collectors.joining(", ")));
        
        // Map to formatted strings
        List<String> stockInfo = stocks.stream()
                .map(stock -> stock.getSymbol() + " (" + stock.getSector() + ")")
                .collect(Collectors.toList());
        
        System.out.println("\nStock Symbol with Sector:");
        stockInfo.forEach(info -> System.out.println("  " + info));
    }
    
    /**
     * LAB2: sorted() - Sort stocks
     */
    private static void demonstrateSorted() {
        System.out.println("\n[3] SORTED OPERATION");
        System.out.println("-".repeat(80));
        
        // Sort by price (ascending)
        System.out.println("Stocks Sorted by Price (Ascending):");
        stocks.stream()
                .sorted(Comparator.comparingDouble(Stock::getCurrentPrice))
                .forEach(stock -> System.out.printf("  %s: $%.2f\n", 
                        stock.getSymbol(), stock.getCurrentPrice()));
        
        // Sort by total value (descending)
        System.out.println("\nStocks Sorted by Total Value (Descending):");
        stocks.stream()
                .sorted(Comparator.comparingDouble(Stock::getTotalValue).reversed())
                .forEach(stock -> System.out.printf("  %s: $%.2f\n", 
                        stock.getSymbol(), stock.getTotalValue()));
    }
    
    /**
     * LAB2: distinct() - Remove duplicates
     */
    private static void demonstrateDistinct() {
        System.out.println("\n[4] DISTINCT OPERATION");
        System.out.println("-".repeat(80));
        
        // Get unique sectors
        List<String> uniqueSectors = stocks.stream()
                .map(Stock::getSector)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        System.out.println("Unique Sectors in Portfolio:");
        uniqueSectors.forEach(sector -> System.out.println("  • " + sector));
        
        // Count stocks in each sector
        System.out.println("\nStocks per Sector:");
        uniqueSectors.forEach(sector -> {
            long count = stocks.stream()
                    .filter(stock -> stock.getSector().equals(sector))
                    .count();
            System.out.printf("  %s: %d stocks\n", sector, count);
        });
    }
    
    /**
     * LAB2: limit() - Limit results
     */
    private static void demonstrateLimit() {
        System.out.println("\n[5] LIMIT OPERATION");
        System.out.println("-".repeat(80));
        
        // Get top 3 most expensive stocks
        System.out.println("Top 3 Most Expensive Stocks:");
        stocks.stream()
                .sorted(Comparator.comparingDouble(Stock::getCurrentPrice).reversed())
                .limit(3)
                .forEach(stock -> System.out.printf("  %s: $%.2f\n", 
                        stock.getSymbol(), stock.getCurrentPrice()));
        
        // Get first 5 stocks
        System.out.println("\nFirst 5 Stocks:");
        stocks.stream()
                .limit(5)
                .forEach(stock -> System.out.printf("  %s\n", stock.getSymbol()));
    }
    
    /**
     * LAB2: skip() - Skip elements
     */
    private static void demonstrateSkip() {
        System.out.println("\n[6] SKIP OPERATION");
        System.out.println("-".repeat(80));
        
        // Skip first 5 stocks
        System.out.println("Stocks after skipping first 5:");
        stocks.stream()
                .skip(5)
                .forEach(stock -> System.out.printf("  %s: $%.2f\n", 
                        stock.getSymbol(), stock.getCurrentPrice()));
        
        // Skip first 3 and take next 3
        System.out.println("\nStocks 4-6 (skip 3, limit 3):");
        stocks.stream()
                .skip(3)
                .limit(3)
                .forEach(stock -> System.out.printf("  %s\n", stock.getSymbol()));
    }
    
    /**
     * LAB2: forEach() - Terminal operation for iteration
     */
    private static void demonstrateForEach() {
        System.out.println("\n[7] FOREACH TERMINAL OPERATION");
        System.out.println("-".repeat(80));
        
        System.out.println("All Stocks with Details:");
        stocks.stream()
                .forEach(stock -> System.out.printf("  %-8s | %-20s | $%-7.2f | Qty: %-3d | %s\n",
                        stock.getSymbol(), 
                        stock.getCompanyName(),
                        stock.getCurrentPrice(),
                        stock.getQuantity(),
                        stock.getSector()));
    }
    
    /**
     * LAB2: count() - Terminal operation to count elements
     */
    private static void demonstrateCount() {
        System.out.println("\n[8] COUNT TERMINAL OPERATION");
        System.out.println("-".repeat(80));
        
        // Total stock count
        long totalStocks = stocks.stream().count();
        System.out.println("Total Stocks: " + totalStocks);
        
        // Count high-value stocks
        long highValueCount = stocks.stream()
                .filter(stock -> stock.getTotalValue() > 10000)
                .count();
        System.out.println("High Value Stocks (>$10,000): " + highValueCount);
        
        // Count by sector
        long techCount = stocks.stream()
                .filter(stock -> stock.getSector().equals("Technology"))
                .count();
        System.out.println("Technology Stocks: " + techCount);
        
        // Count high-risk stocks
        long highRiskCount = stocks.stream()
                .filter(stock -> stock.calculateRisk() > 50.0)
                .count();
        System.out.println("High Risk Stocks (>50%): " + highRiskCount);
    }
    
    /**
     * LAB2: collect() - Terminal operation to collect results
     */
    private static void demonstrateCollect() {
        System.out.println("\n[9] COLLECT TERMINAL OPERATION");
        System.out.println("-".repeat(80));
        
        // Collect to List
        List<String> techSymbols = stocks.stream()
                .filter(stock -> stock.getSector().equals("Technology"))
                .map(Stock::getSymbol)
                .collect(Collectors.toList());
        System.out.println("Technology Stock Symbols (List): " + techSymbols);
        
        // Collect to Set
        Set<String> sectors = stocks.stream()
                .map(Stock::getSector)
                .collect(Collectors.toSet());
        System.out.println("Unique Sectors (Set): " + sectors);
        
        // Collect with joining
        String allSymbols = stocks.stream()
                .map(Stock::getSymbol)
                .collect(Collectors.joining(", "));
        System.out.println("All Symbols (Joined): " + allSymbols);
        
        // Collect to Map
        Map<String, Double> symbolToPriceMap = stocks.stream()
                .collect(Collectors.toMap(
                        Stock::getSymbol,
                        Stock::getCurrentPrice,
                        (existing, replacement) -> existing));
        System.out.println("\nSymbol to Price Map:");
        symbolToPriceMap.forEach((symbol, price) -> 
                System.out.printf("  %s: $%.2f\n", symbol, price));
    }
    
    /**
     * LAB2: reduce() - Terminal operation to reduce to single value
     */
    private static void demonstrateReduce() {
        System.out.println("\n[10] REDUCE TERMINAL OPERATION");
        System.out.println("-".repeat(80));
        
        // Sum of all stock values
        double totalPortfolioValue = stocks.stream()
                .map(Stock::getTotalValue)
                .reduce(0.0, Double::sum);
        System.out.printf("Total Portfolio Value (reduce): $%.2f\n", totalPortfolioValue);
        
        // Sum of all quantities
        int totalQuantity = stocks.stream()
                .map(Stock::getQuantity)
                .reduce(0, Integer::sum);
        System.out.println("Total Quantity (reduce): " + totalQuantity);
        
        // Find maximum price using reduce
        Optional<Double> maxPrice = stocks.stream()
                .map(Stock::getCurrentPrice)
                .reduce(Double::max);
        System.out.printf("Maximum Stock Price (reduce): $%.2f\n", maxPrice.orElse(0.0));
        
        // Custom reduce - concatenate symbols
        String concatenatedSymbols = stocks.stream()
                .limit(5)
                .map(Stock::getSymbol)
                .reduce("", (s1, s2) -> s1.isEmpty() ? s2 : s1 + "-" + s2);
        System.out.println("Concatenated Symbols: " + concatenatedSymbols);
    }
    
    /**
     * LAB2: max() - Terminal operation to find maximum
     */
    private static void demonstrateMax() {
        System.out.println("\n[11] MAX TERMINAL OPERATION");
        System.out.println("-".repeat(80));
        
        // Stock with highest price
        Optional<Stock> maxPriceStock = stocks.stream()
                .max(Comparator.comparingDouble(Stock::getCurrentPrice));
        maxPriceStock.ifPresent(stock -> 
                System.out.printf("Highest Priced Stock: %s at $%.2f\n", 
                        stock.getSymbol(), stock.getCurrentPrice()));
        
        // Stock with highest total value
        Optional<Stock> maxValueStock = stocks.stream()
                .max(Comparator.comparingDouble(Stock::getTotalValue));
        maxValueStock.ifPresent(stock -> 
                System.out.printf("Highest Value Stock: %s with $%.2f\n", 
                        stock.getSymbol(), stock.getTotalValue()));
        
        // Stock with highest quantity
        Optional<Stock> maxQuantityStock = stocks.stream()
                .max(Comparator.comparingInt(Stock::getQuantity));
        maxQuantityStock.ifPresent(stock -> 
                System.out.printf("Highest Quantity Stock: %s with %d shares\n", 
                        stock.getSymbol(), stock.getQuantity()));
    }
    
    /**
     * LAB2: anyMatch() - Terminal operation to check if any element matches
     */
    private static void demonstrateAnyMatch() {
        System.out.println("\n[12] ANYMATCH TERMINAL OPERATION");
        System.out.println("-".repeat(80));
        
        // Check if any stock is high-risk
        boolean hasHighRisk = stocks.stream()
                .anyMatch(stock -> stock.calculateRisk() > 50.0);
        System.out.println("Has any high-risk stock (>50%): " + hasHighRisk);
        
        // Check if any stock price > $300
        boolean hasExpensive = stocks.stream()
                .anyMatch(stock -> stock.getCurrentPrice() > 300.0);
        System.out.println("Has any stock priced > $300: " + hasExpensive);
        
        // Check if any stock is from Healthcare sector
        boolean hasHealthcare = stocks.stream()
                .anyMatch(stock -> stock.getSector().equals("Healthcare"));
        System.out.println("Has any Healthcare stocks: " + hasHealthcare);
        
        // Check if any stock value > $20,000
        boolean hasHighValue = stocks.stream()
                .anyMatch(stock -> stock.getTotalValue() > 20000);
        System.out.println("Has any stock with value > $20,000: " + hasHighValue);
    }
    
    /**
     * LAB2: allMatch() - Terminal operation to check if all elements match
     */
    private static void demonstrateAllMatch() {
        System.out.println("\n[13] ALLMATCH TERMINAL OPERATION");
        System.out.println("-".repeat(80));
        
        // Check if all stocks are low-risk
        boolean allLowRisk = stocks.stream()
                .allMatch(stock -> stock.calculateRisk() <= 50.0);
        System.out.println("All stocks are low-risk (<=50%): " + allLowRisk);
        
        // Check if all stocks have positive price
        boolean allPositivePrice = stocks.stream()
                .allMatch(stock -> stock.getCurrentPrice() > 0);
        System.out.println("All stocks have positive price: " + allPositivePrice);
        
        // Check if all stocks have quantity > 10
        boolean allGoodQuantity = stocks.stream()
                .allMatch(stock -> stock.getQuantity() > 5);
        System.out.println("All stocks have quantity > 5: " + allGoodQuantity);
        
        // Check if all stocks are worth less than $100,000
        boolean allBelowLimit = stocks.stream()
                .allMatch(stock -> stock.getTotalValue() < 100000);
        System.out.println("All stocks worth < $100,000: " + allBelowLimit);
    }
    
    /**
     * LAB2: Complex Stream Pipelines - Multiple operations combined
     */
    private static void demonstrateComplexPipelines() {
        System.out.println("\n[14] COMPLEX STREAM PIPELINES");
        System.out.println("-".repeat(80));
        
        // Pipeline 1: Filter -> Sort -> Limit -> Map -> Collect
        System.out.println("Top 3 Technology stocks by value:");
        List<String> top3Tech = stocks.stream()
                .filter(stock -> stock.getSector().equals("Technology"))
                .sorted(Comparator.comparingDouble(Stock::getTotalValue).reversed())
                .limit(3)
                .map(stock -> stock.getSymbol() + " ($" + 
                        String.format("%.2f", stock.getTotalValue()) + ")")
                .collect(Collectors.toList());
        top3Tech.forEach(s -> System.out.println("  " + s));
        
        // Pipeline 2: Filter -> Map -> Distinct -> Sorted -> Collect
        System.out.println("\nSectors with high-value stocks (sorted):");
        List<String> highValueSectors = stocks.stream()
                .filter(stock -> stock.getTotalValue() > 8000)
                .map(Stock::getSector)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        highValueSectors.forEach(s -> System.out.println("  " + s));
        
        // Pipeline 3: Skip -> Limit -> Filter -> Count
        long middleStocksHighValue = stocks.stream()
                .skip(2)
                .limit(6)
                .filter(stock -> stock.getTotalValue() > 5000)
                .count();
        System.out.printf("\nStocks 3-8 with value > $5000: %d\n", middleStocksHighValue);
        
        // Pipeline 4: Filter -> Map -> Reduce
        String topSectorsSymbols = stocks.stream()
                .filter(stock -> stock.getTotalValue() > 10000)
                .map(Stock::getSymbol)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);
        System.out.println("\nHigh-value stock symbols: " + topSectorsSymbols);
        
        // Pipeline 5: Group and aggregate
        System.out.println("\nAverage price per sector:");
        Map<String, Double> avgPriceBySector = stocks.stream()
                .collect(Collectors.groupingBy(
                        Stock::getSector,
                        Collectors.averagingDouble(Stock::getCurrentPrice)
                ));
        avgPriceBySector.forEach((sector, avg) -> 
                System.out.printf("  %s: $%.2f\n", sector, avg));
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("LAB2 DEMONSTRATION COMPLETE");
        System.out.println("=".repeat(80));
    }
}
