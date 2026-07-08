// platform/WindowsCpuInfo

package platform;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

public class WindowsCpuInfo {

    public static String getCpuName() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();

        return processor.getProcessorIdentifier().getName();
    }

    public static int getWindowsPhysicalCores() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();

        return processor.getPhysicalProcessorCount();
    }

    public static int getLogicalCores() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();

        return processor.getLogicalProcessorCount();
    }

    public static long getBaseClock() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();

        return processor.getMaxFreq();
    }

    public static long getCurrentClock() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();

        long[] freqs = processor.getCurrentFreq();
        if (freqs != null && freqs.length > 0) {
            return freqs[0];
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
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();

        return processor.getLogicalProcessorCount() > processor.getPhysicalProcessorCount();
    }

    public static String getCpuArch() {
        String osArch = System.getProperty("os.arch");
        if (osArch != null) {
            return normalizeArch(osArch);
        }
        
        // Fallback to OSHI
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();
        String arch = processor.getProcessorIdentifier().getMicroarchitecture();
        if (arch != null && !arch.isEmpty()) {
            return normalizeArch(arch);
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
