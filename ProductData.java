import java.util.ArrayList;
import java.util.List;

public class ProductData {
    private static List<Products> products = new ArrayList<>();

    static {
        // Initialize with default data
        products.add(
                new Products("PS4-DMC5SE", "Devil May Cry 5 - Special Edition PlayStation 4", 102, 19.99, "Jan 5"));
        products.add(new Products("PS5-REV", "Resident Evil Village - PlayStation 5", 47, 39.99, "Jan 12"));
        products.add(new Products("NIN-PLZA", "Pokemon Legends ZA - Nintendo Switch", 23, 29.99, "Dec 3"));
        products.add(
                new Products("NIN-BOTW", "Legends Of Zelda Breathe Of The Wild - Nintendo Switch", 35, 19.99, "Oct 9"));
        products.add(new Products("PS5-GOWR", "God of War Ragnarok - PlayStation 5", 94, 49.99, "Nov 20"));
        products.add(
                new Products("NIN-TITS1ST", "Trails in the Sky 1st Chapter - Nintendo Switch", 48, 29.99, "Dec 15"));
        products.add(new Products("PS5-MHWILD", "Monster Hunter Wilds - PlayStation 5", 67, 59.99, "Feb 1"));
        products.add(new Products("PS5-EX33", "Clair Obscur: Expedition 33 - PlayStation 5", 31, 44.99, "Jan 28"));

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

    public static void addProduct(Products product) {
        products.add(product);
    }
}
