// platform/LinuxGpuInfo.java

package platform;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LinuxGpuInfo {

    // -------------------------
// GPU NAME
// -------------------------
    public static String getGpuName() {
        String gpuName = getGpuNameFromGlxInfo();

        if (gpuName.equals("Unknown GPU")) {
            gpuName = getGpuNameFromLspci();
            if (gpuName.equals("Unknown GPU")) {
                gpuName = getGpuNameFromOshi();
            }
        }

        // Nettoyage pour NVIDIA
        return cleanNvidiaName(gpuName);
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
        } catch (Exception ignored) {}
        return "Unknown GPU";
    }

    private static String getGpuNameFromGlxInfo() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "glxinfo | grep 'Device:'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("device")) {
                    return removeUnwantedParenthesesContent(line.split(":")[1].trim());
                }
            }
        } catch (Exception ignored) {}
        return "Unknown GPU";
    }

    private static String getGpuNameFromLspci() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("lspci");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("vga")) {
                    return removeUnwantedParenthesesContent(line.split(":")[2].trim());
                }
            }
        } catch (Exception ignored) {}
        return "Unknown GPU";
    }

// -------------------------
// Nettoyage pour NVIDIA
// -------------------------
    private static String cleanNvidiaName(String name) {
        if (name == null || name.isEmpty()) return "Unknown GPU";

        name = name.trim();

        if (name.toLowerCase().contains("nvidia corporation")) {
            int start = name.indexOf('[');
            int end = name.indexOf(']');
            if (start >= 0 && end > start) {
                String insideBrackets = name.substring(start + 1, end).trim();
                return "NVIDIA" + " " + insideBrackets;
            }
            return name;
        }

        // Pour les autres GPU, juste enlever le contenu entre parenthèses
        return removeUnwantedParenthesesContent(name);
    }

    // -------------------------
    // GPU VRAM (MiB)
    // -------------------------
    public static long getGpuVram() {
        // 1️⃣ récupère d'abord le nom du GPU
        String gpuName = getGpuName();

        // 2️⃣ essaye OSHI en premier
        long vram = getVramFromOshi();

        // 3️⃣ si GPU NVIDIA / GeForce, fallback NVML si OSHI a raté ou valeur trop faible
        if ((gpuName.toLowerCase().contains("nvidia") || gpuName.toLowerCase().contains("geforce"))
                && (vram == 0 || vram <= 288)) {
            long nvmlVram = getVramFromNvml();
            if (nvmlVram > 0) {
                return nvmlVram;
            }
        }

        // 4️⃣ si OSHI a retourné une valeur correcte, on la garde
        if (vram > 0) {
            return vram;
        }

        // 5️⃣ fallback sur glxinfo
        vram = getVramFromGlxInfo();
        if (vram > 0) return vram;

        // 6️⃣ dernier recours, lspci
        return getVramFromLspci();
    }

    private static long getVramFromOshi() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hal = systemInfo.getHardware();
            List<GraphicsCard> graphicsCards = hal.getGraphicsCards();

            if (!graphicsCards.isEmpty()) {
                GraphicsCard gpu = graphicsCards.get(0);
                return gpu.getVRam() / (1024 * 1024); // bytes → MiB
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private static long getVramFromGlxInfo() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("glxinfo");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("total available memory")) {
                    return Long.parseLong(line.replaceAll("[^0-9]", "").trim());
                } else if (line.toLowerCase().contains("video memory")) {
                    return Long.parseLong(line.replaceAll("[^0-9]", "").trim());
                }
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private static long getVramFromLspci() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("lspci", "-v");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean foundVGA = false;

            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("vga compatible controller")) {
                    foundVGA = true;
                }
                if (foundVGA && line.toLowerCase().contains("memory at")) {
                    String[] parts = line.split(" ");
                    for (String part : parts) {
                        if (part.matches("[0-9a-f]+K")) {
                            return Long.parseLong(part.replace("K", "").trim()) / 1024;
                        }
                    }
                    break;
                }
            }
        } catch (Exception ignored) {}
        return 0;
    }

    // -------------------------
    // NVML via JNA
    // -------------------------
    private interface NVML extends Library {
        NVML INSTANCE = Native.load("nvidia-ml", NVML.class);

        int nvmlInit();
        int nvmlShutdown();
        int nvmlDeviceGetCount(int[] count);
        int nvmlDeviceGetHandleByIndex(int index, Pointer[] device);
        int nvmlDeviceGetMemoryInfo(Pointer device, NvmlMemory memory);

        class NvmlMemory extends Structure {
            public long total;
            public long free;
            public long used;

            @Override
            protected List<String> getFieldOrder() {
                return List.of("total", "free", "used");
            }
        }
    }

    private static long getVramFromNvml() {
        try {
            if (NVML.INSTANCE.nvmlInit() != 0) return 0;

            int[] count = new int[1];
            if (NVML.INSTANCE.nvmlDeviceGetCount(count) != 0 || count[0] == 0) {
                NVML.INSTANCE.nvmlShutdown();
                return 0;
            }

            Pointer[] device = new Pointer[1];
            if (NVML.INSTANCE.nvmlDeviceGetHandleByIndex(0, device) != 0) {
                NVML.INSTANCE.nvmlShutdown();
                return 0;
            }

            NVML.NvmlMemory memory = new NVML.NvmlMemory();
            if (NVML.INSTANCE.nvmlDeviceGetMemoryInfo(device[0], memory) != 0) {
                NVML.INSTANCE.nvmlShutdown();
                return 0;
            }

            NVML.INSTANCE.nvmlShutdown();
            return memory.total / (1024 * 1024); // MiB
        } catch (Exception e) {
            return 0;
        }
    }

    // -------------------------
    // UTILS
    // -------------------------
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
                name = cleanNvidiaName(name);
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
