/**
 * Functional Interface for Number Checking Operations
 * Used with Lambda Expressions to check number properties
 * LAB2 Requirement: Define functional interface for checking operations
 */
@FunctionalInterface
public interface CheckOperation {
    /**
     * Checks if a number meets certain criteria
     * @param number Number to check
     * @return true if condition is met, false otherwise
     */
    boolean test(int number);
}
