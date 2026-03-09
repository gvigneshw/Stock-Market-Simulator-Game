import java.util.*;
import java.util.concurrent.*;

// Stock Price Simulator using threads for realistic price movements
public class StockPriceSimulator implements Runnable {
    private List<Stock> stocks;
    private volatile boolean running;
    private Random random;
    private List<PriceUpdateListener> listeners;
    private Map<String, List<Double>> priceHistory; // Generic Map for price history

    public StockPriceSimulator(List<Stock> stocks) {
        this.stocks = stocks;
        this.running = false;
        this.random = new Random();
        this.listeners = new CopyOnWriteArrayList<>(); // Thread-safe list
        this.priceHistory = new ConcurrentHashMap<>(); // Thread-safe map
        
        // Initialize price history with synthetic data for charts
        for (Stock stock : stocks) {
            priceHistory.put(stock.getSymbol(), new ArrayList<>());
        }
        initializePriceHistory();
    }

    // Generate initial price history so charts have data from the start
    private void initializePriceHistory() {
        for (Stock stock : stocks) {
            List<Double> history = priceHistory.get(stock.getSymbol());
            double basePrice = stock.getCurrentPrice();
            double volatility = stock.calculateRisk() / 100.0;
            
            // Start from a price ~85-100% of current and random-walk to current
            double price = basePrice * (0.85 + random.nextDouble() * 0.15);
            for (int i = 0; i < 30; i++) {
                double change = random.nextGaussian() * volatility * 0.025;
                price = price * (1 + change);
                price = Math.max(price, basePrice * 0.5);
                history.add(price);
            }
            history.add(basePrice); // End at the actual current price
        }
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                updatePrices();
                Thread.sleep(3000); // Update every 3 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Update all stock prices
    private void updatePrices() {
        for (Stock stock : stocks) {
            double oldPrice = stock.getCurrentPrice();
            double newPrice = calculateNewPrice(stock);
            stock.setCurrentPrice(newPrice);
            
            // Store price history (limit to 50 data points)
            List<Double> history = priceHistory.get(stock.getSymbol());
            history.add(newPrice);
            if (history.size() > 50) {
                history.remove(0);
            }
            
            // Notify listeners
            notifyListeners(stock, oldPrice, newPrice);
        }
    }

    // Calculate new price based on stock risk and random factors
    private double calculateNewPrice(Stock stock) {
        double currentPrice = stock.getCurrentPrice();
        double volatility = stock.calculateRisk() / 100.0; // Convert risk to decimal
        
        // Random price change based on volatility
        double changePercent = (random.nextGaussian() * volatility * 0.05);
        
        // Ensure price doesn't drop below a minimum threshold
        double newPrice = currentPrice * (1 + changePercent);
        return Math.max(newPrice, currentPrice * 0.5); // Don't drop below 50% of current
    }

    // Add price update listener
    public void addPriceUpdateListener(PriceUpdateListener listener) {
        listeners.add(listener);
    }

    // Notify all listeners of price change
    private void notifyListeners(Stock stock, double oldPrice, double newPrice) {
        for (PriceUpdateListener listener : listeners) {
            listener.onPriceUpdate(stock, oldPrice, newPrice);
        }
    }

    // Get price history for a stock
    public List<Double> getPriceHistory(String symbol) {
        return new ArrayList<>(priceHistory.getOrDefault(symbol, new ArrayList<>()));
    }

    // Stop the simulator
    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    // Interface for price update callbacks
    public interface PriceUpdateListener {
        void onPriceUpdate(Stock stock, double oldPrice, double newPrice);
    }
}
