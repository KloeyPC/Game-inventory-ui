import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MyOrdersPage extends JFrame {

    private JLabel cartLabel;

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
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 250));
        contentPanel.setBorder(new EmptyBorder(30, 100, 30, 100));

        JLabel title = new JLabel("Order History");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(title);
        contentPanel.add(Box.createVerticalStrut(20));

        if (Order.globalOrders.isEmpty()) {
            JLabel emptyLabel = new JLabel("No orders found.");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emptyLabel.setForeground(Color.GRAY);
            contentPanel.add(emptyLabel);
        } else {
            for (Order order : Order.globalOrders) {
                contentPanel.add(createOrderCard(order));
                contentPanel.add(Box.createVerticalStrut(20));
            }
        }

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
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
                
                if (itemsSummary.length() > 0) itemsSummary.append(", ");
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
                finalItems
            );
            Order.globalOrders.add(0, newOrder);

            JOptionPane.showMessageDialog(dialog, "Order Placed Successfully!\nYou can track it in My Orders.");
            CartData.items.clear();
            updateCartLabel();
            dialog.dispose();
            
            this.dispose();
            new MyOrdersPage(this.getLocation()).setVisible(true);
        });

        footer.add(totalLbl, BorderLayout.WEST);
        footer.add(checkoutBtn, BorderLayout.EAST);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createOrderCard(Order order) {
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