import java.io.Serializable;
import java.awt.Color;
import java.util.Random;

public class Contact implements Serializable, Comparable<Contact> {
    private static final long serialVersionUID = 1L;
    private String id, name, phone, email, group;
    private Color avatarColor;

    public Contact(String id, String name, String phone, String email, String group) {
        this.id = id; this.name = name.trim(); this.phone = phone; 
        this.email = email; this.group = group.isEmpty() ? "General" : group;
        
        Color[] colors = {new Color(234, 67, 53), new Color(66, 133, 244), 
                          new Color(52, 168, 83), new Color(251, 188, 5), new Color(156, 39, 176)};
        this.avatarColor = colors[new Random().nextInt(colors.length)];
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getGroup() { return group; }
    public Color getAvatarColor() { return avatarColor; }
    
    public String getInitial() { 
        if (name == null || name.isEmpty()) return "#";
        char first = name.toUpperCase().charAt(0);
        return Character.isLetter(first) ? String.valueOf(first) : "#";
    }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setGroup(String group) { this.group = group; }

    @Override
    public int compareTo(Contact other) { return this.name.compareToIgnoreCase(other.name); }
}