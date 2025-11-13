import java.util.ArrayList;
import java.util.List;

// Player class representing the game player with account management
public class Player {
    private String name;
    private double cash;
    private double initialCash;
    private Portfolio<Stock> portfolio;
    private List<Transaction> transactionHistory;
    private int level;
    private double totalProfit;

    public Player(String name, double initialCash) {
        this.name = name;
        this.cash = initialCash;
        this.initialCash = initialCash;
        this.portfolio = new Portfolio<>("My Portfolio");
        this.transactionHistory = new ArrayList<>();
        this.level = 1;
        this.totalProfit = 0.0;
    }

    // Buy stock
    public boolean buyStock(Stock stock, int quantity) {
        double totalCost = stock.getCurrentPrice() * quantity;
        
        if (cash >= totalCost) {
            cash -= totalCost;
            
            // Check if we already own this stock
            Stock existingStock = findStockInPortfolio(stock.getSymbol());
            if (existingStock != null) {
                existingStock.setQuantity(existingStock.getQuantity() + quantity);
            } else {
                // Create new stock instance with purchased quantity
                Stock newStock = createStockCopy(stock, quantity);
                portfolio.addItem(newStock);
            }
            
            transactionHistory.add(new Transaction("BUY", stock.getSymbol(), 
                quantity, stock.getCurrentPrice(), totalCost));
            
            return true;
        }
        return false;
    }

    // Sell stock
    public boolean sellStock(Stock stock, int quantity) {
        Stock ownedStock = findStockInPortfolio(stock.getSymbol());
        
        if (ownedStock != null && ownedStock.getQuantity() >= quantity) {
            double totalRevenue = stock.getCurrentPrice() * quantity;
            cash += totalRevenue;
            
            ownedStock.setQuantity(ownedStock.getQuantity() - quantity);
            
            // Remove from portfolio if quantity becomes 0
            if (ownedStock.getQuantity() == 0) {
                portfolio.removeItem(ownedStock);
            }
            
            transactionHistory.add(new Transaction("SELL", stock.getSymbol(), 
                quantity, stock.getCurrentPrice(), totalRevenue));
            
            // Update profit
            calculateTotalProfit();
            
            return true;
        }
        return false;
    }

    // Find stock in portfolio by symbol
    private Stock findStockInPortfolio(String symbol) {
        for (Stock s : portfolio.getHoldings()) {
            if (s.getSymbol().equals(symbol)) {
                return s;
            }
        }
        return null;
    }

    // Create a copy of stock with specific quantity
    private Stock createStockCopy(Stock original, int quantity) {
        if (original instanceof CommonStock) {
            CommonStock cs = (CommonStock) original;
            return new CommonStock(cs.getSymbol(), cs.getCompanyName(), 
                cs.getCurrentPrice(), quantity, cs.getSector(), 
                cs.getDividendYield(), cs.getVolatilityIndex());
        } else if (original instanceof PreferredStock) {
            PreferredStock ps = (PreferredStock) original;
            return new PreferredStock(ps.getSymbol(), ps.getCompanyName(), 
                ps.getCurrentPrice(), quantity, ps.getSector(), 
                ps.getFixedDividendRate(), ps.getParValue());
        } else if (original instanceof Bond) {
            Bond b = (Bond) original;
            return new Bond(b.getSymbol(), b.getCompanyName(), 
                b.getCurrentPrice(), quantity, b.getSector(), 
                b.getCouponRate(), b.getYearsToMaturity(), b.getCreditRating());
        }
        return null;
    }

    // Calculate total portfolio value
    public double getPortfolioValue() {
        double total = 0.0;
        for (Stock stock : portfolio.getHoldings()) {
            total += stock.getTotalValue();
        }
        return total;
    }

    // Calculate net worth
    public double getNetWorth() {
        return cash + getPortfolioValue();
    }

    // Calculate total profit/loss
    public void calculateTotalProfit() {
        totalProfit = getNetWorth() - initialCash;
    }

    // Get profit percentage
    public double getProfitPercentage() {
        return (totalProfit / initialCash) * 100;
    }

    // Level up based on net worth
    public void checkLevelUp() {
        int newLevel = 1 + (int)(getNetWorth() / 100000);
        if (newLevel > level) {
            level = newLevel;
        }
    }

    // Getters
    public String getName() { return name; }
    public double getCash() { return cash; }
    public double getInitialCash() { return initialCash; }
    public Portfolio<Stock> getPortfolio() { return portfolio; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }
    public int getLevel() { return level; }
    public double getTotalProfit() { 
        calculateTotalProfit();
        return totalProfit; 
    }

    // Setters
    public void setCash(double cash) { this.cash = cash; }
}
