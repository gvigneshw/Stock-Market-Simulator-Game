# Stock Market Game - Database Setup Guide

## Prerequisites
1. **MySQL Server** installed and running (MySQL 8.0+ recommended)
2. **MySQL Connector/J JAR** file (already added: `mysql-connector-j-9.4.0.jar`)
3. MySQL root access with password: `Vignesh#1234567890`

## Setup Steps

### 1. Create Database (Optional - Auto-created)
The application will automatically create the database and tables when you first run it.

However, if you want to set it up manually:

```sql
-- Login to MySQL
mysql -u root -p
-- Enter password: Vignesh#1234567890

-- Create database
CREATE DATABASE IF NOT EXISTS stock_market;
USE stock_market;

-- Run the schema.sql file
source schema.sql;
```

Or execute from command line:
```bash
mysql -u root -p stock_market < schema.sql
```

### 2. Configure Classpath for JDBC Driver

**Option A: Compile with classpath**
```bash
javac -cp ".;mysql-connector-j-9.4.0.jar" *.java
java -cp ".;mysql-connector-j-9.4.0.jar" StockMarketGame
```

**Option B: Using VS Code (Recommended)**
The JAR file should be in your project folder. VS Code should automatically detect it.
If not, add it to your Java Project settings:
1. Open Command Palette (Ctrl+Shift+P)
2. Type "Java: Configure Classpath"
3. Add the mysql-connector JAR file

### 3. Database Configuration

Edit `DatabaseManager.java` if needed to change credentials:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/stock_market";
private static final String DB_USER = "root";
private static final String DB_PASSWORD = "Vignesh#1234567890";
```

## Database Schema

### Tables Created:

1. **players** - Stores player account information
   - player_id (Primary Key)
   - name (Unique)
   - cash
   - initial_cash
   - level
   - total_profit
   - timestamps

2. **portfolio** - Stores player's stock holdings
   - portfolio_id (Primary Key)
   - player_id (Foreign Key)
   - stock_symbol
   - stock_name
   - quantity
   - purchase_price
   - stock_type
   - sector

3. **transactions** - Records all buy/sell transactions
   - transaction_id (Primary Key)
   - player_id (Foreign Key)
   - transaction_type (BUY/SELL)
   - stock_symbol
   - quantity
   - price
   - total_amount
   - transaction_date

## Features Implemented

✓ **Automatic database initialization** - Creates tables on first run
✓ **Player persistence** - Save and load player data
✓ **Portfolio persistence** - Save and load stock holdings
✓ **Transaction history** - All trades are recorded
✓ **CRUD operations** - Full database management
✓ **Connection pooling** - Efficient database connections
✓ **Error handling** - Comprehensive exception management

## Usage in Application

The `DatabaseManager` class provides these key methods:

```java
// Save player data
int playerId = dbManager.savePlayer(player);

// Load player data
Player player = dbManager.loadPlayer("PlayerName");

// Save portfolio
dbManager.savePortfolio(playerId, portfolio.getHoldings());

// Load portfolio
List<Stock> holdings = dbManager.loadPortfolio(playerId);

// Save transaction
dbManager.saveTransaction(playerId, transaction);

// Load transactions
List<Transaction> history = dbManager.loadTransactions(playerId);

// Close connection when done
dbManager.closeConnection();
```

## Testing Database Connection

Run this simple test:

```java
public class TestDatabase {
    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        System.out.println("Database connection test completed!");
        db.closeConnection();
    }
}
```

Compile and run:
```bash
javac -cp ".;mysql-connector-j-9.4.0.jar" TestDatabase.java DatabaseManager.java
java -cp ".;mysql-connector-j-9.4.0.jar" TestDatabase
```

## Troubleshooting

### Connection Failed Error
- Check if MySQL server is running
- Verify username and password
- Ensure database 'stock_market' exists
- Check firewall settings (port 3306)

### Driver Not Found Error
- Ensure mysql-connector JAR is in the project folder
- Add JAR to classpath when compiling/running
- Verify JAR file is not corrupted

### Table Creation Failed
- Check MySQL user permissions
- Ensure user has CREATE TABLE privileges
- Run schema.sql manually if auto-creation fails

## Useful MySQL Commands

```sql
-- View all tables
SHOW TABLES;

-- View table structure
DESCRIBE players;
DESCRIBE portfolio;
DESCRIBE transactions;

-- View all players
SELECT * FROM players;

-- View player statistics
SELECT * FROM player_statistics;

-- Clear all data (careful!)
TRUNCATE TABLE transactions;
TRUNCATE TABLE portfolio;
TRUNCATE TABLE players;

-- Drop database (if needed)
DROP DATABASE stock_market;
```

## Next Steps

To integrate database functionality into your game:
1. Add DatabaseManager instance to StockMarketGame
2. Call savePlayer() after game updates
3. Call savePortfolio() after buy/sell operations
4. Call saveTransaction() after each trade
5. Add "Load Game" option to load existing player data

The database will automatically persist all game progress!
