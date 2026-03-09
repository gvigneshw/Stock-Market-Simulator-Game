import javax.swing.*;
import java.awt.*;
import java.util.List;

// News panel displaying market events
public class NewsPanel extends JPanel {
    private JTextArea newsArea;
    private MarketNewsGenerator newsGenerator;
    
    public NewsPanel(MarketNewsGenerator newsGenerator) {
        this.newsGenerator = newsGenerator;
        
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("📰 Market News & Events"));
        
        newsArea = new JTextArea();
        newsArea.setEditable(false);
        newsArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        newsArea.setLineWrap(true);
        newsArea.setWrapStyleWord(true);
        newsArea.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(newsArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        add(scrollPane, BorderLayout.CENTER);
        
        updateNews();
    }
    
    public void updateNews() {
        StringBuilder sb = new StringBuilder();
        List<MarketNewsGenerator.NewsEvent> recentNews = newsGenerator.getRecentNews();
        
        if (recentNews.isEmpty()) {
            sb.append("No news yet. Market is calm...\n");
        } else {
            for (int i = recentNews.size() - 1; i >= 0; i--) {
                MarketNewsGenerator.NewsEvent event = recentNews.get(i);
                sb.append("• ").append(event.toString()).append("\n\n");
            }
        }
        
        newsArea.setText(sb.toString());
        newsArea.setCaretPosition(0);
    }
    
    public void addNewsEvent(MarketNewsGenerator.NewsEvent event) {
        updateNews();
    }
}
