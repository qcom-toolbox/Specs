// package/MacCpuInfo.java

package platform;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

public class MacCpuInfo {

    public static String getCpuName() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();

        // Get the CPU name from the processor
        return processor.getProcessorIdentifier().getName();
    }

    public static int getMacPhysicalCores() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();

        // Get the number of physical cores
        return processor.getPhysicalProcessorCount();
    }

    public static int getLogicalCores() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hal = systemInfo.getHardware();
        CentralProcessor processor = hal.getProcessor();

        // Get the number of logical cores
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
}
