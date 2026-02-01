import java.awt.*;
import javax.swing.*;

public class RegisterPage extends JFrame {
    private final Color BACKGROUND_BLUE = new Color(30, 80, 200);
    private final Color BUTTON_WHITE = Color.WHITE;
    private final Color TEXT_BLUE = new Color(30, 80, 200);

    public RegisterPage() {
        setTitle("iSupply - Customer Register");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_BLUE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        
        JLabel titleLabel = new JLabel("<html><center>Customer<br>Register</center></html>", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 50));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 30, 10);
        mainPanel.add(titleLabel, gbc);
        gbc.insets = new Insets(8, 10, 8, 10);

        // Input Fields with GhostText and Icons
        JTextField emailField = createStyledField();
        gbc.gridy = 1;
        mainPanel.add(emailField, gbc);
        new GhostText(emailField, "  \u2709   EMAIL");

        JTextField userField = createStyledField();
        gbc.gridy = 2;
        mainPanel.add(userField, gbc);
        new GhostText(userField, "  \uD83D\uDC64   USERNAME");

        JPasswordField passField = createStyledPasswordField();
        gbc.gridy = 3;
        mainPanel.add(passField, gbc);
        new GhostText(passField, "  \uD83D\uDD12   PASSWORD");

        JPasswordField confirmField = createStyledPasswordField();
        gbc.gridy = 4;
        mainPanel.add(confirmField, gbc);
        new GhostText(confirmField, "        CONFIRM PASSWORD");

        
        JButton registerBtn = new JButton("REGISTER");
        registerBtn.setPreferredSize(new Dimension(350, 45));
        registerBtn.setBackground(BUTTON_WHITE);
        registerBtn.setForeground(TEXT_BLUE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);
        registerBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Account Created Successfully!");
            this.dispose();
            new LoginPage().setVisible(true);
        });
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(registerBtn, gbc);

        
        JLabel backToLogin = new JLabel("Back to login", SwingConstants.CENTER);
        backToLogin.setForeground(Color.WHITE);
        backToLogin.setFont(new Font("Arial", Font.PLAIN, 12));
        backToLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backToLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                new LoginPage().setVisible(true);
            }
        });
        gbc.gridy = 6;
        mainPanel.add(backToLogin, gbc);

        add(mainPanel);
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField(20);
        f.setPreferredSize(new Dimension(350, 40));
        f.setBackground(BACKGROUND_BLUE);
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        return f;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField f = new JPasswordField(20);
        f.setPreferredSize(new Dimension(350, 40));
        f.setBackground(BACKGROUND_BLUE);
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        return f;
    }
}