import java.util.ArrayList;
import java.util.List;

public class MergeSort {
    public static void sort(List<Contact> list) {
        if (list.size() < 2) return;
        int mid = list.size() / 2;
        List<Contact> left = new ArrayList<>(list.subList(0, mid));
        List<Contact> right = new ArrayList<>(list.subList(mid, list.size()));
        sort(left); sort(right); merge(list, left, right);
    }
    private static void merge(List<Contact> list, List<Contact> left, List<Contact> right) {
        int i = 0, j = 0, k = 0;
        while (i < left.size() && j < right.size()) {
            if (left.get(i).compareTo(right.get(j)) <= 0) list.set(k++, left.get(i++));
            else list.set(k++, right.get(j++));
        }
        while (i < left.size()) list.set(k++, left.get(i++));
        while (j < right.size()) list.set(k++, right.get(j++));
    }
}