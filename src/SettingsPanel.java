import java.awt.*;
import javax.swing.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel(Main parent) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        content.setOpaque(false);

        JLabel title = new JLabel("Call Settings");
        title.setFont(ThemeManager.getFont(Font.BOLD, 32));
        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 30)));

        // Theme Toggle
        ModernUI.ToggleSwitch themeToggle = new ModernUI.ToggleSwitch("Enable Dark Mode");
        themeToggle.addActionListener(e -> {
            if (themeToggle.isSelected()) ThemeManager.setDarkTheme();
            else ThemeManager.setLightTheme();
            
            parent.applyTheme(); // Updates the main app
            
            // FIX: Force the Settings backgrounds to properly update to Dark Mode!
            setBackground(ThemeManager.bgPrimary);
            setOpaque(true);
            content.setBackground(ThemeManager.bgPrimary);
            content.setOpaque(true);
            
            // Fix the scrollpane background behind the content
            if (content.getParent() instanceof JViewport) {
                content.getParent().setBackground(ThemeManager.bgPrimary);
            }
            
            updateLabels(content);
        });
        
        content.add(themeToggle);
        content.add(Box.createRigidArea(new Dimension(0, 20)));

        // Samsung Specific Toggles
        content.add(createSamsungSetting("Block numbers"));
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(new ModernUI.ToggleSwitch("Caller ID and spam protection"));
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(createSamsungSetting("Record calls"));
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(createSamsungSetting("Call background"));
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(createSamsungSetting("Call alerts and ringtone"));
        content.add(Box.createRigidArea(new Dimension(0, 20)));
        content.add(new ModernUI.ToggleSwitch("Wi-Fi Calling"));

        JScrollPane scroll = new JScrollPane(content);
        ModernUI.makeModernScroll(scroll);
        add(scroll, BorderLayout.CENTER);
        updateLabels(content);
    }

    private JLabel createSamsungSetting(String text) {
        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.getFont(Font.PLAIN, 18));
        return l;
    }

    private void updateLabels(Container c) {
        for (Component comp : c.getComponents()) {
            if (comp instanceof JLabel) ((JLabel)comp).setForeground(ThemeManager.textPrimary);
            if (comp instanceof ModernUI.ToggleSwitch) ((ModernUI.ToggleSwitch)comp).setForeground(ThemeManager.textPrimary);
            if (comp instanceof Container) updateLabels((Container) comp);
        }
    }
}