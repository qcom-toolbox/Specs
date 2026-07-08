package platform;

import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

public class WindowsRamInfo {

    // Method to get total physical memory in MB
    public static long getRamSize() {
        OperatingSystemMXBean osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long totalPhysicalMemory = (long) Math.ceil(osMXBean.getTotalMemorySize() / (1024.0 * 1024.0));
        return totalPhysicalMemory;
    }

    // Method to get used physical memory in MB
    public static long getRamUsed() {
        OperatingSystemMXBean osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long totalPhysicalMemory = (long) Math.ceil(osMXBean.getTotalMemorySize() / (1024.0 * 1024.0));
        long freePhysicalMemory = (long) Math.ceil(osMXBean.getFreeMemorySize() / (1024.0 * 1024.0));
        long usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;
        return usedPhysicalMemory;
    }

    // Method to get free physical memory in MB
    public static long getRamFree() {
        OperatingSystemMXBean osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long freePhysicalMemory = (long) Math.ceil(osMXBean.getFreeMemorySize() / (1024.0 * 1024.0));
        return freePhysicalMemory;
    }

    public static String getRamSpeed() {
        try {
            Process process = Runtime.getRuntime().exec("wmic memorychip get speed");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().matches("\\d+")) {
                    int speed = Integer.parseInt(line.trim());
                    return speed + " MHz";
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    public static String getRamLatency() {
        try {
            Process process = Runtime.getRuntime().exec("wmic memorychip get ConfiguredVoltage");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().matches("\\d+\\.?\\d*")) {
                    double voltage = Double.parseDouble(line.trim());
                    // Approximate latency based on voltage (DDR3=1.5V, DDR4=1.2V, DDR5=1.1V)
                    if (voltage >= 1.4) return "CL9-CL11 (DDR3)";
                    else if (voltage >= 1.2) return "CL15-CL19 (DDR4)";
                    else if (voltage >= 1.1) return "CL28-CL40 (DDR5)";
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    public static int getRamRanks() {
        try {
            Process process = Runtime.getRuntime().exec("wmic memorychip get ConfiguredClockSpeed");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int count = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().matches("\\d+")) {
                    count++;
                }
            }
            return count > 0 ? count : 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public static String getDdrVersion() {
        try {
            Process process = Runtime.getRuntime().exec("wmic memorychip get ConfiguredVoltage");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().matches("\\d+\\.?\\d*")) {
                    double voltage = Double.parseDouble(line.trim());
                    if (voltage >= 1.4) return "DDR3";
                    else if (voltage >= 1.2) return "DDR4";
                    else if (voltage >= 1.1) return "DDR5";
                    else if (voltage >= 0.8) return "LPDDR4";
                    else if (voltage >= 0.7) return "LPDDR5";
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    public static String getFormFactor() {
        try {
            Process process = Runtime.getRuntime().exec("wmic memorychip get FormFactor");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() > 0 && !line.trim().equalsIgnoreCase("FormFactor")) {
                    String factor = line.trim();
                    if (factor.contains("SODIMM")) return "SO-DIMM";
                    else if (factor.contains("DIMM")) return "DIMM";
                    else if (factor.contains("Soldered") || factor.contains("On-Board")) return "Soldered";
                    else return factor;
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }
}
