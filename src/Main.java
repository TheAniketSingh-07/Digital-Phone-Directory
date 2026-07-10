import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Main extends JFrame {
    public ContactManager manager;
    private JPanel sidebar, contentArea;
    private CardLayout cardLayout;
    
    // Main View Components
    private DefaultListModel<Contact> listModel;
    private JList<Contact> contactList;
    private ContactDetails detailsPanel;
    private ModernUI.ModernTextField searchField;
    
    private JLabel viewTitle, viewSubtitle, favSubtext, groupSubtext;
    private JPanel shortcutsPanel;
    private ModernUI.ModernButton btnBackToGroups; 
    
    // New Groups Dashboard Components
    private JPanel groupsDashboardPanel, groupsGridPanel;
    private JLabel groupsTitle, groupsSubtitle;
    
    public String currentViewMode = "ALL"; 
    public String selectedGroup = ""; // Tracks which group is currently open

    public Main() {
        manager = BackupManager.loadBackup();
        setTitle("Smart Contact Book");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { BackupManager.saveBackup(manager); }
        });
        initUI();
    }

    private void initUI() {
        getContentPane().setLayout(new BorderLayout());

        // --- SIDEBAR ---
        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Contacts");
        title.setFont(ThemeManager.getFont(Font.BOLD, 26));
        sidebar.add(title); sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        sidebar.add(createNavBtn("All Contacts", "MAIN_VIEW", "ALL", () -> { viewTitle.setText("Contacts"); refreshAllViews(); }));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createNavBtn("Favorites", "MAIN_VIEW", "FAV", () -> { viewTitle.setText("Favourites"); refreshAllViews(); }));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createNavBtn("Recent Logs", "MAIN_VIEW", "RECENT", () -> { viewTitle.setText("Recent Logs"); refreshAllViews(); }));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        // Groups now links to the new GROUPS_VIEW dashboard
        sidebar.add(createNavBtn("Groups", "GROUPS_VIEW", "GROUPS", () -> { refreshGroupsDashboard(); }));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createNavBtn("Recycle Bin", "MAIN_VIEW", "TRASH", () -> { viewTitle.setText("Recycle Bin"); refreshAllViews(); }));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createNavBtn("Settings", "SETTINGS_VIEW", "SETTINGS", null));

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);

        // --- 1. MAIN LIST VIEW ---
        JPanel mainView = new JPanel(new BorderLayout());
        mainView.setOpaque(false);
        
        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.setOpaque(false);
        listWrapper.setBorder(new EmptyBorder(20, 10, 20, 10));
        listWrapper.setPreferredSize(new Dimension(400, 0));

        JPanel fixedTopContainer = new JPanel();
        fixedTopContainer.setLayout(new BoxLayout(fixedTopContainer, BoxLayout.Y_AXIS));
        fixedTopContainer.setOpaque(false);

        viewTitle = new JLabel("Contacts");
        viewTitle.setFont(ThemeManager.getFont(Font.BOLD, 28));
        
        viewSubtitle = new JLabel("0 contacts · 0 starred");
        viewSubtitle.setFont(ThemeManager.getFont(Font.PLAIN, 14));
        
        JPanel headerTextPanel = new JPanel(new GridLayout(2,1));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(viewTitle); headerTextPanel.add(viewSubtitle);
        headerTextPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        searchField = new ModernUI.ModernTextField("Search (Linear/Binary)...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { doSearch(); }
            public void removeUpdate(DocumentEvent e) { doSearch(); }
            public void changedUpdate(DocumentEvent e) { doSearch(); }
        });

        ModernUI.ModernButton btnAdd = new ModernUI.ModernButton("+ Create Contact", ThemeManager.accent, Color.WHITE);
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnAdd.addActionListener(e -> showAddDialog());

        fixedTopContainer.add(headerTextPanel);
        fixedTopContainer.add(searchField);
        fixedTopContainer.add(Box.createRigidArea(new Dimension(0, 15)));
        fixedTopContainer.add(btnAdd);
        fixedTopContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Shortcuts Panel (Inside Scroll)
        shortcutsPanel = new JPanel();
        shortcutsPanel.setLayout(new BoxLayout(shortcutsPanel, BoxLayout.Y_AXIS));
        shortcutsPanel.setOpaque(false);
        shortcutsPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        favSubtext = new JLabel("0 starred");
        groupSubtext = new JLabel("0 groups");
        
        shortcutsPanel.add(createShortcutItem("Favourites", favSubtext, 0, () -> {
            currentViewMode = "FAV"; viewTitle.setText("Favourites"); 
            cardLayout.show(contentArea, "MAIN_VIEW"); refreshAllViews();
        }));
        shortcutsPanel.add(createShortcutItem("Groups", groupSubtext, 1, () -> {
            currentViewMode = "GROUPS"; cardLayout.show(contentArea, "GROUPS_VIEW"); refreshGroupsDashboard();
        }));

        listModel = new DefaultListModel<>();
        contactList = new JList<>(listModel);
        contactList.setCellRenderer(new ContactCellRenderer());
        contactList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && contactList.getSelectedValue() != null) {
                Contact c = contactList.getSelectedValue();
                if(!currentViewMode.equals("TRASH")) manager.recents.add(c);
                detailsPanel.loadContact(c);
            }
        });

        JPanel scrollContentWrapper = new JPanel(new BorderLayout());
        scrollContentWrapper.setOpaque(false);
        scrollContentWrapper.add(shortcutsPanel, BorderLayout.NORTH);
        scrollContentWrapper.add(contactList, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(scrollContentWrapper);
        ModernUI.makeModernScroll(scrollPane);

        // NEW: Floating Back Button for Groups
        btnBackToGroups = new ModernUI.ModernButton("← All groups", new Color(30, 30, 30), Color.WHITE);
        btnBackToGroups.addActionListener(e -> {
            currentViewMode = "GROUPS";
            cardLayout.show(contentArea, "GROUPS_VIEW");
            refreshGroupsDashboard();
        });
        JPanel bottomBtnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomBtnWrapper.setOpaque(false);
        bottomBtnWrapper.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottomBtnWrapper.add(btnBackToGroups);

        listWrapper.add(fixedTopContainer, BorderLayout.NORTH);
        listWrapper.add(scrollPane, BorderLayout.CENTER);
        listWrapper.add(bottomBtnWrapper, BorderLayout.SOUTH);

        // Details Panel
        detailsPanel = new ContactDetails(this);
        detailsPanel.setVisible(false);
        JPanel detailsWrapper = new JPanel(new BorderLayout());
        detailsWrapper.setBorder(new EmptyBorder(20,20,20,20));
        detailsWrapper.setOpaque(false);
        detailsWrapper.add(detailsPanel, BorderLayout.CENTER);

        mainView.add(listWrapper, BorderLayout.WEST);
        mainView.add(detailsWrapper, BorderLayout.CENTER);

        // --- 2. NEW GROUPS DASHBOARD VIEW ---
        initGroupsDashboard();

        // Add Views to CardLayout
        contentArea.add(mainView, "MAIN_VIEW");
        contentArea.add(groupsDashboardPanel, "GROUPS_VIEW");
        contentArea.add(new SettingsPanel(this), "SETTINGS_VIEW");

        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(contentArea, BorderLayout.CENTER);

        applyTheme();
        refreshAllViews();
    }

    // Constructs the Grid View for Groups
    private void initGroupsDashboard() {
        groupsDashboardPanel = new JPanel(new BorderLayout());
        groupsDashboardPanel.setOpaque(false);
        groupsDashboardPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        groupsTitle = new JLabel("Groups");
        groupsTitle.setFont(ThemeManager.getFont(Font.BOLD, 32));
        groupsSubtitle = new JLabel("...");
        groupsSubtitle.setFont(ThemeManager.getFont(Font.PLAIN, 15));
        
        header.add(groupsTitle);
        header.add(groupsSubtitle);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));

        groupsGridPanel = new JPanel(new GridLayout(0, 2, 0, 0)); // 2 Column Grid
        groupsGridPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(groupsGridPanel);
        ModernUI.makeModernScroll(scroll);

        groupsDashboardPanel.add(header, BorderLayout.NORTH);
        groupsDashboardPanel.add(scroll, BorderLayout.CENTER);
    }

    // Fills the Grid View with current groups
    private void refreshGroupsDashboard() {
        groupsGridPanel.removeAll();
        int totalStarred = manager.getFavoritesList().size();
        groupsSubtitle.setText(manager.contactsList.size() + " contacts · " + totalStarred + " starred");
        groupsTitle.setForeground(ThemeManager.textPrimary);
        groupsSubtitle.setForeground(ThemeManager.textSecondary);

        Color[] groupColors = {
            new Color(234, 67, 53), // Orange/Red
            new Color(52, 168, 83), // Green
            new Color(66, 133, 244), // Blue
            new Color(156, 39, 176)  // Purple
        };

        int colorIdx = 0;
        for (String gName : manager.groups.keySet()) {
            if (gName.trim().isEmpty()) continue;
            
            List<Contact> gContacts = manager.groups.get(gName);
            int count = gContacts.size();
            Color iconColor = groupColors[colorIdx % groupColors.length];
            
            ModernUI.RoundedPanel card = new ModernUI.RoundedPanel(20);
            card.setBackground(ThemeManager.bgSecondary);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(new EmptyBorder(25, 25, 25, 25));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel icon = new JLabel(new ModernUI.CustomShapeIcon(1, iconColor, 50));
            icon.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel nameLbl = new JLabel(gName);
            nameLbl.setFont(ThemeManager.getFont(Font.BOLD, 20));
            nameLbl.setForeground(ThemeManager.textPrimary);
            nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JLabel countLbl = new JLabel(count + (count == 1 ? " contact" : " contacts"));
            countLbl.setFont(ThemeManager.getFont(Font.PLAIN, 14));
            countLbl.setForeground(ThemeManager.textSecondary);
            countLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            card.add(icon);
            card.add(Box.createRigidArea(new Dimension(0, 15)));
            card.add(nameLbl);
            card.add(Box.createRigidArea(new Dimension(0, 5)));
            card.add(countLbl);

            card.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    selectedGroup = gName;
                    currentViewMode = "SPECIFIC_GROUP";
                    cardLayout.show(contentArea, "MAIN_VIEW");
                    refreshAllViews();
                }
                public void mouseEntered(MouseEvent e) { card.setBackground(ThemeManager.hoverColor); card.repaint(); }
                public void mouseExited(MouseEvent e) { card.setBackground(ThemeManager.bgSecondary); card.repaint(); }
            });

            groupsGridPanel.add(card);
            colorIdx++;
        }
        groupsGridPanel.revalidate();
        groupsGridPanel.repaint();
    }

    private ModernUI.ModernButton createNavBtn(String txt, String cardName, String mode, Runnable action) {
        ModernUI.ModernButton b = new ModernUI.ModernButton(txt, new Color(0,0,0,0), ThemeManager.textPrimary);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(ThemeManager.hoverColor); b.setOpaque(true); }
            public void mouseExited(MouseEvent e) { b.setBackground(new Color(0,0,0,0)); b.setOpaque(false); }
        });
        b.addActionListener(e -> {
            currentViewMode = mode;
            cardLayout.show(contentArea, cardName);
            if (action != null) action.run();
        });
        return b;
    }

    private JPanel createShortcutItem(String title, JLabel subtextLbl, int iconType, Runnable action) {
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setOpaque(false); p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel icon = new JLabel(new ModernUI.CustomShapeIcon(iconType, ThemeManager.warning, 45));
        
        JPanel textP = new JPanel(new GridLayout(2,1)); textP.setOpaque(false);
        JLabel titleLbl = new JLabel(title); 
        titleLbl.setFont(ThemeManager.getFont(Font.PLAIN, 16));
        subtextLbl.setFont(ThemeManager.getFont(Font.PLAIN, 13));
        textP.add(titleLbl); textP.add(subtextLbl);
        
        JLabel chevron = new JLabel(ModernUI.SYMBOL_CHEVRON); chevron.setFont(ThemeManager.getFont(Font.PLAIN, 20));
        
        p.add(icon, BorderLayout.WEST); p.add(textP, BorderLayout.CENTER); p.add(chevron, BorderLayout.EAST);
        p.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { p.setBackground(ThemeManager.hoverColor); p.setOpaque(true); p.repaint(); }
            public void mouseExited(MouseEvent e) { p.setOpaque(false); p.repaint(); }
            public void mouseClicked(MouseEvent e) { action.run(); }
        });

        p.putClientProperty("isShortcut", true);
        titleLbl.putClientProperty("isShortcutTitle", true);
        subtextLbl.putClientProperty("isShortcutSub", true);
        chevron.putClientProperty("isChevron", true);
        return p;
    }

    private void doSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { refreshAllViews(); return; }
        listModel.clear();
        Contact exact = SearchManager.binarySearch(manager.contactsList, q);
        if (exact != null) listModel.addElement(exact);
        for (Contact c : manager.contactsList) {
            if (c.getName().toLowerCase().contains(q.toLowerCase()) && c != exact) {
                listModel.addElement(c);
            }
        }
    }

    public void loadList(List<Contact> list) {
        listModel.clear();
        MergeSort.sort(list);
        for (Contact c : list) listModel.addElement(c);
        detailsPanel.setVisible(false);
    }

    public void refreshAllViews() {
        // Toggle specific elements based on view mode
        boolean isAllView = currentViewMode.equals("ALL");
        boolean isGroupView = currentViewMode.equals("SPECIFIC_GROUP");
        
        shortcutsPanel.setVisible(isAllView);
        btnBackToGroups.setVisible(isGroupView); // Show back button ONLY in group view

        int total = manager.contactsList.size();
        int favs = manager.getFavoritesList().size();
        
        // Count active groups (ignore empty ones)
        int activeGroups = 0;
        for (List<Contact> g : manager.groups.values()) if(!g.isEmpty()) activeGroups++;
        
        favSubtext.setText(favs + " starred");
        groupSubtext.setText(activeGroups + (activeGroups == 1 ? " group" : " groups"));

        if(isAllView) {
            viewSubtitle.setText(total + " contacts · " + favs + " starred");
            loadList(manager.contactsList);
        }
        else if(currentViewMode.equals("FAV")) {
            viewSubtitle.setText(favs + " starred");
            loadList(manager.getFavoritesList());
        }
        else if(currentViewMode.equals("RECENT")) {
            viewSubtitle.setText(manager.recents.getItems().size() + " recent");
            loadList(manager.recents.getItems());
        }
        else if(isGroupView) {
            // Load ONLY contacts matching the selected group
            List<Contact> groupContacts = new ArrayList<>();
            int groupFavs = 0;
            for(Contact c : manager.contactsList) {
                if(c.getGroup().equalsIgnoreCase(selectedGroup)) {
                    groupContacts.add(c);
                    if(manager.isFavorite(c)) groupFavs++;
                }
            }
            viewTitle.setText(selectedGroup);
            viewSubtitle.setText(groupContacts.size() + " contacts · " + groupFavs + " starred");
            loadList(groupContacts);
        }
        else if(currentViewMode.equals("TRASH")) {
            viewSubtitle.setText(manager.getTrashList().size() + " deleted");
            loadList(manager.getTrashList());
        }
    }

    private void showAddDialog() {
        JDialog d = new JDialog(this, "New Contact", true);
        d.setSize(350, 400); d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(ThemeManager.bgPrimary);
        
        JPanel p = new JPanel(new GridLayout(5, 1, 10, 15)); p.setOpaque(false); p.setBorder(new EmptyBorder(20, 20, 20, 20));
        ModernUI.ModernTextField fName = new ModernUI.ModernTextField("Full Name");
        ModernUI.ModernTextField fPhone = new ModernUI.ModernTextField("Phone Number");
        ModernUI.ModernTextField fEmail = new ModernUI.ModernTextField("Email Address");
        ModernUI.ModernTextField fGroup = new ModernUI.ModernTextField("Group (e.g. Family)");
        
        ModernUI.ModernButton btnSave = new ModernUI.ModernButton("Save Contact", ThemeManager.accent, Color.WHITE);
        btnSave.addActionListener(e -> {
            if (fName.getText().trim().isEmpty()) return;
            manager.addContact(new Contact(UUID.randomUUID().toString(), fName.getText(), fPhone.getText(), fEmail.getText(), fGroup.getText()));
            refreshAllViews(); d.dispose();
        });
        fName.setForeground(ThemeManager.textPrimary); fPhone.setForeground(ThemeManager.textPrimary);
        fEmail.setForeground(ThemeManager.textPrimary); fGroup.setForeground(ThemeManager.textPrimary);
        
        p.add(fName); p.add(fPhone); p.add(fEmail); p.add(fGroup); p.add(btnSave);
        d.add(p); d.setVisible(true);
    }

    public void showEditDialog(Contact c) {
        JDialog d = new JDialog(this, "Edit Contact", true);
        d.setSize(350, 400); d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(ThemeManager.bgPrimary);
        
        JPanel p = new JPanel(new GridLayout(5, 1, 10, 15)); p.setOpaque(false); p.setBorder(new EmptyBorder(20, 20, 20, 20));
        ModernUI.ModernTextField fName = new ModernUI.ModernTextField("Full Name"); fName.setText(c.getName());
        ModernUI.ModernTextField fPhone = new ModernUI.ModernTextField("Phone Number"); fPhone.setText(c.getPhone());
        ModernUI.ModernTextField fEmail = new ModernUI.ModernTextField("Email Address"); fEmail.setText(c.getEmail());
        ModernUI.ModernTextField fGroup = new ModernUI.ModernTextField("Group"); fGroup.setText(c.getGroup());
        
        fName.setForeground(ThemeManager.textPrimary); fPhone.setForeground(ThemeManager.textPrimary);
        fEmail.setForeground(ThemeManager.textPrimary); fGroup.setForeground(ThemeManager.textPrimary);

        ModernUI.ModernButton btnSave = new ModernUI.ModernButton("Update Contact", ThemeManager.accent, Color.WHITE);
        btnSave.addActionListener(e -> {
            if (fName.getText().trim().isEmpty()) return;
            
            // FIX: Properly remove and re-add to keep HashMaps & Trees perfectly sorted!
            manager.deleteContact(c);
            manager.trashCan.pop(); // Remove from trash since it was an edit, not a delete
            
            c.setName(fName.getText()); c.setPhone(fPhone.getText());
            c.setEmail(fEmail.getText()); c.setGroup(fGroup.getText());
            
            manager.addContact(c);
            
            detailsPanel.loadContact(c);
            refreshAllViews(); d.dispose();
        });
        
        p.add(fName); p.add(fPhone); p.add(fEmail); p.add(fGroup); p.add(btnSave);
        d.add(p); d.setVisible(true);
    }

    public void applyTheme() {
        getContentPane().setBackground(ThemeManager.bgPrimary);
        sidebar.setBackground(ThemeManager.bgSecondary);
        contentArea.setBackground(ThemeManager.bgPrimary);
        contactList.setBackground(ThemeManager.bgPrimary);
        searchField.setForeground(ThemeManager.textPrimary);
        
        viewTitle.setForeground(ThemeManager.textPrimary);
        viewSubtitle.setForeground(ThemeManager.textSecondary);
        groupsDashboardPanel.setBackground(ThemeManager.bgPrimary);
        
        updateComponentColors(this);
        detailsPanel.applyTheme();
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void updateComponentColors(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                if (l.getClientProperty("isShortcutTitle") != null) l.setForeground(ThemeManager.textPrimary);
                if (l.getClientProperty("isShortcutSub") != null) l.setForeground(ThemeManager.textSecondary);
                if (l.getClientProperty("isChevron") != null) l.setForeground(ThemeManager.textSecondary);
            }
            if (c instanceof ModernUI.ModernButton && c != btnBackToGroups) {
                ((ModernUI.ModernButton)c).setForeground(ThemeManager.textPrimary);
            }
            if (c instanceof Container) updateComponentColors((Container) c);
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    class ContactCellRenderer extends DefaultListCellRenderer {
        private JPanel wrapper = new JPanel(new BorderLayout());
        private JLabel headerLbl = new JLabel();
        private JPanel p = new JPanel(new BorderLayout(15, 0));
        private JLabel lblAvatar = new JLabel(), lblName = new JLabel(), lblSub = new JLabel();
        private JPanel textPanel = new JPanel(new GridLayout(2,1));
        
        public ContactCellRenderer() {
            wrapper.setOpaque(false);
            headerLbl.setFont(ThemeManager.getFont(Font.BOLD, 14));
            headerLbl.setBorder(new EmptyBorder(10, 15, 5, 15));
            
            p.setBorder(new EmptyBorder(10, 15, 10, 15));
            lblName.setFont(ThemeManager.getFont(Font.PLAIN, 16));
            lblSub.setFont(ThemeManager.getFont(Font.PLAIN, 13));
            textPanel.setOpaque(false);
            textPanel.add(lblName); textPanel.add(lblSub);
            p.add(lblAvatar, BorderLayout.WEST); p.add(textPanel, BorderLayout.CENTER);
        }
        
        @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSel, boolean cellHasFocus) {
            wrapper.removeAll();
            Contact c = (Contact) value;
            
            String displayName = c.getName();
            if (manager.isFavorite(c)) displayName += " " + ModernUI.SYMBOL_STAR;
            lblName.setText(displayName);
            
            lblName.setForeground(isSel ? Color.WHITE : ThemeManager.textPrimary);
            lblSub.setForeground(isSel ? Color.LIGHT_GRAY : ThemeManager.textSecondary);
            lblAvatar.setIcon(new ModernUI.AvatarIcon(c.getInitial(), c.getAvatarColor(), 45));
            lblSub.setText(c.getGroup());
            
            p.setBackground(isSel ? ThemeManager.accent : ThemeManager.bgPrimary);
            p.setOpaque(isSel);

            boolean showHeader = false;
            // Headers show in ALL, FAV, and SPECIFIC_GROUP modes
            if (currentViewMode.equals("ALL") || currentViewMode.equals("FAV") || currentViewMode.equals("SPECIFIC_GROUP")) {
                if (index == 0) showHeader = true;
                else {
                    Contact prev = listModel.getElementAt(index - 1);
                    if (!prev.getInitial().equals(c.getInitial())) showHeader = true;
                }
            }

            if (showHeader) {
                headerLbl.setText(c.getInitial());
                headerLbl.setForeground(ThemeManager.textSecondary);
                wrapper.add(headerLbl, BorderLayout.NORTH);
            }
            wrapper.add(p, BorderLayout.CENTER);
            return wrapper;
        }
    }
}