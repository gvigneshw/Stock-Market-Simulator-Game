@echo off
echo ================================================
echo   StockSim - Modern Stock Market Simulator
echo   JavaFX + MySQL Database Integration
echo ================================================
echo.

REM Set JavaFX path (adjust if needed)
if not defined PATH_TO_FX set PATH_TO_FX=C:\Users\vicky\Downloads\javafx-sdk-25.0.1\lib

echo Compiling...
javac --module-path "%PATH_TO_FX%" --add-modules javafx.controls -cp ".;mysql-connector-j-9.4.0.jar" *.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed!
    echo Set PATH_TO_FX to your JavaFX SDK lib folder.
    pause
    exit /b 1
)
echo Compilation successful!
echo.
echo Launching StockSim...
java --module-path "%PATH_TO_FX%" --add-modules javafx.controls -cp ".;mysql-connector-j-9.4.0.jar" StockMarketGameFX
pause
