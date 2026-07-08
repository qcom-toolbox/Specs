package platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BsdRamInfo {

    // Method to get total physical memory in MB
    public static long getRamSize() {
        long totalMemory = 0;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "-n", "hw.physmem");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                totalMemory = Long.parseLong(line.trim()) / (1024 * 1024); // Bytes -> MB
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return totalMemory;
    }

    // Method to get free physical memory in MB
    public static long getRamFree() {
        long freeMemory = 0;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "-n", "vm.stats.vm.v_free_count");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                long freePages = Long.parseLong(line.trim());
                // page size
                ProcessBuilder pbPage = new ProcessBuilder("sysctl", "-n", "hw.pagesize");
                Process pageProcess = pbPage.start();
                BufferedReader pageReader = new BufferedReader(new InputStreamReader(pageProcess.getInputStream()));
                long pageSize = Long.parseLong(pageReader.readLine().trim());
                freeMemory = (freePages * pageSize) / (1024 * 1024); // Bytes -> MB
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return freeMemory;
    }

    // Method to get used physical memory in MB
    public static long getRamUsed() {
        long total = getRamSize();
        long free = getRamFree();
        if (total >= 0 && free >= 0) {
            return total - free;
        } else {
            return -1;
        }
    }

    public static String getRamSpeed() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "-n", "hw.memfreq");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                return line.trim() + " MHz";
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    public static String getRamLatency() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "-n", "hw.memfreq");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                int speed = Integer.parseInt(line.trim());
                // Approximate latency based on speed
                if (speed >= 1600) return "CL9-CL11 (DDR3)";
                else if (speed >= 2133) return "CL15-CL19 (DDR4)";
                else if (speed >= 4800) return "CL28-CL40 (DDR5)";
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    public static int getRamRanks() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "-n", "hw.ncpu");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null && line.matches("\\d+")) {
                int cores = Integer.parseInt(line.trim());
                // Approximate ranks based on cores (not accurate but fallback)
                return cores >= 8 ? 2 : 1;
            }
        } catch (Exception e) {
            // Ignore
        }
        return 1;
    }

    public static String getDdrVersion() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "-n", "hw.memfreq");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                int speed = Integer.parseInt(line.trim());
                // Approximate DDR version based on speed
                if (speed >= 1600 && speed < 2133) return "DDR3";
                else if (speed >= 2133 && speed < 4800) return "DDR4";
                else if (speed >= 4800) return "DDR5";
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }

    public static String getFormFactor() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("sysctl", "-n", "hw.ncpu");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null && line.matches("\\d+")) {
                int cores = Integer.parseInt(line.trim());
                // BSD systems often use soldered RAM on laptops, DIMM on desktops
                // This is a rough approximation
                return cores >= 4 ? "DIMM" : "Soldered";
            }
        } catch (Exception e) {
            // Ignore
        }
        return "Unknown";
    }
}
