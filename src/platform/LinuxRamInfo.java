package platform;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LinuxRamInfo {

    // Method to get total physical memory in MB
    public static long getRamSize() {
        long totalPhysicalMemory = 0;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "cat /proc/meminfo | grep MemTotal");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("MemTotal:")) {
                    totalPhysicalMemory = Long.parseLong(line.split("\\s+")[1]) / 1024; // Convert kB to MB
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;  // Indicating an error
        }

        return totalPhysicalMemory;
    }

    // Method to get used physical memory in MB
    public static long getRamUsed() {
        long totalPhysicalMemory = getRamSize();
        long freePhysicalMemory = getRamFree();
        return totalPhysicalMemory - freePhysicalMemory;
    }

    // Method to get free physical memory in MB
    public static long getRamFree() {
        long freePhysicalMemory = 0;

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "cat /proc/meminfo | grep MemAvailable");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("MemAvailable:")) {
                    freePhysicalMemory = Long.parseLong(line.split("\\s+")[1]) / 1024; // Convert kB to MB
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;  // Indicating an error
        }

        return freePhysicalMemory;
    }

    public static String getRamSpeed() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "dmidecode -t memory | grep 'Speed'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Speed") && !line.contains("Unknown")) {
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
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "dmidecode -t memory | grep 'Configured Voltage'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Configured Voltage") && !line.contains("Unknown")) {
                    String voltageStr = line.split(":")[1].trim().replace("V", "");
                    double voltage = Double.parseDouble(voltageStr);
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
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "dmidecode -t memory | grep -c 'Size'");
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
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "dmidecode -t memory | grep 'Type'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Type") && !line.contains("Unknown")) {
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
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "dmidecode -t memory | grep 'Form Factor'");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Form Factor") && !line.contains("Unknown")) {
                    String factor = line.split(":")[1].trim();
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
