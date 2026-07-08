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
                    () -> "Operating System :" + " " + Specs.getOperatingSystemName() + "\n"
                            + "Version :" + " " + Specs.getOperatingSystemVersion() + "\n"
                            + "Computer Name :" + " " + Specs.getComputerName() + "\n"
                            + "Network :" + " " + Specs.getNetworkType(),
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
                        
                        return "CPU :" + " " + Specs.getCpuName() + "\n"
                                + "Architecture :" + " " + Specs.getCpuArch() + "\n"
                                + "Cores :" + " " + Specs.getCpuCores() + "\n"
                                + "Threads :" + " " + Specs.getCpuThreads() + "\n"
                                + "Base Clock :" + " " + baseClockStr + "\n"
                                + "Current Clock :" + " " + currentClockStr + "\n"
                                + "L1 Cache :" + " " + Specs.getCpuL1Cache() + "\n"
                                + "L2 Cache :" + " " + Specs.getCpuL2Cache() + "\n"
                                + "L3 Cache :" + " " + Specs.getCpuL3Cache() + "\n"
                                + "Hyper-Threading :" + " " + hyperThreading;
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
                            sb.append("VRam : ").append(vram == 0L ? "Shared" : vram + " MB");
                        } else {
                            for (int i = 0; i < gpus.size(); i++) {
                                Specs.GpuInfo gpu = gpus.get(i);
                                sb.append("GPU : ").append(gpu.getName()).append("\n");
                                sb.append("VRam : ").append(gpu.getVram() == 0L ? "Shared" : gpu.getVram() + " MB");
                                if (i < gpus.size() - 1) {
                                    sb.append("\n");
                                }
                            }
                        }
                        
                        return sb.toString();
                    },
                    () -> InfoPanel.getGpuIcon(Specs.getGpuName())
            ),
            new SpecsTab(
                    "ram",
                    "RAM",
                    () -> "RAM (Total) :" + " " + Specs.getRamSize() + " MB" + "\n"
                            + "RAM (Used) :" + " " + Specs.getRamUsed() + " MB" + "\n"
                            + "RAM (Free) :" + " " + Specs.getRamFree() + " MB" + "\n"
                            + "Speed :" + " " + Specs.getRamSpeed() + "\n"
                            + "Latency :" + " " + Specs.getRamLatency() + "\n"
                            + "Ranks :" + " " + Specs.getRamRanks() + "\n"
                            + "DDR Version :" + " " + Specs.getDdrVersion() + "\n"
                            + "Form Factor :" + " " + Specs.getFormFactor(),
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
