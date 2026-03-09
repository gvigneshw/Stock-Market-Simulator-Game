@echo off
echo ================================================
echo   Stock Market Game - JavaFX Version
echo   Modern GUI with Database Integration
echo ================================================
echo.
echo Compiling JavaFX version...
javac --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -cp ".;mysql-connector-j-9.4.0.jar" StockMarketGameFX.java TradingPanelFX.java PortfolioPanelFX.java NewsPanelFX.java TransactionHistoryPanelFX.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    echo.
    echo If JavaFX modules not found, set PATH_TO_FX environment variable
    echo or download JavaFX SDK from: https://gluonhq.com/products/javafx/
    pause
    exit /b 1
)
echo Compilation successful!
echo.
echo Starting JavaFX game...
echo.
java --module-path "%PATH_TO_FX%" --add-modules javafx.controls,javafx.fxml -cp ".;mysql-connector-j-9.4.0.jar" StockMarketGameFX
