/**
 * Functional Interface for Arithmetic Operations
 * Used with Lambda Expressions for basic arithmetic calculations
 * LAB2 Requirement: Define functional interface for arithmetic operations
 */
@FunctionalInterface
public interface ArithmeticOperation {
    /**
     * Performs an arithmetic operation on two numbers
     * @param a First operand
     * @param b Second operand
     * @return Result of the operation
     */
    double calculate(double a, double b);
}
