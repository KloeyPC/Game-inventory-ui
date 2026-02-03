import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InventoryPage extends JFrame {
    int item_page_size = 5;
    int currentPage = 0;
    private static final String SEARCH_PLACEHOLDER = "Search inventory...";

    List<Products> products = ProductData.getProducts();
    List<Products> filteredProducts = products;

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
        if (location != null) setLocation(location);
        else setLocationRelativeTo(null);
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
        sidebar.add(createSidebarBtn("\u25CF  Feedback", false));

        JButton addProductBtn = new JButton("\u2795  Add Product");
        addProductBtn.setMaximumSize(new Dimension(220, 40));
        addProductBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        addProductBtn.setForeground(Color.GRAY);
        addProductBtn.setBackground(Color.WHITE);
        addProductBtn.setHorizontalAlignment(SwingConstants.LEFT);
        addProductBtn.setBorder(new EmptyBorder(0, 30, 0, 0));
        addProductBtn.setFocusPainted(false);
        addProductBtn.setBorderPainted(false);

        addProductBtn.addActionListener(e -> new AddProductDialog(this).setVisible(true));
        sidebar.add(addProductBtn);

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
            public void insertUpdate(DocumentEvent e) { searchProducts(search.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchProducts(search.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchProducts(search.getText()); }
        });

        prevBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateTable(currentPage);
            }
        });

        nextBtn.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) filteredProducts.size() / item_page_size);
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
                if (text.contains("Orders")) new GameStoreInventoryUI(currentLocation).setVisible(true);
                else if (text.contains("Dashboard")) new DashboardPage(currentLocation).setVisible(true);
                else if (text.contains("Suppliers")) new SuppliersPage(currentLocation).setVisible(true);
                else if (text.contains("Feedback")) new FeedbackPage(currentLocation).setVisible(true);
                else if (text.contains("Inventory")) new InventoryPage(currentLocation).setVisible(true);
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
            java.util.stream.Stream<Products> stream = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            p.getType().toLowerCase().contains(searchText.toLowerCase()));
            filteredProducts = stream.collect(java.util.stream.Collectors.toList());
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
        int end = Math.min(start + item_page_size, filteredProducts.size());

        for (int i = start; i < end; i++) {
            Products product = filteredProducts.get(i);
            int row = (i - start) + 1;

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = row;
            gbc.insets = new Insets(10, 15, 10, 15);

            gbc.gridx = 0; gbc.weightx = 0.1; gbc.anchor = GridBagConstraints.WEST;
            tablePanel.add(new JLabel(product.getType()), gbc);

            gbc.gridx = 1; gbc.weightx = 0.6; gbc.anchor = GridBagConstraints.WEST;
            JLabel nameLbl = new JLabel(product.getName());
            nameLbl.setFont(new Font("Arial", Font.BOLD, 13));
            tablePanel.add(nameLbl, gbc);

            gbc.gridx = 2; gbc.weightx = 0.1; gbc.anchor = GridBagConstraints.CENTER;
            JLabel stock = new JLabel(String.valueOf(product.getStock()), SwingConstants.CENTER);
            stock.setPreferredSize(new Dimension(55, 30));
            stock.setOpaque(true);
            stock.setBackground(new Color(250, 250, 250));
            stock.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));
            tablePanel.add(stock, gbc);

            gbc.gridx = 3; gbc.weightx = 0.2; gbc.anchor = GridBagConstraints.EAST;
            JPanel actionContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionContainer.setOpaque(false);
            actionContainer.add(new JLabel("\uD83D\uDC64"));
            actionContainer.add(new JLabel("\u22EE"));
            tablePanel.add(actionContainer, gbc);
        }

        int totalPages = (int) Math.ceil((double) filteredProducts.size() / item_page_size);
        if (totalPages == 0) totalPages = 1;
        pageNumber.setText("Page " + (pageIndex + 1));

        tablePanel.revalidate();
        tablePanel.repaint();
    }

    private class AddProductDialog extends JDialog {
        private JTextField typeField, nameField, stockField, priceField, dateField;

        public AddProductDialog(JFrame parent) {
            super(parent, "Add New Product", true);
            setSize(450, 550);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            getContentPane().setBackground(Color.WHITE);

            JPanel header = new JPanel();
            header.setBackground(Color.WHITE);
            JLabel title = new JLabel("New Product Details");
            title.setFont(new Font("Arial", Font.BOLD, 18));
            title.setBorder(new EmptyBorder(20, 0, 10, 0));
            header.add(title);
            add(header, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridBagLayout());
            form.setBackground(Color.WHITE);
            form.setBorder(new EmptyBorder(10, 40, 10, 40));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 5, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.weightx = 1.0;

            typeField = createFormGroup(form, "Type (e.g. PS5)", gbc);
            nameField = createFormGroup(form, "Product Name", gbc);
            stockField = createFormGroup(form, "Stock Quantity", gbc);
            priceField = createFormGroup(form, "Price (\u20B1)", gbc);
            dateField = createFormGroup(form, "Date Ordered", gbc);

            add(form, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            actions.setBackground(Color.WHITE);

            JButton cancel = new JButton("Cancel");
            styleButton(cancel, false);
            cancel.addActionListener(e -> dispose());

            JButton save = new JButton("Add Product");
            styleButton(save, true);
            save.addActionListener(e -> saveProduct());

            actions.add(cancel);
            actions.add(save);
            add(actions, BorderLayout.SOUTH);
        }

        private JTextField createFormGroup(JPanel panel, String labelText, GridBagConstraints gbc) {
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setForeground(Color.GRAY);
            panel.add(label, gbc);

            JTextField field = new JTextField();
            field.setPreferredSize(new Dimension(200, 35));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220)),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)));
            panel.add(field, gbc);
            return field;
        }

        private void styleButton(JButton btn, boolean primary) {
            btn.setPreferredSize(new Dimension(140, 40));
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder());
            if (primary) {
                btn.setBackground(new Color(30, 80, 200));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(new Color(245, 245, 245));
                btn.setForeground(Color.GRAY);
            }
        }

        private void saveProduct() {
            try {
                String type = typeField.getText().trim();
                String name = nameField.getText().trim();
                String date = dateField.getText().trim();

                if (type.isEmpty() || name.isEmpty() || date.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all text fields.");
                    return;
                }

                int stock = Integer.parseInt(stockField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());

                Products newOne = new Products(type, name, stock, price, date);
                ProductData.addProduct(newOne);
                products = ProductData.getProducts();
                searchProducts(search.getText());
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Number Format for Stock or Price.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding product: " + ex.getMessage());
            }
        }
    }
}