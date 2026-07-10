import java.io.Serializable;
public class BST implements Serializable {
    private static final long serialVersionUID = 1L;
    class Node implements Serializable { Contact contact; Node left, right; Node(Contact c) { contact = c; } }
    private Node root;
    public void insert(Contact c) { root = insertRec(root, c); }
    private Node insertRec(Node root, Contact c) {
        if (root == null) return new Node(c);
        if (c.compareTo(root.contact) < 0) root.left = insertRec(root.left, c);
        else if (c.compareTo(root.contact) > 0) root.right = insertRec(root.right, c);
        return root;
    }
}