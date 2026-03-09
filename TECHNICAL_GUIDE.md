# Stock Market Simulator - Technical Implementation Guide

## Project Overview

This application is a multi-threaded stock trading simulator implementing comprehensive Java concepts: generics (class, bounded, methods, interface), object-oriented programming (inheritance, polymorphism, encapsulation), Swing GUI components, concurrent programming with thread safety, and the Collections Framework.

---

## 1. Object-Oriented Programming Implementation

### 1.1 Encapsulation

All domain classes use private fields with public accessor methods to control data access and maintain integrity.

**Implementation in Player.java:**
```java
private double cash;
private Portfolio<Stock> portfolio;
private List<Transaction> transactionHistory;

public double getCash() {
    return cash;
}

public void setCash(double cash) {
    this.cash = cash;
}
```

The player's cash balance is protected from direct external modification. All financial operations must go through controlled methods like `buyStock()` and `sellStock()`, which validate transactions before modifying the cash field.

### 1.2 Inheritance Hierarchy

Three-level inheritance structure with abstract base class and concrete subclasses.

**Stock.java (Abstract Base Class):**
```java
public abstract class Stock {
    private String symbol;
    private String companyName;
    private double currentPrice;
    private int quantity;
    
    public abstract double calculateRisk();
    public abstract String getStockType();
}
```

**CommonStock.java (Concrete Implementation):**
```java
public class CommonStock extends Stock {
    private double volatilityIndex;
    private String sector;
    
    @Override
    public double calculateRisk() {
        return volatilityIndex;
    }
    
    @Override
    public String getStockType() {
        return "Common Stock";
    }
}
```

**PreferredStock.java (Concrete Implementation):**
```java
public class PreferredStock extends Stock {
    private double fixedDividendRate;
    private double parValue;
    
    @Override
    public double calculateRisk() {
        return 35.0;
    }
    
    @Override
    public String getStockType() {
        return "Preferred Stock";
    }
}
```

**Bond.java (Concrete Implementation):**
```java
public class Bond extends Stock {
    private String creditRating;
    private LocalDate maturityDate;
    
    @Override
    public double calculateRisk() {
        switch(creditRating) {
            case "AAA": return 10.0;
            case "AA": return 20.0;
            case "A": return 30.0;
            default: return 50.0;
        }
    }
    
    @Override
    public String getStockType() {
        return "Bond";
    }
}
```

Each subclass inherits common stock properties (symbol, price, quantity) but provides its own risk calculation algorithm. CommonStock uses volatility index, PreferredStock uses fixed value, Bond uses credit rating mapping.

### 1.3 Polymorphism

Runtime polymorphism through method overriding and dynamic dispatch.

**Implementation in StockMarketGame.java:**
```java
List<Stock> availableStocks = new ArrayList<>();
availableStocks.add(new CommonStock("AAPL", "Apple Inc.", 175.50, 65.0, "Technology"));
availableStocks.add(new PreferredStock("VZ-P", "Verizon Preferred", 52.30, 5.5, 100.0));
availableStocks.add(new Bond("T-10Y", "US Treasury 10Y", 98.50, "AAA", maturityDate));

for (Stock stock : availableStocks) {
    double risk = stock.calculateRisk();  // Calls appropriate subclass method
    String type = stock.getStockType();   // Dynamic dispatch at runtime
}
```

The JVM determines the correct method to invoke at runtime based on the actual object type, not the reference type. This allows heterogeneous collections with uniform interface.

---

## 2. Generics Implementation

Four categories of generics: generic class, bounded generic class, generic methods, and generic interface.

### 2.1 Generic Class: Portfolio&lt;T&gt;

Type-safe container that can hold any type specified at instantiation.

**Portfolio.java:**
```java
public class Portfolio<T> {
    private String name;
    private List<T> holdings;
    
    public Portfolio(String name) {
        this.name = name;
        this.holdings = new ArrayList<>();
    }
    
    public void addItem(T item) {
        holdings.add(item);
    }
    
    public void removeItem(T item) {
        holdings.remove(item);
    }
    
    public List<T> getHoldings() {
        return new ArrayList<>(holdings);
    }
    
    public int size() {
        return holdings.size();
    }
}
```

**Type Parameter T:**
- Placeholder replaced with actual type at compile time
- Provides compile-time type safety
- Eliminates need for explicit casting
- Prevents ClassCastException at runtime

**Usage in Player.java:**
```java
private Portfolio<Stock> portfolio;

portfolio = new Portfolio<>("Main Portfolio");
portfolio.addItem(appleStock);  // Type-safe: only Stock objects allowed
```

The compiler validates that only Stock objects can be added to this portfolio. Attempting to add any other type results in compile-time error.

### 2.2 Bounded Generic Class: StockAnalyzer&lt;T extends Stock&gt;

Generic class with upper bound constraint restricting type parameter to Stock or its subclasses.

**StockAnalyzer.java:**
```java
public class StockAnalyzer<T extends Stock> {
    private List<T> stocks;
    
    public StockAnalyzer() {
        this.stocks = new ArrayList<>();
    }
    
    public void addStock(T stock) {
        stocks.add(stock);
    }
    
    public double calculateTotalValue() {
        double total = 0.0;
        for (T stock : stocks) {
            total += stock.getTotalValue();  // Can call Stock methods
        }
        return total;
    }
    
    public double calculateWeightedAverageRisk() {
        double totalValue = calculateTotalValue();
        double weightedRisk = 0.0;
        
        for (T stock : stocks) {
            double weight = stock.getTotalValue() / totalValue;
            weightedRisk += stock.calculateRisk() * weight;
        }
        return weightedRisk;
    }
    
    public T findHighestValueStock() {
        T highest = stocks.get(0);
        for (T stock : stocks) {
            if (stock.getTotalValue() > highest.getTotalValue()) {
                highest = stock;
            }
        }
        return highest;
    }
}
```

**Bounded Type Parameter Advantages:**
- Restricts type to Stock and its subclasses (CommonStock, PreferredStock, Bond)
- Allows calling Stock-specific methods within generic code
- Combines benefits of generics and inheritance
- Provides type safety while accessing superclass methods

**Valid Usage:**
```java
StockAnalyzer<Stock> analyzer1 = new StockAnalyzer<>();
StockAnalyzer<CommonStock> analyzer2 = new StockAnalyzer<>();
StockAnalyzer<PreferredStock> analyzer3 = new StockAnalyzer<>();
```

**Invalid Usage (Compile Error):**
```java
StockAnalyzer<String> analyzer = new StockAnalyzer<>();  // Error: String not subtype of Stock
```

### 2.3 Generic Methods: StockUtility

Methods with type parameters independent of class-level generics.

**StockUtility.java:**
```java
public class StockUtility {
    
    // Unbounded generic method
    public static <T> void displayCollectionDetails(Collection<T> collection, String name) {
        System.out.println("=== " + name + " ===");
        System.out.println("Type: " + collection.getClass().getSimpleName());
        System.out.println("Size: " + collection.size());
        System.out.println("Empty: " + collection.isEmpty());
        
        System.out.println("\nElements:");
        int index = 1;
        for (T item : collection) {
            System.out.println(index++ + ". " + item);
        }
    }
    
    // Bounded generic method
    public static <T extends Stock> void displayStockMetrics(T stock) {
        System.out.println("Symbol: " + stock.getSymbol());
        System.out.println("Company: " + stock.getCompanyName());
        System.out.println("Type: " + stock.getStockType());
        System.out.println("Current Price: $" + String.format("%.2f", stock.getCurrentPrice()));
        System.out.println("Quantity: " + stock.getQuantity());
        System.out.println("Total Value: $" + String.format("%.2f", stock.getTotalValue()));
        System.out.println("Risk Factor: " + String.format("%.1f", stock.calculateRisk()) + "%");
    }
    
    // Multiple bounded type parameters
    public static <T extends Stock> List<T> filterByMinValue(List<T> stocks, double minValue) {
        List<T> filtered = new ArrayList<>();
        for (T stock : stocks) {
            if (stock.getTotalValue() >= minValue) {
                filtered.add(stock);
            }
        }
        return filtered;
    }
}
```

**Type Parameter Scope:**
- Declared before return type: `<T>`
- Scoped only to that method
- Class doesn't need to be generic

**Usage:**
```java
List<Stock> stockList = player.getPortfolio().getHoldings();
StockUtility.displayCollectionDetails(stockList, "My Stocks");

CommonStock apple = new CommonStock("AAPL", "Apple", 175.50, 65.0, "Tech");
StockUtility.displayStockMetrics(apple);

List<Stock> highValue = StockUtility.filterByMinValue(stockList, 10000.0);
```

The compiler infers type parameter from arguments (type inference).

### 2.4 Generic Interface: StockFilter&lt;T&gt;

Generic interface defining filtering contract with multiple implementations.

**StockFilter.java:**
```java
public interface StockFilter<T> {
    boolean meetsCriteria(T item);
    String getFilterDescription();
}
```

**Implementation 1: HighValueStockFilter**
```java
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
        return "High Value Stocks (>= $" + String.format("%.2f", threshold) + ")";
    }
}
```

**Implementation 2: HighRiskStockFilter**
```java
public class HighRiskStockFilter implements StockFilter<Stock> {
    private double riskThreshold;
    
    public HighRiskStockFilter(double riskThreshold) {
        this.riskThreshold = riskThreshold;
    }
    
    @Override
    public boolean meetsCriteria(Stock stock) {
        return stock.calculateRisk() >= riskThreshold;
    }
    
    @Override
    public String getFilterDescription() {
        return "High Risk Stocks (>= " + riskThreshold + "%)";
    }
}
```

**Implementation 3: LowRiskStockFilter**
```java
public class LowRiskStockFilter implements StockFilter<Stock> {
    private double riskThreshold;
    
    public LowRiskStockFilter(double riskThreshold) {
        this.riskThreshold = riskThreshold;
    }
    
    @Override
    public boolean meetsCriteria(Stock stock) {
        return stock.calculateRisk() < riskThreshold;
    }
    
    @Override
    public String getFilterDescription() {
        return "Low Risk Stocks (< " + riskThreshold + "%)";
    }
}
```

**Implementation 4: SectorStockFilter**
```java
public class SectorStockFilter implements StockFilter<Stock> {
    private String targetSector;
    
    public SectorStockFilter(String sector) {
        this.targetSector = sector;
    }
    
    @Override
    public boolean meetsCriteria(Stock stock) {
        if (stock instanceof CommonStock) {
            CommonStock commonStock = (CommonStock) stock;
            return commonStock.getSector().equalsIgnoreCase(targetSector);
        }
        return false;
    }
    
    @Override
    public String getFilterDescription() {
        return "Sector: " + targetSector;
    }
}
```

**Strategy Pattern with Generics:**
```java
// In StockMarketGame.java
public void applyFilter(StockFilter<Stock> filter) {
    List<Stock> filtered = new ArrayList<>();
    for (Stock stock : availableStocks) {
        if (filter.meetsCriteria(stock)) {
            filtered.add(stock);
        }
    }
    displayFilteredStocks(filtered, filter.getFilterDescription());
}

// Usage
StockFilter<Stock> highValueFilter = new HighValueStockFilter(10000.0);
applyFilter(highValueFilter);

StockFilter<Stock> techFilter = new SectorStockFilter("Technology");
applyFilter(techFilter);
```

This implements the Strategy design pattern where filtering algorithm can be changed at runtime.

---

## 3. GUI Architecture (Swing Components)

### 3.1 Component Hierarchy

```
JFrame (StockMarketGame)
├── JMenuBar
│   ├── File Menu (New Game, Save, Load, Exit)
│   ├── View Menu (Refresh, Theme)
│   └── Analysis Menu (Portfolio Analysis, Filter Stocks)
├── JTabbedPane
│   ├── TradingPanel (JPanel)
│   │   ├── JTable (stock listing)
│   │   ├── JScrollPane (table container)
│   │   ├── JSpinner (quantity selector)
│   │   ├── JButton (Buy)
│   │   ├── JButton (Sell)
│   │   └── JLabel (cash display)
│   ├── PortfolioPanel (JPanel)
│   │   ├── JTable (holdings)
│   │   ├── JScrollPane (table container)
│   │   ├── JLabel (net worth)
│   │   ├── JLabel (profit/loss)
│   │   ├── JProgressBar (level indicator)
│   │   └── JButton (Analysis)
│   ├── TransactionHistoryPanel (JPanel)
│   │   ├── JTable (transaction log)
│   │   └── JScrollPane (table container)
│   └── NewsPanel (JPanel)
│       ├── JTextArea (news feed)
│       └── JScrollPane (text container)
└── JPanel (status bar)
    ├── JLabel (player name)
    ├── JLabel (timestamp)
    └── JLabel (market status)
```

### 3.2 JFrame Configuration

**StockMarketGame.java:**
```java
public class StockMarketGame extends JFrame {
    
    public StockMarketGame() {
        super("Stock Market Simulator - Professional Trading Platform");
        
        // Frame configuration
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);  // Center on screen
        
        // Custom close handler
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
        
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize components
        initializeGame();
        setupGUI();
        startSimulation();
    }
}
```

### 3.3 JTable with Custom Model

**TradingPanel.java:**
```java
private void setupStockTable() {
    String[] columnNames = {"Symbol", "Company", "Type", "Price", "Risk", "Sector"};
    tableModel = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;  // Read-only table
        }
    };
    
    stockTable = new JTable(tableModel);
    stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    stockTable.setRowHeight(25);
    
    // Custom cell renderer for risk column
    stockTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            String riskStr = value.toString().replace("%", "");
            double risk = Double.parseDouble(riskStr);
            
            if (risk < 30) {
                c.setBackground(new Color(200, 255, 200));  // Light green
            } else if (risk < 60) {
                c.setBackground(new Color(255, 255, 200));  // Light yellow
            } else {
                c.setBackground(new Color(255, 200, 200));  // Light red
            }
            
            return c;
        }
    });
}
```

### 3.4 Event Handling with Lambda Expressions

**TradingPanel.java:**
```java
private void setupButtons() {
    buyButton = new JButton("BUY");
    buyButton.addActionListener(e -> handleBuy());
    
    sellButton = new JButton("SELL");
    sellButton.addActionListener(e -> handleSell());
    
    refreshButton = new JButton("Refresh");
    refreshButton.addActionListener(e -> updateStockTable());
}

private void handleBuy() {
    int selectedRow = stockTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a stock to buy.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    Stock selectedStock = availableStocks.get(selectedRow);
    int quantity = (Integer) quantitySpinner.getValue();
    double totalCost = selectedStock.getCurrentPrice() * quantity;
    
    if (totalCost > player.getCash()) {
        JOptionPane.showMessageDialog(this, 
                String.format("Insufficient funds. Required: $%.2f, Available: $%.2f", 
                        totalCost, player.getCash()),
                "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Buy %d shares of %s for $%.2f?", 
                    quantity, selectedStock.getSymbol(), totalCost),
            "Confirm Purchase", JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        if (player.buyStock(selectedStock, quantity)) {
            JOptionPane.showMessageDialog(this, "Purchase successful!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            updateCashLabel();
            updateStockTable();
            portfolioPanel.refreshDisplay();
        }
    }
}
```

Lambda expressions replace verbose anonymous inner classes for ActionListener implementation.

### 3.5 Layout Management

**TradingPanel.java:**
```java
private void setupLayout() {
    setLayout(new BorderLayout(10, 10));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    // Top panel: controls
    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
    controlPanel.add(new JLabel("Quantity:"));
    controlPanel.add(quantitySpinner);
    controlPanel.add(buyButton);
    controlPanel.add(sellButton);
    controlPanel.add(refreshButton);
    
    // Center: table
    JScrollPane scrollPane = new JScrollPane(stockTable);
    
    // Bottom: cash display
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    bottomPanel.add(cashLabel);
    
    add(controlPanel, BorderLayout.NORTH);
    add(scrollPane, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
}
```

BorderLayout divides container into five regions: NORTH, SOUTH, EAST, WEST, CENTER. FlowLayout arranges components left-to-right, top-to-bottom.

---

## 4. Multithreading and Concurrency

### 4.1 Thread Implementation

**StockPriceSimulator.java:**
```java
public class StockPriceSimulator implements Runnable {
    private List<Stock> stocks;
    private volatile boolean running;
    private Map<String, List<Double>> priceHistory;
    private List<PriceUpdateListener> listeners;
    
    public StockPriceSimulator(List<Stock> stocks) {
        this.stocks = stocks;
        this.running = false;
        this.priceHistory = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
        
        for (Stock stock : stocks) {
            priceHistory.put(stock.getSymbol(), new ArrayList<>());
        }
    }
    
    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                updateAllPrices();
                Thread.sleep(3000);  // Update every 3 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void updateAllPrices() {
        for (Stock stock : stocks) {
            double oldPrice = stock.getCurrentPrice();
            double newPrice = calculateNewPrice(stock);
            
            stock.setCurrentPrice(newPrice);
            
            // Store in history
            List<Double> history = priceHistory.get(stock.getSymbol());
            history.add(newPrice);
            
            // Notify listeners
            notifyPriceUpdate(stock, oldPrice, newPrice);
        }
    }
    
    private double calculateNewPrice(Stock stock) {
        double currentPrice = stock.getCurrentPrice();
        double volatility = stock.calculateRisk() / 1000.0;
        Random random = new Random();
        
        // Random walk model
        double change = (random.nextDouble() - 0.5) * 2 * volatility * currentPrice;
        double newPrice = currentPrice + change;
        
        // Price bounds
        return Math.max(newPrice, currentPrice * 0.9);
    }
    
    public void stop() {
        running = false;
    }
}
```

**Thread Initialization in StockMarketGame.java:**
```java
private void startSimulation() {
    priceSimulator = new StockPriceSimulator(availableStocks);
    
    Thread simulatorThread = new Thread(priceSimulator);
    simulatorThread.setDaemon(true);  // Terminates when main thread exits
    simulatorThread.start();
    
    // Add listener for GUI updates
    priceSimulator.addPriceUpdateListener(new PriceUpdateListener() {
        @Override
        public void onPriceUpdate(Stock stock, double oldPrice, double newPrice) {
            SwingUtilities.invokeLater(() -> {
                tradingPanel.updateStockTable();
                portfolioPanel.refreshDisplay();
            });
        }
    });
}
```

### 4.2 Thread Safety Mechanisms

**Volatile Keyword:**
```java
private volatile boolean running;
```
Ensures visibility of changes across threads. Without volatile, thread may cache the variable and not see updates from other threads.

**Thread-Safe Collections:**
```java
private Map<String, List<Double>> priceHistory = new ConcurrentHashMap<>();
private List<PriceUpdateListener> listeners = new CopyOnWriteArrayList<>();
```

ConcurrentHashMap provides thread-safe map operations without external synchronization. CopyOnWriteArrayList creates new copy on write, safe for concurrent iteration.

**SwingUtilities.invokeLater():**
```java
SwingUtilities.invokeLater(() -> {
    tradingPanel.updateStockTable();
    portfolioPanel.refreshDisplay();
});
```

Schedules GUI updates on Event Dispatch Thread (EDT). Swing components are not thread-safe and must only be modified from EDT. Violating this causes unpredictable behavior and potential crashes.

### 4.3 Timer for Periodic Tasks

**StockMarketGame.java:**
```java
private void startTimers() {
    // UI refresh timer (every 5 seconds)
    gameTimer = new Timer(5000, e -> {
        updateStatusBar();
        portfolioPanel.refreshDisplay();
        transactionPanel.updateTransactionTable();
    });
    gameTimer.start();
    
    // News generation timer (every 15 seconds)
    newsTimer = new Timer(15000, e -> {
        generateNewsEvent();
    });
    newsTimer.start();
}
```

javax.swing.Timer executes on EDT, safe for GUI updates. Alternative: java.util.Timer executes on separate thread, requires SwingUtilities.invokeLater() for GUI operations.

---

## 5. Collections Framework Usage

### 5.1 ArrayList&lt;T&gt;

Dynamic array implementation of List interface.

**Usage:**
```java
private List<Stock> availableStocks = new ArrayList<>();
private List<Transaction> transactionHistory = new ArrayList<>();
```

**Operations:**
- add(element) - O(1) amortized
- get(index) - O(1)
- remove(element) - O(n)
- size() - O(1)
- contains(element) - O(n)

### 5.2 HashMap and ConcurrentHashMap

Hash table implementation of Map interface.

**Usage:**
```java
// Non-thread-safe (single thread)
private Map<String, Stock> stockLookup = new HashMap<>();

// Thread-safe (multiple threads)
private Map<String, List<Double>> priceHistory = new ConcurrentHashMap<>();
```

**Operations:**
- put(key, value) - O(1) average
- get(key) - O(1) average
- remove(key) - O(1) average
- containsKey(key) - O(1) average

**ConcurrentHashMap Features:**
- Lock striping for concurrent access
- No locking for read operations
- Atomic operations (putIfAbsent, replace, etc.)
- Safe for concurrent iterations

### 5.3 CopyOnWriteArrayList

Thread-safe List implementation creating new copy on modification.

**Usage:**
```java
private List<PriceUpdateListener> listeners = new CopyOnWriteArrayList<>();
```

**Characteristics:**
- Thread-safe without external synchronization
- Iteration never throws ConcurrentModificationException
- Expensive for writes (full array copy)
- Efficient for read-heavy scenarios
- Ideal for listener lists (rare modifications, frequent iterations)

---

## 6. Control Flow Analysis

### 6.1 Application Startup Sequence

**Step 1: Main Method Entry Point**
```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        StockMarketGame game = new StockMarketGame();
        game.setVisible(true);
    });
}
```

SwingUtilities.invokeLater() ensures GUI creation occurs on EDT.

**Step 2: Constructor Initialization**
```java
public StockMarketGame() {
    super("Stock Market Simulator");
    
    // Set Look and Feel
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    // Initialize game
    initializeGame();
    setupGUI();
    startSimulation();
}
```

**Step 3: Game Initialization**
```java
private void initializeGame() {
    // Get player name
    String playerName = JOptionPane.showInputDialog(this, 
            "Enter your name:", "Player Setup", JOptionPane.QUESTION_MESSAGE);
    
    if (playerName == null || playerName.trim().isEmpty()) {
        playerName = "Player";
    }
    
    // Create player with starting capital
    player = new Player(playerName, 100000.0);
    
    // Initialize available stocks
    availableStocks = new ArrayList<>();
    availableStocks.add(new CommonStock("AAPL", "Apple Inc.", 175.50, 65.0, "Technology"));
    availableStocks.add(new CommonStock("MSFT", "Microsoft Corp.", 380.20, 58.0, "Technology"));
    availableStocks.add(new CommonStock("GOOGL", "Alphabet Inc.", 138.75, 62.0, "Technology"));
    availableStocks.add(new CommonStock("TSLA", "Tesla Inc.", 242.80, 85.0, "Automotive"));
    availableStocks.add(new CommonStock("AMZN", "Amazon.com Inc.", 145.30, 70.0, "E-Commerce"));
    availableStocks.add(new PreferredStock("VZ-P", "Verizon Preferred", 52.30, 5.5, 100.0));
    availableStocks.add(new PreferredStock("T-P", "AT&T Preferred", 48.90, 6.0, 100.0));
    availableStocks.add(new Bond("T-10Y", "US Treasury 10Y", 98.50, "AAA", 
            LocalDate.now().plusYears(10)));
    availableStocks.add(new Bond("CORP-5Y", "Corporate Bond 5Y", 95.20, "AA", 
            LocalDate.now().plusYears(5)));
    
    // Create simulators
    priceSimulator = new StockPriceSimulator(availableStocks);
    newsGenerator = new MarketNewsGenerator(availableStocks);
}
```

**Step 4: GUI Setup**
```java
private void setupGUI() {
    setLayout(new BorderLayout());
    
    // Create tabbed pane
    mainTabbedPane = new JTabbedPane();
    
    // Create panels
    tradingPanel = new TradingPanel(availableStocks, player, this);
    portfolioPanel = new PortfolioPanel(player);
    transactionPanel = new TransactionHistoryPanel(player);
    newsPanel = new NewsPanel(newsGenerator);
    
    // Add tabs
    mainTabbedPane.addTab("Trading", tradingPanel);
    mainTabbedPane.addTab("Portfolio", portfolioPanel);
    mainTabbedPane.addTab("Transactions", transactionPanel);
    mainTabbedPane.addTab("Market News", newsPanel);
    
    add(mainTabbedPane, BorderLayout.CENTER);
    
    // Setup menu bar
    setupMenuBar();
    
    // Setup status bar
    setupStatusBar();
}
```

**Step 5: Simulation Start**
```java
private void startSimulation() {
    // Start price simulator thread
    Thread simulatorThread = new Thread(priceSimulator);
    simulatorThread.setDaemon(true);
    simulatorThread.start();
    
    // Add price update listener
    priceSimulator.addPriceUpdateListener((stock, oldPrice, newPrice) -> {
        SwingUtilities.invokeLater(() -> {
            tradingPanel.updateStockTable();
            portfolioPanel.refreshDisplay();
        });
    });
    
    // Start UI update timer
    gameTimer = new Timer(5000, e -> {
        updateStatusBar();
        portfolioPanel.refreshDisplay();
        transactionPanel.updateTransactionTable();
    });
    gameTimer.start();
    
    // Start news timer
    newsTimer = new Timer(15000, e -> {
        generateNewsEvent();
    });
    newsTimer.start();
}
```

### 6.2 Buy Transaction Flow

**User Action Sequence:**
1. User selects stock row in JTable
2. User adjusts quantity with JSpinner
3. User clicks BUY button
4. ActionListener lambda executes

**TradingPanel.handleBuy():**
```java
private void handleBuy() {
    // 1. Validate selection
    int selectedRow = stockTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Select a stock to buy.");
        return;
    }
    
    // 2. Get selected stock and quantity
    Stock selectedStock = availableStocks.get(selectedRow);
    int quantity = (Integer) quantitySpinner.getValue();
    double totalCost = selectedStock.getCurrentPrice() * quantity;
    
    // 3. Validate funds
    if (totalCost > player.getCash()) {
        JOptionPane.showMessageDialog(this, "Insufficient funds.");
        return;
    }
    
    // 4. Confirm purchase
    int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Buy %d shares for $%.2f?", quantity, totalCost));
    
    if (confirm == JOptionPane.YES_OPTION) {
        // 5. Execute purchase
        if (player.buyStock(selectedStock, quantity)) {
            JOptionPane.showMessageDialog(this, "Purchase successful!");
            updateCashLabel();
            updateStockTable();
            parentFrame.getPortfolioPanel().refreshDisplay();
        }
    }
}
```

**Player.buyStock():**
```java
public boolean buyStock(Stock stock, int quantity) {
    double totalCost = stock.getCurrentPrice() * quantity;
    
    if (cash >= totalCost) {
        // Deduct cash
        cash -= totalCost;
        
        // Check if stock already owned
        Stock existingStock = findStockInPortfolio(stock.getSymbol());
        
        if (existingStock != null) {
            // Increase existing position
            existingStock.setQuantity(existingStock.getQuantity() + quantity);
        } else {
            // Add new position
            Stock newStock = createStockCopy(stock, quantity);
            portfolio.addItem(newStock);
        }
        
        // Record transaction
        Transaction transaction = new Transaction(
            "BUY",
            stock.getSymbol(),
            quantity,
            stock.getCurrentPrice(),
            totalCost,
            LocalDateTime.now()
        );
        transactionHistory.add(transaction);
        
        return true;
    }
    
    return false;
}
```

**createStockCopy():**
```java
private Stock createStockCopy(Stock stock, int quantity) {
    Stock copy;
    
    if (stock instanceof CommonStock) {
        CommonStock cs = (CommonStock) stock;
        copy = new CommonStock(cs.getSymbol(), cs.getCompanyName(), 
                cs.getCurrentPrice(), cs.getVolatilityIndex(), cs.getSector());
    } else if (stock instanceof PreferredStock) {
        PreferredStock ps = (PreferredStock) stock;
        copy = new PreferredStock(ps.getSymbol(), ps.getCompanyName(), 
                ps.getCurrentPrice(), ps.getFixedDividendRate(), ps.getParValue());
    } else if (stock instanceof Bond) {
        Bond bond = (Bond) stock;
        copy = new Bond(bond.getSymbol(), bond.getCompanyName(), 
                bond.getCurrentPrice(), bond.getCreditRating(), bond.getMaturityDate());
    } else {
        return null;
    }
    
    copy.setQuantity(quantity);
    return copy;
}
```

### 6.3 Price Update Flow

**Background Thread (StockPriceSimulator):**
```
While running:
    1. Calculate new prices for all stocks
    2. Update stock objects
    3. Store in price history
    4. Notify all listeners
    5. Sleep 3 seconds
```

**Listener Callback:**
```java
priceSimulator.addPriceUpdateListener((stock, oldPrice, newPrice) -> {
    // This executes on simulator thread
    
    // Schedule GUI update on EDT
    SwingUtilities.invokeLater(() -> {
        tradingPanel.updateStockTable();
        portfolioPanel.refreshDisplay();
    });
});
```

**GUI Update (on EDT):**
```java
public void updateStockTable() {
    for (int i = 0; i < availableStocks.size(); i++) {
        Stock stock = availableStocks.get(i);
        tableModel.setValueAt(String.format("$%.2f", stock.getCurrentPrice()), i, 3);
        tableModel.setValueAt(String.format("%.1f%%", stock.calculateRisk()), i, 4);
    }
}
```

### 6.4 News Event Flow

**Timer triggers every 15 seconds:**
```java
newsTimer = new Timer(15000, e -> {
    generateNewsEvent();
});
```

**Event Generation:**
```java
private void generateNewsEvent() {
    MarketNewsGenerator.NewsEvent event = newsGenerator.generateRandomNews();
    
    if (event != null) {
        // Apply price impact
        event.applyImpact();
        
        // Display in news panel
        newsPanel.addNews(event.getHeadline());
        
        // Show notification
        JOptionPane.showMessageDialog(this, 
                event.getHeadline(), 
                "Market News", 
                JOptionPane.INFORMATION_MESSAGE);
    }
}
```

**NewsEvent Implementation:**
```java
public static class NewsEvent {
    private String headline;
    private Stock affectedStock;
    private double priceImpact;
    private LocalDateTime timestamp;
    
    public NewsEvent(String headline, Stock stock, double impact) {
        this.headline = headline;
        this.affectedStock = stock;
        this.priceImpact = impact;
        this.timestamp = LocalDateTime.now();
    }
    
    public void applyImpact() {
        double currentPrice = affectedStock.getCurrentPrice();
        double newPrice = currentPrice * (1.0 + priceImpact);
        affectedStock.setCurrentPrice(newPrice);
    }
}
```

### 6.5 Portfolio Analysis Flow

**User clicks "Analysis" button:**
```java
analysisButton.addActionListener(e -> performAnalysis());
```

**Analysis Execution:**
```java
private void performAnalysis() {
    List<Stock> holdings = player.getPortfolio().getHoldings();
    
    if (holdings.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Portfolio is empty.");
        return;
    }
    
    // Create analyzer with bounded generic
    StockAnalyzer<Stock> analyzer = new StockAnalyzer<>();
    for (Stock stock : holdings) {
        analyzer.addStock(stock);
    }
    
    // Calculate metrics
    double totalValue = analyzer.calculateTotalValue();
    double avgRisk = analyzer.calculateWeightedAverageRisk();
    Stock highest = analyzer.findHighestValueStock();
    
    // Display results
    StringBuilder report = new StringBuilder();
    report.append("Portfolio Analysis\n");
    report.append("==================\n\n");
    report.append(String.format("Total Value: $%.2f\n", totalValue));
    report.append(String.format("Average Risk: %.2f%%\n", avgRisk));
    report.append(String.format("Highest Value: %s ($%.2f)\n", 
            highest.getSymbol(), highest.getTotalValue()));
    
    JOptionPane.showMessageDialog(this, report.toString(), 
            "Analysis Results", JOptionPane.INFORMATION_MESSAGE);
}
```

### 6.6 Filter Application Flow

**User selects filter from menu:**
```java
JMenuItem highValueItem = new JMenuItem("High Value Stocks");
highValueItem.addActionListener(e -> applyHighValueFilter());
```

**Filter Application:**
```java
private void applyHighValueFilter() {
    String input = JOptionPane.showInputDialog("Enter minimum value:");
    if (input == null) return;
    
    try {
        double threshold = Double.parseDouble(input);
        StockFilter<Stock> filter = new HighValueStockFilter(threshold);
        applyFilter(filter);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Invalid number.");
    }
}

private void applyFilter(StockFilter<Stock> filter) {
    List<Stock> filtered = new ArrayList<>();
    
    for (Stock stock : availableStocks) {
        if (filter.meetsCriteria(stock)) {
            filtered.add(stock);
        }
    }
    
    displayFilteredStocks(filtered, filter.getFilterDescription());
}
```

---

## 7. Data Types and Structures

### 7.1 Primitive Types

**int** - 32-bit signed integer
```java
int quantity = 100;
int selectedRow = stockTable.getSelectedRow();
```

**double** - 64-bit floating point
```java
double price = 175.50;
double totalCost = price * quantity;
double risk = stock.calculateRisk();
```

**boolean** - true/false value
```java
boolean running = true;
boolean isEmpty = portfolio.isEmpty();
```

**long** - 64-bit signed integer
```java
long timestamp = System.currentTimeMillis();
```

### 7.2 Wrapper Classes

**Integer** - Object wrapper for int
```java
Integer qty = (Integer) quantitySpinner.getValue();
```

**Double** - Object wrapper for double
```java
Double priceObj = 175.50;
```

**Auto-boxing and Unboxing:**
```java
int primitive = 10;
Integer wrapper = primitive;  // Auto-boxing
int back = wrapper;           // Unboxing
```

### 7.3 String

Immutable character sequence.

```java
String symbol = "AAPL";
String name = "Apple Inc.";

// String operations
boolean equals = symbol.equals("AAPL");
String upper = symbol.toUpperCase();
String formatted = String.format("$%.2f", price);
```

### 7.4 StringBuilder

Mutable character sequence for efficient string building.

```java
StringBuilder analysis = new StringBuilder();
analysis.append("Total Value: $");
analysis.append(totalValue);
analysis.append("\n");
String result = analysis.toString();
```

### 7.5 Arrays

Fixed-size sequences.

```java
String[] columnNames = {"Symbol", "Company", "Price", "Risk"};
Object[] row = new Object[]{"AAPL", "Apple Inc.", "$175.50", "65.0%"};
```

### 7.6 Generic Collections

**List&lt;T&gt; / ArrayList&lt;T&gt;:**
```java
List<Stock> availableStocks = new ArrayList<>();
List<Transaction> history = new ArrayList<>();
List<Double> priceHistory = new ArrayList<>();
```

**Map&lt;K,V&gt; / HashMap&lt;K,V&gt; / ConcurrentHashMap&lt;K,V&gt;:**
```java
Map<String, Stock> stockMap = new HashMap<>();
Map<String, List<Double>> priceHistory = new ConcurrentHashMap<>();
```

**CopyOnWriteArrayList&lt;T&gt;:**
```java
List<PriceUpdateListener> listeners = new CopyOnWriteArrayList<>();
```

### 7.7 Date/Time API

**LocalDateTime:**
```java
LocalDateTime timestamp = LocalDateTime.now();
LocalDateTime future = LocalDateTime.now().plusYears(10);
```

**LocalDate:**
```java
LocalDate maturityDate = LocalDate.now().plusYears(10);
```

**DateTimeFormatter:**
```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String formatted = timestamp.format(formatter);
```

### 7.8 Custom Classes

Domain-specific types defined in project:

```java
Stock stock;                    // Abstract supertype
CommonStock commonStock;        // Concrete subtype
PreferredStock preferredStock;  // Concrete subtype
Bond bond;                      // Concrete subtype

Player player;
Transaction transaction;
Portfolio<Stock> portfolio;
```

### 7.9 GUI Component Types

Swing framework types:

```java
JFrame frame;
JPanel panel;
JButton button;
JTable table;
JLabel label;
JSpinner spinner;
JMenuBar menuBar;
JMenuItem menuItem;
JTabbedPane tabbedPane;
JScrollPane scrollPane;
JTextArea textArea;
JProgressBar progressBar;
```

### 7.10 Interface Types

```java
StockFilter<Stock> filter;
Runnable simulator;
ActionListener listener;
PriceUpdateListener priceListener;
```

### 7.11 Generic Type Parameters

```java
<T>                    // Unbounded type parameter
<T extends Stock>      // Upper bounded type parameter
<K, V>                 // Multiple type parameters
```

---

## 8. Additional Java Concepts

### 8.1 Inner and Nested Classes

**Static Nested Class in MarketNewsGenerator.java:**
```java
public class MarketNewsGenerator {
    
    public static class NewsEvent {
        private String headline;
        private Stock affectedStock;
        private double priceImpact;
        private LocalDateTime timestamp;
        
        public NewsEvent(String headline, Stock stock, double impact) {
            this.headline = headline;
            this.affectedStock = stock;
            this.priceImpact = impact;
            this.timestamp = LocalDateTime.now();
        }
        
        public void applyImpact() {
            double currentPrice = affectedStock.getCurrentPrice();
            double newPrice = currentPrice * (1.0 + priceImpact);
            affectedStock.setCurrentPrice(newPrice);
        }
    }
}
```

Static nested class can be instantiated without outer class instance. Used for logical grouping when nested class is only relevant to outer class.

### 8.2 Lambda Expressions

Concise syntax for implementing functional interfaces (interfaces with single abstract method).

**Traditional Anonymous Inner Class:**
```java
buyButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        handleBuy();
    }
});
```

**Lambda Expression:**
```java
buyButton.addActionListener(e -> handleBuy());
```

**Multi-line Lambda:**
```java
gameTimer = new Timer(5000, e -> {
    updateStatusBar();
    portfolioPanel.refreshDisplay();
    transactionPanel.updateTransactionTable();
});
```

**Lambda with Multiple Parameters:**
```java
priceSimulator.addPriceUpdateListener((stock, oldPrice, newPrice) -> {
    SwingUtilities.invokeLater(() -> updateUI());
});
```

### 8.3 Exception Handling

**Try-Catch Block:**
```java
try {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
} catch (Exception e) {
    e.printStackTrace();
}
```

**Thread Interruption Handling:**
```java
try {
    Thread.sleep(3000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();  // Restore interrupted status
    break;
}
```

**Input Validation:**
```java
try {
    double threshold = Double.parseDouble(input);
    applyFilter(new HighValueStockFilter(threshold));
} catch (NumberFormatException e) {
    JOptionPane.showMessageDialog(this, "Invalid number format.");
}
```

### 8.4 Instanceof Operator and Type Casting

**Type Checking:**
```java
if (stock instanceof CommonStock) {
    CommonStock commonStock = (CommonStock) stock;
    String sector = commonStock.getSector();
}
```

**Usage in SectorStockFilter:**
```java
@Override
public boolean meetsCriteria(Stock stock) {
    if (stock instanceof CommonStock) {
        CommonStock cs = (CommonStock) stock;
        return cs.getSector().equalsIgnoreCase(targetSector);
    }
    return false;
}
```

### 8.5 Method Overriding

Subclass provides specific implementation of method declared in superclass.

```java
// In Stock.java
public abstract double calculateRisk();

// In CommonStock.java
@Override
public double calculateRisk() {
    return volatilityIndex;
}

// In PreferredStock.java
@Override
public double calculateRisk() {
    return 35.0;
}

// In Bond.java
@Override
public double calculateRisk() {
    switch(creditRating) {
        case "AAA": return 10.0;
        case "AA": return 20.0;
        default: return 50.0;
    }
}
```

The @Override annotation is optional but recommended. Compiler verifies method actually overrides superclass method.

---

## 9. Technical Presentation Guide

### Opening Statement

"This project is a multi-threaded stock trading simulator demonstrating comprehensive Java programming concepts. The application implements all four types of generics, full object-oriented design with inheritance and polymorphism, Swing GUI framework with event-driven architecture, and concurrent programming with proper thread safety mechanisms."

### Section 1: OOP Foundation (2 minutes)

"The domain model uses a three-level inheritance hierarchy. Stock is an abstract base class defining the contract for all stock types. CommonStock, PreferredStock, and Bond are concrete implementations, each providing their own calculateRisk() algorithm. This demonstrates polymorphism - I can store all types in List&lt;Stock&gt; and call methods uniformly, with the JVM dispatching to the correct implementation at runtime."

"All classes follow encapsulation principles. Fields are private with public accessor methods. For example, Player.cash is private, accessible only through getCash() and setCash(), with all modifications validated by business logic methods like buyStock() and sellStock()."

### Section 2: Generics Implementation (4 minutes)

"The project implements four categories of generics:"

"First, Portfolio&lt;T&gt; is a generic class with unbounded type parameter. The type parameter T is replaced at compile time with the actual type, providing compile-time type safety without explicit casting. Creating Portfolio&lt;Stock&gt; restricts the container to Stock objects only."

"Second, StockAnalyzer&lt;T extends Stock&gt; uses a bounded type parameter. The constraint 'T extends Stock' restricts T to Stock and its subclasses while allowing method calls on Stock-specific operations like getTotalValue() and calculateRisk() within the analyzer. This combines generics flexibility with inheritance specificity."

"Third, StockUtility contains generic methods where the type parameter is method-scoped, not class-scoped. Methods like displayCollectionDetails&lt;T&gt; work with any collection type through type inference."

"Fourth, StockFilter&lt;T&gt; is a generic interface implemented by four concrete filters - HighValueStockFilter, HighRiskStockFilter, LowRiskStockFilter, and SectorStockFilter. This implements the Strategy pattern with generics, allowing runtime algorithm swapping."

### Section 3: GUI Architecture (2 minutes)

"The user interface uses Swing components in a hierarchical structure. The main window extends JFrame and contains a JMenuBar for menu operations and a JTabbedPane with four panels - Trading, Portfolio, Transactions, and Market News."

"The Trading panel uses JTable with DefaultTableModel for stock listing, JSpinner for quantity selection, and JButton components with ActionListener for buy/sell operations. All event handlers use lambda expressions for concise implementation."

"Custom cell renderers provide color-coded risk visualization in the table - green for low risk, yellow for moderate, red for high. Layout management uses BorderLayout and FlowLayout for component positioning."

### Section 4: Multithreading (2 minutes)

"Real-time price simulation requires multithreading to prevent GUI blocking. StockPriceSimulator implements Runnable and executes on a separate daemon thread, updating prices every 3 seconds using a while loop with Thread.sleep()."

"Thread safety is ensured through three mechanisms: First, the volatile keyword on the running boolean ensures visibility across threads. Second, ConcurrentHashMap stores price history with internal synchronization. Third, SwingUtilities.invokeLater() schedules GUI updates on the Event Dispatch Thread, which is the only thread that can safely modify Swing components."

"Violating EDT requirements causes race conditions and unpredictable behavior. All background thread callbacks use invokeLater() to marshal GUI operations to the EDT."

### Section 5: Collections Framework (1 minute)

"The project uses multiple collection implementations. ArrayList provides dynamic arrays for stock lists and transaction history. HashMap and ConcurrentHashMap provide O(1) average-time key-value lookup. CopyOnWriteArrayList stores listener references - it creates a new copy on modification, making it safe for concurrent iteration without ConcurrentModificationException."

### Section 6: Control Flow (1 minute)

"Application startup begins in main() with SwingUtilities.invokeLater() to ensure GUI creation on EDT. The constructor initializes domain objects, creates GUI components, and starts background threads. Buy transactions flow through TradingPanel.handleBuy(), which validates selection and funds, then calls Player.buyStock() to execute the transaction, update portfolio, and record transaction history. Price updates occur on the simulator thread, notify listeners, and schedule GUI refreshes on EDT using invokeLater()."

### Closing Statement

"This application integrates 20+ Java concepts into a functional system. All four generic types are implemented with practical use cases. Complete OOP design with inheritance, polymorphism, and encapsulation. Professional GUI with 15+ Swing components. Multithreading with proper concurrency controls. The codebase demonstrates production-level Java programming patterns."

### Anticipated Questions and Responses

**Q: Why use generics instead of Object?**

"Using Object requires explicit casting and loses compile-time type safety. With Object, I could add a String to a stock portfolio and only discover the error at runtime with ClassCastException. Generics provide compile-time validation - the compiler rejects type mismatches before execution. Additionally, generics eliminate casting syntax: portfolio.get(0) returns T directly rather than requiring (Stock)portfolio.get(0)."

**Q: Explain bounded vs unbounded generics.**

"Portfolio&lt;T&gt; is unbounded - T can be any type, providing maximum flexibility but limiting operations to Object methods. StockAnalyzer&lt;T extends Stock&gt; is bounded - T must be Stock or subclass, allowing access to Stock-specific methods like calculateRisk() within generic code. The bound trades some flexibility for type-specific functionality."

**Q: How does thread safety work in your application?**

"Three mechanisms ensure thread safety: volatile boolean makes changes visible across threads without caching. ConcurrentHashMap provides lock-free reads and fine-grained write locking through lock striping. SwingUtilities.invokeLater() marshals GUI updates to the Event Dispatch Thread, preventing concurrent modification of Swing components which are not thread-safe."

**Q: Demonstrate polymorphism in the code.**

"In the initialization, I create CommonStock, PreferredStock, and Bond objects but store them in List&lt;Stock&gt;. When iterating and calling calculateRisk(), the JVM performs dynamic dispatch based on actual object type. CommonStock.calculateRisk() returns volatilityIndex, PreferredStock returns fixed 35.0, Bond returns value based on credit rating. Same method call, different implementations - runtime polymorphism."

**Q: Why this domain choice?**

"Stock market domain naturally requires different security types, demonstrating inheritance. Portfolio management requires type-safe generic containers. Real-time price updates justify multithreading. Filtering and analysis showcase bounded generics and interfaces. The domain provides practical context for demonstrating Java concepts in an integrated system rather than isolated examples."

### Key Technical Terms to Emphasize

- Compile-time type safety
- Runtime polymorphism / dynamic dispatch
- Bounded type parameter
- Thread-safe collections
- Event Dispatch Thread
- Lock striping
- Type inference
- Generic type erasure
- Method overriding
- Encapsulation
- Inheritance hierarchy
- Lambda expressions / functional interfaces

### Code Sections to Highlight

1. Stock.java and subclasses - inheritance and abstract methods
2. Portfolio.java - generic class implementation
3. StockAnalyzer.java - bounded generic with Stock methods
4. StockFilter.java and implementations - generic interface with Strategy pattern
5. StockPriceSimulator.run() - thread loop and price updates
6. SwingUtilities.invokeLater() calls - EDT safety
7. Player.buyStock() - business logic and transaction flow
8. Lambda expressions in event handlers

### Presentation Flow

1. Run application demonstrating functionality (30 seconds)
2. Explain OOP structure with inheritance diagram (2 minutes)
3. Show all four generic implementations with code (4 minutes)
4. Demonstrate GUI components and event handling (2 minutes)
5. Explain multithreading and thread safety (2 minutes)
6. Show control flow from user action to database update (1 minute)
7. Summary of integrated concepts (30 seconds)

Total: approximately 12 minutes with 3 minutes buffer for questions.

---

## 10. Summary

This stock market simulator demonstrates professional Java development practices through integrated implementation of core language features. The generic system provides compile-time type safety across four different patterns. The OOP design uses inheritance, polymorphism, and encapsulation throughout the domain model. The Swing GUI implements event-driven architecture with proper separation of concerns. Multithreading enables real-time simulation without blocking user interaction, with proper thread safety mechanisms ensuring data integrity. The Collections Framework provides efficient data structures for various use cases. Together, these components create a functional application suitable for demonstrating comprehensive Java knowledge.
