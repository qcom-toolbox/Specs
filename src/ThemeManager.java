// ThemeManager.java
// Applies and persists a FlatLaf theme. This is what actually gives the
// app its modern look (rounded controls, flat colors, dark mode, etc.)
// and lets the user pick between several bundled IntelliJ-derived themes.

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatNordIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class ThemeManager {

    private static final Preferences PREFS = Preferences.userNodeForPackage(ThemeManager.class);
    private static final String PREF_KEY = "theme";
    private static final String DEFAULT_THEME = "Light";

    // Order here is the order shown in the Theme menu.
    public static final Map<String, Class<? extends LookAndFeel>> THEMES = new LinkedHashMap<>();

    static {
        THEMES.put("Light", FlatLightLaf.class);
        THEMES.put("Dark", FlatDarkLaf.class);
        THEMES.put("Arc Orange", FlatArcOrangeIJTheme.class);
        THEMES.put("One Dark", FlatOneDarkIJTheme.class);
        THEMES.put("Nord", FlatNordIJTheme.class);
        THEMES.put("Solarized Light", FlatSolarizedLightIJTheme.class);
    }

    /** Call this once, before any Swing component is created. */
    public static void applyStartupTheme() {
        apply(PREFS.get(PREF_KEY, DEFAULT_THEME));
    }

    /** Switches theme at runtime; caller is responsible for updating already-visible windows. */
    public static void apply(String themeName) {
        Class<? extends LookAndFeel> lafClass = THEMES.getOrDefault(themeName, FlatLightLaf.class);
        try {
            UIManager.setLookAndFeel(lafClass.getDeclaredConstructor().newInstance());
            PREFS.put(PREF_KEY, THEMES.containsKey(themeName) ? themeName : DEFAULT_THEME);
        } catch (Exception e) {
            System.err.println("Failed to apply theme '" + themeName + "': " + e.getMessage());
        }
    }

    public static String getCurrentThemeName() {
        return PREFS.get(PREF_KEY, DEFAULT_THEME);
    }
}
