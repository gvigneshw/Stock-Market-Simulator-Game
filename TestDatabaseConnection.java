/**
 * TestDatabaseConnection - Simple test to verify JDBC connection
 * Run this first to ensure database setup is correct
 */
public class TestDatabaseConnection {
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  Stock Market Game - Database Test");
        System.out.println("===========================================\n");
        
        try {
            // Test database connection
            System.out.println("Testing database connection...");
            DatabaseManager dbManager = new DatabaseManager();
            
            System.out.println("\n✓ SUCCESS! Database is ready to use.");
            System.out.println("\nDatabase Details:");
            System.out.println("- Database: stock_market");
            System.out.println("- Tables: players, portfolio, transactions");
            System.out.println("- Status: Connected and initialized");
            
            // Test getting all player names
            System.out.println("\nExisting Players:");
            var players = dbManager.getAllPlayerNames();
            if (players.isEmpty()) {
                System.out.println("  (No players found - database is empty)");
            } else {
                for (String name : players) {
                    System.out.println("  - " + name);
                }
            }
            
            // Close connection
            dbManager.closeConnection();
            
            System.out.println("\n===========================================");
            System.out.println("  Test completed successfully!");
            System.out.println("===========================================");
            
        } catch (Exception e) {
            System.err.println("\n✗ ERROR: Database connection failed!");
            System.err.println("\nPossible issues:");
            System.err.println("1. MySQL server is not running");
            System.err.println("2. Database 'stock_market' doesn't exist");
            System.err.println("3. Username or password is incorrect");
            System.err.println("4. MySQL JDBC driver not in classpath");
            System.err.println("\nError details:");
            e.printStackTrace();
            
            System.err.println("\n===========================================");
            System.err.println("  Please check DATABASE_SETUP.md for help");
            System.err.println("===========================================");
        }
    }
}
