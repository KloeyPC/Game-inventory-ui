import java.util.ArrayList;
import java.util.List;

public class OrderData {
    
    public static class Order {
        String id, date, total, status, items;
        
        public Order(String id, String date, String total, String status, String items) {
            this.id = id;
            this.date = date;
            this.total = total;
            this.status = status;
            this.items = items;
        }
    }

    private static List<Order> orders = new ArrayList<>();

    public static List<Order> getOrders() {
        return orders;
    }

    public static void addOrder(Order order) {
        orders.add(0, order); 
    }
}