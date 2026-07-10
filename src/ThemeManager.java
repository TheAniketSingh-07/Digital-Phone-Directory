import java.awt.*;

public class ThemeManager {
    public static boolean isDarkMode = false; // Default to Light Mode

    public static Color bgPrimary;
    public static Color bgSecondary;
    public static Color textPrimary;
    public static Color textSecondary;
    public static Color accent = new Color(26, 115, 232);      // Blue
    public static Color warning = new Color(245, 158, 11);     // Orange (for Favourites/Groups icons)
    public static Color inputBg;
    public static Color border;
    public static Color hoverColor;

    public static String fontFamily = "Segoe UI";

    static {
        setLightTheme(); 
    }

    public static void setDarkTheme() {
        isDarkMode = true;
        bgPrimary = new Color(18, 18, 18);
        bgSecondary = new Color(30, 30, 30);
        textPrimary = new Color(232, 234, 237);
        textSecondary = new Color(154, 160, 166);
        inputBg = new Color(45, 45, 45);
        border = new Color(60, 60, 60);
        hoverColor = new Color(45, 45, 45);
    }

    public static void setLightTheme() {
        isDarkMode = false;
        bgPrimary = new Color(255, 255, 255);
        bgSecondary = new Color(245, 247, 249);
        textPrimary = new Color(32, 33, 36);
        textSecondary = new Color(95, 99, 104);
        inputBg = new Color(241, 243, 244);
        border = new Color(218, 220, 224);
        hoverColor = new Color(232, 234, 237);
    }

    public static Font getFont(int style, int size) {
        return new Font(fontFamily, style, size);
    }
}