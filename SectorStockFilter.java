// Filter for stocks by sector
public class SectorStockFilter implements StockFilter<Stock> {
    private String sector;

    public SectorStockFilter(String sector) {
        this.sector = sector;
    }

    @Override
    public boolean meetsCriteria(Stock stock) {
        return stock.getSector().equalsIgnoreCase(sector);
    }

    @Override
    public String getFilterDescription() {
        return "Stocks in " + sector + " sector";
    }
}
