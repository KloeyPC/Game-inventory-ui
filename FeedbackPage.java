import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class FeedbackPage extends JFrame {

    static class FeedbackEntry {
        String name;
        String date;
        int rating;
        String comment;

        public FeedbackEntry(String name, String date, int rating, String comment) {
            this.name = name;
            this.date = date;
            this.rating = rating;
            this.comment = comment;
        }
    }

    int item_page_size = 3;
    int currentPage = 0;
    private static final String SEARCH_PLACEHOLDER = "Search feedback...";

    List<FeedbackEntry> allFeedback = new ArrayList<>();
    List<FeedbackEntry> filteredFeedback;

    JTextField search = new JTextField("");
    JPanel feedbackListPanel = new JPanel();
    JLabel pageNumber = new JLabel();

    public FeedbackPage() {
        this(null);
    }

    public FeedbackPage(Point location) {
        initData();
        filteredFeedback = new ArrayList<>(allFeedback);

        setTitle("iSupply - Feedback");
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
        sidebar.add(createSidebarBtn("\u25CF  Suppliers", false));
        sidebar.add(createSidebarBtn("\u25CF  Feedback", true));

        JButton addFeedbackBtn = new JButton("\u2795  Add Feedback");
        addFeedbackBtn.setMaximumSize(new Dimension(220, 40));
        addFeedbackBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        addFeedbackBtn.setForeground(Color.GRAY);
        addFeedbackBtn.setBackground(Color.WHITE);
        addFeedbackBtn.setHorizontalAlignment(SwingConstants.LEFT);
        addFeedbackBtn.setBorder(new EmptyBorder(0, 30, 0, 0));
        addFeedbackBtn.setFocusPainted(false);
        addFeedbackBtn.setBorderPainted(false);
        addFeedbackBtn.addActionListener(e -> new AddFeedbackDialog(this).setVisible(true));
        sidebar.add(addFeedbackBtn);

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

        JLabel headerLabel = new JLabel("Feedback");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));

        new GhostText(search, SEARCH_PLACEHOLDER);
        search.setPreferredSize(new Dimension(350, 35));
        search.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(search, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        feedbackListPanel.setLayout(new BoxLayout(feedbackListPanel, BoxLayout.Y_AXIS));
        feedbackListPanel.setBackground(Color.WHITE);
        feedbackListPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JScrollPane scrollPane = new JScrollPane(feedbackListPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

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
            public void insertUpdate(DocumentEvent e) { searchFeedback(search.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchFeedback(search.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchFeedback(search.getText()); }
        });

        prevBtn.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                updateFeedbackList(currentPage);
            }
        });

        nextBtn.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) filteredFeedback.size() / item_page_size);
            if (currentPage < totalPages - 1) {
                currentPage++;
                updateFeedbackList(currentPage);
            }
        });

        updateFeedbackList(currentPage);
    }

    private void initData() {
        allFeedback.add(new FeedbackEntry("Alex Chen", "Feb 3, 2026", 5, "Amazing service! The Pokemon Legends ZA pre-order arrived right on release day. Packaging was excellent."));
        allFeedback.add(new FeedbackEntry("Marcus Rivera", "Feb 1, 2026", 4, "Great inventory tracking. I always know what's in stock. Wish there were more PS5 restocks though."));
        allFeedback.add(new FeedbackEntry("Sarah Jenkins", "Jan 28, 2026", 5, "The dashboard UI makes it so easy to see our total profit and low stock. Saved me hours of spreadsheet work!"));
        allFeedback.add(new FeedbackEntry("David Kim", "Jan 15, 2026", 3, "Decent prices but shipping took a little longer than expected for the Switch games."));
        allFeedback.add(new FeedbackEntry("Elena Rostova", "Jan 10, 2026", 5, "Customer support was very helpful in tracking down an obscure retro game for me."));
        allFeedback.add(new FeedbackEntry("Jordan Lee", "Jan 5, 2026", 2, "Website was a bit slow on mobile, but the product quality is undeniable."));
    }

    private void searchFeedback(String searchText) {
        if (searchText.isEmpty() || searchText.equals(SEARCH_PLACEHOLDER)) {
            filteredFeedback = new ArrayList<>(allFeedback);
        } else {
            String lowerSearch = searchText.toLowerCase();
            filteredFeedback = allFeedback.stream()
                .filter(f -> f.name.toLowerCase().contains(lowerSearch) || 
                             f.comment.toLowerCase().contains(lowerSearch))
                .collect(Collectors.toList());
        }
        currentPage = 0;
        updateFeedbackList(currentPage);
    }

    private void updateFeedbackList(int pageIndex) {
        feedbackListPanel.removeAll();

        int start = pageIndex * item_page_size;
        int end = Math.min(start + item_page_size, filteredFeedback.size());

        for (int i = start; i < end; i++) {
            FeedbackEntry entry = filteredFeedback.get(i);
            feedbackListPanel.add(createFeedbackCard(entry.name, entry.date, entry.rating, entry.comment));
            feedbackListPanel.add(Box.createVerticalStrut(15));
        }

        int totalPages = (int) Math.ceil((double) filteredFeedback.size() / item_page_size);
        if (totalPages == 0) totalPages = 1;
        pageNumber.setText("Page " + (pageIndex + 1));

        feedbackListPanel.revalidate();
        feedbackListPanel.repaint();
    }

    private JPanel createFeedbackCard(String name, String date, int rating, String comment) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(235, 235, 235), 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(1000, 140));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        
        JLabel nameLbl = new JLabel("\uD83D\uDC64  " + name);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 14));
        
        String stars = "";
        for(int i=0; i<5; i++) stars += (i < rating) ? "★" : "☆";
        JLabel ratingLbl = new JLabel(stars + "  " + date);
        ratingLbl.setForeground(new Color(255, 180, 0));
        ratingLbl.setFont(new Font("Arial", Font.PLAIN, 12));

        header.add(nameLbl, BorderLayout.WEST);
        header.add(ratingLbl, BorderLayout.EAST);

        JTextArea commentArea = new JTextArea(comment);
        commentArea.setFont(new Font("Arial", Font.PLAIN, 13));
        commentArea.setForeground(Color.DARK_GRAY);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setEditable(false);
        commentArea.setBorder(new EmptyBorder(10, 0, 0, 0));

        card.add(header, BorderLayout.NORTH);
        card.add(commentArea, BorderLayout.CENTER);

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
                else if (text.contains("Suppliers")) new SuppliersPage(currentLocation).setVisible(true);
                else if (text.contains("Feedback")) new FeedbackPage(currentLocation).setVisible(true);
            });
        }
        return btn;
    }

    private class AddFeedbackDialog extends JDialog {
        private JTextField nameField, dateField;
        private JComboBox<Integer> ratingBox;
        private JTextArea commentArea;

        public AddFeedbackDialog(JFrame parent) {
            super(parent, "Add New Feedback", true);
            setSize(450, 500);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            getContentPane().setBackground(Color.WHITE);

            JPanel header = new JPanel();
            header.setBackground(Color.WHITE);
            JLabel title = new JLabel("Customer Feedback");
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

            nameField = createFormGroup(form, "Customer Name", gbc);
            dateField = createFormGroup(form, "Date (e.g. Feb 4, 2026)", gbc);
            
            JLabel ratingLabel = new JLabel("Rating (1-5 Stars)");
            ratingLabel.setFont(new Font("Arial", Font.BOLD, 12));
            ratingLabel.setForeground(Color.GRAY);
            form.add(ratingLabel, gbc);
            ratingBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
            ratingBox.setBackground(Color.WHITE);
            form.add(ratingBox, gbc);

            JLabel commentLabel = new JLabel("Comment");
            commentLabel.setFont(new Font("Arial", Font.BOLD, 12));
            commentLabel.setForeground(Color.GRAY);
            form.add(commentLabel, gbc);
            
            commentArea = new JTextArea(4, 20);
            commentArea.setLineWrap(true);
            commentArea.setWrapStyleWord(true);
            commentArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            form.add(commentArea, gbc);

            add(form, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            actions.setBackground(Color.WHITE);

            JButton cancel = new JButton("Cancel");
            styleButton(cancel, false);
            cancel.addActionListener(e -> dispose());

            JButton save = new JButton("Submit");
            styleButton(save, true);
            save.addActionListener(e -> saveFeedback());

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
            btn.setPreferredSize(new Dimension(120, 35));
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

        private void saveFeedback() {
            String name = nameField.getText().trim();
            String date = dateField.getText().trim();
            String comment = commentArea.getText().trim();

            if (name.isEmpty() || date.isEmpty() || comment.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            int rating = (int) ratingBox.getSelectedItem();
            FeedbackEntry newFeedback = new FeedbackEntry(name, date, rating, comment);
            allFeedback.add(0, newFeedback); 
            searchFeedback(search.getText());
            dispose();
        }
    }
}