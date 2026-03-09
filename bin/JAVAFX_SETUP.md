# Stock Market Game - JavaFX Version Setup Guide

## 🎨 Modern GUI with JavaFX

Your Stock Market Game has been converted to **JavaFX** for a sleek, modern user interface!

## 📋 Prerequisites

### 1. JavaFX SDK
JavaFX is not included in standard JDK (11+). You need to download it separately:

**Download JavaFX:**
- Visit: https://gluonhq.com/products/javafx/
- Download JavaFX SDK for your platform
- Extract to a location (e.g., `C:\javafx-sdk-21`)

### 2. Set Environment Variable (Recommended)
```cmd
setx PATH_TO_FX "C:\javafx-sdk-21\lib"
```
Replace with your actual JavaFX lib path.

### 3. MySQL Connector JAR
Already in your project folder: `mysql-connector-j-9.4.0.jar` ✓

## 🚀 How to Run

### Method 1: Using Batch File (After Setting PATH_TO_FX)
```cmd
run_game_fx.bat
```

### Method 2: VS Code (Recommended)
VS Code with Java Extension Pack should automatically detect JavaFX.
Just click Run!

### Method 3: Manual Command
```cmd
# Compile
javac --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls ^
  -cp ".;mysql-connector-j-9.4.0.jar" ^
  StockMarketGameFX.java TradingPanelFX.java PortfolioPanelFX.java ^
  NewsPanelFX.java TransactionHistoryPanelFX.java

# Run
java --module-path "C:\javafx-sdk-21\lib" --add-modules javafx.controls ^
  -cp ".;mysql-connector-j-9.4.0.jar" StockMarketGameFX
```

## 📁 New Files Created

### JavaFX Application Files:
- `StockMarketGameFX.java` - Main JavaFX application
- `TradingPanelFX.java` - Trading panel with modern controls
- `PortfolioPanelFX.java` - Portfolio display with progress bars
- `NewsPanelFX.java` - News panel
- `TransactionHistoryPanelFX.java` - Transaction history table
- `style.css` - Modern stylesheet
- `run_game_fx.bat` - Launch script

### Original Swing Files (Still Available):
- `StockMarketGame.java` - Original Swing version
- `TradingPanel.java`
- `PortfolioPanel.java`
- `NewsPanel.java`
- `TransactionHistoryPanel.java`

## ✨ JavaFX Improvements

### 1. **Modern Look & Feel**
- Clean, professional interface
- Smooth animations
- Better color schemes
- Gradient effects

### 2. **Better Controls**
- Improved table views with sorting
- Modern dialogs and alerts
- Progress bars with smooth animation
- Responsive buttons

### 3. **Enhanced User Experience**
- Cleaner layouts
- Better spacing and padding
- Consistent styling
- Professional appearance

### 4. **Performance**
- Hardware-accelerated rendering
- Smoother updates
- Better memory management

## 🎯 Features (All Retained)

✓ All Swing functionality preserved
✓ Database integration working
✓ Auto-save on trades and exit
✓ Load previous session
✓ Multiple player profiles
✓ Real-time price updates
✓ Market news system
✓ Portfolio analytics
✓ Transaction history

## 🎨 Customization

### Edit Colors/Styles
Modify `style.css` to change:
- Colors
- Fonts
- Button styles
- Table appearance
- Dialog designs

Example:
```css
.button {
    -fx-background-color: #4CAF50;
    -fx-text-fill: white;
}
```

## 📊 Comparison: Swing vs JavaFX

| Feature | Swing | JavaFX |
|---------|-------|--------|
| Look & Feel | Basic | Modern |
| Animations | Limited | Smooth |
| Styling | Code-based | CSS-based |
| Graphics | Software | Hardware-accelerated |
| Learning Curve | Easy | Moderate |
| Future Support | Maintenance | Active Development |

## 🔧 Troubleshooting

### Problem: "JavaFX runtime components are missing"
**Solution:** 
1. Download JavaFX SDK from Gluon
2. Set PATH_TO_FX environment variable
3. Use --module-path in compile/run commands

### Problem: ClassNotFoundException for JavaFX
**Solution:** 
Make sure --add-modules javafx.controls is included in both compile and run commands

### Problem: UnsupportedClassVersionError
**Solution:** 
Ensure your JDK version matches JavaFX version (both should be 11+ or 17+)

### Problem: Module not found errors
**Solution:**
```cmd
# Use explicit module path
--module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls
```

### Problem: Database connection issues
**Solution:** 
Same as before - ensure MySQL is running and credentials are correct

## 💡 VS Code Setup

### settings.json (in .vscode folder):
```json
{
    "java.project.referencedLibraries": [
        "lib/**/*.jar",
        "mysql-connector-j-9.4.0.jar"
    ],
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-17",
            "path": "C:\\Program Files\\Java\\jdk-17",
            "default": true
        }
    ]
}
```

### launch.json:
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "StockMarketGameFX",
            "request": "launch",
            "mainClass": "StockMarketGameFX",
            "vmArgs": "--module-path \"${env:PATH_TO_FX}\" --add-modules javafx.controls",
            "classpath": [
                "${workspaceFolder}",
                "${workspaceFolder}/mysql-connector-j-9.4.0.jar"
            ]
        }
    ]
}
```

## 🎮 Quick Start

1. **Set PATH_TO_FX:**
   ```cmd
   setx PATH_TO_FX "C:\javafx-sdk-21\lib"
   ```

2. **Restart VS Code**

3. **Run the game:**
   - Press F5 in VS Code
   - Or double-click `run_game_fx.bat`

## 📸 What to Expect

### Modern UI Features:
- ✨ Smooth table selection and highlighting
- 🎨 Color-coded risk levels (green/yellow/red)
- 📊 Animated progress bars for levels
- 🎯 Clean, professional dialogs
- 💫 Better typography and spacing
- 🖼️ CSS-based styling (easy to customize)

## ⚡ Performance Tips

1. JavaFX uses GPU acceleration - ensure graphics drivers are updated
2. Tables update smoothly with ObservableList
3. Timeline animations are efficient
4. Memory usage is optimized

## 🎉 You're Ready!

Both versions are available:
- **Swing Version:** Run `run_game.bat` (classic look)
- **JavaFX Version:** Run `run_game_fx.bat` (modern look)

**Recommended:** Use JavaFX version for the best visual experience!

---

**Note:** All database features work identically in both versions. Your saved games are compatible with both!
