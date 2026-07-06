// InfoPanel.java

import platform.BsdOSInfo;
import platform.LinuxOSInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class InfoPanel {

    // General method to load an icon from a path
    private static ImageIcon loadIcon(String path) {
        try {
            return new ImageIcon(Objects.requireNonNull(InfoPanel.class.getResource(path), "Icon not found: " + path));
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
            return new ImageIcon();
        }
    }

    // Method to get the OS icon
    public static ImageIcon getOsIcon() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osVersion = System.getProperty("os.version", "").toLowerCase();

        String iconPath = getOsIconPath(osName, osVersion);
        return loadIcon(iconPath);
    }

    // Determine OS icon path based on the OS and version
    private static String getOsIconPath(String osName, String osVersion) {
        if (osName.contains("win")) {
            if (osName.contains("xp")) return "/icon/Windows XP 128x128.png";
            if (osName.contains("vista")) return "/icon/Windows Vista 128x128.png";
            if (osName.contains("7")) return "/icon/Windows 7 128x128.png";
            if (osName.contains("8")) return "/icon/Windows 8 128x128.png";
            if (osName.contains("8.1")) return "/icon/Windows 8.1 128x128.png";
            if (osName.contains("10")) return "/icon/Windows 10 128x128.png";
            if (osName.contains("11")) return "/icon/Windows 11 128x128.png";
            return "/icon/Microsoft Windows 128x128.png";
        } else if (osName.contains("mac")) {
            return getMacOsIconPath(osVersion);
        } else if (osName.contains("unix") || osName.contains("linux")) {
            return getLinuxOsIconPath(LinuxOSInfo.getLinuxOSVersion());
        } else if (osName.contains("bsd")) {
            return getBSDOsIconPath(BsdOSInfo.getBsdOSVersion());
        } else {
            return "/icon/Unknown 128x128.png";
        }
    }

    // Get macOS icon path based on version
    private static String getMacOsIconPath(String osVersion) {
        if (osVersion.contains("10.15")) return "/icon/Mac OS 10.15 128x128.png";
        if (osVersion.contains("10.14")) return "/icon/Mac OS 10.14 128x128.png";
        if (osVersion.contains("10.13")) return "/icon/Mac OS 10.13 128x128.png";
        if (osVersion.contains("10.12")) return "/icon/Mac OS 10.12 128x128.png";
        if (osVersion.contains("10.11")) return "/icon/Mac OS 10.11 128x128.png";
        if (osVersion.contains("10.10")) return "/icon/Mac OS 10.10 128x128.png";
        if (osVersion.contains("10.9")) return "/icon/Mac OS 10.9 128x128.png";
        if (osVersion.contains("10.8")) return "/icon/Mac OS 10.8 128x128.png";
        if (osVersion.contains("10.7")) return "/icon/Mac OS 10.7 128x128.png";
        if (osVersion.contains("10.6")) return "/icon/Mac OS 10.6 128x128.png";
        if (osVersion.contains("10.5")) return "/icon/Mac OS 10.5 128x128.png";
        if (osVersion.contains("10.4")) return "/icon/Mac OS 10.4 128x128.png";
        if (osVersion.contains("10.3")) return "/icon/Mac OS 10.3 128x128.png";
        if (osVersion.contains("10.2")) return "/icon/Mac OS 10.2 128x128.png";
        if (osVersion.contains("10.1")) return "/icon/Mac OS 10.1 128x128.png";
        if (osVersion.contains("10.0")) return "/icon/Mac OS 10.0 128x128.png";
        if (osVersion.contains("15")) return "/icon/Mac OS 15 128x128.png";
        if (osVersion.contains("14")) return "/icon/Mac OS 14 128x128.png";
        if (osVersion.contains("13")) return "/icon/Mac OS 13 128x128.png";
        if (osVersion.contains("12")) return "/icon/Mac OS 12 128x128.png";
        if (osVersion.contains("11")) return "/icon/Mac OS 11 128x128.png";
        if (osVersion.contains("26")) return "/icon/Mac OS 26 128x128.png";
        if (osVersion.contains("27")) return "/icon/Mac OS 27 128x128.png";
        return "/icon/Apple Mac OS 128x128.png";
    }

    // Get Linux OS icon path based on the version
    private static String getLinuxOsIconPath(String osVersion) {
        if (osVersion.toLowerCase().contains("ubuntu")) return "/icon/Ubuntu Linux 128x128.png";
        if (osVersion.toLowerCase().contains("debian")) return "/icon/Debian Linux 128x128.png";
        if (osVersion.toLowerCase().contains("fedora")) return "/icon/Fedora Linux 128x128.png";
        if (osVersion.toLowerCase().contains("arch")) return "/icon/Arch Linux 128x128.png";
        if (osVersion.toLowerCase().contains("gentoo")) return "/icon/Gentoo Linux 128x128.png";
        if (osVersion.toLowerCase().contains("pop")) return "/icon/POP OS Linux 128x128.png";
        if (osVersion.toLowerCase().contains("mint")) return "/icon/Linux Mint 128x128.png";
        if (osVersion.toLowerCase().contains("zorin")) return "/icon/Zorin OS Linux 128x128.png";
        if (osVersion.toLowerCase().contains("manjaro")) return "/icon/Manjaro Linux 128x128.png";
        if (osVersion.toLowerCase().contains("elementary")) return "/icon/Elementary OS Linux 128x128.png";
        if (osVersion.toLowerCase().contains("nix")) return "/icon/Nix OS Linux 128x128.png";
        if (osVersion.toLowerCase().contains("kali")) return "/icon/Kali Linux 128x128.png";
        if (osVersion.toLowerCase().contains("red")) return "/icon/Red Hat Linux 128x128.png";
        if (osVersion.toLowerCase().contains("hat")) return "/icon/Red Hat Linux 128x128.png";
        return "/icon/GNU Linux 128x128.png";
    }

    private static String getBSDOsIconPath(String osVersion) {
        if (osVersion.toLowerCase().contains("free")) return "/icon/Free BSD 128x128.png";
        if (osVersion.toLowerCase().contains("net")) return "/icon/Net BSD 128x128.png";
        if (osVersion.toLowerCase().contains("open")) return "/icon/Open BSD 128x128.png";
        if (osVersion.toLowerCase().contains("ghost")) return "/icon/Ghost BSD 128x128.png";
        return "/icon/BSD 128x128.png";
    }

    // Method to get the CPU icon
    public static ImageIcon getCpuIcon(String cpuInfo) {
        cpuInfo = cpuInfo.toLowerCase();
        String iconPath;

        if (cpuInfo.contains("intel")) {
            iconPath = "/icon/Intel CPU 128x128.png";
        } else if (cpuInfo.contains("ryzen")) {
            iconPath = "/icon/AMD Ryzen 128x128.png";
        } else if (cpuInfo.contains("amd")) {
            iconPath = "/icon/AMD 128x128.png";
        } else if (cpuInfo.contains("apple")) {
            iconPath = "/icon/Apple CPU 128x128.png";
        } else if (cpuInfo.contains("arm")) {
            iconPath = "/icon/ARM 128x128.png";
        } else if (cpuInfo.contains("mediatek")) {
            iconPath = "/icon/Mediatek 128x128.png";
        } else if (cpuInfo.contains("snapdragon")) {
            iconPath = "/icon/Snapdragon 128x128.png";
        } else {
            // Logo inconnu si aucun CPU reconnu
            iconPath = "/icon/Unknown CPU 128x128.png";
        }

        return loadIcon(iconPath);
    }

    // Method to get the RAM icon
    public static ImageIcon getRamIcon() {
        return loadIcon("/icon/RAM 128x128.png");
    }

    // Method to get the GPU icon
    public static ImageIcon getGpuIcon(String gpuInfo) {
        String iconPath;
        if (gpuInfo.toLowerCase().contains("arc")) {
            iconPath = "/icon/Intel ARC 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("intel")) {
            iconPath = "/icon/Intel GPU 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("amd")) {
            iconPath = "/icon/AMD Radeon 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("force")) {
            iconPath = "/icon/Nvidia 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("nvidia")) {
            iconPath = "/icon/Nvidia 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("ati")) {
            iconPath = "/icon/ATI Graphics 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("radeon")) {
            iconPath = "/icon/AMD Radeon 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("apple")) {
            iconPath = "/icon/Apple GPU 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("arm")) {
            iconPath = "/icon/ARM 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("mali")) {
            iconPath = "/icon/Mali 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("helio")) {
            iconPath = "/icon/Helio 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("adreno")) {
            iconPath = "/icon/Adreno 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("vmware")) {
            iconPath = "/icon/VM Ware 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("virtual")) {
                iconPath = "/icon/Virtual Machine 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("vm")) {
            iconPath = "/icon/Virtual Machine 128x128.png";
        } else if (gpuInfo.toLowerCase().contains("hd")) {
            iconPath = "/icon/Intel GPU 128x128.png";
        } else {
            iconPath = "/icon/Unknown GPU 128x128.png";
        }
        return loadIcon(iconPath);
    }

    // Method to create an information panel with transparent background
    public static JPanel createInfoPanel(String title, String info, ImageIcon icon) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        // Use JTextPane for transparency
        JTextPane textPane = new JTextPane();
        textPane.setText(info);
        textPane.setEditable(false);
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setBackground(new Color(0, 0, 0, 0));
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textPane.setFocusable(false);
        textPane.setOpaque(false);

        panel.add(iconLabel);
        panel.add(textPane);

        return panel;
    }
}