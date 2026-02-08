import java.util.List;

public class ProductData {

    static {
        // Initialize DB and Seed if empty
        DatabaseHelper.initialize();
        if (DatabaseHelper.getAllProducts().isEmpty()) {
            // Seed initial data
            seedData();
        }
    }

    private static void seedData() {
        DatabaseHelper.addProduct(new Products("PS4", "Devil May Cry 5 - Special Edition", 50, 1495.00, "2026-01-15"));
        DatabaseHelper.addProduct(new Products("PS5", "Resident Evil Village", 30, 1995.00, "2026-01-20"));
        DatabaseHelper.addProduct(new Products("Switch", "Pokemon Legends ZA", 100, 2995.00, "2026-02-01"));
        DatabaseHelper
                .addProduct(new Products("Switch", "Legends Of Zelda Breath Of The Wild", 45, 2890.00, "2026-01-10"));
        DatabaseHelper.addProduct(new Products("PS5", "God of War Ragnarok", 60, 3490.00, "2026-01-25"));
        DatabaseHelper.addProduct(new Products("Switch", "Trails in the Sky 1st Chapter", 25, 2490.00, "2026-02-02"));
        DatabaseHelper.addProduct(new Products("PS5", "Monster Hunter Wilds", 150, 3790.00, "2026-02-03"));
        DatabaseHelper.addProduct(new Products("PS5", "Clair Obscur: Expedition 33", 40, 2990.00, "2026-01-30"));
    }

    public static List<Products> getProducts() {
        return DatabaseHelper.getAllProducts();
    }

    public static void addProduct(Products p) {
        DatabaseHelper.addProduct(p);
    }
}