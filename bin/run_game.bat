@echo off
echo ================================================
echo   Stock Market Game with Database Integration
echo ================================================
echo.
echo Compiling Swing version...
javac -cp ".;mysql-connector-j-9.4.0.jar" Bond.java CommonStock.java DatabaseManager.java HighRiskStockFilter.java HighValueStockFilter.java LowRiskStockFilter.java MarketNewsGenerator.java NewsPanel.java Player.java Portfolio.java PortfolioPanel.java PreferredStock.java SectorStockFilter.java Stock.java StockAnalyzer.java StockFilter.java StockMarketGame.java StockPriceSimulator.java StockUtility.java TradingPanel.java Transaction.java TransactionHistoryPanel.java
if errorlevel 1 (
    echo.
    echo [ERROR] Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful!
echo.
echo Starting game...
echo.
java -cp ".;mysql-connector-j-9.4.0.jar" StockMarketGame
pause
