import java.awt.*;
import javax.swing.*;

public class GameStoreInventoryUI extends JFrame {

    
    String[][] inventory = {
            {"PS4", "Devil May Cry 5 - Special Edition", "20", "Low", "Jan 5"},
            {"Switch", "Pokemon Legends ZA", "100", "High", "Dec 3"},
            {"Switch", "Zelda Breath of the Wild", "35", "Low", "Oct 9"},
            {"PS5", "Melty Blood Type Lumina", "67", "Med", "Feb 9"}
    };

    public GameStoreInventoryUI() {
        setTitle("Game Store Inventory Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        JLabel title = new JLabel("GAME STORE INVENTORY MANAGEMENT SYSTEM");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        add(title, BorderLayout.NORTH);

       
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel inventoryLbl = new JLabel("Inventory");
        inventoryLbl.setFont(new Font("Arial", Font.BOLD, 16));
        inventoryLbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));

        JLabel ordersLbl = new JLabel("Orders");
        ordersLbl.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 10));

        sidebar.add(inventoryLbl);
        sidebar.add(ordersLbl);

        add(sidebar, BorderLayout.WEST);

        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

       
        JTextField search = new JTextField("Search orders...");
        search.setPreferredSize(new Dimension(200, 30));
        mainPanel.add(search, BorderLayout.NORTH);

        
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new GridLayout(0, 5, 10, 10));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        
        addHeader(tablePanel, "Type");
        addHeader(tablePanel, "Name");
        addHeader(tablePanel, "Stock");
        addHeader(tablePanel, "Demand");
        addHeader(tablePanel, "Date Ordered");

        
        addRowsRecursive(tablePanel, 0);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    
    private void addHeader(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label);
    }

    
    private void addRowsRecursive(JPanel panel, int index) {
        if (index >= inventory.length) {
            return; 
        }

        for (int i = 0; i < inventory[index].length; i++) {
            panel.add(new JLabel(inventory[index][i]));
        }

        
        addRowsRecursive(panel, index + 1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameStoreInventoryUI().setVisible(true);
        });
    }
}
