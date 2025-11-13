// Bond - represents debt securities
public class Bond extends Stock {
    private double couponRate;
    private int yearsToMaturity;
    private String creditRating;

    public Bond(String symbol, String companyName, double currentPrice, 
               int quantity, String sector, double couponRate, int yearsToMaturity, String creditRating) {
        super(symbol, companyName, currentPrice, quantity, sector);
        this.couponRate = couponRate;
        this.yearsToMaturity = yearsToMaturity;
        this.creditRating = creditRating;
    }

    public double getCouponRate() {
        return couponRate;
    }

    public void setCouponRate(double couponRate) {
        this.couponRate = couponRate;
    }

    public int getYearsToMaturity() {
        return yearsToMaturity;
    }

    public void setYearsToMaturity(int yearsToMaturity) {
        this.yearsToMaturity = yearsToMaturity;
    }

    public String getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
    }

    @Override
    public double calculateRisk() {
        // Risk based on credit rating
        switch (creditRating) {
            case "AAA":
            case "AA":
                return 10.0;
            case "A":
            case "BBB":
                return 25.0;
            case "BB":
            case "B":
                return 50.0;
            default:
                return 75.0;
        }
    }

    @Override
    public String getStockType() {
        return "Bond";
    }

    public double calculateAnnualCoupon() {
        return getCurrentPrice() * getQuantity() * (couponRate / 100);
    }
}
