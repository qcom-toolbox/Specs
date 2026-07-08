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
}
