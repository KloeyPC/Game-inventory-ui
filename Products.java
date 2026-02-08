public class Products {
    private int id;
    private String type;
    private String name;
    private int stock;
    private double price;
    private String dateOrdered;
    private String status;

    public Products(String type, String name, int stock, double price, String dateOrdered) {
        this.type = type;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.dateOrdered = dateOrdered;
        this.status = "ACTIVE";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(String dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    @Override
    public String toString() {
        return "Products [id=" + id + ", dateOrdered=" + dateOrdered + ", name=" + name + ", price=" + price
                + ", stock=" + stock
                + ", type=" + type + ", status=" + status + "]";
    }
}
