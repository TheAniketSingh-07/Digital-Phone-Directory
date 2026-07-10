import java.util.List;

public class SearchManager {
    public static Contact binarySearch(List<Contact> sortedList, String targetName) {
        int left = 0, right = sortedList.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            Contact midContact = sortedList.get(mid);
            int cmp = midContact.getName().compareToIgnoreCase(targetName);
            if (cmp == 0) return midContact;
            if (cmp < 0) left = mid + 1; else right = mid - 1;
        }
        return null;
    }
}