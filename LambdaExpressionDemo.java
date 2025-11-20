import java.util.*;
import java.util.stream.*;

/**
 * Comprehensive Lambda Expression Demonstration
 * LAB2 Implementation: All Lambda Expression Requirements
 * 
 * Demonstrates:
 * a) Lambda Expressions for Arithmetic, StringOp, and Check operations
 * b) Lambda with no argument, one argument, two arguments, and block lambda
 * c) All functional interfaces
 */
public class LambdaExpressionDemo {
    
    /**
     * Demonstrates all Lambda Expression requirements
     */
    public static void demonstrateAllLambdas() {
        System.out.println("=".repeat(80));
        System.out.println("LAB2: LAMBDA EXPRESSION DEMONSTRATION");
        System.out.println("=".repeat(80));
        
        demonstrateArithmeticOperations();
        demonstrateStringOperations();
        demonstrateCheckOperations();
        demonstrateLambdaVariations();
    }
    
    /**
     * LAB2 Requirement A: Arithmetic Operations using Lambda
     * Lambda with TWO arguments
     */
    private static void demonstrateArithmeticOperations() {
        System.out.println("\n[A] ARITHMETIC OPERATIONS - Lambda with Two Arguments");
        System.out.println("-".repeat(80));
        
        // Lambda for Addition
        ArithmeticOperation addition = (a, b) -> a + b;
        
        // Lambda for Subtraction
        ArithmeticOperation subtraction = (a, b) -> a - b;
        
        // Lambda for Multiplication
        ArithmeticOperation multiplication = (a, b) -> a * b;
        
        // Lambda for Division
        ArithmeticOperation division = (a, b) -> b != 0 ? a / b : 0;
        
        double stockPrice1 = 150.50;
        double stockPrice2 = 75.25;
        
        System.out.printf("Stock Price 1: $%.2f\n", stockPrice1);
        System.out.printf("Stock Price 2: $%.2f\n", stockPrice2);
        System.out.println();
        
        System.out.printf("Addition (Total Investment): $%.2f\n", 
                         addition.calculate(stockPrice1, stockPrice2));
        System.out.printf("Subtraction (Price Difference): $%.2f\n", 
                         subtraction.calculate(stockPrice1, stockPrice2));
        System.out.printf("Multiplication (Portfolio Value): $%.2f\n", 
                         multiplication.calculate(stockPrice1, stockPrice2));
        System.out.printf("Division (Price Ratio): %.2f\n", 
                         division.calculate(stockPrice1, stockPrice2));
    }
    
    /**
     * LAB2 Requirement A: String Operations using Lambda
     * Lambda with ONE argument
     */
    private static void demonstrateStringOperations() {
        System.out.println("\n[A] STRING OPERATIONS - Lambda with One Argument");
        System.out.println("-".repeat(80));
        
        String stockSymbol = "AAPL Technology";
        
        // Lambda for String Reversal
        StringOperation reverseString = str -> new StringBuilder(str).reverse().toString();
        
        // Lambda for Vowel Count - Block Lambda Expression
        StringOperation vowelCount = str -> {
            int count = 0;
            String lowerStr = str.toLowerCase();
            for (char c : lowerStr.toCharArray()) {
                if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                    count++;
                }
            }
            return "Vowel Count: " + count;
        };
        
        // Lambda for Uppercase Conversion
        StringOperation toUpperCase = str -> str.toUpperCase();
        
        System.out.println("Original Stock Symbol: " + stockSymbol);
        System.out.println("Reversed: " + reverseString.process(stockSymbol));
        System.out.println(vowelCount.process(stockSymbol));
        System.out.println("Uppercase: " + toUpperCase.process(stockSymbol));
    }
    
    /**
     * LAB2 Requirement A: Check Operations using Lambda
     * Lambda with ONE argument
     */
    private static void demonstrateCheckOperations() {
        System.out.println("\n[A] CHECK OPERATIONS - Lambda with One Argument");
        System.out.println("-".repeat(80));
        
        // Lambda for Even Check
        CheckOperation isEven = num -> num % 2 == 0;
        
        // Lambda for Odd Check
        CheckOperation isOdd = num -> num % 2 != 0;
        
        // Lambda for Prime Check - Block Lambda Expression
        CheckOperation isPrime = num -> {
            if (num <= 1) return false;
            if (num <= 3) return true;
            if (num % 2 == 0 || num % 3 == 0) return false;
            for (int i = 5; i * i <= num; i += 6) {
                if (num % i == 0 || num % (i + 2) == 0) {
                    return false;
                }
            }
            return true;
        };
        
        int[] stockQuantities = {10, 15, 17, 20, 23, 50};
        
        System.out.println("Stock Quantities: " + Arrays.toString(stockQuantities));
        System.out.println();
        
        for (int qty : stockQuantities) {
            System.out.printf("Quantity %d: Even=%s, Odd=%s, Prime=%s\n", 
                             qty, isEven.test(qty), isOdd.test(qty), isPrime.test(qty));
        }
    }
    
    /**
     * LAB2 Requirement B: All Lambda Variations
     */
    private static void demonstrateLambdaVariations() {
        System.out.println("\n[B] LAMBDA EXPRESSION VARIATIONS");
        System.out.println("-".repeat(80));
        
        // 1. Lambda with NO argument
        System.out.println("\n1. Lambda with NO Argument:");
        StockReportGenerator marketStatus = () -> "Stock Market is OPEN for trading!";
        System.out.println("   " + marketStatus.generateReport());
        
        StockReportGenerator portfolioSummary = () -> {
            return "Portfolio Summary: Total Holdings = 5 stocks, Value = $10,250.00";
        };
        System.out.println("   " + portfolioSummary.generateReport());
        
        // 2. Lambda with ONE argument
        System.out.println("\n2. Lambda with ONE Argument:");
        StringOperation formatTicker = ticker -> "[" + ticker + "]";
        System.out.println("   Formatted Ticker: " + formatTicker.process("GOOGL"));
        
        // 3. Lambda with TWO arguments
        System.out.println("\n3. Lambda with TWO Arguments:");
        ArithmeticOperation calculateReturn = (buyPrice, sellPrice) -> 
            ((sellPrice - buyPrice) / buyPrice) * 100;
        System.out.printf("   Return on Investment: %.2f%%\n", 
                         calculateReturn.calculate(100.0, 125.0));
        
        // 4. Block Lambda Expression
        System.out.println("\n4. Block Lambda Expression:");
        ArithmeticOperation complexCalculation = (principal, rate) -> {
            double interest = principal * rate / 100;
            double total = principal + interest;
            System.out.printf("   Principal: $%.2f, Rate: %.2f%%, Interest: $%.2f\n", 
                             principal, rate, interest);
            return total;
        };
        double finalAmount = complexCalculation.calculate(1000.0, 5.5);
        System.out.printf("   Final Amount: $%.2f\n", finalAmount);
    }
    
    /**
     * Main method to run demonstration
     */
    public static void main(String[] args) {
        demonstrateAllLambdas();
    }
}
