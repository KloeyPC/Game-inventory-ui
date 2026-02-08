import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GameStoreInventoryUI extends JFrame {
    int item_page_size = 8;
    int currentPage = 0;
    private static final String SEARCH_PLACEHOLDER = "Search orders...";

    List<Order> allOrders;
    List<Order> filteredOrders;

    JTextField search = new JTextField("");
    JPanel paginationPanel = new JPanel();
    JPanel tablePanel = new JPanel();
    JLabel pageNumber = new JLabel();

    public GameStoreInventoryUI() {
        this(null);
    }

    public GameStoreInventoryUI(Point location) {
        // LOAD ORDERS FROM DB
        try {
            allOrders = DatabaseHelper.getAllOrders();
        } catch (Exception e) {
            allOrders = java.util.Collections.emptyList();
        }
        filteredOrders = allOrders;

        setTitle("iSupply - Orders Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (location != null)
            setLocation(location);
        else
            setLocationRelativeTo(null);
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

        sidebar.add(createSidebarBtn("\u25CF  Orders", true));
        sidebar.add(createSidebarBtn("\u25CF  Inventory", false));
        sidebar.add(createSidebarBtn("\u25CF  Dashboard", false));
        sidebar.add(createSidebarBtn("\u25CF  Suppliers", false));
        sidebar.add(createSidebarBtn("\u25CF  Feedback", false));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createSidebarBtn("\u25CF  Logout", false);
        logoutBtn.addActionListener(e -> {
            Point loc = this.getLocation();
            this.dispose();
            new LoginPage(loc).setVisible(true);
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));
        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 20));
        headerPanel.setBackground(Color.WHITE);

        JLabel headerLabel = new JLabel("Orders Management");
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

        paginationPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
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
                searchOrders(search.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchOrders(search.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchOrders(search.getText());
            }
        });

        prevBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateTable(currentPage);
            }
        });

        nextBtn.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) filteredOrders.size() / item_page_size);
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
                Point currentLocation = this.getLocation();
                this.dispose();
                if (text.contains("Inventory"))
                    new InventoryPage(currentLocation).setVisible(true);
                else if (text.contains("Dashboard"))
                    new DashboardPage(currentLocation).setVisible(true);
                else if (text.contains("Suppliers"))
                    new SuppliersPage(currentLocation).setVisible(true);
                else if (text.contains("Feedback"))
                    new FeedbackPage(currentLocation).setVisible(true);
                else if (text.contains("Orders"))
                    new GameStoreInventoryUI(currentLocation).setVisible(true);
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

    private void searchOrders(String searchText) {
        if (searchText.isEmpty() || searchText.equals(SEARCH_PLACEHOLDER)) {
            filteredOrders = allOrders;
        } else {
            java.util.stream.Stream<Order> stream = allOrders.stream()
                    .filter(o -> o.getId().toLowerCase().contains(searchText.toLowerCase()) ||
                            (o.getUsername() != null
                                    && o.getUsername().toLowerCase().contains(searchText.toLowerCase())));
            filteredOrders = stream.collect(java.util.stream.Collectors.toList());
        }
        currentPage = 0;
        updateTable(currentPage);
    }

    private void updateTable(int pageIndex) {
        tablePanel.removeAll();
        tablePanel.setLayout(new GridBagLayout());

        // Headers for Orders
        addHeader(tablePanel, "Order ID", 0.2, GridBagConstraints.WEST);
        addHeader(tablePanel, "Customer", 0.2, GridBagConstraints.WEST);
        addHeader(tablePanel, "Total", 0.15, GridBagConstraints.CENTER);
        addHeader(tablePanel, "Date", 0.2, GridBagConstraints.CENTER);
        addHeader(tablePanel, "Status", 0.15, GridBagConstraints.CENTER);
        addHeader(tablePanel, "Items", 0.1, GridBagConstraints.EAST);

        int start = pageIndex * item_page_size;
        int end = Math.min(start + item_page_size, filteredOrders.size());

        for (int i = start; i < end; i++) {
            Order order = filteredOrders.get(i);
            int row = (i - start) + 1;

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = row;
            gbc.insets = new Insets(15, 15, 15, 15);

            // Order ID
            gbc.gridx = 0;
            gbc.weightx = 0.2;
            gbc.anchor = GridBagConstraints.WEST;
            tablePanel.add(new JLabel(order.getId()), gbc);

            // Customer
            gbc.gridx = 1;
            gbc.weightx = 0.2;
            gbc.anchor = GridBagConstraints.WEST;
            JLabel userLbl = new JLabel(order.getUsername() == null ? "Guest" : order.getUsername());
            userLbl.setFont(new Font("Arial", Font.BOLD, 13));
            tablePanel.add(userLbl, gbc);

            // Total
            gbc.gridx = 2;
            gbc.weightx = 0.15;
            gbc.anchor = GridBagConstraints.CENTER;
            JLabel totalLbl = new JLabel(order.getTotal());
            totalLbl.setForeground(new Color(0, 150, 0));
            tablePanel.add(totalLbl, gbc);

            // Date
            gbc.gridx = 3;
            gbc.weightx = 0.2;
            gbc.anchor = GridBagConstraints.CENTER;
            tablePanel.add(new JLabel(order.getDate()), gbc);

            // Status
            gbc.gridx = 4;
            gbc.weightx = 0.15;
            gbc.anchor = GridBagConstraints.CENTER;
            JLabel statusLbl = new JLabel(order.getStatus(), SwingConstants.CENTER);
            statusLbl.setPreferredSize(new Dimension(80, 25));
            statusLbl.setOpaque(true);
            statusLbl.setBackground(new Color(230, 240, 255));
            statusLbl.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 230), 1, true));
            tablePanel.add(statusLbl, gbc);

            // Items (Details)
            gbc.gridx = 5;
            gbc.weightx = 0.1;
            gbc.anchor = GridBagConstraints.EAST;
            JButton detailsBtn = new JButton("View");
            detailsBtn.setFont(new Font("Arial", Font.PLAIN, 11));
            detailsBtn.setFocusPainted(false);
            detailsBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Order Items:\n" + order.getItems(), "Order Details",
                        JOptionPane.INFORMATION_MESSAGE);
            });
            tablePanel.add(detailsBtn, gbc);
        }

        int totalPages = (int) Math.ceil((double) filteredOrders.size() / item_page_size);
        if (totalPages == 0)
            totalPages = 1;
        pageNumber.setText("Page " + (pageIndex + 1));

        tablePanel.revalidate();
        tablePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}