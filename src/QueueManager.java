import java.io.Serializable;
import java.util.LinkedList;

public class QueueManager<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int limit;
    private final LinkedList<T> queue = new LinkedList<>();
    public QueueManager(int limit) { this.limit = limit; }
    public void add(T item) {
        queue.remove(item); queue.addFirst(item);
        if (queue.size() > limit) queue.removeLast();
    }
    public LinkedList<T> getItems() { return queue; }
}