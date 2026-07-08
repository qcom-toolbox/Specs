import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import platform.*;

public class Specs {

    public static String getOperatingSystemName() {
        String osName = System.getProperty("os.name");
        osName = osName.replace("-", " ");
        return osName;
    }

    public static String getOperatingSystemVersion() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osVersion = System.getProperty("os.version");

        if (osName.contains("linux")) {
            osVersion = LinuxOSInfo.getLinuxOSVersion();
        } else if (osName.contains("bsd")) {
            osVersion = BsdOSInfo.getBsdOSVersion();
        }

        osVersion = osVersion.replace("-", " ");
        return osVersion;
    }

    public static String getCpuName() {
        String cpuName = "";
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("mac")) {
            cpuName = MacCpuInfo.getCpuName();
        } else if (osName.contains("linux")) {
            cpuName = LinuxCpuInfo.getCpuName();
        } else if (osName.contains("win")) {
            cpuName = WindowsCpuInfo.getCpuName();
        } else if (osName.contains("bsd")) {
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

    public static int getCpuCores() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        try {
            if (osName.contains("win")) {
                return WindowsCpuInfo.getWindowsPhysicalCores();
            } else if (osName.contains("mac")) {
                return MacCpuInfo.getMacPhysicalCores();
            } else if (osName.contains("linux")) {
                return LinuxCpuInfo.getLinuxPhysicalCores();
            } else if (osName.contains("bsd")) {
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
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("mac")) {
            gpuName = MacGpuInfo.getGpuName();
        } else if (osName.contains("linux")) {
            gpuName = LinuxGpuInfo.getGpuName();
        } else if (osName.contains("win")) {
            gpuName = WindowsGpuInfo.getGpuName();
        } else if (osName.contains("bsd")) {
            gpuName = BsdGpuInfo.getGpuName();
        } else {
            gpuName = "Unknown GPU";
        }

        if (!osName.contains("linux")) {
            gpuName = gpuName.replaceAll("\\(.*?\\)", "").trim();
        }

        return gpuName;
    }

    public static String getGpuVram() {
        long gpuVram = 0;
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("mac")) {
            gpuVram = MacGpuInfo.getGpuVram();
        } else if (osName.contains("linux")) {
            gpuVram = LinuxGpuInfo.getGpuVram();
        } else if (osName.contains("win")) {
            gpuVram = WindowsGpuInfo.getGpuVram();
        } else if (osName.contains("bsd")) {
            gpuVram = BsdGpuInfo.getGpuVram();
        } else {
            gpuVram = -1;
        }

        return String.valueOf(gpuVram);
    }

    public static long getRamSize() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("linux")) {
            return LinuxRamInfo.getRamSize();
        } else if (osName.contains("mac")) {
            return MacRamInfo.getRamSize();
        } else if (osName.contains("win")) {
            return WindowsRamInfo.getRamSize();
        } else if (osName.contains("bsd")) {
            return BsdRamInfo.getRamSize();
        } else {
            return -1;
        }
    }

    public static long getRamUsed() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("linux")) {
            return LinuxRamInfo.getRamUsed();
        } else if (osName.contains("mac")) {
            return MacRamInfo.getRamUsed();
        } else if (osName.contains("win")) {
            return WindowsRamInfo.getRamUsed();
        } else if (osName.contains("bsd")) {
            return BsdRamInfo.getRamUsed();
        } else {
            return -1;
        }
    }

    public static long getRamFree() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("linux")) {
            return LinuxRamInfo.getRamFree();
        } else if (osName.contains("mac")) {
            return MacRamInfo.getRamFree();
        } else if (osName.contains("win")) {
            return WindowsRamInfo.getRamFree();
        } else if (osName.contains("bsd")) {
            return BsdRamInfo.getRamFree();
        } else {
            return -1;
        }
    }

    public static String getComputerName() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return System.getenv("COMPUTERNAME");
            } else if (osName.contains("mac") || osName.contains("bsd")) {
                Process process = Runtime.getRuntime().exec("hostname");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String hostname = reader.readLine();
                process.waitFor();
                return hostname != null ? hostname.trim() : "Unknown";
            } else if (osName.contains("linux")) {
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
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
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
            } else if (osName.contains("mac")) {
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
            } else if (osName.contains("linux")) {
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
            } else if (osName.contains("bsd")) {
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
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsCpuInfo.getBaseClock();
            } else if (osName.contains("mac")) {
                return MacCpuInfo.getBaseClock();
            } else if (osName.contains("linux")) {
                return LinuxCpuInfo.getBaseClock();
            } else if (osName.contains("bsd")) {
                return BsdCpuInfo.getBaseClock();
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public static long getCpuCurrentClock() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsCpuInfo.getCurrentClock();
            } else if (osName.contains("mac")) {
                return MacCpuInfo.getCurrentClock();
            } else if (osName.contains("linux")) {
                return LinuxCpuInfo.getCurrentClock();
            } else if (osName.contains("bsd")) {
                return BsdCpuInfo.getCurrentClock();
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public static String getCpuL1Cache() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsCpuInfo.getL1Cache();
            } else if (osName.contains("mac")) {
                return MacCpuInfo.getL1Cache();
            } else if (osName.contains("linux")) {
                return LinuxCpuInfo.getL1Cache();
            } else if (osName.contains("bsd")) {
                return BsdCpuInfo.getL1Cache();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getCpuL2Cache() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsCpuInfo.getL2Cache();
            } else if (osName.contains("mac")) {
                return MacCpuInfo.getL2Cache();
            } else if (osName.contains("linux")) {
                return LinuxCpuInfo.getL2Cache();
            } else if (osName.contains("bsd")) {
                return BsdCpuInfo.getL2Cache();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getCpuL3Cache() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsCpuInfo.getL3Cache();
            } else if (osName.contains("mac")) {
                return MacCpuInfo.getL3Cache();
            } else if (osName.contains("linux")) {
                return LinuxCpuInfo.getL3Cache();
            } else if (osName.contains("bsd")) {
                return BsdCpuInfo.getL3Cache();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static boolean supportsHyperThreading() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsCpuInfo.supportsHyperThreading();
            } else if (osName.contains("mac")) {
                return MacCpuInfo.supportsHyperThreading();
            } else if (osName.contains("linux")) {
                return LinuxCpuInfo.supportsHyperThreading();
            } else if (osName.contains("bsd")) {
                return BsdCpuInfo.supportsHyperThreading();
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static String getRamSpeed() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsRamInfo.getRamSpeed();
            } else if (osName.contains("mac")) {
                return MacRamInfo.getRamSpeed();
            } else if (osName.contains("linux")) {
                return LinuxRamInfo.getRamSpeed();
            } else if (osName.contains("bsd")) {
                return BsdRamInfo.getRamSpeed();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getRamLatency() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsRamInfo.getRamLatency();
            } else if (osName.contains("mac")) {
                return MacRamInfo.getRamLatency();
            } else if (osName.contains("linux")) {
                return LinuxRamInfo.getRamLatency();
            } else if (osName.contains("bsd")) {
                return BsdRamInfo.getRamLatency();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static int getRamRanks() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsRamInfo.getRamRanks();
            } else if (osName.contains("mac")) {
                return MacRamInfo.getRamRanks();
            } else if (osName.contains("linux")) {
                return LinuxRamInfo.getRamRanks();
            } else if (osName.contains("bsd")) {
                return BsdRamInfo.getRamRanks();
            }
        } catch (Exception e) {
            return 1;
        }
        return 1;
    }

    public static String getDdrVersion() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsRamInfo.getDdrVersion();
            } else if (osName.contains("mac")) {
                return MacRamInfo.getDdrVersion();
            } else if (osName.contains("linux")) {
                return LinuxRamInfo.getDdrVersion();
            } else if (osName.contains("bsd")) {
                return BsdRamInfo.getDdrVersion();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static String getFormFactor() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return WindowsRamInfo.getFormFactor();
            } else if (osName.contains("mac")) {
                return MacRamInfo.getFormFactor();
            } else if (osName.contains("linux")) {
                return LinuxRamInfo.getFormFactor();
            } else if (osName.contains("bsd")) {
                return BsdRamInfo.getFormFactor();
            }
        } catch (Exception e) {
            return "Unknown";
        }
        return "Unknown";
    }

    public static List<GpuInfo> getAllGpus() {
        String osName = System.getProperty("os.name").toLowerCase();
        try {
            if (osName.contains("win")) {
                return convertToGpuInfoList(WindowsGpuInfo.getAllGpus());
            } else if (osName.contains("mac")) {
                return convertToGpuInfoList(MacGpuInfo.getAllGpus());
            } else if (osName.contains("linux")) {
                return convertToGpuInfoList(LinuxGpuInfo.getAllGpus());
            } else if (osName.contains("bsd")) {
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
