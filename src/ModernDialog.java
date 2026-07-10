import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ModernDialog extends JDialog {
    public ModernDialog(JFrame parent, String title) {
        super(parent, title, true);
        setSize(450, 550);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(ThemeManager.bgPrimary);
        setLayout(new BorderLayout());
    }

    public void addContent(JPanel content) {
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 30, 30, 30));
        add(content, BorderLayout.CENTER);
    }
}