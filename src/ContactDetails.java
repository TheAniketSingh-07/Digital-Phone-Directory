import java.awt.*;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ContactDetails extends ModernUI.RoundedPanel {
    private Contact currentContact;
    private JLabel avatarLabel, nameLabel, phoneLabel, emailLabel, groupLabel;
    private Main parent;
    private ModernUI.ModernButton btnCall, btnText, btnStar;

    public ContactDetails(Main parent) {
        super(30); this.parent = parent;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(40, 40, 40, 40));
        setBackground(ThemeManager.bgSecondary);

        avatarLabel = new JLabel(); avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel = new JLabel(); nameLabel.setFont(ThemeManager.getFont(Font.BOLD, 28));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Circular Actions Row
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
        actions.setOpaque(false);
        btnCall = new ModernUI.ModernButton("Call", new Color(245, 158, 11), Color.WHITE);
        btnText = new ModernUI.ModernButton("Text", new Color(245, 158, 11), Color.WHITE);
        btnStar = new ModernUI.ModernButton("Starred", new Color(245, 158, 11), Color.WHITE);
        actions.add(btnCall); actions.add(btnText); actions.add(btnStar);

        // Detail Rows
        JPanel details = new JPanel(); details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setOpaque(false);
        phoneLabel = createDetailRow("Mobile", "");
        emailLabel = createDetailRow("Email", "");
        groupLabel = createDetailRow("Group", "");
        
        details.add(phoneLabel); details.add(Box.createRigidArea(new Dimension(0, 15)));
        details.add(emailLabel); details.add(Box.createRigidArea(new Dimension(0, 15)));
        details.add(groupLabel);

        // Bottom Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        ModernUI.ModernButton btnEdit = new ModernUI.ModernButton("Edit", new Color(100, 100, 100), Color.WHITE);
        ModernUI.ModernButton btnDel = new ModernUI.ModernButton("Delete", new Color(239, 68, 68), Color.WHITE);
        btnEdit.addActionListener(e -> parent.showEditDialog(currentContact));
        btnDel.addActionListener(e -> { parent.manager.deleteContact(currentContact); parent.refreshAllViews(); setVisible(false); });
        footer.add(btnEdit); footer.add(btnDel);

        add(avatarLabel); add(Box.createRigidArea(new Dimension(0, 20)));
        add(nameLabel); add(Box.createRigidArea(new Dimension(0, 10)));
        add(actions); add(Box.createRigidArea(new Dimension(0, 20)));
        add(details); add(Box.createVerticalGlue()); add(footer);
    }

    private JLabel createDetailRow(String label, String val) {
        JLabel l = new JLabel(label + ": " + val);
        l.setFont(ThemeManager.getFont(Font.PLAIN, 16));
        l.setForeground(ThemeManager.textSecondary);
        return l;
    }

    public void loadContact(Contact c) {
        this.currentContact = c;
        avatarLabel.setIcon(new ModernUI.AvatarIcon(c.getInitial(), c.getAvatarColor(), 100));
        nameLabel.setText(c.getName());
        phoneLabel.setText("Mobile: " + c.getPhone());
        emailLabel.setText("Email: " + c.getEmail());
        groupLabel.setText("Group: " + c.getGroup());
        btnStar.setText(parent.manager.isFavorite(c) ? "Starred" : "Star");
        setVisible(true);
    }

    public void applyTheme() {
        setBackground(ThemeManager.bgSecondary);
        nameLabel.setForeground(ThemeManager.textPrimary);
        phoneLabel.setForeground(ThemeManager.textSecondary);
    }
}