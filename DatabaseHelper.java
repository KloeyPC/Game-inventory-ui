
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:game_inventory.db";

    // Initialize Database: Create tables if they don't exist
    // Modified to persistent storage with manual reset option
    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();

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

                System.out.println("Database initialized.");

                // Seed 10 Games ONLY if table is empty
                if (isProductsEmpty(conn)) {
                    seedGames(conn);
                }
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

    public static void resetDatabase() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                stmt.execute("DROP TABLE IF EXISTS users");
                stmt.execute("DROP TABLE IF EXISTS products");
                stmt.execute("DROP TABLE IF EXISTS orders");
                System.out.println("Database dropped for reset.");
            }
        } catch (SQLException e) {
            System.out.println("Error dropping tables: " + e.getMessage());
        }

        // Re-initialize to create tables and seed
        initialize();
    }

    private static boolean isProductsEmpty(Connection conn) {
        String sql = "SELECT COUNT(*) FROM products";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static void seedGames(Connection conn) throws SQLException {
        String sql = "INSERT INTO products(type, name, stock, price, date_ordered, status) VALUES(?, ?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Object[][] games = {
                    { "PS4", "Devil May Cry 5 - Special Edition", 50, 1495.00, "2026-01-15" },
                    { "PS5", "Resident Evil Village", 30, 1995.00, "2026-01-20" },
                    { "Switch", "Pokemon Legends ZA", 100, 2995.00, "2026-02-01" },
                    { "Switch", "Legends Of Zelda Breath Of The Wild", 45, 2890.00, "2026-01-10" },
                    { "PS5", "God of War Ragnarok", 60, 3490.00, "2026-01-25" },
                    { "Switch", "Trails in the Sky 1st Chapter", 25, 2490.00, "2026-02-02" },
                    { "PS5", "Monster Hunter Wilds", 150, 3790.00, "2026-02-03" },
                    { "PS5", "Clair Obscur: Expedition 33", 40, 2990.00, "2026-01-30" }
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
            System.out.println("Seeded " + games.length + " sample games.");
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
        // Only fetch ACTIVE products
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
            // Safe parse
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
}