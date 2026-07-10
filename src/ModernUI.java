import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ModernUI {
    // These constants MUST be here so Main.java can see them
    public static final String SYMBOL_CHEVRON = "\u203A";
    public static final String SYMBOL_STAR = "★"; 

    public static class RoundedPanel extends JPanel {
        private int radius;
        public RoundedPanel(int radius) { this.radius = radius; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class ModernButton extends JButton {
        public ModernButton(String text, Color bg, Color fg) {
            super(text); setContentAreaFilled(false); setFocusPainted(false);
            setBorder(new EmptyBorder(10, 20, 10, 20)); setFont(ThemeManager.getFont(Font.BOLD, 14));
            setBackground(bg); setForeground(fg); setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground()); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g); g2.dispose();
        }
    }

    public static class ModernTextField extends JTextField {
        private String placeholder;
        public ModernTextField(String placeholder) {
            this.placeholder = placeholder; setOpaque(false); 
            setBorder(new EmptyBorder(12, 15, 12, 15));
            setFont(ThemeManager.getFont(Font.PLAIN, 15));
            setCaretColor(ThemeManager.accent);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ThemeManager.inputBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g);
            if (getText().isEmpty() && placeholder != null) {
                g2.setColor(ThemeManager.textSecondary);
                g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
            }
            g2.dispose();
        }
    }

    public static class ToggleSwitch extends JCheckBox {
        public ToggleSwitch(String text) {
            super(text); setOpaque(false); setFocusPainted(false);
            setFont(ThemeManager.getFont(Font.PLAIN, 16));
            setIcon(new SwitchIcon(false)); setSelectedIcon(new SwitchIcon(true));
        }
        private class SwitchIcon implements Icon {
            private boolean isSelected;
            public SwitchIcon(boolean isSelected) { this.isSelected = isSelected; }
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected ? ThemeManager.accent : ThemeManager.border);
                g2.fillRoundRect(x, y, 40, 20, 20, 20);
                g2.setColor(Color.WHITE);
                g2.fillOval(isSelected ? x + 22 : x + 2, y + 2, 16, 16);
                g2.dispose();
            }
            @Override public int getIconWidth() { return 50; }
            @Override public int getIconHeight() { return 20; }
        }
    }

    public static void makeModernScroll(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = ThemeManager.border; this.trackColor = ThemeManager.bgPrimary; }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() { JButton j = new JButton(); j.setPreferredSize(new Dimension(0, 0)); return j; }
        });
    }

    public static class AvatarIcon implements Icon {
        private String text; private Color color; private int size;
        public AvatarIcon(String text, Color color, int size) { 
            this.text = (text == null || text.isEmpty()) ? "#" : text; 
            this.color = (color != null) ? color : new Color(26, 115, 232); 
            this.size = size; 
        }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.fillOval(x, y, size, size);
            g2.setColor(Color.WHITE); g2.setFont(ThemeManager.getFont(Font.BOLD, size / 2));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (size - fm.stringWidth(text)) / 2;
            int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty); g2.dispose();
        }
        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }

    public static class CustomShapeIcon implements Icon {
        private int type; private Color bgColor; private int size;
        public CustomShapeIcon(int type, Color bgColor, int size) {
            this.type = type; this.bgColor = (bgColor != null) ? bgColor : new Color(245, 158, 11); this.size = size;
        }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor); g2.fillOval(x, y, size, size);
            g2.setColor(Color.WHITE);
            if (type == 0) { // Star
                int[] xP = new int[10]; int[] yP = new int[10];
                for (int i = 0; i < 10; i++) {
                    double angle = Math.PI/2 - i*(Math.PI/5); double r = (i%2==0)?9.0:4.5;
                    xP[i] = x + size/2 + (int)(Math.cos(angle)*r); yP[i] = y + size/2 - (int)(Math.sin(angle)*r);
                }
                g2.drawPolygon(xP, yP, 10);
            } else { // Group
                g2.fillOval(x+size/2-8, y+size/2-8, 6, 6);
                g2.fillOval(x+size/2+2, y+size/2-2, 6, 6);
            }
            g2.dispose();
        }
        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }
}