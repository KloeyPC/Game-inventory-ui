import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MyOrdersPage extends JFrame {

    public MyOrdersPage() {
        this(null);
    }

    public MyOrdersPage(Point location) {
        setTitle("iSupply - My Orders");
        setSize(1200, 850);
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

        JButton homeBtn = createNavButton("Home", false);
        homeBtn.addActionListener(e -> {
            this.dispose();
            new CustomerHome(this.getLocation()).setVisible(true);
        });

        JButton trackBtn = createNavButton("My Orders", true);
        
        JLabel cartLabel = new JLabel("Cart");
        cartLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        cartLabel.setForeground(Color.DARK_GRAY);

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
        contentPanel.setBorder(new EmptyBorder(30, 100, 30, 100));

        JLabel title = new JLabel("Order History");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(title);
        contentPanel.add(Box.createVerticalStrut(20));

        List<OrderData.Order> myOrders = OrderData.getOrders();

        if (myOrders.isEmpty()) {
            JLabel empty = new JLabel("You haven't placed any orders yet.");
            empty.setFont(new Font("Arial", Font.ITALIC, 14));
            empty.setForeground(Color.GRAY);
            contentPanel.add(empty);
        } else {
            for (OrderData.Order order : myOrders) {
                contentPanel.add(createOrderCard(order));
                contentPanel.add(Box.createVerticalStrut(20));
            }
        }

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createOrderCard(OrderData.Order order) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(1000, 180));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        
        JLabel idLabel = new JLabel("Order #" + order.id + "  |  " + order.date);
        idLabel.setFont(new Font("Arial", Font.BOLD, 14));
        idLabel.setForeground(Color.DARK_GRAY);
        
        JLabel priceLabel = new JLabel("Total: " + order.total);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        priceLabel.setForeground(new Color(30, 80, 200));

        header.add(idLabel, BorderLayout.WEST);
        header.add(priceLabel, BorderLayout.EAST);
        card.add(header, BorderLayout.NORTH);

        JLabel itemsLabel = new JLabel("Items: " + order.items);
        itemsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        itemsLabel.setBorder(new EmptyBorder(15, 0, 15, 0));
        card.add(itemsLabel, BorderLayout.CENTER);

        JPanel trackerPanel = createTracker(order.status);
        card.add(trackerPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createTracker(String status) {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        String[] steps = {"Placed", "Processing", "Shipped", "Delivered"};
        int currentStepIndex = -1;

        for(int i=0; i<steps.length; i++) {
            if(steps[i].equals(status)) currentStepIndex = i;
        }

        for (int i = 0; i < steps.length; i++) {
            boolean isCompleted = i <= currentStepIndex;
            
            JLabel stepLbl = new JLabel(steps[i], SwingConstants.CENTER);
            stepLbl.setOpaque(true);
            stepLbl.setFont(new Font("Arial", Font.BOLD, 12));
            
            if (isCompleted) {
                stepLbl.setBackground(new Color(220, 255, 220)); 
                stepLbl.setForeground(new Color(0, 100, 0));     
                stepLbl.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(0, 150, 0))); 
            } else {
                stepLbl.setBackground(new Color(245, 245, 245)); 
                stepLbl.setForeground(Color.GRAY);
                stepLbl.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.LIGHT_GRAY)); 
            }
            panel.add(stepLbl);
        }

        return panel;
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
}