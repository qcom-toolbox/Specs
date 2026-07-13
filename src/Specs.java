import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import platform.*;

public class Specs {
    private static final String OS_NAME = System.getProperty("os.name", "").toLowerCase();

    public static String getOperatingSystemName() {
        String osName = System.getProperty("os.name");
        osName = osName.replace("-", " ");
        return osName;
    }

    public static String getOperatingSystemVersion() {
        String osVersion = System.getProperty("os.version");

        if (OS_NAME.contains("linux")) {
            osVersion = LinuxOSInfo.getLinuxOSVersion();
        } else if (OS_NAME.contains("bsd")) {
            osVersion = BsdOSInfo.getBsdOSVersion();
        }

        osVersion = osVersion.replace("-", " ");
        return osVersion;
    }

    public static String getCpuName() {
        String cpuName = "";

        if (OS_NAME.contains("mac")) {
            cpuName = MacCpuInfo.getCpuName();
        } else if (OS_NAME.contains("linux")) {
            cpuName = LinuxCpuInfo.getCpuName();
        } else if (OS_NAME.contains("win")) {
            cpuName = WindowsCpuInfo.getCpuName();
        } else if (OS_NAME.contains("bsd")) {
            cpuName = BsdCpuInfo.getCpuName();
        } else {
            cpuName = "Unknown CPU";
        }

        cpuName = cpuName
                .replaceAll("\\(.*?\\)", "")
                .replace("-", " ")
                .replaceAll("@?\\s*\\d+(\\.\\d+)?\\s*ghz", "")
                .replaceAll("(?i)\\b\\d{1,2}(st|nd|rd|th)\\s+gen\\b", "")
                .replaceAll("\\s+", " ")
                .trim();

        return cpuName;
    }

    public static String getCpuArch() {
        if (OS_NAME.contains("mac")) {
            return MacCpuInfo.getCpuArch();
        } else if (OS_NAME.contains("linux")) {
            return LinuxCpuInfo.getCpuArch();
        } else if (OS_NAME.contains("win")) {
            return WindowsCpuInfo.getCpuArch();
        } else if (OS_NAME.contains("bsd")) {
            return BsdCpuInfo.getCpuArch();
        } else {
            return "Unknown";
        }
    }

    public static int getCpuCores() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsCpuInfo.getWindowsPhysicalCores();
            } else if (OS_NAME.contains("mac")) {
                return MacCpuInfo.getMacPhysicalCores();
            } else if (OS_NAME.contains("linux")) {
                return LinuxCpuInfo.getLinuxPhysicalCores();
            } else if (OS_NAME.contains("bsd")) {
                return BsdCpuInfo.getBsdPhysicalCores();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Runtime.getRuntime().availableProcessors();
    }

    public static int getCpuThreads() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static String getGpuName() {
        String gpuName = "";

        if (OS_NAME.contains("mac")) {
            gpuName = MacGpuInfo.getGpuName();
        } else if (OS_NAME.contains("linux")) {
            gpuName = LinuxGpuInfo.getGpuName();
        } else if (OS_NAME.contains("win")) {
            gpuName = WindowsGpuInfo.getGpuName();
        } else if (OS_NAME.contains("bsd")) {
            gpuName = BsdGpuInfo.getGpuName();
        } else {
            gpuName = "Unknown GPU";
        }

        if (!OS_NAME.contains("linux")) {
            gpuName = gpuName.replaceAll("\\(.*?\\)", "").trim();
        }

        return gpuName;
    }

    public static String getGpuVram() {
        long gpuVram = 0;

        if (OS_NAME.contains("mac")) {
            gpuVram = MacGpuInfo.getGpuVram();
        } else if (OS_NAME.contains("linux")) {
            gpuVram = LinuxGpuInfo.getGpuVram();
        } else if (OS_NAME.contains("win")) {
            gpuVram = WindowsGpuInfo.getGpuVram();
        } else if (OS_NAME.contains("bsd")) {
            gpuVram = BsdGpuInfo.getGpuVram();
        } else {
            gpuVram = -1;
        }

        return String.valueOf(gpuVram);
    }

    public static String getGpuDisplayManager() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsGpuInfo.getDisplayManager();
            } else if (OS_NAME.contains("mac")) {
                return MacGpuInfo.getDisplayManager();
            } else if (OS_NAME.contains("linux")) {
                return LinuxGpuInfo.getDisplayManager();
            } else if (OS_NAME.contains("bsd")) {
                return "Unknown";
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getGpuSupportedTechnologies() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsGpuInfo.getSupportedTechnologies();
            } else if (OS_NAME.contains("mac")) {
                return MacGpuInfo.getSupportedTechnologies();
            } else if (OS_NAME.contains("linux")) {
                return LinuxGpuInfo.getSupportedTechnologies();
            } else if (OS_NAME.contains("bsd")) {
                return "Unknown";
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static long getRamSize() {
        if (OS_NAME.contains("linux")) {
            return LinuxRamInfo.getRamSize();
        } else if (OS_NAME.contains("mac")) {
            return MacRamInfo.getRamSize();
        } else if (OS_NAME.contains("win")) {
            return WindowsRamInfo.getRamSize();
        } else if (OS_NAME.contains("bsd")) {
            return BsdRamInfo.getRamSize();
        } else {
            return -1;
        }
    }

    public static long getRamUsed() {
        if (OS_NAME.contains("linux")) {
            return LinuxRamInfo.getRamUsed();
        } else if (OS_NAME.contains("mac")) {
            return MacRamInfo.getRamUsed();
        } else if (OS_NAME.contains("win")) {
            return WindowsRamInfo.getRamUsed();
        } else if (OS_NAME.contains("bsd")) {
            return BsdRamInfo.getRamUsed();
        } else {
            return -1;
        }
    }

    public static long getRamFree() {
        if (OS_NAME.contains("linux")) {
            return LinuxRamInfo.getRamFree();
        } else if (OS_NAME.contains("mac")) {
            return MacRamInfo.getRamFree();
        } else if (OS_NAME.contains("win")) {
            return WindowsRamInfo.getRamFree();
        } else if (OS_NAME.contains("bsd")) {
            return BsdRamInfo.getRamFree();
        } else {
            return -1;
        }
    }

    public static String getComputerName() {
        try {
            if (OS_NAME.contains("win")) {
                return System.getenv("COMPUTERNAME");
            } else if (OS_NAME.contains("mac") || OS_NAME.contains("bsd")) {
                Process process = Runtime.getRuntime().exec("hostname");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String hostname = reader.readLine();
                process.waitFor();
                return hostname != null ? hostname.trim() : "Unknown";
            } else if (OS_NAME.contains("linux")) {
                Process process = Runtime.getRuntime().exec("hostname");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String hostname = reader.readLine();
                process.waitFor();
                return hostname != null ? hostname.trim() : "Unknown";
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getNetworkType() {
        try {
            if (OS_NAME.contains("win")) {
                Process process = Runtime.getRuntime().exec("powershell \"Get-NetAdapter | Where-Object {$_.Status -eq 'Up'} | Select-Object -ExpandProperty Name\"");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains("wi-fi") || line.toLowerCase().contains("wireless")) {
                        process.waitFor();
                        return "WiFi";
                    }
                }
                process.waitFor();
                return "Ethernet";
            } else if (OS_NAME.contains("mac")) {
                Process process = Runtime.getRuntime().exec("networksetup -listallhardwareports");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                boolean isWifi = false;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("Wi-Fi")) {
                        isWifi = true;
                    }
                    if (isWifi && line.contains("Device:")) {
                        process.waitFor();
                        return "WiFi";
                    }
                }
                process.waitFor();
                return "Ethernet";
            } else if (OS_NAME.contains("linux")) {
                Process process = Runtime.getRuntime().exec("nmcli device status");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains("wifi") || line.toLowerCase().contains("wlan")) {
                        process.waitFor();
                        return "WiFi";
                    }
                }
                process.waitFor();
                return "Ethernet";
            } else if (OS_NAME.contains("bsd")) {
                Process process = Runtime.getRuntime().exec("ifconfig");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().contains("wlan") || line.toLowerCase().contains("wi")) {
                        process.waitFor();
                        return "WiFi";
                    }
                }
                process.waitFor();
                return "Ethernet";
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static long getCpuBaseClock() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsCpuInfo.getBaseClock();
            } else if (OS_NAME.contains("mac")) {
                return MacCpuInfo.getBaseClock();
            } else if (OS_NAME.contains("linux")) {
                return LinuxCpuInfo.getBaseClock();
            } else if (OS_NAME.contains("bsd")) {
                return BsdCpuInfo.getBaseClock();
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public static long getCpuCurrentClock() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsCpuInfo.getCurrentClock();
            } else if (OS_NAME.contains("mac")) {
                return MacCpuInfo.getCurrentClock();
            } else if (OS_NAME.contains("linux")) {
                return LinuxCpuInfo.getCurrentClock();
            } else if (OS_NAME.contains("bsd")) {
                return BsdCpuInfo.getCurrentClock();
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public static String getCpuL1Cache() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsCpuInfo.getL1Cache();
            } else if (OS_NAME.contains("mac")) {
                return MacCpuInfo.getL1Cache();
            } else if (OS_NAME.contains("linux")) {
                return LinuxCpuInfo.getL1Cache();
            } else if (OS_NAME.contains("bsd")) {
                return BsdCpuInfo.getL1Cache();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getCpuL2Cache() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsCpuInfo.getL2Cache();
            } else if (OS_NAME.contains("mac")) {
                return MacCpuInfo.getL2Cache();
            } else if (OS_NAME.contains("linux")) {
                return LinuxCpuInfo.getL2Cache();
            } else if (OS_NAME.contains("bsd")) {
                return BsdCpuInfo.getL2Cache();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getCpuL3Cache() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsCpuInfo.getL3Cache();
            } else if (OS_NAME.contains("mac")) {
                return MacCpuInfo.getL3Cache();
            } else if (OS_NAME.contains("linux")) {
                return LinuxCpuInfo.getL3Cache();
            } else if (OS_NAME.contains("bsd")) {
                return BsdCpuInfo.getL3Cache();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getCpuSupportedTechnologies() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsCpuInfo.getSupportedTechnologies();
            } else if (OS_NAME.contains("mac")) {
                return MacCpuInfo.getSupportedTechnologies();
            } else if (OS_NAME.contains("linux")) {
                return LinuxCpuInfo.getSupportedTechnologies();
            } else if (OS_NAME.contains("bsd")) {
                return "Unknown";
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static boolean supportsHyperThreading() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsCpuInfo.supportsHyperThreading();
            } else if (OS_NAME.contains("mac")) {
                return MacCpuInfo.supportsHyperThreading();
            } else if (OS_NAME.contains("linux")) {
                return LinuxCpuInfo.supportsHyperThreading();
            } else if (OS_NAME.contains("bsd")) {
                return BsdCpuInfo.supportsHyperThreading();
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static String getRamSpeed() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsRamInfo.getRamSpeed();
            } else if (OS_NAME.contains("mac")) {
                return MacRamInfo.getRamSpeed();
            } else if (OS_NAME.contains("linux")) {
                return LinuxRamInfo.getRamSpeed();
            } else if (OS_NAME.contains("bsd")) {
                return BsdRamInfo.getRamSpeed();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getRamLatency() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsRamInfo.getRamLatency();
            } else if (OS_NAME.contains("mac")) {
                return MacRamInfo.getRamLatency();
            } else if (OS_NAME.contains("linux")) {
                return LinuxRamInfo.getRamLatency();
            } else if (OS_NAME.contains("bsd")) {
                return BsdRamInfo.getRamLatency();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static int getRamRanks() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsRamInfo.getRamRanks();
            } else if (OS_NAME.contains("mac")) {
                return MacRamInfo.getRamRanks();
            } else if (OS_NAME.contains("linux")) {
                return LinuxRamInfo.getRamRanks();
            } else if (OS_NAME.contains("bsd")) {
                return BsdRamInfo.getRamRanks();
            }
        } catch (Exception e) {
            return 1;
        }
        return 1;
    }

    public static String getDdrVersion() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsRamInfo.getDdrVersion();
            } else if (OS_NAME.contains("mac")) {
                return MacRamInfo.getDdrVersion();
            } else if (OS_NAME.contains("linux")) {
                return LinuxRamInfo.getDdrVersion();
            } else if (OS_NAME.contains("bsd")) {
                return BsdRamInfo.getDdrVersion();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getFormFactor() {
        try {
            if (OS_NAME.contains("win")) {
                return WindowsRamInfo.getFormFactor();
            } else if (OS_NAME.contains("mac")) {
                return MacRamInfo.getFormFactor();
            } else if (OS_NAME.contains("linux")) {
                return LinuxRamInfo.getFormFactor();
            } else if (OS_NAME.contains("bsd")) {
                return BsdRamInfo.getFormFactor();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static List<GpuInfo> getAllGpus() {
        try {
            if (OS_NAME.contains("win")) {
                return convertToGpuInfoList(WindowsGpuInfo.getAllGpus());
            } else if (OS_NAME.contains("mac")) {
                return convertToGpuInfoList(MacGpuInfo.getAllGpus());
            } else if (OS_NAME.contains("linux")) {
                return convertToGpuInfoList(LinuxGpuInfo.getAllGpus());
            } else if (OS_NAME.contains("bsd")) {
                return convertToGpuInfoList(BsdGpuInfo.getAllGpus());
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    private static List<GpuInfo> convertToGpuInfoList(List<?> platformGpuList) {
        List<GpuInfo> result = new ArrayList<>();
        for (Object gpu : platformGpuList) {
            try {
                String name = (String) gpu.getClass().getMethod("getName").invoke(gpu);
                long vram = (long) gpu.getClass().getMethod("getVram").invoke(gpu);
                result.add(new GpuInfo(name, vram));
            } catch (Exception e) {
                // Skip invalid entries
            }
        }
        return result;
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
