package platform;

import com.sun.management.OperatingSystemMXBean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

public class MacRamInfo {

    // Method to get total physical memory in MB
    public static long getRamSize() {
        long totalPhysicalMemory = 0;

        try {
            // Retrieve total physical memory using OperatingSystemMXBean
            OperatingSystemMXBean osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            totalPhysicalMemory = osMXBean.getTotalMemorySize() / (1024 * 1024); // Convert to MB
        } catch (Exception e) {
            e.printStackTrace();
            return -1;  // Indicating an error
        }

        return totalPhysicalMemory;
    }

    // Method to get used physical memory in MB
    public static long getRamUsed() {
        long usedPhysicalMemory = 0;
        long pagesWired = 0;
        long pagesActive = 0;
        long pageSize = 4096; // Default page size, often 4 KB on macOS

        try {
            // Create a ProcessBuilder to run the "vm_stat" command
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "vm_stat");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Read and extract information from "vm_stat"
            while ((line = reader.readLine()) != null) {
                if (line.contains("Pages wired down:")) {
                    pagesWired = Long.parseLong(line.replaceAll("[^0-9]", "").trim());
                } else if (line.contains("Pages active:")) {
                    pagesActive = Long.parseLong(line.replaceAll("[^0-9]", "").trim());
                }
            }

            // Calculate used memory (wired + active) in MB
            usedPhysicalMemory = ((pagesWired + pagesActive) * pageSize) / (1024 * 1024); // Convert to MB

        } catch (Exception e) {
            e.printStackTrace();
            return -1;  // Indicating an error
        }

        return usedPhysicalMemory;
    }

    // Method to get free physical memory in MB
    public static long getRamFree() {
        long freePhysicalMemory = 0;

        try {
            long totalPhysicalMemory = getRamSize();
            long usedPhysicalMemory = getRamUsed();
            freePhysicalMemory = totalPhysicalMemory - usedPhysicalMemory;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;  // Indicating an error
        }

        return freePhysicalMemory;
    }

    public static String getRamSpeed() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "system_profiler SPMemoryDataType | grep 'Speed'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Speed")) {
                    String speed = line.split(":")[1].trim();
                    return speed;
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    public static String getRamLatency() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "system_profiler SPMemoryDataType | grep 'Type'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Type")) {
                    String type = line.split(":")[1].trim();
                    if (type.contains("DDR3")) return "CL9-CL11 (DDR3)";
                    else if (type.contains("DDR4")) return "CL15-CL19 (DDR4)";
                    else if (type.contains("DDR5")) return "CL28-CL40 (DDR5)";
                    return type;
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    public static int getRamRanks() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "system_profiler SPMemoryDataType | grep -c 'Size'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null && line.matches("\\d+")) {
                return Integer.parseInt(line.trim());
            }
        } catch (Exception e) {
            // Ignore
        }
        return 1;
    }

    public static String getDdrVersion() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "system_profiler SPMemoryDataType | grep 'Type'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Type")) {
                    String type = line.split(":")[1].trim();
                    if (type.contains("DDR3")) return "DDR3";
                    else if (type.contains("DDR4")) return "DDR4";
                    else if (type.contains("DDR5")) return "DDR5";
                    else if (type.contains("LPDDR3")) return "LPDDR3";
                    else if (type.contains("LPDDR4")) return "LPDDR4";
                    else if (type.contains("LPDDR5")) return "LPDDR5";
                    return type;
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    public static String getFormFactor() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "system_profiler SPMemoryDataType | grep 'Type'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Type")) {
                    String type = line.split(":")[1].trim();
                    // Mac typically uses soldered RAM or SO-DIMM for older models
                    if (type.toLowerCase().contains("soldered") || type.toLowerCase().contains("on-board")) return "Soldered";
                    else if (type.toLowerCase().contains("dimm")) return "SO-DIMM";
                    else return "Soldered"; // Most modern Macs have soldered RAM
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }
}
