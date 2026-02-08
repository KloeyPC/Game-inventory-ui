
public class TestIntegration {
    public static void main(String[] args) {
        System.out.println("Starting Integration Test...");

        // 1. Initialize DB
        DatabaseHelper.initialize();

        // 2. Test Registration
        String testUser = "testUser" + System.currentTimeMillis();
        boolean regSuccess = DatabaseHelper.registerUser(testUser, "TestPass123!", "test@test.com");
        System.out.println("Registration Success: " + regSuccess);

        // 3. Test Login
        String role = DatabaseHelper.validateUser(testUser, "TestPass123!");
        System.out.println("Login Role: " + role); // Should be CUSTOMER

        String failRole = DatabaseHelper.validateUser(testUser, "WrongPass");
        System.out.println("Fail Login Result: " + failRole); // Should be null

        // 4. Test Admin Login (Seeded)
        String adminRole = DatabaseHelper.validateUser("admin", "Admin123!");
        System.out.println("Admin Login Role: " + adminRole); // Should be ADMIN

        // 5. Test Product Management
        int initialCount = DatabaseHelper.getAllProducts().size();
        System.out.println("Initial Product Count: " + initialCount);

        Products p = new Products("TestType", "TestName", 10, 99.99, "2026-02-08");
        DatabaseHelper.addProduct(p);

        int newCount = DatabaseHelper.getAllProducts().size();
        System.out.println("New Product Count: " + newCount);

        if (newCount == initialCount + 1) {
            System.out.println("Product Added Successfully!");
        } else {
            System.out.println("Product Add Failed!");
        }
    }
}
