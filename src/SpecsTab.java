import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registry of hardware info tabs. Add a new entry to {@link #DEFAULT_TABS} to scale the UI.
 */
public final class SpecsTab {

    private final String id;
    private final String title;
    private final Supplier<String> contentSupplier;
    private final Supplier<ImageIcon> iconSupplier;

    private SpecsTab(String id, String title, Supplier<String> contentSupplier, Supplier<ImageIcon> iconSupplier) {
        this.id = id;
        this.title = title;
        this.contentSupplier = contentSupplier;
        this.iconSupplier = iconSupplier;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return contentSupplier.get();
    }

    public ImageIcon getIcon() {
        return iconSupplier.get();
    }

    public static final List<SpecsTab> DEFAULT_TABS = List.of(
            new SpecsTab(
                    "os",
                    "OS",
                    () -> {
                        StringBuilder sb = new StringBuilder(128);
                        sb.append("Operating System : ").append(Specs.getOperatingSystemName()).append("\n");
                        sb.append("Version : ").append(Specs.getOperatingSystemVersion()).append("\n");
                        sb.append("Computer Name : ").append(Specs.getComputerName()).append("\n");
                        sb.append("Network : ").append(Specs.getNetworkType());
                        return sb.toString();
                    },
                    InfoPanel::getOsIcon
            ),
            new SpecsTab(
                    "cpu",
                    "CPU",
                    () -> {
                        long baseClock = Specs.getCpuBaseClock();
                        long currentClock = Specs.getCpuCurrentClock();
                        String baseClockStr = baseClock > 0 ? String.format("%.2f GHz", baseClock / 1_000_000_000.0) : "Unknown";
                        String currentClockStr = currentClock > 0 ? String.format("%.2f GHz", currentClock / 1_000_000_000.0) : "Unknown";
                        String hyperThreading = Specs.supportsHyperThreading() ? "Yes" : "No";
                        
                        StringBuilder sb = new StringBuilder(256);
                        sb.append("CPU : ").append(Specs.getCpuName()).append("\n");
                        sb.append("Architecture : ").append(Specs.getCpuArch()).append("\n");
                        sb.append("Cores : ").append(Specs.getCpuCores()).append("\n");
                        sb.append("Threads : ").append(Specs.getCpuThreads()).append("\n");
                        sb.append("Base Clock : ").append(baseClockStr).append("\n");
                        sb.append("Current Clock : ").append(currentClockStr).append("\n");
                        sb.append("L1 Cache : ").append(Specs.getCpuL1Cache()).append("\n");
                        sb.append("L2 Cache : ").append(Specs.getCpuL2Cache()).append("\n");
                        sb.append("L3 Cache : ").append(Specs.getCpuL3Cache()).append("\n");
                        sb.append("Hyper-Threading : ").append(hyperThreading).append("\n");
                        sb.append("Supported Technologies : ").append(Specs.getCpuSupportedTechnologies());
                        return sb.toString();
                    },
                    () -> InfoPanel.getCpuIcon(Specs.getCpuName())
            ),
            new SpecsTab(
                    "gpu",
                    "GPU",
                    () -> {
                        StringBuilder sb = new StringBuilder();
                        java.util.List<Specs.GpuInfo> gpus = Specs.getAllGpus();
                        
                        if (gpus.isEmpty()) {
                            long vram = Long.parseLong(Specs.getGpuVram());
                            sb.append("GPU : ").append(Specs.getGpuName()).append("\n");
                            sb.append("VRam : ").append(vram == 0L ? "Shared" : vram + " MB").append("\n\n");
                        } else {
                            for (int i = 0; i < gpus.size(); i++) {
                                Specs.GpuInfo gpu = gpus.get(i);
                                sb.append("GPU : ").append(gpu.getName()).append("\n");
                                sb.append("VRam : ").append(gpu.getVram() == 0L ? "Shared" : gpu.getVram() + " MB");
                                if (i < gpus.size() - 1) {
                                    sb.append("\n\n");
                                }
                            }
                            sb.append("\n");
                        }
                        
                        sb.append("Display Manager : ").append(Specs.getGpuDisplayManager()).append("\n");
                        sb.append("Supported Technologies : ").append(Specs.getGpuSupportedTechnologies());
                        
                        return sb.toString();
                    },
                    () -> InfoPanel.getGpuIcon(Specs.getGpuName())
            ),
            new SpecsTab(
                    "ram",
                    "RAM",
                    () -> {
                        StringBuilder sb = new StringBuilder(256);
                        sb.append("RAM (Total) : ").append(Specs.getRamSize()).append(" MB\n");
                        sb.append("RAM (Used) : ").append(Specs.getRamUsed()).append(" MB\n");
                        sb.append("RAM (Free) : ").append(Specs.getRamFree()).append(" MB\n");
                        sb.append("Speed : ").append(Specs.getRamSpeed()).append("\n");
                        sb.append("Latency : ").append(Specs.getRamLatency()).append("\n");
                        sb.append("Ranks : ").append(Specs.getRamRanks()).append("\n");
                        sb.append("DDR Version : ").append(Specs.getDdrVersion()).append("\n");
                        sb.append("Form Factor : ").append(Specs.getFormFactor());
                        return sb.toString();
                    },
                    InfoPanel::getRamIcon
            )
    );

    private static final List<SpecsTab> tabs = new ArrayList<>(DEFAULT_TABS);
    private static final Map<String, JPanel> panelById = new LinkedHashMap<>();

    public static List<SpecsTab> getTabs() {
        return Collections.unmodifiableList(tabs);
    }

    public static void register(SpecsTab tab) {
        tabs.add(tab);
    }

    public static void register(int index, SpecsTab tab) {
        tabs.add(index, tab);
    }

    public static Map<String, JPanel> getPanelById() {
        return panelById;
    }

    public static JPanel getPanel(String id) {
        return panelById.get(id);
    }

    public static void bindPanel(String id, JPanel panel) {
        panelById.put(id, panel);
    }

    public static void clearPanels() {
        panelById.clear();
    }

    public static void refreshAll() {
        for (SpecsTab tab : tabs) {
            JPanel panel = panelById.get(tab.getId());
            if (panel != null) {
                InfoPanel.updateInfoPanel(panel, tab.getContent(), tab.getIcon());
            }
        }
    }
}
