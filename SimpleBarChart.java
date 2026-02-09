import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class SimpleBarChart extends JPanel {
    private Map<String, Double> data;

    public SimpleBarChart(Map<String, Double> data) {
        this.data = data;
        // Adjust size as needed
        setPreferredSize(new Dimension(500, 250));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) {
            g.drawString("No sales data available.", 20, 100);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int padding = 40;
        
        List<String> keys = new ArrayList<>(data.keySet());
        Collections.reverse(keys); // Show oldest to newest

        double maxValue = data.values().stream().max(Double::compare).orElse(1.0);
        
        int barWidth = (width - 2 * padding) / keys.size();
        if(barWidth > 60) barWidth = 60; 

        int x = padding;

        for (String key : keys) {
            double value = data.get(key);
            int barHeight = (int) ((value / maxValue) * (height - 2 * padding));
            
            // Bar (Google Blue)
            g2.setColor(new Color(66, 133, 244));
            g2.fillRect(x, height - padding - barHeight, barWidth - 10, barHeight);
            
            // Date Label
            g2.setColor(Color.DARK_GRAY);
            // Try to shorten date string if it's long (e.g. 2026-02-09 -> 02-09)
            String dateLabel = key.length() > 5 ? key.substring(5) : key;
            g2.drawString(dateLabel, x, height - 10);
            
            // Value Label
            g2.setColor(Color.BLACK);
            g2.drawString(String.valueOf((int)value), x, height - padding - barHeight - 5);

            x += barWidth;
        }
    }
}