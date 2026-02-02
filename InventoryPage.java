import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InventoryPage extends JFrame {
    int item_page_size = 5;
    int currentPage = 0;
    private static final String SEARCH_PLACEHOLDER = "Search inventory...";

    Products[] products = {
            new Products("PS4-DMC5SE", "Devil May Cry 5 - Special Edition PlayStation 4", 102, 19.99, "Jan 5"),
            new Products("PS5-REV", "Resident Evil Village - PlayStation 5", 47, 39.99, "Jan 12"),
            new Products("NIN-PLZA", "Pokemon Legends ZA - Nintendo Switch", 23, 29.99, "Dec 3"),
            new Products("NIN-BOTW", "Legends Of Zelda Breathe Of The Wild - Nintendo Switch", 35, 19.99, "Oct 9"),
            new Products("PS5-GOWR", "God of War Ragnarok - PlayStation 5", 94, 49.99, "Nov 20"),
            new Products("NIN-TITS1ST", "Trails in the Sky 1st Chapter - Nintendo Switch", 48, 29.99, "Dec 15"),
            new Products("PS5-MHWILD", "Monster Hunter Wilds - PlayStation 5", 67, 59.99, "Feb 1"),
            new Products("PS5-EX33", "Clair Obscur: Expedition 33 - PlayStation 5", 31, 44.99, "Jan 28")
    };
    Products[] filteredProducts = products;

    JTextField search = new JTextField("");
    JPanel tablePanel = new JPanel();
    JLabel pageNumber = new JLabel();

    public InventoryPage() {
        this(null);
    }

    public InventoryPage(Point location) {
        setTitle("iSupply - Inventory");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (location != null) {
            setLocation(location);
        } else {
            setLocationRelativeTo(null);
        }
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(240, 240, 240)));

        JLabel logo = new JLabel("iSupply");
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setBorder(new EmptyBorder(30, 30, 30, 0));
        sidebar.add(logo);

        sidebar.add(createSidebarBtn("\u25CF  Orders", false));
        sidebar.add(createSidebarBtn("\u25CF  Inventory", true));
        sidebar.add(createSidebarBtn("\u25CF  Dashboard", false));
        sidebar.add(createSidebarBtn("\u25CF  Suppliers", false));
        sidebar.add(createSidebarBtn("\u25CF  Reports", false));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createSidebarBtn("\u25CF  Logout", false);
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginPage(this.getLocation()).setVisible(true);
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));
        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 20));
        headerPanel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("Inventory");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));

        new GhostText(search, SEARCH_PLACEHOLDER);
        search.setPreferredSize(new Dimension(350, 35));
        search.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(search, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        paginationPanel.setBackground(Color.WHITE);
        JButton prevBtn = new JButton("<");
        JButton nextBtn = new JButton(">");
        paginationPanel.add(prevBtn);
        paginationPanel.add(pageNumber);
        paginationPanel.add(nextBtn);
        mainPanel.add(paginationPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

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

        updateTable(currentPage);
    }

    private JButton createSidebarBtn(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setFont(new Font("Arial", active ? Font.BOLD : Font.PLAIN, 14));
        btn.setForeground(active ? Color.BLACK : Color.GRAY);
        btn.setBackground(active ? new Color(245, 245, 245) : Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 30, 0, 0));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);

        if (!active) {
            btn.addActionListener(e -> {
                this.dispose();
                if (text.contains("Orders")) {
                    new GameStoreInventoryUI(this.getLocation()).setVisible(true);
                } else if (text.contains("Dashboard")) {
                    new DashboardPage(this.getLocation()).setVisible(true);
                } else if (text.contains("Inventory")) {
                    new InventoryPage(this.getLocation()).setVisible(true);
                }
            });
        }
        return btn;
    }

    private void addHeader(JPanel panel, String text, double weight, int anchor) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(160, 160, 160));
        label.setFont(new Font("Arial", Font.BOLD, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weightx = weight;
        gbc.anchor = anchor;
        gbc.insets = new Insets(0, 15, 20, 15);
        panel.add(label, gbc);
    }

    private void searchProducts(String searchText) {
        if (searchText.isEmpty() || searchText.equals(SEARCH_PLACEHOLDER)) {
            filteredProducts = products;
        } else {
            filteredProducts = Arrays.stream(products)
                    .filter(p -> p.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            p.getType().toLowerCase().contains(searchText.toLowerCase()))
                    .toArray(Products[]::new);
        }
        currentPage = 0;
        updateTable(currentPage);
    }

    private void updateTable(int pageIndex) {
        tablePanel.removeAll();
        tablePanel.setLayout(new GridBagLayout());

        addHeader(tablePanel, "Type", 0.1, GridBagConstraints.WEST);
        addHeader(tablePanel, "Name", 0.6, GridBagConstraints.WEST);
        addHeader(tablePanel, "Stock", 0.1, GridBagConstraints.CENTER);
        addHeader(tablePanel, "", 0.2, GridBagConstraints.EAST);

        int start = pageIndex * item_page_size;
        int end = Math.min(start + item_page_size, filteredProducts.length);

        for (int i = start; i < end; i++) {
            Products product = filteredProducts[i];
            int row = (i - start) + 1;

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = row;
            gbc.insets = new Insets(10, 15, 10, 15);

            gbc.gridx = 0;
            gbc.weightx = 0.1;
            gbc.anchor = GridBagConstraints.WEST;
            tablePanel.add(new JLabel(product.getType()), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.6;
            gbc.anchor = GridBagConstraints.WEST;
            JLabel nameLbl = new JLabel(product.getName());
            nameLbl.setFont(new Font("Arial", Font.BOLD, 13));
            tablePanel.add(nameLbl, gbc);

            gbc.gridx = 2;
            gbc.weightx = 0.1;
            gbc.anchor = GridBagConstraints.CENTER;
            JLabel stock = new JLabel(String.valueOf(product.getStock()), SwingConstants.CENTER);
            stock.setPreferredSize(new Dimension(55, 30));
            stock.setOpaque(true);
            stock.setBackground(new Color(250, 250, 250));
            stock.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
            tablePanel.add(stock, gbc);

            gbc.gridx = 3;
            gbc.weightx = 0.2;
            gbc.anchor = GridBagConstraints.EAST;
            JPanel actionContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionContainer.setOpaque(false);
            actionContainer.add(new JLabel("\uD83D\uDC64"));
            actionContainer.add(new JLabel("\u22EE"));
            tablePanel.add(actionContainer, gbc);
        }

        int totalPages = (int) Math.ceil((double) filteredProducts.length / item_page_size);
        if (totalPages == 0)
            totalPages = 1;
        pageNumber.setText("Page " + (pageIndex + 1));

        tablePanel.revalidate();
        tablePanel.repaint();
    }
}