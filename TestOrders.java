import java.util.List;

public class TestOrders {
    public static void main(String[] args) {
        System.out.println("Beginning Order Verification...");

        // 1. Initialize DB
        DatabaseHelper.initialize();

        // 2. Register Test Customer
        String user = "test_customer_" + System.currentTimeMillis();
        boolean reg = DatabaseHelper.registerUser(user, "Pass123!", "test@example.com");
        System.out.println("Registered User: " + user + " -> " + reg);

        // 3. Create Order
        String orderId = "ORD-TEST-" + System.currentTimeMillis();
        Order o = new Order(orderId, "2026-02-08", "₱1,500.00", "Placed", "Test Item (x1)");
        o.setUsername(user);
        DatabaseHelper.createOrder(o);
        System.out.println("Created Order: " + orderId);

        // 4. Verify Order Retrieval (Admin View)
        List<Order> allOrders = DatabaseHelper.getAllOrders();
        boolean foundInAll = allOrders.stream().anyMatch(ord -> ord.getId().equals(orderId));
        System.out.println("Found in All Orders: " + foundInAll);

        // 5. Verify Order Retrieval (User View)
        List<Order> userOrders = DatabaseHelper.getUserOrders(user);
        boolean foundInUser = userOrders.stream().anyMatch(ord -> ord.getId().equals(orderId));
        System.out.println("Found in User Orders: " + foundInUser);

        // 6. Test Product Archive
        // First add a product
        Products p = new Products("TestType", "TestProductToArchive", 10, 100.0, "2026-01-01");
        DatabaseHelper.addProduct(p);
        // We need to find its ID. Since addProduct doesn't return ID and getProducts
        // doesn't reliably return ID unless we fetch all...
        // Let's fetch all active products and find it.
        List<Products> active = DatabaseHelper.getAllProducts();
        Products target = active.stream()
                .filter(prod -> prod.getName().equals("TestProductToArchive"))
                .findFirst()
                .orElse(null);

        if (target != null) {
            System.out.println("Found target product ID: " + target.getId());
            // Archive it
            DatabaseHelper.archiveProduct(target.getId());
            System.out.println("Archived product.");

            // Allow DB update time (instantly generally but for good measure logic)

            // Verify it's gone from ACTIVE list
            List<Products> activeAfter = DatabaseHelper.getAllProducts();
            boolean stillActive = activeAfter.stream().anyMatch(prod -> prod.getId() == target.getId());
            System.out.println("Product still in active list: " + stillActive); // Should be false
        } else {
            System.out.println("Failed to find added test product.");
        }

        System.out.println("Verification Complete.");
    }
}
