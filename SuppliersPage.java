import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SuppliersPage extends JFrame {

    static class SupplierEntry {
        String name;
        String category;
        String imagePath; 
        Color brandColor; 

        public SupplierEntry(String name, String category, String imagePath, Color brandColor) {
            this.name = name;
            this.category = category;
            this.imagePath = imagePath;
            this.brandColor = brandColor;
        }
    }

    List<SupplierEntry> suppliers = new ArrayList<>();

    public SuppliersPage() {
        this(null);
    }

    public SuppliersPage(Point location) {
        initData(); 

        setTitle("iSupply - Suppliers");
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
        sidebar.add(createSidebarBtn("\u25CF  Inventory", false));
        sidebar.add(createSidebarBtn("\u25CF  Dashboard", false));
        sidebar.add(createSidebarBtn("\u25CF  Suppliers", true)); 
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

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(252, 252, 252)); 
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Suppliers");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(Color.BLACK); 
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel contentGrid = new JPanel(new GridLayout(0, 3, 25, 25)); 
        contentGrid.setBackground(new Color(252, 252, 252)); 

        for (SupplierEntry supplier : suppliers) {
            contentGrid.add(createSupplierCard(supplier));
        }

        JScrollPane scrollPane = new JScrollPane(contentGrid);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void initData() {
        // Updated to use the 6 local images you provided
        suppliers.add(new SupplierEntry("Sony PlayStation", "Consoles & First-Party Games", 
            "images/PlayStation.jpg", new Color(0, 55, 145)));
            
        suppliers.add(new SupplierEntry("Nintendo", "Switch & Family Titles", 
            "images/nintendo.jpg", new Color(230, 0, 18)));
            
        suppliers.add(new SupplierEntry("Microsoft Xbox", "GamePass & Consoles", 
            "images/xbox.jpg", new Color(16, 124, 16)));
            
        suppliers.add(new SupplierEntry("Bandai Namco", "Anime & Fighting Games", 
            "images/bandai.png", Color.ORANGE.darker()));
            
        suppliers.add(new SupplierEntry("Capcom", "Action & Horror Games", 
            "images/Capcom.jpg", new Color(0, 85, 160)));
            
        suppliers.add(new SupplierEntry("Square Enix", "JRPGs & Strategy", 
            "images/SQ.png", new Color(220, 20, 20)));
    }

    private JPanel createSupplierCard(SupplierEntry supplier) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE); 
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1)); 

        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE); 
        imageLabel.setPreferredSize(new Dimension(300, 180));
        
        try {
            // Load local image
            ImageIcon icon = new ImageIcon(supplier.imagePath);
            
            // Dynamic scaling for square vs rectangular logos
            int imgWidth = (supplier.name.equals("Nintendo") || supplier.name.equals("Bandai Namco") || supplier.name.equals("Capcom") || supplier.name.equals("Square Enix") || supplier.name.equals("Sony PlayStation") || supplier.name.equals("Microsoft Xbox")) ? 150 : 200;
            int imgHeight = (supplier.name.equals("Nintendo") || supplier.name.equals("Bandai Namco") || supplier.name.equals("Capcom") || supplier.name.equals("Square Enix") || supplier.name.equals("Sony PlayStation") || supplier.name.equals("Microsoft Xbox")) ? 150 : 70;

            Image img = icon.getImage().getScaledInstance(imgWidth, imgHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            imageLabel.setText(supplier.name); 
        }
        card.add(imageLabel, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        infoPanel.setBackground(Color.WHITE); 
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel nameLbl = new JLabel(supplier.name);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 16));
        nameLbl.setForeground(Color.BLACK); 

        JLabel categoryLbl = new JLabel(supplier.category);
        categoryLbl.setForeground(Color.GRAY); 
        categoryLbl.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton orderBtn = new JButton("Browse Catalog");
        orderBtn.setFont(new Font("Arial", Font.BOLD, 12));
        orderBtn.setForeground(Color.WHITE);
        orderBtn.setBackground(supplier.brandColor); 
        orderBtn.setFocusPainted(false);
        orderBtn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        orderBtn.addActionListener(e -> {
             JOptionPane.showMessageDialog(this, 
                "Opening ordering portal for: " + supplier.name);
        });

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        btnWrapper.setOpaque(false);
        btnWrapper.add(orderBtn);

        infoPanel.add(nameLbl);
        infoPanel.add(categoryLbl);
        infoPanel.add(btnWrapper);

        card.add(infoPanel, BorderLayout.CENTER);

        return card;
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
                else if (text.contains("Inventory")) new InventoryPage(currentLocation).setVisible(true);
                else if (text.contains("Dashboard")) new DashboardPage(currentLocation).setVisible(true);
                else if (text.contains("Feedback")) new FeedbackPage(currentLocation).setVisible(true);
                else if (text.contains("Suppliers")) new SuppliersPage(currentLocation).setVisible(true);
            });
        }
        return btn;
    }
}