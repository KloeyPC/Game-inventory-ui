import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CustomerHome extends JFrame {

    private Map<Products, Integer> cart = new HashMap<>();
    private JLabel cartLabel;
    
    private static final int BULK_THRESHOLD = 3;
    private static final double DISCOUNT_RATE = 0.10;

    private Map<String, String> coverArtMap = new HashMap<>();

    public CustomerHome() {
        this(null);
    }

    public CustomerHome(Point location) {
        initCoverArt();

        setTitle("iSupply - Game Store");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (location != null) setLocation(location);
        else setLocationRelativeTo(null);
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
        
        cartLabel = new JLabel("Cart (0)");
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
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 250));
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT));
        banner.setBackground(new Color(220, 255, 220));
        banner.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 0), 1));
        banner.setMaximumSize(new Dimension(1200, 40));
        JLabel dealLabel = new JLabel("\uD83C\uDFF7\uFE0F BULK DEAL: Buy " + BULK_THRESHOLD + " or more copies of any game to get " + (int)(DISCOUNT_RATE*100) + "% OFF!");
        dealLabel.setForeground(new Color(0, 100, 0));
        dealLabel.setFont(new Font("Arial", Font.BOLD, 12));
        banner.add(dealLabel);
        
        contentPanel.add(banner);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel welcome = new JLabel("Featured Games");
        welcome.setFont(new Font("Arial", Font.BOLD, 24));
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(welcome);
        contentPanel.add(Box.createVerticalStrut(20));

        JPanel grid = new JPanel(new GridLayout(0, 4, 20, 20)); 
        grid.setBackground(new Color(245, 245, 250));
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<Products> inventory = ProductData.getProducts();
        for (Products p : inventory) {
            grid.add(createProductCard(p));
        }

        contentPanel.add(grid);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private void updateCartLabel() {
        int totalItems = cart.values().stream().mapToInt(Integer::intValue).sum();
        cartLabel.setText("Cart (" + totalItems + ")");
    }

    private void showCartDialog() {
        JDialog dialog = new JDialog(this, "Your Cart", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        listPanel.setBackground(Color.WHITE);

        double tempTotal = 0; // Temporary variable for calculation

        if (cart.isEmpty()) {
            listPanel.add(new JLabel("Your cart is empty."));
        } else {
            for (Map.Entry<Products, Integer> entry : cart.entrySet()) {
                Products p = entry.getKey();
                int qty = entry.getValue();
                
                double unitPrice = p.getPrice();
                double lineTotal = unitPrice * qty;
                boolean isDiscounted = qty >= BULK_THRESHOLD;
                
                if (isDiscounted) {
                    lineTotal = lineTotal * (1.0 - DISCOUNT_RATE);
                }
                
                tempTotal += lineTotal;

                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));
                row.setMaximumSize(new Dimension(550, 60));

                JLabel nameLbl = new JLabel("<html><b>" + p.getName() + "</b><br>Qty: " + qty + "</html>");
                nameLbl.setBorder(new EmptyBorder(10, 0, 10, 0));
                
                String priceText = String.format("₱%.2f", lineTotal);
                if (isDiscounted) {
                    priceText = "<html><font color='red'><s>₱" + String.format("%.2f", p.getPrice() * qty) + "</s></font> " + 
                                "<font color='green'>₱" + String.format("%.2f", lineTotal) + "</font><br>" +
                                "<font size='2' color='green'>Bulk -10%</font></html>";
                }
                
                JLabel priceLbl = new JLabel(priceText, SwingConstants.RIGHT);
                
                row.add(nameLbl, BorderLayout.CENTER);
                row.add(priceLbl, BorderLayout.EAST);
                listPanel.add(row);
                listPanel.add(Box.createVerticalStrut(5));
            }
        }

        // Create a FINAL variable to hold the total for the button to use
        final double grandTotal = tempTotal;

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
        
        // Now using the final variable inside the listener
        checkoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Order Placed Successfully!\nTotal: ₱" + String.format("%.2f", grandTotal));
            cart.clear();
            updateCartLabel();
            dialog.dispose();
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
                if (path.startsWith("http")) {
                    URL url = new URL(path);
                    Image image = ImageIO.read(url);
                    if (image != null) {
                        Image scaled = image.getScaledInstance(180, 220, Image.SCALE_SMOOTH);
                        imgLabel.setIcon(new ImageIcon(scaled));
                    } else {
                        imgLabel.setText("No Image");
                    }
                } else {
                    ImageIcon icon = new ImageIcon(path);
                    Image img = icon.getImage().getScaledInstance(180, 220, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(img));
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

        JButton addBtn = new JButton("Add to Cart");
        addBtn.setBackground(new Color(30, 80, 200));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.setMaximumSize(new Dimension(200, 35));
        
        addBtn.addActionListener(e -> {
            cart.put(p, cart.getOrDefault(p, 0) + 1);
            updateCartLabel();
        });

        info.add(title);
        info.add(Box.createVerticalStrut(5));
        info.add(price);
        info.add(Box.createVerticalStrut(10));
        info.add(addBtn);

        card.add(info, BorderLayout.CENTER);
        return card;
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
        coverArtMap.put("Devil May Cry", "https://upload.wikimedia.org/wikipedia/en/a/a2/Devil_May_Cry_5_cover_art.png");
        coverArtMap.put("Resident Evil", "https://upload.wikimedia.org/wikipedia/en/2/2c/Resident_Evil_Village_cover_art.png");
        coverArtMap.put("Pokemon", "https://upload.wikimedia.org/wikipedia/en/3/3b/Pokemon_Legends_Z-A_cover_art.jpg");
        coverArtMap.put("Zelda", "https://upload.wikimedia.org/wikipedia/en/c/c6/The_Legend_of_Zelda_Breath_of_the_Wild.jpg");
        coverArtMap.put("God of War", "https://upload.wikimedia.org/wikipedia/en/e/ee/God_of_War_Ragnar%C3%B6k_cover.jpg");
        coverArtMap.put("Trails", "https://upload.wikimedia.org/wikipedia/en/7/72/Trails_in_the_Sky_FC_cover.jpg");
        coverArtMap.put("Monster Hunter", "https://upload.wikimedia.org/wikipedia/en/thumb/5/52/Monster_Hunter_Wilds_cover_art.jpg/220px-Monster_Hunter_Wilds_cover_art.jpg");
        coverArtMap.put("Clair", "https://upload.wikimedia.org/wikipedia/en/thumb/a/a7/Clair_Obscur_Expedition_33_cover_art.jpg/220px-Clair_Obscur_Expedition_33_cover_art.jpg");
    }
}