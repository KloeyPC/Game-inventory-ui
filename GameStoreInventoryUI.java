import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GameStoreInventoryUI extends JFrame {
    int item_page_size = 5;
    int currentPage = 0;
    private static final String SEARCH_PLACEHOLDER = "Search orders...";

    Products[] products = {
            new Products("PS4", "Devil May Cry 5 - Special Edition", 20, 19.99, "Jan 5"),
            new Products("Switch", "Pokemon Legends ZA", 100, 29.99, "Dec 3"),
            new Products("Switch", "Zelda Breath of the Wild", 35, 19.99, "Oct 9"),
            new Products("PS5", "Melty Blood Type Lumina", 67, 29.99, "Feb 9")
    };
    Products[] filteredProducts = products;

    JTextField search = new JTextField("");

    JPanel paginationPanel = new JPanel();
    JPanel tablePanel = new JPanel();
    JLabel pageNumber = new JLabel();

    public GameStoreInventoryUI() {
        setTitle("Game Store Inventory Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("GAME STORE INVENTORY MANAGEMENT SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        add(title, BorderLayout.NORTH);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JButton addSampleProductsBtn = new JButton("Add Sample Products");
        addSampleProductsBtn.addActionListener(e -> {
            Products[] temproducts = new Products[] {
                    new Products("PS6", "Devil May Cry 5 - Special Edition", 20, 19.99, "Jan 5"),
                    new Products("Switch", "Pokemon Legends ZA", 100, 29.99, "Dec 3"),
                    new Products("Switch", "Zelda Breath of the Wild", 35, 19.99, "Oct 9"),
                    new Products("PS5", "Melty Blood Type Lumina", 67, 29.99, "Feb 9")
            };
            Products[] newProducts = Arrays.copyOf(products, products.length + temproducts.length);
            System.arraycopy(temproducts, 0, newProducts, products.length, temproducts.length);
            products = newProducts;
            searchProducts(search.getText()); 
        });

        sidebar.add(addSampleProductsBtn);

        JLabel inventoryLbl = new JLabel("Inventory");
        inventoryLbl.setFont(new Font("Arial", Font.BOLD, 16));
        inventoryLbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));

        JLabel ordersLbl = new JLabel("Orders");
        ordersLbl.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 10));

        sidebar.add(inventoryLbl);
        sidebar.add(ordersLbl);

        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GhostText ghostText = new GhostText(search, SEARCH_PLACEHOLDER);
        ghostText.setGhostColor(Color.LIGHT_GRAY);
        search.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(search, BorderLayout.NORTH);

        tablePanel.setLayout(new GridLayout(0, 5, 10, 10));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        paginationPanel.setLayout(new FlowLayout());
        JButton prevBtn = new JButton("Previous");
        JButton nextBtn = new JButton("Next");

        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchProducts(search.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchProducts(search.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchProducts(search.getText());
            }
        });

        prevBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateTable(currentPage);
            }
        });

        nextBtn.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) filteredProducts.length / item_page_size);
            if (currentPage < totalPages - 1) {
                currentPage++;
                updateTable(currentPage);
            }
        });

        paginationPanel.add(prevBtn);
        paginationPanel.add(pageNumber);
        paginationPanel.add(nextBtn);

        mainPanel.add(paginationPanel, BorderLayout.SOUTH);

        updateTable(currentPage);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void addHeader(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label);
    }

    private void searchProducts(String searchText) {
        if (searchText.isEmpty() || searchText.equals(SEARCH_PLACEHOLDER)) {
            filteredProducts = products;
        } else {
            filteredProducts = Arrays.stream(products)
                    .filter(product -> product.getName().toLowerCase().contains(searchText.toLowerCase()))
                    .toArray(Products[]::new);
        }
        currentPage = 0; 
        updateTable(currentPage);
    }

    private void updateTable(int pageIndex) {
        
        tablePanel.removeAll();
        addHeader(tablePanel, "Type");
        addHeader(tablePanel, "Name");
        addHeader(tablePanel, "Stock");
        addHeader(tablePanel, "Price");
        addHeader(tablePanel, "Date Ordered");

        int start = pageIndex * item_page_size;
        int end = Math.min(start + item_page_size, filteredProducts.length);

        for (int i = start; i < end; i++) {
            Products product = filteredProducts[i];
            tablePanel.add(new JLabel(product.getType()));
            tablePanel.add(new JLabel(product.getName()));
            tablePanel.add(new JLabel(String.valueOf(product.getStock())));
            tablePanel.add(new JLabel(String.valueOf(product.getPrice())));
            tablePanel.add(new JLabel(product.getDateOrdered()));
        }

        int totalPages = (int) Math.ceil((double) filteredProducts.length / item_page_size);
        if (totalPages == 0) totalPages = 1; 

        pageNumber.setText("Page " + (pageIndex + 1) + " of " + totalPages);

        tablePanel.revalidate();
        tablePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            new LoginPage().setVisible(true);
        });
    }
}