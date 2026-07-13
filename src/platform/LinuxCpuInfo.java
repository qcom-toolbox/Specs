// platform/LinuxCpuInfo.java

package platform;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LinuxCpuInfo {

    public static String getCpuName() {
        String cpuName = getCpuNameFromOshi();

        if (cpuName.equals("Unknown CPU")) {
            cpuName = getCpuNameFromProcCpuinfo();
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

    private static String getCpuNameFromProcCpuinfo() {
        String cpuName = "Unknown CPU";
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("model name")) {
                    cpuName = line.split(":")[1].trim();
                    break;
                }
            }
        } catch (IOException e) {
            cpuName = "Error retrieving CPU name";
        }
        return cpuName;
    }

    public static int getLinuxPhysicalCores() throws IOException {
        int cores = getPhysicalCoresFromOshi();

        if (cores == 0) {
            cores = getLinuxPhysicalCoresFromProcCpuinfo();
            if (cores == 0) {
                cores = getLinuxPhysicalCoresFromLscpu();
            }
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

    private static int getLinuxPhysicalCoresFromProcCpuinfo() throws IOException {
        int cores = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("processor")) {
                    cores++;
                }
            }
        }
        return cores;
    }

    private static int getLinuxPhysicalCoresFromLscpu() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "lscpu | grep 'Core(s) per socket:' | awk '{print $NF}'");
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String coresLine = reader.readLine();

            if (coresLine != null && coresLine.matches("\\d+")) {
                return Integer.parseInt(coresLine.trim());
            }
        }
        return 0;
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

    public static String getSupportedTechnologies() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", 
                "lscpu | grep 'Flags' | awk -F: '{print $2}'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            process.waitFor();
            
            if (line != null && !line.isEmpty()) {
                return line.trim();
            }
        } catch (Exception e) {
            // Ignore
        }
        
        return "Unknown";
    }
}
