import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DashboardPage extends JFrame {

    public DashboardPage() {
        this(null);
    }

    public DashboardPage(Point location) {
        setTitle("iSupply - Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (location != null)
            setLocation(location);
        else
            setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(240, 240, 240)));

        JLabel logo = new JLabel("iSupply");
        logo.setFont(new Font("Arial", Font.BOLD, 22));
        logo.setBorder(new EmptyBorder(30, 30, 30, 0));
        sidebar.add(logo);

        sidebar.add(createSidebarBtn("●  Orders", false));
        sidebar.add(createSidebarBtn("●  Inventory", false));
        sidebar.add(createSidebarBtn("●  Dashboard", true));
        sidebar.add(createSidebarBtn("●  Suppliers", false));
        sidebar.add(createSidebarBtn("●  Feedback", false));

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createSidebarBtn("●  Logout", false);
        logoutBtn.addActionListener(e -> {
            Point loc = this.getLocation();
            this.dispose();
            new LoginPage(loc).setVisible(true);
        });
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(20));
        add(sidebar, BorderLayout.WEST);

        // --- Main Content ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(252, 252, 252));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(title, BorderLayout.NORTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel dashboardGrid = new JPanel(new GridBagLayout());
        dashboardGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        JPanel filterPanel = createTimeFilter();
        gbc.gridy = 0;
        dashboardGrid.add(filterPanel, gbc);

        // --- 1. SALES CHART ---
        gbc.gridy = 1;
        gbc.weighty = 0.5;
        dashboardGrid.add(createGraphPanel(), gbc);

        JPanel bottomRow = new JPanel(new GridBagLayout());
        bottomRow.setOpaque(false);
        GridBagConstraints bGbc = new GridBagConstraints();
        bGbc.fill = GridBagConstraints.BOTH;
        bGbc.insets = new Insets(0, 0, 0, 20);

        bGbc.gridx = 0;
        bGbc.weightx = 0.65;
        bGbc.weighty = 1.0;
        bottomRow.add(createPopularSalesPanel(), bGbc);

        // --- 2. FETCH REAL STATS ---
        DatabaseHelper db = new DatabaseHelper();
        int totalProducts = 0;
        int totalOrders = 0;
        double salesToday = 0.0; 
        
        try {
            // Get Counts
            totalProducts = DatabaseHelper.getAllProducts().size();
            totalOrders = DatabaseHelper.getAllOrders().size();
            // Get Sales Today
            salesToday = db.getSalesToday();
        } catch (Exception e) {
            System.out.println("Error fetching dashboard stats: " + e.getMessage());
        }

        bGbc.gridx = 1;
        bGbc.weightx = 0.35;
        bGbc.insets = new Insets(0, 0, 0, 0);

        JPanel kpiPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        kpiPanel.setOpaque(false);
        
        // --- 3. DISPLAY SALES TODAY ---
        kpiPanel.add(createKPICard("Sales Today", String.format("₱%,.2f", salesToday), "Daily Performance"));
        kpiPanel.add(createKPICard("Total Orders", String.valueOf(totalOrders), "Lifetime"));
        kpiPanel.add(createKPICard("Active Products", String.valueOf(totalProducts), "In Stock"));

        bottomRow.add(kpiPanel, bGbc);

        gbc.gridy = 2;
        gbc.weighty = 0.4;
        dashboardGrid.add(bottomRow, gbc);

        mainPanel.add(dashboardGrid, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTimeFilter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);
        String[] labels = { "1D", "1W", "1M", "3M", "6M", "YTD", "1Y", "ALL" };
        for (String label : labels) {
            JButton btn = new JButton(label);
            btn.setFont(new Font("Arial", Font.PLAIN, 12));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            if (label.equals("1W")) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                        new EmptyBorder(5, 10, 5, 10)));
            } else {
                btn.setBackground(new Color(245, 245, 245));
                btn.setForeground(Color.GRAY);
            }
            panel.add(btn);
        }
        return panel;
    }

    private JPanel createGraphPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)));
        
        JLabel title = new JLabel("Sales Overview (Last 7 Days)");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        // Use SimpleBarChart with Real Data
        DatabaseHelper db = new DatabaseHelper();
        Map<String, Double> weeklySales = db.getWeeklySales();
        panel.add(new SimpleBarChart(weeklySales), BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createKPICard(String title, String value, String subtext) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
                new EmptyBorder(15, 15, 15, 15))); 
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Arial", Font.BOLD, 24)); 
        JLabel subLbl = new JLabel(subtext);
        subLbl.setForeground(new Color(0, 150, 0));
        subLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLbl, BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createPopularSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
                new EmptyBorder(20, 20, 20, 20)));
        JLabel title = new JLabel("Popular Sales");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = { "Title", "Orders", "Change" };
        Object[][] data = {
                { "Pokemon Legends ZA", "100", "+84%" },
                { "Clair Obscur: Expedition 33", "87", "-8%" },
                { "Elden Ring Shadow of the Erdtree", "68", "+2%" },
                { "Trails in the Sky 1st Chapter", "90", "+33%" },
                { "Pokemon Scarlet", "85", "+30%" }
        };

        JPanel grid = new JPanel(new GridLayout(data.length + 1, 3, 0, 10));
        grid.setOpaque(false);
        for (String c : cols)
            grid.add(new JLabel(c)).setForeground(Color.GRAY);
        for (Object[] row : data) {
            grid.add(new JLabel((String) row[0]));
            grid.add(new JLabel((String) row[1]));
            JLabel change = new JLabel((String) row[2]);
            change.setForeground(((String) row[2]).contains("+") ? new Color(0, 150, 0) : Color.RED);
            grid.add(change);
        }
        panel.add(grid, BorderLayout.CENTER);
        return panel;
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
                if (text.contains("Orders"))
                    new GameStoreInventoryUI(currentLocation).setVisible(true);
                else if (text.contains("Inventory"))
                    new InventoryPage(currentLocation).setVisible(true);
                else if (text.contains("Feedback"))
                    new FeedbackPage(currentLocation).setVisible(true);
                else if (text.contains("Suppliers"))
                    new SuppliersPage(currentLocation).setVisible(true);
                else if (text.contains("Dashboard"))
                    new DashboardPage(currentLocation).setVisible(true);
            });
        }
        return btn;
    }
}