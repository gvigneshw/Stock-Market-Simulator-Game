import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

// Trading panel for buying and selling stocks
public class TradingPanel extends JPanel {
    private JTable stockTable;
    private DefaultTableModel tableModel;
    private List<Stock> availableStocks;
    private Player player;
    private JLabel cashLabel;
    private JButton buyButton;
    private JButton sellButton;
    private JSpinner quantitySpinner;
    
    public TradingPanel(List<Stock> stocks, Player player) {
        this.availableStocks = stocks;
        this.player = player;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with cash display
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(240, 248, 255));
        cashLabel = new JLabel();
        cashLabel.setFont(new Font("Arial", Font.BOLD, 16));
        updateCashLabel();
        topPanel.add(cashLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with stock table
        createStockTable();
        JScrollPane scrollPane = new JScrollPane(stockTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with trading controls
        JPanel bottomPanel = createTradingControlPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void createStockTable() {
        String[] columnNames = {"Symbol", "Company", "Type", "Price", "Sector", "Risk %", "Owned"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        stockTable = new JTable(tableModel);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        stockTable.setRowHeight(25);
        stockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        stockTable.setFont(new Font("Arial", Font.PLAIN, 11));
        
        // Color code rows by risk
        stockTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String riskStr = (String) table.getValueAt(row, 5);
                    double risk = Double.parseDouble(riskStr);
                    
                    if (risk < 30) {
                        c.setBackground(new Color(200, 255, 200)); // Light green
                    } else if (risk < 60) {
                        c.setBackground(new Color(255, 255, 200)); // Light yellow
                    } else {
                        c.setBackground(new Color(255, 200, 200)); // Light red
                    }
                }
                
                return c;
            }
        });
        
        updateStockTable();
    }
    
    private JPanel createTradingControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(240, 248, 255));
        
        // Quantity spinner
        panel.add(new JLabel("Quantity:"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        quantitySpinner.setPreferredSize(new Dimension(100, 30));
        panel.add(quantitySpinner);
        
        // Buy button
        buyButton = new JButton("BUY");
        buyButton.setBackground(new Color(76, 175, 80));
        buyButton.setForeground(Color.WHITE);
        buyButton.setFocusPainted(false);
        buyButton.setFont(new Font("Arial", Font.BOLD, 14));
        buyButton.addActionListener(e -> handleBuy());
        panel.add(buyButton);
        
        // Sell button
        sellButton = new JButton("SELL");
        sellButton.setBackground(new Color(244, 67, 54));
        sellButton.setForeground(Color.WHITE);
        sellButton.setFocusPainted(false);
        sellButton.setFont(new Font("Arial", Font.BOLD, 14));
        sellButton.addActionListener(e -> handleSell());
        panel.add(sellButton);
        
        // Refresh button
        JButton refreshButton = new JButton("↻ Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.addActionListener(e -> updateStockTable());
        panel.add(refreshButton);
        
        return panel;
    }
    
    private void handleBuy() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a stock to buy!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Stock selectedStock = availableStocks.get(selectedRow);
        int quantity = (Integer) quantitySpinner.getValue();
        double totalCost = selectedStock.getCurrentPrice() * quantity;
        
        if (totalCost > player.getCash()) {
            JOptionPane.showMessageDialog(this, 
                String.format("Insufficient funds!\nNeed: $%.2f\nHave: $%.2f", 
                    totalCost, player.getCash()),
                "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Buy %d shares of %s (%s) for $%.2f?",
                quantity, selectedStock.getCompanyName(), selectedStock.getSymbol(), totalCost),
            "Confirm Purchase", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (player.buyStock(selectedStock, quantity)) {
                JOptionPane.showMessageDialog(this, 
                    "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateCashLabel();
                updateStockTable();
            }
        }
    }
    
    private void handleSell() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a stock to sell!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Stock selectedStock = availableStocks.get(selectedRow);
        int quantity = (Integer) quantitySpinner.getValue();
        
        // Check owned quantity
        Stock ownedStock = null;
        for (Stock s : player.getPortfolio().getHoldings()) {
            if (s.getSymbol().equals(selectedStock.getSymbol())) {
                ownedStock = s;
                break;
            }
        }
        
        if (ownedStock == null || ownedStock.getQuantity() < quantity) {
            JOptionPane.showMessageDialog(this, 
                String.format("You don't own enough shares!\nOwned: %d\nTrying to sell: %d",
                    ownedStock != null ? ownedStock.getQuantity() : 0, quantity),
                "Insufficient Shares", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double totalRevenue = selectedStock.getCurrentPrice() * quantity;
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Sell %d shares of %s (%s) for $%.2f?",
                quantity, selectedStock.getCompanyName(), selectedStock.getSymbol(), totalRevenue),
            "Confirm Sale", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (player.sellStock(selectedStock, quantity)) {
                JOptionPane.showMessageDialog(this, 
                    "Sale successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateCashLabel();
                updateStockTable();
            }
        }
    }
    
    public void updateStockTable() {
        tableModel.setRowCount(0);
        
        for (Stock stock : availableStocks) {
            int ownedQty = 0;
            for (Stock s : player.getPortfolio().getHoldings()) {
                if (s.getSymbol().equals(stock.getSymbol())) {
                    ownedQty = s.getQuantity();
                    break;
                }
            }
            
            Object[] row = {
                stock.getSymbol(),
                stock.getCompanyName(),
                stock.getStockType(),
                String.format("$%.2f", stock.getCurrentPrice()),
                stock.getSector(),
                String.format("%.1f", stock.calculateRisk()),
                ownedQty
            };
            tableModel.addRow(row);
        }
    }
    
    public void updateCashLabel() {
        cashLabel.setText(String.format("💰 Available Cash: $%.2f", player.getCash()));
    }
}
