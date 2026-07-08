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
                            + "Version :" + " " + Specs.getOperatingSystemVersion(),
                    InfoPanel::getOsIcon
            ),
            new SpecsTab(
                    "cpu",
                    "CPU",
                    () -> "CPU :" + " " + Specs.getCpuName() + "\n"
                            + "Cores :" + " " + Specs.getCpuCores() + "\n"
                            + "Threads :" + " " + Specs.getCpuThreads(),
                    () -> InfoPanel.getCpuIcon(Specs.getCpuName())
            ),
            new SpecsTab(
                    "gpu",
                    "GPU",
                    () -> {
                        long vram = Long.parseLong(Specs.getGpuVram());
                        return "GPU :" + " " + Specs.getGpuName() + "\nVram :"
                                + " " + (vram == 0L ? "Shared" : vram + " MB");
                    },
                    () -> InfoPanel.getGpuIcon(Specs.getGpuName())
            ),
            new SpecsTab(
                    "ram",
                    "RAM",
                    () -> "RAM (Total) :" + " " + Specs.getRamSize() + " MB" + "\n"
                            + "RAM (Used) :" + " " + Specs.getRamUsed() + " MB" + "\n"
                            + "RAM (Free) :" + " " + Specs.getRamFree() + " MB",
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
