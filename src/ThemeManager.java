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
        public final Color surface1;
        public final Color surface2;
        public final Color surface3;
        public final Color success;
        public final Color warning;
        public final Color error;
        public final Color glow;
        public final Color glassOverlay;

        public Palette(Color windowBackground, Color panelBackground, Color textPrimary,
                       Color textSecondary, Color border, Color tabSelected,
                       Color tabBackground, Color accent, Color surface1, Color surface2,
                       Color surface3, Color success, Color warning, Color error,
                       Color glow, Color glassOverlay) {
            this.windowBackground = windowBackground;
            this.panelBackground = panelBackground;
            this.textPrimary = textPrimary;
            this.textSecondary = textSecondary;
            this.border = border;
            this.tabSelected = tabSelected;
            this.tabBackground = tabBackground;
            this.accent = accent;
            this.surface1 = surface1;
            this.surface2 = surface2;
            this.surface3 = surface3;
            this.success = success;
            this.warning = warning;
            this.error = error;
            this.glow = glow;
            this.glassOverlay = glassOverlay;
        }

        public boolean isDark() {
            return luminance(windowBackground) < 0.5;
        }

        private static double luminance(Color color) {
            return (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255.0;
        }
    }

    private static final Palette LIGHT_PALETTE = new Palette(
            new Color(250, 252, 255),
            new Color(255, 255, 255),
            new Color(30, 41, 59),
            new Color(71, 85, 105),
            new Color(203, 213, 225),
            new Color(255, 255, 255),
            new Color(241, 245, 249),
            new Color(99, 102, 241),
            new Color(250, 252, 255),
            new Color(245, 248, 252),
            new Color(230, 235, 242),
            new Color(34, 197, 94),
            new Color(234, 179, 8),
            new Color(239, 68, 68),
            new Color(99, 102, 241),
            new Color(255, 255, 255, 140)
    );

    private static final Palette DARK_PALETTE = new Palette(
            new Color(15, 17, 28),
            new Color(28, 30, 42),
            new Color(235, 240, 250),
            new Color(156, 172, 195),
            new Color(55, 60, 75),
            new Color(28, 30, 42),
            new Color(20, 22, 35),
            new Color(129, 140, 248),
            new Color(28, 30, 42),
            new Color(35, 38, 52),
            new Color(45, 48, 65),
            new Color(74, 222, 128),
            new Color(250, 204, 21),
            new Color(248, 113, 113),
            new Color(129, 140, 248),
            new Color(255, 255, 255, 100)
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

    public static Color createGlassOverlay(Color baseColor, double opacity) {
        return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 
                         (int) (255 * opacity));
    }

    public static Color createShadow(Color baseColor, int elevation) {
        double shadowOpacity = 0.08 + (elevation * 0.04);
        return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(),
                         (int) (255 * Math.min(shadowOpacity, 0.35)));
    }

    public static Color createGlow(Color accentColor, double intensity) {
        return new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(),
                         (int) (255 * Math.min(intensity, 0.5)));
    }

    public static double easeInOut(double t) {
        return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
    }

    public static double easeOut(double t) {
        return t * (2 - t);
    }

    public static double easeIn(double t) {
        return t * t;
    }

    public static Color interpolateColor(Color c1, Color c2, double t) {
        double easedT = easeInOut(t);
        return blendColors(c1, c2, easedT);
    }

    // Typography System
    public enum FontScale {
        DISPLAY(32f),
        HEADING_LARGE(24f),
        HEADING_MEDIUM(20f),
        HEADING_SMALL(18f),
        BODY_LARGE(16f),
        BODY(14f),
        BODY_SMALL(13f),
        CAPTION(12f),
        LABEL(11f);

        private final float size;

        FontScale(float size) {
            this.size = size;
        }

        public float getSize() {
            return size;
        }
    }

    public enum FontWeight {
        LIGHT(0.85f),
        REGULAR(1.0f),
        MEDIUM(1.25f),
        SEMIBOLD(1.5f),
        BOLD(2.0f);

        private final float weight;

        FontWeight(float weight) {
            this.weight = weight;
        }

        public float getWeight() {
            return weight;
        }
    }

    // Spacing Scale (4px base unit)
    public static final int SPACING_UNIT = 4;
    public static final int SPACING_XS = SPACING_UNIT;
    public static final int SPACING_SM = SPACING_UNIT * 2;
    public static final int SPACING_MD = SPACING_UNIT * 3;
    public static final int SPACING_LG = SPACING_UNIT * 4;
    public static final int SPACING_XL = SPACING_UNIT * 6;
    public static final int SPACING_XXL = SPACING_UNIT * 8;

    public static Font getFont(FontScale scale, FontWeight weight) {
        String os = System.getProperty("os.name", "").toLowerCase();
        String fontName;
        
        if (os.contains("mac")) {
            fontName = "SF Pro Text";
        } else if (os.contains("win")) {
            fontName = "Segoe UI";
        } else {
            fontName = "Roboto";
        }
        
        Font baseFont = new Font(fontName, Font.PLAIN, (int) scale.getSize());
        return baseFont.deriveFont(weight.getWeight() * scale.getSize());
    }

    public static Font getMonoFont(FontScale scale) {
        String os = System.getProperty("os.name", "").toLowerCase();
        String fontName;
        
        if (os.contains("mac")) {
            fontName = "SF Mono";
        } else if (os.contains("win")) {
            fontName = "Consolas";
        } else {
            fontName = "Monospace";
        }
        
        return new Font(fontName, Font.PLAIN, (int) scale.getSize());
    }

    // Animation constants
    public static final int ANIMATION_FAST = 150;
    public static final int ANIMATION_NORMAL = 250;
    public static final int ANIMATION_SLOW = 400;
    
    public static class AnimatedComponent {
        protected float animationProgress = 0f;
        protected boolean isAnimating = false;
        protected long animationStartTime = 0;
        protected int animationDuration = ANIMATION_NORMAL;
        
        public void startAnimation() {
            animationStartTime = System.currentTimeMillis();
            isAnimating = true;
            animationProgress = 0f;
        }
        
        public void updateAnimation() {
            if (!isAnimating) return;
            
            long elapsed = System.currentTimeMillis() - animationStartTime;
            animationProgress = Math.min(elapsed / (float) animationDuration, 1f);
            
            if (animationProgress >= 1f) {
                isAnimating = false;
            }
        }
        
        public float getAnimationProgress() {
            return animationProgress;
        }
        
        public boolean isAnimating() {
            return isAnimating;
        }
        
        public float getEasedProgress() {
            return (float) easeInOut(animationProgress);
        }
    }
    
    public static class FadeInComponent extends AnimatedComponent {
        public float getAlpha() {
            return getEasedProgress();
        }
    }
    
    public static class ScaleComponent extends AnimatedComponent {
        private float minScale = 0.95f;
        private float maxScale = 1.0f;
        
        public float getScale() {
            float eased = getEasedProgress();
            return minScale + (maxScale - minScale) * eased;
        }
    }
    
    public static class SlideComponent extends AnimatedComponent {
        private float startX = 0f;
        private float endX = 0f;
        
        public void setStartX(float x) {
            this.startX = x;
        }
        
        public void setEndX(float x) {
            this.endX = x;
        }
        
        public float getCurrentX() {
            float eased = getEasedProgress();
            return startX + (endX - startX) * eased;
        }
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

        // Modern scrollbar styling with enhanced appearance
        UIManager.put("ScrollBar.width", 14);
        UIManager.put("ScrollBar.thumb", dark ? new Color(90, 95, 110) : new Color(170, 175, 190));
        UIManager.put("ScrollBar.thumbDarkShadow", dark ? new Color(70, 75, 85) : new Color(150, 155, 170));
        UIManager.put("ScrollBar.thumbShadow", dark ? new Color(80, 85, 95) : new Color(160, 165, 180));
        UIManager.put("ScrollBar.track", palette.windowBackground);
        UIManager.put("ScrollBar.trackHighlight", palette.tabBackground);
        UIManager.put("ScrollBar.foreground", dark ? new Color(90, 95, 110) : new Color(170, 175, 190));
        UIManager.put("ScrollBar.background", palette.windowBackground);

        // Button styling
        UIManager.put("Button.background", palette.accent);
        UIManager.put("Button.foreground", dark ? Color.WHITE : Color.WHITE);
        UIManager.put("Button.focus", palette.glow);
        UIManager.put("Button.select", palette.surface2);

        if (dark) {
            UIManager.put("nimbusBase", palette.panelBackground);
            UIManager.put("nimbusBlueGrey", palette.tabBackground);
            UIManager.put("control", palette.panelBackground);
        }
    }

    private static void startSystemThemeWatcher() {
        Timer timer = new Timer(10000, e -> {
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
