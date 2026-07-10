import java.io.Serializable;
import java.util.*;

public class ContactManager implements Serializable {
    private static final long serialVersionUID = 1L;
    public List<Contact> contactsList = new ArrayList<>();
    public HashSet<String> favorites = new HashSet<>();
    public HashMap<String, List<Contact>> groups = new HashMap<>();
    public QueueManager<Contact> recents = new QueueManager<>(10);
    public Stack<Contact> trashCan = new Stack<>();
    public BST bst = new BST();

    public void addContact(Contact c) {
        contactsList.add(c); bst.insert(c);
        groups.computeIfAbsent(c.getGroup(), k -> new ArrayList<>()).add(c);
    }

    public void deleteContact(Contact c) {
        contactsList.remove(c); favorites.remove(c.getId());
        if (groups.containsKey(c.getGroup())) groups.get(c.getGroup()).remove(c);
        trashCan.push(c);
    }

    public void markFavorite(Contact c) { favorites.add(c.getId()); }
    public void unmarkFavorite(Contact c) { favorites.remove(c.getId()); }
    public boolean isFavorite(Contact c) { return favorites.contains(c.getId()); }
    
    public List<Contact> getFavoritesList() {
        List<Contact> favs = new ArrayList<>();
        for(Contact c : contactsList) if(isFavorite(c)) favs.add(c);
        return favs;
    }
    
    public List<Contact> getTrashList() { return new ArrayList<>(trashCan); }
}