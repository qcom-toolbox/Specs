//platform/MacGpuInfo.java

package platform;

import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;

import java.util.ArrayList;
import java.util.List;

public class MacGpuInfo {

    public static String getGpuName() {
        String gpuName = "Unknown GPU";
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();

            if (!graphicsCards.isEmpty()) {
                GraphicsCard gpu = graphicsCards.get(0);
                gpuName = gpu.getName();
            }
        } catch (Exception e) {
            gpuName = "Error retrieving GPU name";
        }
        return gpuName;
    }

    public static long getGpuVram() {
        long vram = 0;
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();

            if (!graphicsCards.isEmpty()) {
                GraphicsCard gpu = graphicsCards.get(0);
                vram = gpu.getVRam();
            }
        } catch (Exception e) {
            vram = 0;
        }
        return vram / (1024 * 1024);
    }

    public static List<GpuInfo> getAllGpus() {
        List<GpuInfo> gpus = new ArrayList<>();
        try {
            SystemInfo systemInfo = new SystemInfo();
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();

            for (GraphicsCard gpu : graphicsCards) {
                long vram = gpu.getVRam() / (1024 * 1024);
                gpus.add(new GpuInfo(gpu.getName(), vram));
            }
        } catch (Exception e) {
            // Return empty list on error
        }
        return gpus;
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
