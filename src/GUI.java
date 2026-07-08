//GUI.java

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class GUI {
    private static ImageIcon icon;
    private static JFrame jframe;
    private static JTabbedPane tabbedPane;

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
        tabbedPane = createTabbedPane();

        jframe.setJMenuBar(menuBar);
        jframe.add(tabbedPane, BorderLayout.CENTER);

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

        JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        pane.setFont(pane.getFont().deriveFont(Font.BOLD, 13f));
        pane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        for (SpecsTab tab : SpecsTab.getTabs()) {
            JPanel infoPanel = InfoPanel.createInfoPanel(tab.getTitle(), tab.getContent(), tab.getIcon());
            SpecsTab.bindPanel(tab.getId(), infoPanel);
            pane.addTab(tab.getTitle(), scaleIcon(tab.getIcon(), 20), infoPanel, tab.getTitle() + " specifications");
        }

        return pane;
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
        ThemeManager.applyToWindow(jframe);
    }

    static JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}
