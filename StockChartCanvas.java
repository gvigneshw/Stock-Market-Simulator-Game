import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom chart component that renders a beautiful line chart
 * with gradient fill on a dark background — matching modern crypto UI design.
 */
public class StockChartCanvas extends Pane {
    private Canvas canvas;
    private List<Double> priceData;
    private String stockSymbol;
    private Color lineColor = Color.web("#fcd535");
    private Color bgColor = Color.web("#0b0e11");
    private Color gridColor = Color.web("#2b3139");
    private Color textColor = Color.web("#848e9c");
    private Color dotColor = Color.web("#fcd535");

    public StockChartCanvas() {
        canvas = new Canvas();
        getChildren().add(canvas);

        // Bind canvas to parent size
        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        widthProperty().addListener((obs, o, n) -> draw());
        heightProperty().addListener((obs, o, n) -> draw());

        priceData = new ArrayList<>();
        setStyle("-fx-background-color: #0b0e11;");
    }

    public void setLineColor(Color color) {
        this.lineColor = color;
        draw();
    }

    public void updateData(List<Double> prices, String symbol) {
        this.priceData = prices != null ? new ArrayList<>(prices) : new ArrayList<>();
        this.stockSymbol = symbol;
        draw();
    }

    private void draw() {
        double w = canvas.getWidth();
        double h = canvas.getHeight();
        if (w <= 0 || h <= 0) return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, w, h);

        // Background
        gc.setFill(bgColor);
        gc.fillRect(0, 0, w, h);

        if (priceData == null || priceData.size() < 2) {
            gc.setFill(textColor);
            gc.setFont(Font.font("Segoe UI", 14));
            gc.fillText("Waiting for price data...", w / 2 - 80, h / 2);
            return;
        }

        double padLeft = 60;
        double padRight = 20;
        double padTop = 20;
        double padBottom = 35;
        double chartW = w - padLeft - padRight;
        double chartH = h - padTop - padBottom;

        if (chartW <= 0 || chartH <= 0) return;

        double minPrice = Collections.min(priceData);
        double maxPrice = Collections.max(priceData);
        double range = maxPrice - minPrice;
        if (range < 0.01) range = maxPrice * 0.1; // Prevent division by zero for flat lines

        double padding = range * 0.08; // 8% padding above/below
        minPrice -= padding;
        maxPrice += padding;
        range = maxPrice - minPrice;

        // Horizontal grid lines + price labels
        gc.setStroke(gridColor);
        gc.setLineWidth(0.5);
        gc.setFont(Font.font("Segoe UI", 10));
        gc.setFill(textColor);
        int gridLines = 5;
        for (int i = 0; i <= gridLines; i++) {
            double y = padTop + i * chartH / gridLines;
            gc.strokeLine(padLeft, y, w - padRight, y);

            double price = maxPrice - i * range / gridLines;
            String label;
            if (price >= 1000) label = String.format("$%.0f", price);
            else if (price >= 100) label = String.format("$%.1f", price);
            else label = String.format("$%.2f", price);
            gc.fillText(label, 2, y + 4);
        }

        // Vertical grid lines (subtle)
        int vLines = Math.min(priceData.size() - 1, 6);
        if (vLines > 0) {
            for (int i = 1; i < vLines; i++) {
                double x = padLeft + i * chartW / vLines;
                gc.strokeLine(x, padTop, x, padTop + chartH);
            }
        }

        // Calculate x,y coords for each data point
        double[] xPoints = new double[priceData.size()];
        double[] yPoints = new double[priceData.size()];
        for (int i = 0; i < priceData.size(); i++) {
            xPoints[i] = padLeft + (i * chartW / (priceData.size() - 1));
            yPoints[i] = padTop + (1.0 - (priceData.get(i) - minPrice) / range) * chartH;
        }

        // Gradient fill under the line
        gc.beginPath();
        gc.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < priceData.size(); i++) {
            gc.lineTo(xPoints[i], yPoints[i]);
        }
        gc.lineTo(xPoints[xPoints.length - 1], padTop + chartH);
        gc.lineTo(xPoints[0], padTop + chartH);
        gc.closePath();

        LinearGradient gradient = new LinearGradient(
            0, padTop, 0, padTop + chartH, false, CycleMethod.NO_CYCLE,
            new Stop(0, Color.color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 0.25)),
            new Stop(1, Color.color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 0.0))
        );
        gc.setFill(gradient);
        gc.fill();

        // Draw the line
        gc.setStroke(lineColor);
        gc.setLineWidth(2.5);
        gc.setLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        gc.setLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        gc.beginPath();
        gc.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < priceData.size(); i++) {
            gc.lineTo(xPoints[i], yPoints[i]);
        }
        gc.stroke();

        // Glow dot on the last data point
        int last = priceData.size() - 1;
        double lx = xPoints[last];
        double ly = yPoints[last];

        // Glow
        gc.setFill(Color.color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), 0.3));
        gc.fillOval(lx - 8, ly - 8, 16, 16);
        // Solid dot
        gc.setFill(dotColor);
        gc.fillOval(lx - 4, ly - 4, 8, 8);

        // Price tooltip on last point
        String lastPrice = String.format("$%,.2f", priceData.get(last));
        gc.setFill(Color.web("#2b3139"));
        gc.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        double tooltipX = lx - 35;
        double tooltipY = ly - 22;
        if (tooltipY < padTop + 5) tooltipY = ly + 15;
        if (tooltipX < padLeft) tooltipX = lx + 10;
        
        // Tooltip background
        gc.fillRoundRect(tooltipX - 6, tooltipY - 12, 
            lastPrice.length() * 8 + 12, 20, 8, 8);
        gc.setFill(Color.web("#eaecef"));
        gc.fillText(lastPrice, tooltipX, tooltipY + 2);

        // Bottom time labels
        gc.setFill(textColor);
        gc.setFont(Font.font("Segoe UI", 10));
        int labelCount = Math.min(priceData.size(), 7);
        if (labelCount > 1) {
            int step = Math.max(1, priceData.size() / (labelCount - 1));
            for (int i = 0; i < priceData.size(); i += step) {
                double x = padLeft + (i * chartW / (priceData.size() - 1));
                gc.fillText(String.valueOf(i + 1), x - 3, h - 8);
            }
        }
    }
}
