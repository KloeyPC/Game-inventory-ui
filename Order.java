import java.util.ArrayList;
import java.util.List;

public class Order {
    String id, date, total, status, items, username;

    // Kept for backward compatibility if needed, but we rely on DB now
    public static List<Order> globalOrders = new ArrayList<>();

    public Order(String id, String date, String total, String status, String items) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.status = status;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public String getItems() {
        return items;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}