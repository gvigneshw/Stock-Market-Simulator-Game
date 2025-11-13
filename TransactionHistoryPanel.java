import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Transaction history panel
public class TransactionHistoryPanel extends JPanel {
    private Player player;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    
    public TransactionHistoryPanel(Player player) {
        this.player = player;
        
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("📜 Transaction History"));
        
        createTransactionTable();
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> updateTransactionTable());
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void createTransactionTable() {
        String[] columnNames = {"Time", "Type", "Symbol", "Quantity", "Price", "Total Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(25);
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 10));
        
        updateTransactionTable();
    }
    
    public void updateTransactionTable() {
        tableModel.setRowCount(0);
        
        List<Transaction> history = player.getTransactionHistory();
        
        if (history.isEmpty()) {
            Object[] row = {"—", "No transactions yet", "—", "—", "—", "—"};
            tableModel.addRow(row);
        } else {
            // Show most recent first
            for (int i = history.size() - 1; i >= 0; i--) {
                Transaction t = history.get(i);
                Object[] row = {
                    t.getFormattedTimestamp(),
                    t.getType(),
                    t.getSymbol(),
                    t.getQuantity(),
                    String.format("$%.2f", t.getPrice()),
                    String.format("$%.2f", t.getTotalAmount())
                };
                tableModel.addRow(row);
            }
        }
    }
}
