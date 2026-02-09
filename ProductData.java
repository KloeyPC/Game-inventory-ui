import java.util.List;

public class ProductData {

    static {
        // Initialize DB
        DatabaseHelper.initialize();
    }

    public static List<Products> getProducts() {
        return DatabaseHelper.getAllProducts();
    }

    public static void addProduct(Products p) {
        DatabaseHelper.addProduct(p);
    }
}