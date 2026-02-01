import java.awt.*;
import javax.swing.*;

public class LoginPage extends JFrame {
    private final Color BACKGROUND_BLUE = new Color(30, 80, 200);
    private final Color BUTTON_WHITE = Color.WHITE;
    private final Color TEXT_BLUE = new Color(30, 80, 200);

    public LoginPage() {
        setTitle("iSupply Login");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_BLUE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo
        JLabel logoLabel = new JLabel("iSupply", SwingConstants.CENTER);
        logoLabel.setFont(new Font("SansSerif", Font.PLAIN, 60));
        logoLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 40, 10); 
        mainPanel.add(logoLabel, gbc);
        gbc.insets = new Insets(10, 10, 10, 10); 

        // Username
        JTextField userField = new JTextField(20);
        userField.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 1;
        mainPanel.add(userField, gbc);
        new GhostText(userField, "  \uD83D\uDC64   USERNAME"); 

        // Password
        JPasswordField passField = new JPasswordField(20);
        passField.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 2;
        mainPanel.add(passField, gbc);
        new GhostText(passField, "  \uD83D\uDD12   PASSWORD"); 

        
        JButton employeeBtn = createStyledButton("EMPLOYEE LOGIN");
        employeeBtn.addActionListener(e -> {
            this.dispose(); 
            new GameStoreInventoryUI().setVisible(true);
        });
        gbc.gridy = 3;
        mainPanel.add(employeeBtn, gbc);

        
        JButton customerBtn = createStyledButton("CUSTOMER LOGIN");
        gbc.gridy = 4;
        mainPanel.add(customerBtn, gbc);

        
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        footerPanel.setOpaque(false);

        JLabel registerBtn = new JLabel("Register");
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new RegisterPage().setVisible(true);
            }
        });

        JLabel forgotPwd = new JLabel("Forgot password?");
        forgotPwd.setForeground(Color.WHITE);
        forgotPwd.setFont(new Font("Arial", Font.PLAIN, 12));
        forgotPwd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        footerPanel.add(registerBtn);
        footerPanel.add(forgotPwd);

        gbc.gridy = 5;
        mainPanel.add(footerPanel, gbc);

        add(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(300, 45));
        btn.setBackground(BUTTON_WHITE);
        btn.setForeground(TEXT_BLUE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}