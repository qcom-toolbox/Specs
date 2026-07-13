package platform;

import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WindowsGpuInfo {

    private static final String[] BLACKLIST = {
            "parsec"
    };

    private static boolean isBlacklisted(String name) {
        String n = name.toLowerCase();
        for (String b : BLACKLIST) {
            if (n.contains(b)) return true;
        }
        return false;
    }

    public static String getGpuName() {
        SystemInfo si = new SystemInfo();
        String fallback = "Unknown GPU";

        for (GraphicsCard gpu : si.getHardware().getGraphicsCards()) {
            String name = gpu.getName();
            if (!isBlacklisted(name)) {
                return name;
            }
            // garde un fallback si ya que des virtuels
            if (fallback.equals("Unknown GPU")) {
                fallback = name;
            }
        }
        return fallback;
    }

    public static long getGpuVram() {
        SystemInfo si = new SystemInfo();
        long vram = 0;

        // essaye d'abord sur les GPU non-blacklistés
        for (GraphicsCard gpu : si.getHardware().getGraphicsCards()) {
            if (!isBlacklisted(gpu.getName())) {
                vram = Math.max(vram, gpu.getVRam());
            }
        }

        // si aucun GPU "valide", prendre le plus gros de tout
        if (vram == 0) {
            for (GraphicsCard gpu : si.getHardware().getGraphicsCards()) {
                vram = Math.max(vram, gpu.getVRam());
            }
        }

        return (long) Math.ceil(vram / (1024.0 * 1024.0));
    }

    public static List<GpuInfo> getAllGpus() {
        SystemInfo si = new SystemInfo();
        List<GpuInfo> gpus = new ArrayList<>();

        for (GraphicsCard gpu : si.getHardware().getGraphicsCards()) {
            String name = gpu.getName();
            if (!isBlacklisted(name)) {
                long vram = (long) Math.ceil(gpu.getVRam() / (1024.0 * 1024.0));
                gpus.add(new GpuInfo(name, vram));
            }
        }

        // si aucun GPU "valide", prendre tous
        if (gpus.isEmpty()) {
            for (GraphicsCard gpu : si.getHardware().getGraphicsCards()) {
                long vram = (long) Math.ceil(gpu.getVRam() / (1024.0 * 1024.0));
                gpus.add(new GpuInfo(gpu.getName(), vram));
            }
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

    public static String getDisplayManager() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("powershell", 
                "Get-WmiObject Win32_DesktopMonitor | Select-Object -ExpandProperty Name");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            
            if (line != null && !line.isEmpty()) {
                return line;
            }
        } catch (Exception e) {
            // Ignore
        }
        
        return "Unknown";
    }

    public static String getSupportedTechnologies() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("powershell", 
                "Get-WmiObject Win32_VideoController | Select-Object -ExpandProperty DriverVersion");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            
            if (line != null && !line.isEmpty()) {
                return "DirectX 12, " + line;
            }
        } catch (Exception e) {
            // Ignore
        }
        
        return "DirectX 12";
    }
}
