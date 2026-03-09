import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * News panel — displays market news events in a beautiful dark-themed feed.
 * Each news item is a card with color-coded sentiment indicator.
 */
public class NewsPanelFX extends VBox {
    private MarketNewsGenerator newsGenerator;
    private VBox newsFeed;

    public NewsPanelFX(MarketNewsGenerator newsGenerator) {
        this.newsGenerator = newsGenerator;

        setSpacing(0);
        setStyle("-fx-background-color: #0b0e11;");

        VBox content = new VBox(16);
        content.setPadding(new Insets(24));

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Market News & Events");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#eaecef"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label subtitle = new Label("Real-time market events affecting stock prices");
        subtitle.setFont(Font.font("Segoe UI", 13));
        subtitle.setTextFill(Color.web("#848e9c"));

        header.getChildren().addAll(title, spacer, subtitle);

        // Legend
        HBox legend = createLegend();

        // News feed (scrollable)
        newsFeed = new VBox(10);
        newsFeed.setPadding(new Insets(4));

        ScrollPane scrollPane = new ScrollPane(newsFeed);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(header, legend, scrollPane);
        VBox.setVgrow(content, Priority.ALWAYS);
        getChildren().add(content);

        updateNews();
    }

    private HBox createLegend() {
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.setPadding(new Insets(0, 0, 4, 0));

        legend.getChildren().addAll(
            legendItem("▲ Positive", "#0ecb81"),
            legendItem("▼ Negative", "#f6465d"),
            legendItem("● Neutral", "#848e9c")
        );
        return legend;
    }

    private HBox legendItem(String text, String color) {
        HBox item = new HBox(4);
        item.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Segoe UI", 12));
        lbl.setTextFill(Color.web(color));
        item.getChildren().add(lbl);
        return item;
    }

    public void updateNews() {
        newsFeed.getChildren().clear();
        List<MarketNewsGenerator.NewsEvent> news = newsGenerator.getRecentNews();

        if (news.isEmpty()) {
            VBox emptyCard = new VBox(8);
            emptyCard.getStyleClass().add("card-flat");
            emptyCard.setAlignment(Pos.CENTER);
            emptyCard.setPadding(new Insets(40));

            Label emptyMsg = new Label("No market news yet");
            emptyMsg.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            emptyMsg.setTextFill(Color.web("#848e9c"));

            Label emptyHint = new Label("News events will appear here as the market simulation runs.\nEvents can impact stock prices — stay informed!");
            emptyHint.setFont(Font.font("Segoe UI", 13));
            emptyHint.setTextFill(Color.web("#848e9c"));
            emptyHint.setWrapText(true);
            emptyHint.setAlignment(Pos.CENTER);

            emptyCard.getChildren().addAll(emptyMsg, emptyHint);
            newsFeed.getChildren().add(emptyCard);
        } else {
            // Show newest first
            for (int i = news.size() - 1; i >= 0; i--) {
                newsFeed.getChildren().add(createNewsCard(news.get(i)));
            }
        }
    }

    public void addNewsEvent(MarketNewsGenerator.NewsEvent event) {
        // Add to top of feed
        if (newsFeed.getChildren().size() == 1) {
            // Check if it's the empty placeholder
            if (newsFeed.getChildren().get(0) instanceof VBox) {
                newsFeed.getChildren().clear();
            }
        }
        newsFeed.getChildren().add(0, createNewsCard(event));

        // Limit displayed items
        while (newsFeed.getChildren().size() > 25) {
            newsFeed.getChildren().remove(newsFeed.getChildren().size() - 1);
        }
    }

    private HBox createNewsCard(MarketNewsGenerator.NewsEvent event) {
        HBox card = new HBox(14);
        card.getStyleClass().add("news-card");
        card.setPadding(new Insets(14, 16, 14, 16));

        String sentiment = event.getSentiment();
        if ("POSITIVE".equals(sentiment)) {
            card.getStyleClass().add("news-card-positive");
        } else if ("NEGATIVE".equals(sentiment)) {
            card.getStyleClass().add("news-card-negative");
        } else {
            card.getStyleClass().add("news-card-neutral");
        }

        // Left: Sentiment icon
        VBox iconBox = new VBox();
        iconBox.setAlignment(Pos.TOP_CENTER);
        iconBox.setMinWidth(30);

        Label icon = new Label();
        icon.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        switch (sentiment) {
            case "POSITIVE":
                icon.setText("▲");
                icon.setTextFill(Color.web("#0ecb81"));
                break;
            case "NEGATIVE":
                icon.setText("▼");
                icon.setTextFill(Color.web("#f6465d"));
                break;
            default:
                icon.setText("●");
                icon.setTextFill(Color.web("#848e9c"));
                break;
        }
        iconBox.getChildren().add(icon);

        // Center: Content
        VBox contentBox = new VBox(4);
        HBox.setHgrow(contentBox, Priority.ALWAYS);

        Label headline = new Label(event.getHeadline());
        headline.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        headline.setTextFill(Color.web("#eaecef"));
        headline.setWrapText(true);

        HBox metaRow = new HBox(12);
        Label stockLabel = new Label(event.getAffectedStock().getSymbol());
        stockLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        stockLabel.setTextFill(Color.web("#fcd535"));

        Label impactLabel = new Label(String.format("Impact: %+.2f%%", event.getPriceImpact() * 100));
        impactLabel.setFont(Font.font("Segoe UI", 12));
        impactLabel.setTextFill(event.getPriceImpact() >= 0 ? Color.web("#0ecb81") : Color.web("#f6465d"));

        Label timeLabel = new Label(new SimpleDateFormat("HH:mm:ss").format(new Date(event.getTimestamp())));
        timeLabel.setFont(Font.font("Segoe UI", 11));
        timeLabel.setTextFill(Color.web("#848e9c"));

        metaRow.getChildren().addAll(stockLabel, impactLabel, timeLabel);
        contentBox.getChildren().addAll(headline, metaRow);

        // Right: Price info
        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        priceBox.setMinWidth(90);

        Label priceLabel = new Label(String.format("$%,.2f", event.getAffectedStock().getCurrentPrice()));
        priceLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        priceLabel.setTextFill(Color.web("#eaecef"));

        Label sectorLabel = new Label(event.getAffectedStock().getSector());
        sectorLabel.setFont(Font.font("Segoe UI", 11));
        sectorLabel.setTextFill(Color.web("#848e9c"));

        priceBox.getChildren().addAll(priceLabel, sectorLabel);

        card.getChildren().addAll(iconBox, contentBox, priceBox);
        return card;
    }
}
