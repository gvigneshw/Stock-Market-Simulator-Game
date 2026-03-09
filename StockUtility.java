import java.util.Collection;

// Utility class with generic methods
public class StockUtility {

    // Generic method to process and display details of any collection
    public static <T> void displayCollectionDetails(Collection<T> collection, String collectionName) {
        System.out.println("\n========================================");
        System.out.println("Collection: " + collectionName);
        System.out.println("========================================");
        System.out.println("Type: " + collection.getClass().getSimpleName());
        System.out.println("Size: " + collection.size());
        System.out.println("Is Empty: " + collection.isEmpty());
        System.out.println("\nContents:");
        System.out.println("----------------------------------------");
        
        if (collection.isEmpty()) {
            System.out.println("No items in collection.");
        } else {
            int index = 1;
            for (T item : collection) {
                System.out.println(index++ + ". " + item);
            }
        }
        System.out.println("========================================\n");
    }

    // Generic method to display details of any single item
    public static <T> void displayItemDetails(T item, String itemDescription) {
        System.out.println("\n========================================");
        System.out.println("Item Details: " + itemDescription);
        System.out.println("========================================");
        System.out.println("Type: " + item.getClass().getSimpleName());
        System.out.println("Value: " + item.toString());
        System.out.println("========================================\n");
    }

    // Generic method to count items in a collection
    public static <T> int countItems(Collection<T> collection) {
        return collection.size();
    }

    // Generic method to check if collection contains an item
    public static <T> boolean contains(Collection<T> collection, T item) {
        return collection.contains(item);
    }

    // Generic method to display statistics for any collection
    public static <T> void displayStatistics(Collection<T> collection, String collectionName) {
        System.out.println("\n========================================");
        System.out.println("Statistics for: " + collectionName);
        System.out.println("========================================");
        System.out.println("Total Items: " + collection.size());
        System.out.println("Collection Type: " + collection.getClass().getSimpleName());
        System.out.println("Memory Address: " + System.identityHashCode(collection));
        System.out.println("========================================\n");
    }

    // Generic method to process any type of stock and display calculated metrics
    public static <T extends Stock> void displayStockMetrics(T stock) {
        System.out.println("\n========================================");
        System.out.println("STOCK METRICS");
        System.out.println("========================================");
        System.out.println("Symbol: " + stock.getSymbol());
        System.out.println("Company: " + stock.getCompanyName());
        System.out.println("Type: " + stock.getStockType());
        System.out.printf("Current Price: $%.2f\n", stock.getCurrentPrice());
        System.out.println("Quantity: " + stock.getQuantity());
        System.out.printf("Total Value: $%.2f\n", stock.getTotalValue());
        System.out.printf("Risk Score: %.2f%%\n", stock.calculateRisk());
        System.out.println("Sector: " + stock.getSector());
        
        // Display type-specific information
        if (stock instanceof CommonStock) {
            CommonStock cs = (CommonStock) stock;
            System.out.printf("Dividend Yield: %.2f%%\n", cs.getDividendYield());
            System.out.printf("Annual Dividend: $%.2f\n", cs.calculateAnnualDividend());
        } else if (stock instanceof PreferredStock) {
            PreferredStock ps = (PreferredStock) stock;
            System.out.printf("Fixed Dividend Rate: %.2f%%\n", ps.getFixedDividendRate());
            System.out.printf("Annual Dividend: $%.2f\n", ps.calculateFixedAnnualDividend());
        } else if (stock instanceof Bond) {
            Bond b = (Bond) stock;
            System.out.printf("Coupon Rate: %.2f%%\n", b.getCouponRate());
            System.out.println("Years to Maturity: " + b.getYearsToMaturity());
            System.out.println("Credit Rating: " + b.getCreditRating());
            System.out.printf("Annual Coupon: $%.2f\n", b.calculateAnnualCoupon());
        }
        System.out.println("========================================\n");
    }
}
