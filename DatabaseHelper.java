import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:game_inventory.db";

    // --- INITIALIZATION ---

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();

                // DROP TABLES for Reset
                stmt.execute("DROP TABLE IF EXISTS users");
                stmt.execute("DROP TABLE IF EXISTS products");
                stmt.execute("DROP TABLE IF EXISTS orders");

                // Users Table
                String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "username TEXT UNIQUE NOT NULL, "
                        + "password_hash TEXT NOT NULL, "
                        + "email TEXT, "
                        + "role TEXT DEFAULT 'CUSTOMER')";
                stmt.execute(createUsersTable);

                // Products Table
                String createProductsTable = "CREATE TABLE IF NOT EXISTS products ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "type TEXT, "
                        + "name TEXT, "
                        + "stock INTEGER, "
                        + "price REAL, "
                        + "date_ordered TEXT, "
                        + "status TEXT DEFAULT 'ACTIVE')";
                stmt.execute(createProductsTable);

                // Orders Table
                String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders ("
                        + "order_id TEXT PRIMARY KEY, "
                        + "username TEXT, "
                        + "total_amount REAL, "
                        + "date TEXT, "
                        + "status TEXT, "
                        + "items_summary TEXT)";
                stmt.execute(createOrdersTable);

                System.out.println("Database reset and initialized.");

                // Seed 10 Games
                seedGames(conn);
            }
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }

        // Seeding Admin
        if (!checkUserExists("admin")) {
            registerUser("admin", "Admin123!", "admin@isupply.com");
            updateUserRole("admin", "ADMIN");
        }

        // Seeding Customer
        if (!checkUserExists("customer")) {
            registerUser("customer", "Pass123!", "customer@gmail.com");
        }
    }

    private static void seedGames(Connection conn) throws SQLException {
        String sql = "INSERT INTO products(type, name, stock, price, date_ordered, status) VALUES(?, ?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Object[][] games = {
                    { "RPG", "Elden Ring", 50, 2999.00, "2024-01-10" },
                    { "Action", "God of War Ragnarok", 40, 3490.00, "2024-02-15" },
                    { "RPG", "Final Fantasy XVI", 35, 3290.00, "2024-03-01" },
                    { "Adventure", "Zelda: Tears of the Kingdom", 60, 2890.00, "2024-01-20" },
                    { "RPG", "Cyberpunk 2077", 45, 2500.00, "2024-02-10" },
                    { "Action", "Spider-Man 2", 55, 3190.00, "2024-03-05" },
                    { "Sim", "Animal Crossing", 30, 2400.00, "2024-01-05" },
                    { "Shooter", "Call of Duty: MW3", 70, 3500.00, "2024-02-28" },
                    { "Sports", "NBA 2K24", 80, 2800.00, "2024-03-10" },
                    { "Horror", "Resident Evil 4 Remake", 25, 2600.00, "2024-01-15" }
            };

            for (Object[] game : games) {
                pstmt.setString(1, (String) game[0]);
                pstmt.setString(2, (String) game[1]);
                pstmt.setInt(3, (int) game[2]);
                pstmt.setDouble(4, (double) game[3]);
                pstmt.setString(5, (String) game[4]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Seeded 10 games.");
        }
    }

    private static boolean checkUserExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            return pstmt.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    private static void updateUserRole(String username, String role) {
        String sql = "UPDATE users SET role = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating role: " + e.getMessage());
        }
    }

    // --- AUTHENTICATION ---

    public static boolean registerUser(String username, String password, String email) {
        String sql = "INSERT INTO users(username, password_hash, email) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public static String validateUser(String username, String password) {
        String sql = "SELECT role FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null; // Login failed
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // --- PRODUCTS ---

    public static List<Products> getAllProducts() {
        List<Products> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE status = 'ACTIVE' OR status IS NULL";
        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Products p = new Products(
                        rs.getString("type"),
                        rs.getString("name"),
                        rs.getInt("stock"),
                        rs.getDouble("price"),
                        rs.getString("date_ordered"));
                p.setId(rs.getInt("id"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
        return list;
    }

    public static void addProduct(Products p) {
        String sql = "INSERT INTO products(type, name, stock, price, date_ordered, status) VALUES(?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getType());
            pstmt.setString(2, p.getName());
            pstmt.setInt(3, p.getStock());
            pstmt.setDouble(4, p.getPrice());
            pstmt.setString(5, p.getDateOrdered());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    public static void updateProduct(Products p) {
        String sql = "UPDATE products SET type=?, name=?, stock=?, price=?, date_ordered=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getType());
            pstmt.setString(2, p.getName());
            pstmt.setInt(3, p.getStock());
            pstmt.setDouble(4, p.getPrice());
            pstmt.setString(5, p.getDateOrdered());
            pstmt.setInt(6, p.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }

    public static void archiveProduct(int id) {
        String sql = "UPDATE products SET status='ARCHIVED' WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error archiving product: " + e.getMessage());
        }
    }

    // --- ORDERS ---

    public static void createOrder(Order order) {
        String sql = "INSERT INTO orders(order_id, username, total_amount, date, status, items_summary) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, order.getId());
            pstmt.setString(2, order.getUsername());
            // Safe parse currency
            double amount = 0.0;
            try {
                amount = Double.parseDouble(order.getTotal().replace("₱", "").replace(",", ""));
            } catch (Exception e) {
            }

            pstmt.setDouble(3, amount);
            pstmt.setString(4, order.getDate());
            pstmt.setString(5, order.getStatus());
            pstmt.setString(6, order.getItems());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error creating order: " + e.getMessage());
        }
    }

    public static List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY date DESC";
        try (Connection conn = DriverManager.getConnection(URL);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Order o = new Order(
                        rs.getString("order_id"),
                        rs.getString("date"),
                        String.format("₱%.2f", rs.getDouble("total_amount")),
                        rs.getString("status"),
                        rs.getString("items_summary"));
                o.setUsername(rs.getString("username"));
                list.add(o);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all orders: " + e.getMessage());
        }
        return list;
    }

    public static List<Order> getUserOrders(String username) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE username=? ORDER BY date DESC";
        try (Connection conn = DriverManager.getConnection(URL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order o = new Order(
                        rs.getString("order_id"),
                        rs.getString("date"),
                        String.format("₱%.2f", rs.getDouble("total_amount")), 
                        rs.getString("status"),
                        rs.getString("items_summary"));
                o.setUsername(rs.getString("username"));
                list.add(o);
            }
        } catch (SQLException e) {
            System.out.println("Error getting user orders: " + e.getMessage());
        }
        return list;
    }
    
    // ==========================================
    //  NEW METHODS FOR DASHBOARD ANALYTICS
    // ==========================================

    // 1. Save a simulated sale from the "Simulate Purchase" button
    public void saveSimulatedOrder(String username, double totalAmount) {
        // We use date('now') for the current date, and generate a random ID
        String sql = "INSERT INTO orders(order_id, date, total_amount, username, status, items_summary) VALUES(?, date('now'), ?, ?, 'Completed', 'Simulated Purchase')";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "SIM-" + System.currentTimeMillis());
            pstmt.setDouble(2, totalAmount);
            pstmt.setString(3, username);
            pstmt.executeUpdate();
        } catch (SQLException e) { 
            System.out.println("Error saving simulation: " + e.getMessage());
        }
    }

    // 2. Calculate Total Sales for Today
    public double getSalesToday() {
        String sql = "SELECT SUM(total_amount) FROM orders WHERE date = date('now')";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0.0;
    }

    // 3. Get Data for the Bar Chart (Last 7 Days)
    public Map<String, Double> getWeeklySales() {
        Map<String, Double> data = new LinkedHashMap<>();
        // Group by 'date' column
        String sql = "SELECT date, SUM(total_amount) FROM orders GROUP BY date ORDER BY date DESC LIMIT 7";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return data;
    }
}