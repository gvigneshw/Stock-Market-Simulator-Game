import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * LAB2: Advanced Stock Market Utility with Lambda and Stream API
 * Combines functional interfaces, lambda expressions, and stream operations
 * 
 * Demonstrates:
 * - Custom functional interfaces with lambdas
 * - All lambda variations (0, 1, 2 arguments, block lambdas)
 * - Complete Stream API coverage
 * - Real-world stock market calculations
 */
public class AdvancedStockUtility {
    
    // ============================================================================
    // LAB2: Functional Interfaces (Custom)
    // ============================================================================
    
    /**
     * Functional interface for stock comparison (2 arguments)
     */
    @FunctionalInterface
    interface StockComparator {
        int compare(Stock s1, Stock s2);
    }
    
    /**
     * Functional interface for stock validation (1 argument)
     */
    @FunctionalInterface
    interface StockValidator {
        boolean isValid(Stock stock);
    }
    
    /**
     * Functional interface for market status (no arguments)
     */
    @FunctionalInterface
    interface MarketStatus {
        String getStatus();
    }
    
    /**
     * Functional interface for price calculator (2 arguments)
     */
    @FunctionalInterface
    interface PriceCalculator {
        double calculate(double price, double percentage);
    }
    
    // ============================================================================
    // LAB2: Lambda Demonstrations
    // ============================================================================
    
    /**
     * Demonstrates all types of lambda expressions
     */
    public static void demonstrateLambdaTypes() {
        System.out.println("=== LAMBDA EXPRESSION TYPES ===\n");
        
        // 1. Lambda with NO arguments
        MarketStatus marketOpen = () -> "Market is OPEN";
        MarketStatus marketClosed = () -> {
            String time = java.time.LocalTime.now().toString();
            return "Market is CLOSED at " + time;
        };
        System.out.println("1. Lambda with NO arguments:");
        System.out.println("   " + marketOpen.getStatus());
        System.out.println("   " + marketClosed.getStatus());
        
        // 2. Lambda with ONE argument
        StockValidator highPriceValidator = stock -> stock.getCurrentPrice() > 100;
        StockValidator lowRiskValidator = stock -> {
            double risk = stock.calculateRisk();
            return risk < 30.0;
        };
        System.out.println("\n2. Lambda with ONE argument:");
        Stock testStock = new CommonStock("TEST", "Test Corp", 150.0, 10, "Technology", 1.5, 25.0);
        System.out.println("   High Price: " + highPriceValidator.isValid(testStock));
        System.out.println("   Low Risk: " + lowRiskValidator.isValid(testStock));
        
        // 3. Lambda with TWO arguments
        PriceCalculator applyDiscount = (price, discount) -> price * (1 - discount / 100);
        PriceCalculator applyMarkup = (price, markup) -> {
            double markupAmount = price * markup / 100;
            return price + markupAmount;
        };
        System.out.println("\n3. Lambda with TWO arguments:");
        System.out.printf("   Original: $%.2f, After 10%% discount: $%.2f\n", 
                100.0, applyDiscount.calculate(100.0, 10.0));
        System.out.printf("   Original: $%.2f, After 20%% markup: $%.2f\n", 
                100.0, applyMarkup.calculate(100.0, 20.0));
        
        // 4. Block Lambda Expression
        StockComparator compareByValue = (s1, s2) -> {
            double value1 = s1.getTotalValue();
            double value2 = s2.getTotalValue();
            if (value1 > value2) return 1;
            else if (value1 < value2) return -1;
            else return 0;
        };
        System.out.println("\n4. Block Lambda Expression:");
        Stock stock1 = new CommonStock("A", "Alpha", 100.0, 10, "Tech", 1.0, 20.0);
        Stock stock2 = new CommonStock("B", "Beta", 50.0, 30, "Tech", 1.0, 20.0);
        System.out.printf("   Comparing %s vs %s: %d\n", 
                stock1.getSymbol(), stock2.getSymbol(), compareByValue.compare(stock1, stock2));
    }
    
    /**
     * Perform stock calculations using lambda expressions
     */
    public static void demonstrateArithmeticLambdas() {
        System.out.println("\n=== ARITHMETIC OPERATIONS (Lambda) ===\n");
        
        ArithmeticOperation add = (a, b) -> a + b;
        ArithmeticOperation subtract = (a, b) -> a - b;
        ArithmeticOperation multiply = (a, b) -> a * b;
        ArithmeticOperation divide = (a, b) -> b != 0 ? a / b : 0;
        
        double buyPrice = 150.50;
        double sellPrice = 175.75;
        
        System.out.printf("Buy Price: $%.2f, Sell Price: $%.2f\n", buyPrice, sellPrice);
        System.out.printf("Total Investment (add): $%.2f\n", add.calculate(buyPrice, sellPrice));
        System.out.printf("Profit (subtract): $%.2f\n", subtract.calculate(sellPrice, buyPrice));
        System.out.printf("Portfolio Value (multiply by 10): $%.2f\n", multiply.calculate(buyPrice, 10));
        System.out.printf("Average Price (divide by 2): $%.2f\n", divide.calculate(add.calculate(buyPrice, sellPrice), 2));
    }
    
    /**
     * Perform string operations using lambda expressions
     */
    public static void demonstrateStringLambdas() {
        System.out.println("\n=== STRING OPERATIONS (Lambda) ===\n");
        
        StringOperation reverse = str -> new StringBuilder(str).reverse().toString();
        StringOperation uppercase = str -> str.toUpperCase();
        StringOperation vowelCount = str -> {
            long count = str.toLowerCase().chars()
                    .filter(c -> "aeiou".indexOf(c) >= 0)
                    .count();
            return "Vowels: " + count;
        };
        
        String stockName = "Apple Technology";
        
        System.out.println("Original: " + stockName);
        System.out.println("Reversed: " + reverse.process(stockName));
        System.out.println("Uppercase: " + uppercase.process(stockName));
        System.out.println(vowelCount.process(stockName));
    }
    
    /**
     * Perform check operations using lambda expressions
     */
    public static void demonstrateCheckLambdas() {
        System.out.println("\n=== CHECK OPERATIONS (Lambda) ===\n");
        
        CheckOperation isEven = n -> n % 2 == 0;
        CheckOperation isOdd = n -> n % 2 != 0;
        CheckOperation isPrime = n -> {
            if (n <= 1) return false;
            for (int i = 2; i <= Math.sqrt(n); i++) {
                if (n % i == 0) return false;
            }
            return true;
        };
        
        int[] quantities = {10, 15, 17, 20, 23};
        
        System.out.println("Stock Quantities Analysis:");
        for (int qty : quantities) {
            System.out.printf("Qty %d: Even=%s, Odd=%s, Prime=%s\n", 
                    qty, isEven.test(qty), isOdd.test(qty), isPrime.test(qty));
        }
    }
    
    // ============================================================================
    // LAB2: Stream API Utility Methods
    // ============================================================================
    
    public static List<Stock> filterStocks(List<Stock> stocks, Predicate<Stock> predicate) {
        return stocks.stream().filter(predicate).collect(Collectors.toList());
    }
    
    public static List<Stock> sortStocks(List<Stock> stocks, Comparator<Stock> comparator) {
        return stocks.stream().sorted(comparator).collect(Collectors.toList());
    }
    
    public static List<Stock> getTopN(List<Stock> stocks, int n, Comparator<Stock> comparator) {
        return stocks.stream().sorted(comparator).limit(n).collect(Collectors.toList());
    }
    
    public static List<Stock> skipFirst(List<Stock> stocks, int n) {
        return stocks.stream().skip(n).collect(Collectors.toList());
    }
    
    public static List<String> mapToStrings(List<Stock> stocks, Function<Stock, String> mapper) {
        return stocks.stream().map(mapper).collect(Collectors.toList());
    }
    
    public static List<String> getDistinctSectors(List<Stock> stocks) {
        return stocks.stream().map(Stock::getSector).distinct().sorted().collect(Collectors.toList());
    }
    
    public static long countMatching(List<Stock> stocks, Predicate<Stock> predicate) {
        return stocks.stream().filter(predicate).count();
    }
    
    public static double calculateTotalValue(List<Stock> stocks) {
        return stocks.stream().map(Stock::getTotalValue).reduce(0.0, Double::sum);
    }
    
    public static Optional<Stock> findMaxValueStock(List<Stock> stocks) {
        return stocks.stream().max(Comparator.comparingDouble(Stock::getTotalValue));
    }
    
    public static boolean hasAnyMatching(List<Stock> stocks, Predicate<Stock> predicate) {
        return stocks.stream().anyMatch(predicate);
    }
    
    public static boolean allMatching(List<Stock> stocks, Predicate<Stock> predicate) {
        return stocks.stream().allMatch(predicate);
    }
    
    public static void printAllStocks(List<Stock> stocks) {
        stocks.stream().forEach(stock -> System.out.printf("%s: $%.2f x %d = $%.2f\n",
                stock.getSymbol(), stock.getCurrentPrice(), stock.getQuantity(), stock.getTotalValue()));
    }
    
    public static Map<String, List<Stock>> groupBySector(List<Stock> stocks) {
        return stocks.stream().collect(Collectors.groupingBy(Stock::getSector));
    }
    
    public static DoubleSummaryStatistics getPriceStatistics(List<Stock> stocks) {
        return stocks.stream().mapToDouble(Stock::getCurrentPrice).summaryStatistics();
    }
    
    /**
     * Main method to demonstrate all LAB2 requirements
     */
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("LAB2: ADVANCED STOCK UTILITY - Lambda & Stream API");
        System.out.println("=".repeat(80));
        
        demonstrateLambdaTypes();
        demonstrateArithmeticLambdas();
        demonstrateStringLambdas();
        demonstrateCheckLambdas();
        
        List<Stock> stocks = createSampleStocks();
        
        System.out.println("\n=== STREAM API UTILITY METHODS ===\n");
        
        List<Stock> highValue = filterStocks(stocks, s -> s.getTotalValue() > 10000);
        System.out.println("High Value Stocks (>$10,000): " + highValue.size());
        
        long techCount = countMatching(stocks, s -> s.getSector().equals("Technology"));
        System.out.println("Technology Stocks: " + techCount);
        
        double total = calculateTotalValue(stocks);
        System.out.printf("Total Portfolio Value: $%.2f\n", total);
        
        findMaxValueStock(stocks).ifPresent(s -> 
                System.out.printf("Max Value Stock: %s ($%.2f)\n", s.getSymbol(), s.getTotalValue()));
        
        boolean hasExpensive = hasAnyMatching(stocks, s -> s.getCurrentPrice() > 300);
        System.out.println("Has stock > $300: " + hasExpensive);
        
        boolean allPositive = allMatching(stocks, s -> s.getCurrentPrice() > 0);
        System.out.println("All prices positive: " + allPositive);
        
        List<String> sectors = getDistinctSectors(stocks);
        System.out.println("Distinct Sectors: " + sectors);
        
        DoubleSummaryStatistics stats = getPriceStatistics(stocks);
        System.out.printf("\nPrice Statistics: Count=%d, Avg=$%.2f, Min=$%.2f, Max=$%.2f\n",
                stats.getCount(), stats.getAverage(), stats.getMin(), stats.getMax());
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("ALL LAB2 REQUIREMENTS DEMONSTRATED");
        System.out.println("=".repeat(80));
    }
    
    private static List<Stock> createSampleStocks() {
        List<Stock> stocks = new ArrayList<>();
        stocks.add(new CommonStock("AAPL", "Apple", 175.50, 100, "Technology", 0.5, 25.0));
        stocks.add(new CommonStock("MSFT", "Microsoft", 380.75, 50, "Technology", 0.7, 22.0));
        stocks.add(new CommonStock("GOOGL", "Alphabet", 142.30, 75, "Technology", 0.0, 28.0));
        stocks.add(new CommonStock("JPM", "JPMorgan", 155.20, 60, "Finance", 2.9, 18.0));
        stocks.add(new CommonStock("JNJ", "Johnson&Johnson", 162.30, 80, "Healthcare", 3.0, 12.0));
        return stocks;
    }
}
