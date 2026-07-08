// platform/BsdCpuInfo.java

package platform;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BsdCpuInfo {

    public static String getCpuName() {
        String cpuName = getCpuNameFromOshi();

        if (cpuName.equals("Unknown CPU")) {
            cpuName = getCpuNameFromSysctl();
        }

        return cpuName;
    }

    private static String getCpuNameFromOshi() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            return processor.getProcessorIdentifier().getName();
        } catch (Exception e) {
            return "Unknown CPU";
        }
    }

    private static String getCpuNameFromSysctl() {
        String cpuName = "Unknown CPU";
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "-n", "hw.model");
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && !line.isEmpty()) {
                    cpuName = line.trim();
                }
            }
        } catch (IOException e) {
            cpuName = "Error retrieving CPU name";
        }
        return cpuName;
    }

    public static int getBsdPhysicalCores() throws IOException {
        int cores = getPhysicalCoresFromOshi();

        if (cores == 0) {
            cores = getPhysicalCoresFromSysctl();
        }

        return cores;
    }

    private static int getPhysicalCoresFromOshi() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            return processor.getPhysicalProcessorCount();
        } catch (Exception e) {
            return 0;
        }
    }

    private static int getPhysicalCoresFromSysctl() throws IOException {
        int cores = 0;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "-n", "hw.ncpu");
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                if (line != null && line.matches("\\d+")) {
                    cores = Integer.parseInt(line.trim());
                }
            }
        } catch (IOException e) {
            cores = 0;
        }
        return cores;
    }

    public static long getBaseClock() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            return processor.getMaxFreq();
        } catch (Exception e) {
            return 0;
        }
    }

    public static long getCurrentClock() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            long[] freqs = processor.getCurrentFreq();
            if (freqs != null && freqs.length > 0) {
                return freqs[0];
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    public static String getL1Cache() {
        return "Unknown";
    }

    public static String getL2Cache() {
        return "Unknown";
    }

    public static String getL3Cache() {
        return "Unknown";
    }

    public static boolean supportsHyperThreading() {
        try {
            SystemInfo systemInfo = new SystemInfo();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            return processor.getLogicalProcessorCount() > processor.getPhysicalProcessorCount();
        } catch (Exception e) {
            return false;
        }
    }

    public static String getCpuArch() {
        String osArch = System.getProperty("os.arch");
        if (osArch != null) {
            return normalizeArch(osArch);
        }
        
        // Fallback to OSHI
        try {
            SystemInfo systemInfo = new SystemInfo();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            String arch = processor.getProcessorIdentifier().getMicroarchitecture();
            if (arch != null && !arch.isEmpty()) {
                return normalizeArch(arch);
            }
        } catch (Exception e) {
            // Ignore
        }
        
        return "Unknown";
    }

    private static String normalizeArch(String arch) {
        String lowerArch = arch.toLowerCase();
        if (lowerArch.contains("aarch64") || lowerArch.contains("arm64")) {
            return "arm64";
        } else if (lowerArch.contains("arm")) {
            return "arm";
        } else if (lowerArch.contains("x86_64") || lowerArch.contains("x64") || lowerArch.contains("amd64")) {
            return "x64";
        } else if (lowerArch.contains("x86") || lowerArch.contains("i386") || lowerArch.contains("i686")) {
            return "x86";
        }
        return arch;
    }
}
