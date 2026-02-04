import java.util.ArrayList;
import java.util.List;

public class ProductData {
    private static List<Products> products = new ArrayList<>();

    static {
        // Updated with Philippine SRP (approximate)
        products.add(new Products("PS4", "Devil May Cry 5 - Special Edition", 50, 1495.00, "2026-01-15"));
        products.add(new Products("PS5", "Resident Evil Village", 30, 1995.00, "2026-01-20"));
        products.add(new Products("Switch", "Pokemon Legends ZA", 100, 2995.00, "2026-02-01"));
        products.add(new Products("Switch", "Legends Of Zelda Breath Of The Wild", 45, 2890.00, "2026-01-10"));
        products.add(new Products("PS5", "God of War Ragnarok", 60, 3490.00, "2026-01-25"));
        products.add(new Products("Switch", "Trails in the Sky 1st Chapter", 25, 2490.00, "2026-02-02"));
        products.add(new Products("PS5", "Monster Hunter Wilds", 150, 3790.00, "2026-02-03"));
        products.add(new Products("PS5", "Clair Obscur: Expedition 33", 40, 2990.00, "2026-01-30"));


        // Add items from GameStoreInventoryUI that were different, effectively merging

        // them or just sticking to one set.

        // The user's prompt implies "Inventory" is the main source, but

        // GameStoreInventoryUI had different items in the original code.

        // GameStoreInventoryUI items:

        // "PS4", "Devil May Cry 5 - Special Edition", 20, 19.99, "Jan 5"

        // "Switch", "Pokemon Legends ZA", 100, 29.99, "Dec 3"

        // "Switch", "Zelda Breath of the Wild", 35, 19.99, "Oct 9"

        // "PS5", "Melty Blood Type Lumina", 67, 29.99, "Feb 9"



        // I will use the superset or just the InventoryPage ones as the 'true'

        // inventory for now,

        // ensuring the specific ones mentioned in GameStoreInventoryUI are covered if

        // they differ significantly.

        // It seems InventoryPage has more detailed IDs/Names. I'll stick with

        // InventoryPage's list as the base.
    }

    public static List<Products> getProducts() {
        return products;
    }

    public static void addProduct(Products p) {
        products.add(p);
    }
}