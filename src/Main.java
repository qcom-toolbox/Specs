//Main.java

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        if (isMac()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "Specs");
            System.setProperty("apple.awt.application.appearance", resolveMacStartupAppearance());
        }

        if (isWayland()) {
            System.setProperty("_JAVA_AWT_WM_NONREPARENTING", "1");
        }

        javax.swing.SwingUtilities.invokeLater(() -> GUI.main(args));
    }

    private static boolean isMac() {
        return System.getProperty("os.name", "").toLowerCase().contains("mac");
    }

    private static boolean isWayland() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (!os.contains("linux") && !os.contains("nux")) {
            return false;
        }
        
        String xdgSessionType = System.getenv("XDG_SESSION_TYPE");
        String waylandDisplay = System.getenv("WAYLAND_DISPLAY");
        
        return "wayland".equalsIgnoreCase(xdgSessionType) || waylandDisplay != null;
    }

    private static String resolveMacStartupAppearance() {
        String theme = readSavedTheme();
        if ("Dark".equalsIgnoreCase(theme)) {
            return "NSAppearanceNameDarkAqua";
        }
        if ("Light".equalsIgnoreCase(theme)) {
            return "NSAppearanceNameLightAqua";
        }
        return "NSAppearanceNameAqua";
    }

    private static String readSavedTheme() {
        Path preferences = Path.of(System.getProperty("user.home"), ".specs", "preferences.properties");
        if (!Files.exists(preferences)) {
            return "System";
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(preferences)) {
            properties.load(input);
            return properties.getProperty("theme", "System");
        } catch (IOException e) {
            return "System";
        }
    }
}
