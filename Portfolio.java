import java.util.ArrayList;
import java.util.List;

// Generic class to manage a collection of domain-specific objects
public class Portfolio<T> {
    private String portfolioName;
    private List<T> holdings;

    public Portfolio(String portfolioName) {
        this.portfolioName = portfolioName;
        this.holdings = new ArrayList<>();
    }

    // Add item to portfolio
    public void addItem(T item) {
        holdings.add(item);
        // Silent add for GUI (comment out for console version)
        // System.out.println("✓ Added to " + portfolioName + ": " + item);
    }

    // Remove item from portfolio
    public boolean removeItem(T item) {
        boolean removed = holdings.remove(item);
        // Silent remove for GUI (comment out for console version)
        /*
        if (removed) {
            System.out.println("✓ Removed from " + portfolioName + ": " + item);
        } else {
            System.out.println("✗ Item not found in " + portfolioName);
        }
        */
        return removed;
    }

    // Display all items
    public void displayPortfolio() {
        System.out.println("\n========================================");
        System.out.println("Portfolio: " + portfolioName);
        System.out.println("========================================");
        if (holdings.isEmpty()) {
            System.out.println("Portfolio is empty.");
        } else {
            System.out.println("Total Holdings: " + holdings.size());
            System.out.println("----------------------------------------");
            for (int i = 0; i < holdings.size(); i++) {
                System.out.println((i + 1) + ". " + holdings.get(i));
            }
        }
        System.out.println("========================================\n");
    }

    // Get all holdings
    public List<T> getHoldings() {
        return new ArrayList<>(holdings);
    }

    // Get portfolio size
    public int size() {
        return holdings.size();
    }

    // Check if portfolio is empty
    public boolean isEmpty() {
        return holdings.isEmpty();
    }

    // Clear all holdings
    public void clear() {
        holdings.clear();
        System.out.println("✓ Portfolio " + portfolioName + " cleared.");
    }

    public String getPortfolioName() {
        return portfolioName;
    }
}
