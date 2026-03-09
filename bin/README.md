# 🎮 Stock Market Simulator - Java GUI Game

## Quick Start

### Run the Game
```bash
java StockMarketGame
```

That's it! The game window will open.

### If You Need to Recompile
```bash
javac *.java
java StockMarketGame
```

---

## 📋 What This Is

A **fully functional Windows-style GUI game** demonstrating:
- ✅ **Java Generics** (Generic Classes, Bounded Generics, Generic Methods, Generic Interfaces)
- ✅ **OOP** (Inheritance, Polymorphism, Encapsulation, Abstraction)
- ✅ **GUI Programming** (Swing components, Event handling)
- ✅ **Multithreading** (Real-time price simulation)
- ✅ **Collections Framework** (Lists, Maps, Iterators)
- ✅ **And 15+ more Java concepts**

---

## 🎮 How to Play

1. **Start with $100,000** in cash
2. **Trade stocks** - Buy low, sell high!
3. **Watch prices** update in real-time (every 3 seconds)
4. **Read market news** that affects stock prices
5. **Level up** by growing your net worth
6. **Goal**: Maximize your profit!

### Game Controls
- **Trading Tab**: Select stock → Set quantity → Click BUY or SELL
- **Portfolio Tab**: View your holdings and statistics
- **Transactions Tab**: See complete trade history
- **Menu → Analysis**: Use Portfolio Analysis and Stock Filters

---

## 📊 Available Stocks

- **Common Stocks** (10): AAPL, MSFT, GOOGL, NVDA, TSLA, F, JPM, BAC, JNJ, PFE
- **Preferred Stocks** (3): VZ-P, BAC-P, T-P
- **Bonds** (3): T-10Y, CORP-5Y, MUNI-7Y

**Color Codes:**
- 🟢 Green = Low Risk (Safe)
- 🟡 Yellow = Medium Risk
- 🔴 Red = High Risk (Volatile)

---

## 🎯 Generics Implementation (For Teacher)

### 1. Generic Class
**File**: `Portfolio.java`
```java
public class Portfolio<T> {
    private List<T> holdings;
    // Manages any type collection
}
```

### 2. Bounded Generic Class + Inheritance
**Hierarchy**: `Stock` → `CommonStock`, `PreferredStock`, `Bond`  
**File**: `StockAnalyzer.java`
```java
public class StockAnalyzer<T extends Stock> {
    // Only works with Stock types
}
```

### 3. Generic Methods
**File**: `StockUtility.java`
```java
public static <T> void displayCollectionDetails(Collection<T> collection)
public static <T extends Stock> void displayStockMetrics(T stock)
```

### 4. Generic Interface
**File**: `StockFilter.java`
```java
public interface StockFilter<T> {
    boolean meetsCriteria(T item);
}
```
**Implementations**: 4 different filters (HighValue, HighRisk, LowRisk, Sector)

### 5. Comprehensive Demonstration
**Main**: `StockMarketGame.java` - Full GUI game demonstrating all concepts

---

## 📁 Project Files (22 Java Files)

### Core Domain (Inheritance)
- `Stock.java` - Abstract base class
- `CommonStock.java`, `PreferredStock.java`, `Bond.java` - Subclasses

### Generics (⭐ Main Focus)
- `Portfolio.java` - Generic class
- `StockAnalyzer.java` - Bounded generic
- `StockUtility.java` - Generic methods
- `StockFilter.java` + 4 implementations - Generic interface

### Game System
- `Player.java` - Account management
- `Transaction.java` - Trade records
- `StockPriceSimulator.java` - Real-time updates (Threading)
- `MarketNewsGenerator.java` - Market events

### GUI Components
- `StockMarketGame.java` - Main window
- `TradingPanel.java` - Buy/sell interface
- `PortfolioPanel.java` - Portfolio viewer
- `NewsPanel.java` - News feed
- `TransactionHistoryPanel.java` - Transaction log

---

## 🎨 Features

- ✅ Real-time price updates (every 3 seconds)
- ✅ Market news events (every 15 seconds)
- ✅ Level up system (every $100k net worth)
- ✅ Color-coded risk levels
- ✅ Portfolio analysis with generic StockAnalyzer
- ✅ Stock filtering with generic StockFilter
- ✅ Complete transaction history
- ✅ Professional Windows-style GUI

---

## 🏆 Why This Project Stands Out

✨ **Not just examples** → Full playable game  
✨ **Not just console** → Professional GUI  
✨ **Not just generics** → ALL Java concepts  
✨ **Not just homework** → Portfolio-quality project  

**All requirements exceeded!** 🎊

---

## 📖 Documentation

- **README_GAME.md** - Complete detailed documentation
- **QUICK_START.txt** - Quick reference guide
- **This file** - Overview and quick start

---

## 🎓 Educational Value

Perfect for demonstrating:
- Complete understanding of Java Generics
- Professional GUI programming
- Multithreading in practice
- Real-world application architecture
- Clean, maintainable code

---

## ✅ Requirements Checklist

- [x] Generic class managing collections
- [x] Inheritance + Bounded generic class
- [x] Generic methods
- [x] Generic interface
- [x] Comprehensive demonstration
- [x] **BONUS**: Full GUI game with advanced features!

---

## 💡 Tips for Playing

1. **Start Safe**: Buy some bonds (low risk, stable)
2. **Diversify**: Don't put all money in one stock
3. **Watch News**: Events affect prices
4. **Buy Low, Sell High**: Classic strategy
5. **Use Filters**: Menu → Analysis → Filter Stocks
6. **Check Portfolio**: Monitor your progress

---

## 🎊 Enjoy the Game!

**Have fun trading and good luck with your lab!** 📈💰

---

*Created for Java Programming Lab - Stock Market Simulator Domain*  
*Demonstrating OOP, Generics, GUI, Threads, and more!*
