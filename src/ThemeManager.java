import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class ThemeManager {

    public enum ThemeMode {
        SYSTEM("System"),
        LIGHT("Light"),
        DARK("Dark");

        private final String label;

        ThemeMode(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static ThemeMode fromLabel(String label) {
            for (ThemeMode mode : values()) {
                if (mode.label.equalsIgnoreCase(label)) {
                    return mode;
                }
            }
            return SYSTEM;
        }
    }

    public enum ViewMode {
        TABBED("Tabbed"),
        LEGACY("Legacy");

        private final String label;

        ViewMode(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static ViewMode fromLabel(String label) {
            for (ViewMode mode : values()) {
                if (mode.label.equalsIgnoreCase(label)) {
                    return mode;
                }
            }
            return TABBED;
        }
    }

    public static final class Palette {
        public final Color windowBackground;
        public final Color panelBackground;
        public final Color textPrimary;
        public final Color textSecondary;
        public final Color border;
        public final Color tabSelected;
        public final Color tabBackground;
        public final Color accent;

        public Palette(Color windowBackground, Color panelBackground, Color textPrimary,
                       Color textSecondary, Color border, Color tabSelected,
                       Color tabBackground, Color accent) {
            this.windowBackground = windowBackground;
            this.panelBackground = panelBackground;
            this.textPrimary = textPrimary;
            this.textSecondary = textSecondary;
            this.border = border;
            this.tabSelected = tabSelected;
            this.tabBackground = tabBackground;
            this.accent = accent;
        }

        public boolean isDark() {
            return luminance(windowBackground) < 0.5;
        }

        private static double luminance(Color color) {
            return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
        }
    }

    private static final Palette LIGHT_PALETTE = new Palette(
            new Color(249, 250, 252),
            new Color(255, 255, 255),
            new Color(15, 23, 42),
            new Color(100, 116, 139),
            new Color(226, 232, 240),
            new Color(255, 255, 255),
            new Color(241, 245, 249),
            new Color(59, 130, 246)
    );

    private static final Palette DARK_PALETTE = new Palette(
            new Color(15, 23, 42),
            new Color(30, 41, 59),
            new Color(248, 250, 252),
            new Color(148, 163, 184),
            new Color(51, 65, 85),
            new Color(30, 41, 59),
            new Color(15, 23, 42),
            new Color(96, 165, 250)
    );

    private static final Path PREFERENCES_PATH = Path.of(
            System.getProperty("user.home"), ".specs", "preferences.properties"
    );

    private static ThemeMode currentMode = ThemeMode.SYSTEM;
    private static Palette currentPalette = LIGHT_PALETTE;
    private static ViewMode currentViewMode = ViewMode.TABBED;
    private static Window mainWindow;

    private ThemeManager() {
    }

    public static void initialize(Window window) {
        mainWindow = window;
        loadPreference();
        applyTheme();
        startSystemThemeWatcher();
    }

    public static ThemeMode getMode() {
        return currentMode;
    }

    public static Palette getPalette() {
        return currentPalette;
    }

    public static Color blendColors(Color c1, Color c2, double ratio) {
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        int a = (int) (c1.getAlpha() * (1 - ratio) + c2.getAlpha() * ratio);
        return new Color(r, g, b, a);
    }

    public static ViewMode getViewMode() {
        return currentViewMode;
    }

    public static void setMode(ThemeMode mode) {
        currentMode = mode;
        savePreference();
        applyTheme();
    }

    public static void setViewMode(ViewMode mode) {
        currentViewMode = mode;
        savePreference();
    }

    public static void applyTheme() {
        boolean dark = resolveDarkMode();
        currentPalette = dark ? DARK_PALETTE : LIGHT_PALETTE;
        applyUIManagerDefaults(currentPalette);
        applyPlatformWindowAppearance();

        for (Window window : Window.getWindows()) {
            if (window instanceof RootPaneContainer) {
                SwingUtilities.updateComponentTreeUI(window);
            }
            applyWindowBackground(window, currentPalette.windowBackground);
            applyToComponentTree(window);
            window.repaint();
        }
    }

    private static void applyWindowBackground(Window window, Color background) {
        window.setBackground(background);
        if (window instanceof RootPaneContainer rootPaneContainer) {
            JRootPane rootPane = rootPaneContainer.getRootPane();
            if (rootPane != null) {
                rootPane.setOpaque(true);
                rootPane.setBackground(background);
                rootPane.getContentPane().setBackground(background);
                rootPane.getLayeredPane().setBackground(background);
            }
        }
    }

    private static void applyPlatformWindowAppearance() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac")) {
            applyMacWindowAppearance();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void applyMacWindowAppearance() {
        try {
            Class<?> appClass = Class.forName("com.apple.eawt.Application");
            Object app = appClass.getMethod("getApplication").invoke(null);
            Class<?> modeClass = Class.forName("com.apple.laf.aqua.AquaAppearance$AppearanceMode");
            String modeName = switch (currentMode) {
                case DARK -> "DARK";
                case LIGHT -> "LIGHT";
                case SYSTEM -> "SYSTEM";
            };
            Object mode = Enum.valueOf((Class<Enum>) modeClass, modeName);
            appClass.getMethod("setAppearance", modeClass).invoke(app, mode);
        } catch (ReflectiveOperationException ignored) {
            // Older JDKs fall back to Swing-only theming.
        }
    }

    public static void applyToComponentTree(Component component) {
        if (component instanceof JComponent jComponent) {
            styleComponent(jComponent);
        }
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyToComponentTree(child);
            }
        }
    }

    public static void applyToWindow(Window window) {
        applyWindowBackground(window, currentPalette.windowBackground);
        applyToComponentTree(window);
        window.repaint();
    }

    private static void styleComponent(JComponent component) {
        Palette palette = currentPalette;

        if (component instanceof JPanel) {
            component.setBackground(palette.panelBackground);
            component.setForeground(palette.textPrimary);
            component.setOpaque(true);
        } else if (component instanceof JRootPane rootPane) {
            rootPane.setOpaque(true);
            rootPane.setBackground(palette.windowBackground);
        } else if (component instanceof JTabbedPane tabbedPane) {
            tabbedPane.setBackground(palette.windowBackground);
            tabbedPane.setForeground(palette.textPrimary);
            tabbedPane.setOpaque(true);
        } else if (component.getParent() instanceof JTabbedPane && component instanceof JPanel) {
            component.setBackground(palette.panelBackground);
            component.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        } else if (component instanceof JScrollPane scrollPane) {
            scrollPane.setBackground(palette.windowBackground);
            scrollPane.getViewport().setBackground(palette.panelBackground);
            scrollPane.setOpaque(true);
        } else if (component instanceof JTextPane textPane) {
            textPane.setBackground(new Color(0, 0, 0, 0));
            textPane.setForeground(palette.textPrimary);
            textPane.setOpaque(false);
        } else if (component instanceof JTextArea textArea) {
            textArea.setBackground(palette.panelBackground);
            textArea.setForeground(palette.textPrimary);
        } else if (component instanceof JLabel label) {
            label.setForeground(palette.textPrimary);
        } else if (component instanceof JMenuBar || component instanceof JMenu || component instanceof JMenuItem) {
            component.setBackground(palette.panelBackground);
            component.setForeground(palette.textPrimary);
        }

        if (component.getBorder() instanceof javax.swing.border.TitledBorder titledBorder) {
            titledBorder.setTitleColor(palette.textSecondary);
        }
    }

    private static boolean resolveDarkMode() {
        return switch (currentMode) {
            case DARK -> true;
            case LIGHT -> false;
            case SYSTEM -> isSystemDarkMode();
        };
    }

    static boolean isSystemDarkMode() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac")) {
            return isMacDarkMode();
        }
        if (os.contains("win")) {
            return isWindowsDarkMode();
        }
        return isLinuxDarkMode();
    }

    private static boolean isMacDarkMode() {
        try {
            Process process = new ProcessBuilder("defaults", "read", "-g", "AppleInterfaceStyle").start();
            String output = new String(process.getInputStream().readAllBytes()).trim();
            process.waitFor();
            return "Dark".equalsIgnoreCase(output);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isWindowsDarkMode() {
        try {
            Process process = new ProcessBuilder(
                    "reg", "query",
                    "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                    "/v", "AppsUseLightTheme"
            ).start();
            String output = new String(process.getInputStream().readAllBytes());
            process.waitFor();
            return output.contains("0x0");
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isLinuxDarkMode() {
        try {
            Process process = new ProcessBuilder(
                    "gsettings", "get", "org.gnome.desktop.interface", "color-scheme"
            ).start();
            String output = new String(process.getInputStream().readAllBytes()).trim();
            process.waitFor();
            if (output.toLowerCase().contains("dark")) {
                return true;
            }
        } catch (Exception ignored) {
        }

        try {
            Process process = new ProcessBuilder(
                    "gsettings", "get", "org.gnome.desktop.interface", "gtk-theme"
            ).start();
            String output = new String(process.getInputStream().readAllBytes()).trim().toLowerCase();
            process.waitFor();
            return output.contains("dark");
        } catch (Exception e) {
            return false;
        }
    }

    private static void applyUIManagerDefaults(Palette palette) {
        boolean dark = palette.isDark();

        UIManager.put("Panel.background", palette.panelBackground);
        UIManager.put("Viewport.background", palette.panelBackground);
        UIManager.put("TextArea.background", palette.panelBackground);
        UIManager.put("TextPane.background", palette.panelBackground);
        UIManager.put("TextField.background", palette.panelBackground);
        UIManager.put("ComboBox.background", palette.panelBackground);
        UIManager.put("List.background", palette.panelBackground);
        UIManager.put("Table.background", palette.panelBackground);
        UIManager.put("ScrollPane.background", palette.windowBackground);
        UIManager.put("ScrollBar.background", palette.windowBackground);
        UIManager.put("MenuBar.background", palette.panelBackground);
        UIManager.put("Menu.background", palette.panelBackground);
        UIManager.put("MenuItem.background", palette.panelBackground);
        UIManager.put("PopupMenu.background", palette.panelBackground);
        UIManager.put("OptionPane.background", palette.panelBackground);
        UIManager.put("Frame.background", palette.windowBackground);
        UIManager.put("Dialog.background", palette.windowBackground);
        UIManager.put("window", palette.windowBackground);
        UIManager.put("control", palette.panelBackground);

        UIManager.put("Panel.foreground", palette.textPrimary);
        UIManager.put("Label.foreground", palette.textPrimary);
        UIManager.put("TextArea.foreground", palette.textPrimary);
        UIManager.put("TextPane.foreground", palette.textPrimary);
        UIManager.put("TextField.foreground", palette.textPrimary);
        UIManager.put("Menu.foreground", palette.textPrimary);
        UIManager.put("MenuItem.foreground", palette.textPrimary);
        UIManager.put("OptionPane.messageForeground", palette.textPrimary);

        UIManager.put("TabbedPane.background", palette.windowBackground);
        UIManager.put("TabbedPane.foreground", palette.textPrimary);
        UIManager.put("TabbedPane.selected", palette.tabSelected);
        UIManager.put("TabbedPane.contentAreaColor", palette.windowBackground);
        UIManager.put("TabbedPane.unselectedBackground", palette.tabBackground);
        UIManager.put("TabbedPane.highlight", palette.accent);
        UIManager.put("TabbedPane.focus", palette.accent);
        UIManager.put("TabbedPane.borderHightlightColor", palette.border);
        UIManager.put("TabbedPane.darkShadow", palette.border);
        UIManager.put("TabbedPane.shadow", palette.border);

        // Modern scrollbar styling
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("ScrollBar.thumb", dark ? new Color(80, 80, 85) : new Color(180, 180, 185));
        UIManager.put("ScrollBar.thumbDarkShadow", dark ? new Color(60, 60, 65) : new Color(160, 160, 165));
        UIManager.put("ScrollBar.thumbShadow", dark ? new Color(70, 70, 75) : new Color(170, 170, 175));
        UIManager.put("ScrollBar.track", palette.windowBackground);
        UIManager.put("ScrollBar.trackHighlight", palette.tabBackground);

        if (dark) {
            UIManager.put("nimbusBase", palette.panelBackground);
            UIManager.put("nimbusBlueGrey", palette.tabBackground);
            UIManager.put("control", palette.panelBackground);
        }
    }

    private static void startSystemThemeWatcher() {
        Timer timer = new Timer(3000, e -> {
            if (currentMode == ThemeMode.SYSTEM) {
                boolean wasDark = currentPalette.isDark();
                boolean isDark = isSystemDarkMode();
                if (wasDark != isDark) {
                    applyTheme();
                }
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private static void loadPreference() {
        Properties properties = new Properties();
        if (Files.exists(PREFERENCES_PATH)) {
            try (InputStream input = Files.newInputStream(PREFERENCES_PATH)) {
                properties.load(input);
                currentMode = ThemeMode.fromLabel(properties.getProperty("theme", ThemeMode.SYSTEM.getLabel()));
                currentViewMode = ViewMode.fromLabel(properties.getProperty("view", ViewMode.TABBED.getLabel()));
            } catch (IOException e) {
                currentMode = ThemeMode.SYSTEM;
                currentViewMode = ViewMode.TABBED;
            }
        }
    }

    private static void savePreference() {
        try {
            Files.createDirectories(PREFERENCES_PATH.getParent());
            Properties properties = new Properties();
            if (Files.exists(PREFERENCES_PATH)) {
                try (InputStream input = Files.newInputStream(PREFERENCES_PATH)) {
                    properties.load(input);
                }
            }
            properties.setProperty("theme", currentMode.getLabel());
            properties.setProperty("view", currentViewMode.getLabel());
            try (OutputStream output = Files.newOutputStream(PREFERENCES_PATH)) {
                properties.store(output, "Specs preferences");
            }
        } catch (IOException e) {
            System.err.println("Could not save theme preference: " + e.getMessage());
        }
    }
}
