import java.io.*;
public class BackupManager {
    private static final String FILE_NAME = "contacts_backup.dat";
    public static void saveBackup(ContactManager manager) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) { oos.writeObject(manager); } catch (Exception e) {}
    }
    public static ContactManager loadBackup() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new ContactManager();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (ContactManager) ois.readObject();
        } catch (Exception e) { return new ContactManager(); }
    }
}