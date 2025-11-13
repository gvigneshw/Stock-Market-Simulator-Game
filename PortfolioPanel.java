import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Portfolio panel displaying user's holdings and statistics
public class PortfolioPanel extends JPanel {
    private Player player;
    private JTable portfolioTable;
    private DefaultTableModel tableModel;
    private JLabel netWorthLabel;
    private JLabel profitLabel;
    private JLabel levelLabel;
    private JProgressBar levelProgressBar;
    
    public PortfolioPanel(Player player) {
        this.player = player;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with statistics
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.NORTH);
        
        // Center panel with portfolio table
        createPortfolioTable();
        JScrollPane scrollPane = new JScrollPane(portfolioTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with analysis
        JPanel analysisPanel = createAnalysisPanel();
        add(analysisPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createTitledBorder("Account Statistics"));
        
        // Net worth
        netWorthLabel = new JLabel();
        netWorthLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(netWorthLabel);
        
        // Profit/Loss
        profitLabel = new JLabel();
        profitLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(profitLabel);
        
        // Level
        JPanel levelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        levelPanel.setBackground(new Color(245, 245, 245));
        levelLabel = new JLabel();
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        levelProgressBar = new JProgressBar(0, 100);
        levelProgressBar.setPreferredSize(new Dimension(200, 20));
        levelProgressBar.setStringPainted(true);
        levelPanel.add(levelLabel);
        levelPanel.add(levelProgressBar);
        panel.add(levelPanel);
        
        updateStats();
        
        return panel;
    }
    
    private void createPortfolioTable() {
        String[] columnNames = {"Symbol", "Company", "Type", "Quantity", "Avg Price", 
                                "Current Price", "Total Value", "P/L %", "Risk %"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        portfolioTable = new JTable(tableModel);
        portfolioTable.setRowHeight(30);
        portfolioTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        portfolioTable.setFont(new Font("Arial", Font.PLAIN, 11));
        
        updatePortfolioTable();
    }
    
    private JPanel createAnalysisPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(BorderFactory.createTitledBorder("Portfolio Analysis"));
        
        // Use generic StockAnalyzer
        StockAnalyzer<Stock> analyzer = new StockAnalyzer<>(player.getPortfolio().getHoldings());
        
        // Total Value Card
        JPanel valueCard = createInfoCard("Total Portfolio Value", 
            String.format("$%.2f", analyzer.calculateTotalValue()), new Color(76, 175, 80));
        panel.add(valueCard);
        
        // Average Risk Card
        JPanel riskCard = createInfoCard("Weighted Avg Risk", 
            String.format("%.2f%%", analyzer.calculateWeightedAverageRisk()), new Color(255, 152, 0));
        panel.add(riskCard);
        
        // Holdings Count Card
        JPanel holdingsCard = createInfoCard("Total Holdings", 
            String.valueOf(player.getPortfolio().size()), new Color(33, 150, 243));
        panel.add(holdingsCard);
        
        return panel;
    }
    
    private JPanel createInfoCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createLineBorder(color.darker(), 2));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    public void updatePortfolioTable() {
        tableModel.setRowCount(0);
        
        List<Stock> holdings = player.getPortfolio().getHoldings();
        
        if (holdings.isEmpty()) {
            // Show empty message
            Object[] row = {"—", "No holdings yet", "—", "—", "—", "—", "—", "—", "—"};
            tableModel.addRow(row);
        } else {
            for (Stock stock : holdings) {
                // Calculate average purchase price (simplified - using current price as approximation)
                double avgPrice = stock.getCurrentPrice(); // In real app, track purchase prices
                double currentPrice = stock.getCurrentPrice();
                double profitLossPercent = 0.0; // Simplified
                
                Object[] row = {
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    stock.getStockType(),
                    stock.getQuantity(),
                    String.format("$%.2f", avgPrice),
                    String.format("$%.2f", currentPrice),
                    String.format("$%.2f", stock.getTotalValue()),
                    String.format("%.2f%%", profitLossPercent),
                    String.format("%.1f%%", stock.calculateRisk())
                };
                tableModel.addRow(row);
            }
        }
    }
    
    public void updateStats() {
        player.checkLevelUp();
        player.calculateTotalProfit();
        
        netWorthLabel.setText(String.format("💼 Net Worth: $%.2f (Cash: $%.2f + Portfolio: $%.2f)", 
            player.getNetWorth(), player.getCash(), player.getPortfolioValue()));
        
        double profit = player.getTotalProfit();
        double profitPercent = player.getProfitPercentage();
        String profitText = String.format("📈 Total Profit/Loss: $%.2f (%+.2f%%)", profit, profitPercent);
        
        profitLabel.setText(profitText);
        if (profit >= 0) {
            profitLabel.setForeground(new Color(76, 175, 80));
        } else {
            profitLabel.setForeground(new Color(244, 67, 54));
        }
        
        levelLabel.setText(String.format("⭐ Level %d", player.getLevel()));
        
        // Progress to next level
        int currentLevelThreshold = (player.getLevel() - 1) * 100000;
        int nextLevelThreshold = player.getLevel() * 100000;
        int progress = (int)(((player.getNetWorth() - currentLevelThreshold) / 
                             (nextLevelThreshold - currentLevelThreshold)) * 100);
        levelProgressBar.setValue(Math.max(0, Math.min(100, progress)));
        levelProgressBar.setString(String.format("%.0f%% to Level %d", 
            (double)progress, player.getLevel() + 1));
    }
    
    public void refreshDisplay() {
        updatePortfolioTable();
        updateStats();
    }
}
