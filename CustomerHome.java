import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class CustomerHome extends JFrame {

    private List<Products> allInventory;
    private List<Products> currentViewList;

    private int currentPage = 0;
    private final int ITEMS_PER_PAGE = 8;

    private JLabel cartLabel;
    private JPanel gridPanel;
    private JLabel pageLabel;
    private JButton prevBtn, nextBtn;
    private JTextField searchField;

    private static final int BULK_MIN = 10;

    private Map<String, String> coverArtMap = new HashMap<>();

    private String currentUser;

    public CustomerHome() {
        this(null, "Guest");
    }

    public CustomerHome(Point location) {
        this(location, "Guest");
    }

    public CustomerHome(Point location, String username) {
        this.currentUser = username;
        initCoverArt();
        allInventory = ProductData.getProducts();
        currentViewList = new ArrayList<>(allInventory);

        setTitle("iSupply - Game Store (" + username + ")");
        setSize(1200, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (location != null)
            setLocation(location);
        else
            setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 250));

        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(Color.WHITE);
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        navBar.setPreferredSize(new Dimension(1200, 70));

        JLabel logo = new JLabel("  iSupply Store");
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setForeground(new Color(30, 80, 200));
        navBar.add(logo, BorderLayout.WEST);

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        navButtons.setOpaque(false);

        JButton homeBtn = createNavButton("Home", true);
        JButton trackBtn = createNavButton("My Orders", false);

        trackBtn.addActionListener(e -> {
            this.dispose();
            // Pass username to MyOrdersPage
            new MyOrdersPage(this.getLocation(), currentUser).setVisible(true);
        });

        cartLabel = new JLabel();
        updateCartLabel();
        cartLabel.setFont(new Font("Arial", Font.BOLD, 14));
        cartLabel.setForeground(new Color(30, 80, 200));
        cartLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cartLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCartDialog();
            }
        });

        JButton logoutBtn = createNavButton("Logout", false);
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new LoginPage(this.getLocation()).setVisible(true);
        });

        navButtons.add(homeBtn);
        navButtons.add(trackBtn);
        navButtons.add(cartLabel);
        navButtons.add(new JSeparator(SwingConstants.VERTICAL));
        navButtons.add(logoutBtn);

        navBar.add(navButtons, BorderLayout.EAST);
        add(navBar, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 250));
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 250));
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel welcome = new JLabel("Featured Games");
        welcome.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        JLabel searchIcon = new JLabel("\uD83D\uDD0D ");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterProducts();
            }

            public void removeUpdate(DocumentEvent e) {
                filterProducts();
            }

            public void changedUpdate(DocumentEvent e) {
                filterProducts();
            }
        });

        searchPanel.add(searchIcon);
        searchPanel.add(searchField);

        headerPanel.add(welcome, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        gridPanel.setBackground(new Color(245, 245, 250));

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(new Color(245, 245, 250));
        gridWrapper.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(gridWrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scroll, BorderLayout.CENTER);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        paginationPanel.setBackground(new Color(245, 245, 250));
        paginationPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        prevBtn = createNavButton("< Previous", false);
        nextBtn = createNavButton("Next >", false);
        pageLabel = new JLabel("Page 1");
        pageLabel.setFont(new Font("Arial", Font.BOLD, 14));

        prevBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateGrid();
            }
        });

        nextBtn.addActionListener(e -> {
            int maxPage = (int) Math.ceil((double) currentViewList.size() / ITEMS_PER_PAGE) - 1;
            if (currentPage < maxPage) {
                currentPage++;
                updateGrid();
            }
        });

        paginationPanel.add(prevBtn);
        paginationPanel.add(pageLabel);
        paginationPanel.add(nextBtn);
        contentPanel.add(paginationPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        updateGrid();
    }

    private void filterProducts() {
        String query = searchField.getText().toLowerCase().trim();

        if (query.isEmpty()) {
            currentViewList = new ArrayList<>(allInventory);
        } else {
            currentViewList = allInventory.stream()
                    .filter(p -> p.getName().toLowerCase().contains(query))
                    .collect(java.util.stream.Collectors.toList());
        }

        currentPage = 0;
        updateGrid();
    }

    private void updateGrid() {
        gridPanel.removeAll();

        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, currentViewList.size());

        for (int i = start; i < end; i++) {
            gridPanel.add(createProductCard(currentViewList.get(i)));
        }

        int itemsToShow = end - start;
        int emptySlots = ITEMS_PER_PAGE - itemsToShow;
        for (int i = 0; i < emptySlots; i++) {
            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            gridPanel.add(spacer);
        }

        int totalPages = (int) Math.ceil((double) currentViewList.size() / ITEMS_PER_PAGE);
        if (totalPages == 0)
            totalPages = 1;
        pageLabel.setText("Page " + (currentPage + 1) + " of " + totalPages);

        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setEnabled(currentPage < totalPages - 1);

        prevBtn.setForeground(prevBtn.isEnabled() ? new Color(30, 80, 200) : Color.LIGHT_GRAY);
        nextBtn.setForeground(nextBtn.isEnabled() ? new Color(30, 80, 200) : Color.LIGHT_GRAY);

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void updateCartLabel() {
        int totalItems = CartData.items.values().stream().mapToInt(Integer::intValue).sum();
        cartLabel.setText("Cart (" + totalItems + ")");
    }

    private void showCartDialog() {
        JDialog dialog = new JDialog(this, "Your Cart", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        listPanel.setBackground(Color.WHITE);

        double tempTotal = 0;
        StringBuilder itemsSummary = new StringBuilder();

        if (CartData.items.isEmpty()) {
            listPanel.add(new JLabel("Your cart is empty."));
        } else {
            for (Map.Entry<Products, Integer> entry : CartData.items.entrySet()) {
                Products p = entry.getKey();
                int qty = entry.getValue();

                if (itemsSummary.length() > 0)
                    itemsSummary.append(", ");
                itemsSummary.append(p.getName()).append(" (x").append(qty).append(")");

                double discountRate = 0.0;
                String discountLabel = "";

                if (qty >= 50) {
                    discountRate = 0.20;
                    discountLabel = "20% OFF (Bulk 50+)";
                } else if (qty >= 20) {
                    discountRate = 0.15;
                    discountLabel = "15% OFF (Bulk 20+)";
                } else if (qty >= 10) {
                    discountRate = 0.10;
                    discountLabel = "10% OFF (Bulk 10+)";
                }

                double unitPrice = p.getPrice();
                double grossTotal = unitPrice * qty;
                double lineTotal = grossTotal * (1.0 - discountRate);

                tempTotal += lineTotal;

                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
                row.setMaximumSize(new Dimension(650, 75));

                JLabel nameLbl = new JLabel("<html><b>" + p.getName() + "</b><br>Qty: " + qty + "</html>");
                nameLbl.setBorder(new EmptyBorder(10, 0, 10, 0));

                String priceText;
                if (discountRate > 0) {
                    priceText = "<html><div style='text-align: right;'>" +
                            "<font color='gray'><s>SRP: ₱" + String.format("%.2f", grossTotal) + "</s></font><br>" +
                            "<font color='green'><b>₱" + String.format("%.2f", lineTotal) + "</b></font><br>" +
                            "<font size='2' color='green'>" + discountLabel + "</font>" +
                            "</div></html>";
                } else {
                    priceText = "<html><b>₱" + String.format("%.2f", lineTotal) + "</b></html>";
                }

                JLabel priceLbl = new JLabel(priceText, SwingConstants.RIGHT);

                row.add(nameLbl, BorderLayout.CENTER);
                row.add(priceLbl, BorderLayout.EAST);
                listPanel.add(row);
                listPanel.add(Box.createVerticalStrut(5));
            }
        }

        final double grandTotal = tempTotal;
        final String finalItems = itemsSummary.toString();

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(20, 20, 20, 20));
        footer.setBackground(new Color(250, 250, 250));

        JLabel totalLbl = new JLabel("Total: ₱" + String.format("%.2f", grandTotal));
        totalLbl.setFont(new Font("Arial", Font.BOLD, 18));

        JButton checkoutBtn = new JButton("Checkout");
        checkoutBtn.setBackground(new Color(30, 80, 200));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutBtn.setFocusPainted(false);

        checkoutBtn.addActionListener(e -> {
            if (CartData.items.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Cart is empty!");
                return;
            }

            Order newOrder = new Order(
                    "ORD-" + (System.currentTimeMillis() % 10000),
                    LocalDate.now().toString(),
                    String.format("₱%.2f", grandTotal),
                    "Placed",
                    finalItems);
            newOrder.setUsername(currentUser); // Set Username

            // SAVE TO DB
            DatabaseHelper.createOrder(newOrder);

            JOptionPane.showMessageDialog(dialog, "Order Placed Successfully!\nYou can track it in My Orders.");
            CartData.items.clear();
            updateCartLabel();
            dialog.dispose();

            this.dispose();
            new MyOrdersPage(this.getLocation(), currentUser).setVisible(true);
        });

        footer.add(totalLbl, BorderLayout.WEST);
        footer.add(checkoutBtn, BorderLayout.EAST);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createProductCard(Products p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        card.setPreferredSize(new Dimension(250, 380));

        JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(250, 250));
        imgLabel.setBackground(new Color(240, 240, 240));
        imgLabel.setOpaque(true);

        try {
            String path = null;
            for (String key : coverArtMap.keySet()) {
                if (p.getName().contains(key)) {
                    path = coverArtMap.get(key);
                    break;
                }
            }

            if (path != null) {
                URL url = getClass().getClassLoader().getResource(path);
                Image imageToScale = null;

                if (url != null) {
                    imageToScale = ImageIO.read(url);
                } else {
                    ImageIcon icon = new ImageIcon(path);
                    if (icon.getIconWidth() > 0) {
                        imageToScale = icon.getImage();
                    }
                }

                if (imageToScale != null) {
                    imgLabel.setIcon(scaleImagePreservingRatio(imageToScale, 240, 240));
                } else {
                    imgLabel.setText("No Image");
                }
            } else {
                imgLabel.setText(p.getName());
            }
        } catch (Exception e) {
            imgLabel.setText("Image Error");
        }
        card.add(imgLabel, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(10, 10, 10, 10));
        info.setBackground(Color.WHITE);

        JLabel title = new JLabel("<html>" + p.getName() + "</html>");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel price = new JLabel("₱" + p.getPrice());
        price.setFont(new Font("Arial", Font.BOLD, 16));
        price.setForeground(new Color(0, 150, 0));
        price.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        qtySpinner.setPreferredSize(new Dimension(50, 35));

        JButton addBtn = new JButton("Add");
        addBtn.setBackground(new Color(30, 80, 200));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setPreferredSize(new Dimension(80, 35));

        addBtn.addActionListener(e -> {
            int quantity = (Integer) qtySpinner.getValue();
            CartData.items.put(p, CartData.items.getOrDefault(p, 0) + quantity);
            updateCartLabel();
            qtySpinner.setValue(1);
        });

        actionPanel.add(qtySpinner);
        actionPanel.add(Box.createHorizontalStrut(10));
        actionPanel.add(addBtn);

        info.add(title);
        info.add(Box.createVerticalStrut(5));
        info.add(price);
        info.add(Box.createVerticalStrut(10));
        info.add(actionPanel);

        card.add(info, BorderLayout.CENTER);
        return card;
    }

    private ImageIcon scaleImagePreservingRatio(Image originalImage, int targetWidth, int targetHeight) {
        int originalWidth = originalImage.getWidth(null);
        int originalHeight = originalImage.getHeight(null);

        double widthRatio = (double) targetWidth / originalWidth;
        double heightRatio = (double) targetHeight / originalHeight;

        double scaleFactor = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (originalWidth * scaleFactor);
        int newHeight = (int) (originalHeight * scaleFactor);

        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private JButton createNavButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", active ? Font.BOLD : Font.PLAIN, 14));
        btn.setForeground(active ? new Color(30, 80, 200) : Color.DARK_GRAY);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void initCoverArt() {
        coverArtMap.put("Devil May Cry", "images/dmcv.png");
        coverArtMap.put("Resident Evil", "images/Residentvil.jpg");
        coverArtMap.put("Pokemon", "images/Pokemon_Legends_Z-A_Key_Art_Logo.png");
        coverArtMap.put("Zelda", "images/zelda.jpg");
        coverArtMap.put("God of War", "images/godofwar.jpg");
        coverArtMap.put("Trails", "images/trailsinthesky1st.jpg");
        coverArtMap.put("Monster Hunter", "images/mhw.jpg");
        coverArtMap.put("Clair", "images/expedition33.jpg");
    }
}