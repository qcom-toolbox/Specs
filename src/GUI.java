//GUI.java

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GUI {
    private static ImageIcon icon;
    private static JFrame jframe;
    private static JTabbedPane tabbedPane;
    private static JPanel legacyPanel;

    public static void main(String[] args) {

        Upload uploadHandler = new Upload();

        icon = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("/icon/Icon 128x128.png")));

        jframe = new JFrame("Specs");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(900, 520);
        jframe.setMinimumSize(new Dimension(640, 400));
        jframe.setLocationRelativeTo(null);
        jframe.setIconImage(icon.getImage());
        jframe.setLayout(new BorderLayout());

        JMenuBar menuBar = createMenuBar(uploadHandler);

        if (ThemeManager.getViewMode() == ThemeManager.ViewMode.LEGACY) {
            legacyPanel = createLegacyPanel();
            jframe.add(legacyPanel, BorderLayout.CENTER);
        } else {
            tabbedPane = createTabbedPane();
            jframe.add(tabbedPane, BorderLayout.CENTER);
        }

        jframe.setJMenuBar(menuBar);

        ThemeManager.initialize(jframe);
        jframe.setVisible(true);

        Refresh.startAutoRefresh();
    }

    private static JMenuBar createMenuBar(Upload uploadHandler) {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem refreshMenuItem = new JMenuItem("Refresh");
        refreshMenuItem.addActionListener(e -> refreshSpecs());
        fileMenu.add(refreshMenuItem);

        JMenuItem uploadMenuItem = new JMenuItem("Validate");
        uploadMenuItem.addActionListener(e -> uploadHandler.uploadSpecs());
        fileMenu.add(uploadMenuItem);

        JMenu settingsMenu = new JMenu("Settings");

        JMenuItem autoRefreshMenuItem = new JMenuItem("Auto Refresh");
        autoRefreshMenuItem.addActionListener(e -> Refresh.showAutoRefreshDialog(jframe));
        settingsMenu.add(autoRefreshMenuItem);

        JMenu themeMenu = new JMenu("Theme");
        ButtonGroup themeGroup = new ButtonGroup();
        for (ThemeManager.ThemeMode mode : ThemeManager.ThemeMode.values()) {
            JRadioButtonMenuItem themeItem = new JRadioButtonMenuItem(mode.getLabel());
            themeItem.setSelected(mode == ThemeManager.getMode());
            themeItem.addActionListener(e -> ThemeManager.setMode(mode));
            themeGroup.add(themeItem);
            themeMenu.add(themeItem);
        }
        settingsMenu.add(themeMenu);

        JMenu viewMenu = new JMenu("View");
        ButtonGroup viewGroup = new ButtonGroup();
        for (ThemeManager.ViewMode mode : ThemeManager.ViewMode.values()) {
            JRadioButtonMenuItem viewItem = new JRadioButtonMenuItem(mode.getLabel());
            viewItem.setSelected(mode == ThemeManager.getViewMode());
            viewItem.addActionListener(e -> switchViewMode(mode));
            viewGroup.add(viewItem);
            viewMenu.add(viewItem);
        }
        settingsMenu.add(viewMenu);

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> About.showAbout(jframe));
        settingsMenu.add(aboutItem);
        fileMenu.add(settingsMenu);

        JMenuItem quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);

        menuBar.add(fileMenu);

        JMenu stressTestMenu = new JMenu("Stress Test");
        JMenuItem stressTestMenuItem = new JMenuItem("Stress Test");
        stressTestMenuItem.addActionListener(e -> StressTest.showStressTest(jframe, icon));
        stressTestMenu.add(stressTestMenuItem);
        menuBar.add(stressTestMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpMenuItem = new JMenuItem("Help");
        helpMenuItem.addActionListener(e -> Help.showHelp(jframe, icon));
        helpMenu.add(helpMenuItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private static JTabbedPane createTabbedPane() {
        SpecsTab.clearPanels();

        JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
        pane.setFont(getModernFont().deriveFont(Font.PLAIN, 13f));
        pane.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        pane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

        for (SpecsTab tab : SpecsTab.getTabs()) {
            String headerTitle = getHeaderTitle(tab.getId());
            JPanel infoPanel = InfoPanel.createModernInfoPanel(headerTitle, tab.getContent(), tab.getIcon());
            SpecsTab.bindPanel(tab.getId(), infoPanel);
            pane.addTab(tab.getTitle(), scaleIcon(tab.getIcon(), 24), infoPanel, tab.getTitle() + " specifications");
        }

        return pane;
    }

    private static String getHeaderTitle(String tabId) {
        return switch (tabId) {
            case "os" -> Specs.getOperatingSystemName();
            case "cpu" -> Specs.getCpuName();
            case "gpu" -> Specs.getGpuName();
            case "ram" -> Specs.getRamSize() + " MB";
            default -> tabId.toUpperCase();
        };
    }

    private static Font getModernFont() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("mac")) {
            return new Font("SF Pro Text", Font.PLAIN, 13);
        } else if (os.contains("win")) {
            return new Font("Segoe UI", Font.PLAIN, 13);
        } else {
            return new Font("Roboto", Font.PLAIN, 13);
        }
    }

    private static JPanel createLegacyPanel() {
        SpecsTab.clearPanels();

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (SpecsTab tab : SpecsTab.getTabs()) {
            JPanel infoPanel = InfoPanel.createInfoPanel(tab.getTitle(), tab.getContent(), tab.getIcon());
            SpecsTab.bindPanel(tab.getId(), infoPanel);
            panel.add(infoPanel);
        }

        return panel;
    }

    private static ImageIcon scaleIcon(ImageIcon icon, int size) {
        if (icon == null || icon.getIconWidth() <= 0) {
            return icon;
        }
        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    static void refreshSpecs() {
        SpecsTab.refreshAll();
        updateHeaderTitles();
        ThemeManager.applyToWindow(jframe);
    }

    private static void updateHeaderTitles() {
        for (SpecsTab tab : SpecsTab.getTabs()) {
            JPanel panel = SpecsTab.getPanel(tab.getId());
            if (panel != null) {
                Component[] components = panel.getComponents();
                if (components.length > 0 && components[0] instanceof JPanel content) {
                    Component[] contentChildren = content.getComponents();
                    for (Component child : contentChildren) {
                        if (child instanceof JPanel header && header.getComponentCount() > 1) {
                            Component headerChild = header.getComponent(1);
                            if (headerChild instanceof JLabel titleLabel) {
                                titleLabel.setText(getHeaderTitle(tab.getId()));
                            }
                        }
                    }
                }
            }
        }
    }

    static void switchViewMode(ThemeManager.ViewMode mode) {
        ThemeManager.setViewMode(mode);
        jframe.remove(tabbedPane != null ? tabbedPane : legacyPanel);
        
        if (mode == ThemeManager.ViewMode.LEGACY) {
            legacyPanel = createLegacyPanel();
            tabbedPane = null;
            jframe.add(legacyPanel, BorderLayout.CENTER);
        } else {
            tabbedPane = createTabbedPane();
            legacyPanel = null;
            jframe.add(tabbedPane, BorderLayout.CENTER);
        }
        
        ThemeManager.applyToWindow(jframe);
        jframe.revalidate();
        jframe.repaint();
    }

    static JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}
