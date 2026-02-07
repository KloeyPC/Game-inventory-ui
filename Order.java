import java.util.ArrayList;
import java.util.List;

public class Order {
    String id, date, total, status, items;
    
    public static List<Order> globalOrders = new ArrayList<>();

    public Order(String id, String date, String total, String status, String items) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.status = status;
        this.items = items;
    }
}