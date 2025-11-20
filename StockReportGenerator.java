/**
 * Functional Interface with No Arguments
 * Used for generating stock market reports
 * LAB2 Requirement: Lambda with no argument
 */
@FunctionalInterface
public interface StockReportGenerator {
    /**
     * Generates a report
     * @return Report string
     */
    String generateReport();
}
