public class Products {
    private String type;
    private String name;
    private int stock;
    private double price;
    private String dateOrdered;

    public Products(String type, String name, int stock, double price, String dateOrdered) {
        this.type = type;
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.dateOrdered = dateOrdered;
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
        return "Products [dateOrdered=" + dateOrdered + ", name=" + name + ", price=" + price + ", stock=" + stock
                + ", type=" + type + "]";
    }
}
