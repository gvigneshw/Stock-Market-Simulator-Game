/**
 * Functional Interface for String Operations
 * Used with Lambda Expressions for string manipulations
 * LAB2 Requirement: Define functional interface for string operations
 */
@FunctionalInterface
public interface StringOperation {
    /**
     * Performs a string operation
     * @param str Input string
     * @return Result of the operation
     */
    String process(String str);
}
