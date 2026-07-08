// platform/BsdGpuInfo.java

package platform;

import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BsdGpuInfo {

    public static String getGpuName() {
        String gpuName = getGpuNameFromSysctl();

        if (gpuName.equals("Unknown GPU")) {
            gpuName = getGpuNameFromPciconf();
            if (gpuName.equals("Unknown GPU")) {
                gpuName = getGpuNameFromOshi();
            }
        }

        return gpuName;
    }

    private static String getGpuNameFromOshi() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hal = systemInfo.getHardware();
            List<GraphicsCard> graphicsCards = hal.getGraphicsCards();

            if (!graphicsCards.isEmpty()) {
                GraphicsCard gpu = graphicsCards.get(0);
                return removeUnwantedParenthesesContent(gpu.getName());
            }
        } catch (Exception e) {
        }
        return "Unknown GPU";
    }

    private static String getGpuNameFromSysctl() {
        // Sur BSD, certaines cartes Intel/AMD/NVIDIA apparaissent via hw.vendor + hw.model
        String gpuName = "Unknown GPU";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "-n", "hw.model");
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.isEmpty()) {
                    gpuName = line.trim();
                }
            }
        } catch (IOException e) {
        }
        return gpuName;
    }

    private static String getGpuNameFromPciconf() {
        String gpuName = "Unknown GPU";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("pciconf", "-lv");
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains("vga")) {
                        String[] parts = line.split(":");
                        if (parts.length > 1) {
                            gpuName = removeUnwantedParenthesesContent(parts[1].trim());
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
        return gpuName;
    }

    public static long getGpuVram() {
        long vram = getVramFromOshi();

        if (vram == 0) {
            vram = getVramFromPciconf();
        }

        return vram;
    }

    private static long getVramFromOshi() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hal = systemInfo.getHardware();
            List<GraphicsCard> graphicsCards = hal.getGraphicsCards();

            if (!graphicsCards.isEmpty()) {
                GraphicsCard gpu = graphicsCards.get(0);
                return gpu.getVRam() / (1024 * 1024); // Mo
            }
        } catch (Exception e) {
        }
        return 0;
    }

    private static long getVramFromPciconf() {
        long vram = 0;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("pciconf", "-lv");
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains("vga")) {
                        if (reader.readLine() != null && reader.readLine().toLowerCase().contains("memory")) {
                            String memLine = line.replaceAll("[^0-9]", "");
                            if (!memLine.isEmpty()) {
                                vram = Long.parseLong(memLine) / (1024 * 1024); // Mo
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
        return vram;
    }

    private static String removeUnwantedParenthesesContent(String name) {
        return name.replaceAll("\\s*\\(.*?\\)", "");
    }

    public static List<GpuInfo> getAllGpus() {
        List<GpuInfo> gpus = new ArrayList<>();
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hal = systemInfo.getHardware();
            List<GraphicsCard> graphicsCards = hal.getGraphicsCards();

            for (GraphicsCard gpu : graphicsCards) {
                String name = removeUnwantedParenthesesContent(gpu.getName());
                long vram = gpu.getVRam() / (1024 * 1024);
                gpus.add(new GpuInfo(name, vram));
            }
        } catch (Exception e) {
            // Return empty list on error
        }
        return gpus;
    }

    public static class GpuInfo {
        private final String name;
        private final long vram;

        public GpuInfo(String name, long vram) {
            this.name = name;
            this.vram = vram;
        }

        public String getName() {
            return name;
        }

        public long getVram() {
            return vram;
        }
    }
}
