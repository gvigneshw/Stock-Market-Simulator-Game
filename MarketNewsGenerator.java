import java.util.*;

// Market news and events system affecting stock prices
public class MarketNewsGenerator {
    private Random random;
    private List<NewsEvent> recentNews;
    private List<Stock> stocks;

    public MarketNewsGenerator(List<Stock> stocks) {
        this.random = new Random();
        this.recentNews = new ArrayList<>();
        this.stocks = stocks;
    }

    // Generate random market news
    public NewsEvent generateNews() {
        int eventType = random.nextInt(10);
        Stock affectedStock = stocks.get(random.nextInt(stocks.size()));
        
        NewsEvent event = null;
        
        if (eventType < 3) {
            // Positive news (30% chance)
            event = generatePositiveNews(affectedStock);
        } else if (eventType < 6) {
            // Negative news (30% chance)
            event = generateNegativeNews(affectedStock);
        } else {
            // Neutral news (40% chance)
            event = generateNeutralNews(affectedStock);
        }
        
        recentNews.add(event);
        if (recentNews.size() > 20) {
            recentNews.remove(0);
        }
        
        return event;
    }

    private NewsEvent generatePositiveNews(Stock stock) {
        String[] positiveTemplates = {
            "{company} announces record quarterly earnings!",
            "{company} secures major contract worth millions!",
            "{company} launches innovative new product line!",
            "{company} receives upgrade from major analyst!",
            "{company} expands into new markets!",
            "{company} reports strong revenue growth!"
        };
        
        String news = positiveTemplates[random.nextInt(positiveTemplates.length)]
            .replace("{company}", stock.getCompanyName());
        
        double impact = 0.02 + random.nextDouble() * 0.08; // 2-10% increase
        return new NewsEvent(news, stock, impact, "POSITIVE");
    }

    private NewsEvent generateNegativeNews(Stock stock) {
        String[] negativeTemplates = {
            "{company} faces regulatory investigation!",
            "{company} reports disappointing earnings!",
            "{company} loses major client!",
            "{company} CEO steps down amid controversy!",
            "{company} recalls defective products!",
            "{company} faces increased competition!"
        };
        
        String news = negativeTemplates[random.nextInt(negativeTemplates.length)]
            .replace("{company}", stock.getCompanyName());
        
        double impact = -(0.02 + random.nextDouble() * 0.08); // 2-10% decrease
        return new NewsEvent(news, stock, impact, "NEGATIVE");
    }

    private NewsEvent generateNeutralNews(Stock stock) {
        String[] neutralTemplates = {
            "{company} maintains steady performance in Q3.",
            "{company} announces routine board meeting.",
            "{company} opens new regional office.",
            "Market analysts review {company} projections.",
            "{company} participates in industry conference.",
            "{company} updates sustainability initiatives."
        };
        
        String news = neutralTemplates[random.nextInt(neutralTemplates.length)]
            .replace("{company}", stock.getCompanyName());
        
        double impact = -0.01 + random.nextDouble() * 0.02; // -1% to +1%
        return new NewsEvent(news, stock, impact, "NEUTRAL");
    }

    public List<NewsEvent> getRecentNews() {
        return new ArrayList<>(recentNews);
    }

    // Alias for generateNews() with auto-apply impact
    public NewsEvent generateRandomNews() {
        NewsEvent event = generateNews();
        event.applyImpact();
        return event;
    }

    // Inner class representing a news event
    public static class NewsEvent {
        private String headline;
        private Stock affectedStock;
        private double priceImpact; // Percentage impact
        private String sentiment; // POSITIVE, NEGATIVE, NEUTRAL
        private long timestamp;

        public NewsEvent(String headline, Stock affectedStock, double priceImpact, String sentiment) {
            this.headline = headline;
            this.affectedStock = affectedStock;
            this.priceImpact = priceImpact;
            this.sentiment = sentiment;
            this.timestamp = System.currentTimeMillis();
        }

        public void applyImpact() {
            double currentPrice = affectedStock.getCurrentPrice();
            double newPrice = currentPrice * (1 + priceImpact);
            affectedStock.setCurrentPrice(Math.max(newPrice, 1.0)); // Minimum price $1
        }

        public String getHeadline() { return headline; }
        public Stock getAffectedStock() { return affectedStock; }
        public double getPriceImpact() { return priceImpact; }
        public String getSentiment() { return sentiment; }
        public long getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format("[%s] %s (Impact: %+.2f%% on %s)",
                sentiment, headline, priceImpact * 100, affectedStock.getSymbol());
        }
    }
}
