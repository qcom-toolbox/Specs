//GUI.java

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
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
        menuBar.setBorder(BorderFactory.createEmptyBorder(ThemeManager.SPACING_SM, 
                                                      ThemeManager.SPACING_MD, 
                                                      ThemeManager.SPACING_SM, 
                                                      ThemeManager.SPACING_MD));
        menuBar.setFont(ThemeManager.getFont(ThemeManager.FontScale.BODY, ThemeManager.FontWeight.REGULAR));
        menuBar.setBackground(ThemeManager.getPalette().panelBackground);
        
        JMenu fileMenu = createStyledMenu("File");

        JMenuItem refreshMenuItem = createStyledMenuItem("Refresh");
        refreshMenuItem.addActionListener(e -> refreshSpecs());
        fileMenu.add(refreshMenuItem);

        JMenuItem uploadMenuItem = createStyledMenuItem("Validate");
        uploadMenuItem.addActionListener(e -> uploadHandler.uploadSpecs());
        fileMenu.add(uploadMenuItem);

        JMenu settingsMenu = createStyledMenu("Settings");

        JMenuItem autoRefreshMenuItem = createStyledMenuItem("Auto Refresh");
        autoRefreshMenuItem.addActionListener(e -> Refresh.showAutoRefreshDialog(jframe));
        settingsMenu.add(autoRefreshMenuItem);

        JMenu themeMenu = createStyledMenu("Theme");
        ButtonGroup themeGroup = new ButtonGroup();
        for (ThemeManager.ThemeMode mode : ThemeManager.ThemeMode.values()) {
            JRadioButtonMenuItem themeItem = createStyledRadioButtonMenuItem(mode.getLabel());
            themeItem.setSelected(mode == ThemeManager.getMode());
            themeItem.addActionListener(e -> ThemeManager.setMode(mode));
            themeGroup.add(themeItem);
            themeMenu.add(themeItem);
        }
        settingsMenu.add(themeMenu);

        JMenu viewMenu = createStyledMenu("View");
        ButtonGroup viewGroup = new ButtonGroup();
        for (ThemeManager.ViewMode mode : ThemeManager.ViewMode.values()) {
            JRadioButtonMenuItem viewItem = createStyledRadioButtonMenuItem(mode.getLabel());
            viewItem.setSelected(mode == ThemeManager.getViewMode());
            viewItem.addActionListener(e -> switchViewMode(mode));
            viewGroup.add(viewItem);
            viewMenu.add(viewItem);
        }
        settingsMenu.add(viewMenu);

        JMenuItem aboutItem = createStyledMenuItem("About");
        aboutItem.addActionListener(e -> About.showAbout(jframe));
        settingsMenu.add(aboutItem);
        fileMenu.add(settingsMenu);

        JMenuItem quitMenuItem = createStyledMenuItem("Quit");
        quitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.addSeparator();
        fileMenu.add(quitMenuItem);

        menuBar.add(fileMenu);

        JMenu stressTestMenu = createStyledMenu("Stress Test");
        JMenuItem stressTestMenuItem = createStyledMenuItem("Stress Test");
        stressTestMenuItem.addActionListener(e -> StressTest.showStressTest(jframe, icon));
        stressTestMenu.add(stressTestMenuItem);
        menuBar.add(stressTestMenu);

        JMenu helpMenu = createStyledMenu("Help");
        JMenuItem helpMenuItem = createStyledMenuItem("Help");
        helpMenuItem.addActionListener(e -> Help.showHelp(jframe, icon));
        helpMenu.add(helpMenuItem);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private static JMenu createStyledMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setFont(ThemeManager.getFont(ThemeManager.FontScale.BODY, ThemeManager.FontWeight.MEDIUM));
        menu.setBorder(BorderFactory.createEmptyBorder(ThemeManager.SPACING_SM, 
                                                     ThemeManager.SPACING_MD, 
                                                     ThemeManager.SPACING_SM, 
                                                     ThemeManager.SPACING_MD));
        menu.setBackground(new Color(0, 0, 0, 0));
        return menu;
    }

    private static JMenuItem createStyledMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(ThemeManager.getFont(ThemeManager.FontScale.BODY, ThemeManager.FontWeight.REGULAR));
        item.setBorder(BorderFactory.createEmptyBorder(ThemeManager.SPACING_SM, 
                                                     ThemeManager.SPACING_LG, 
                                                     ThemeManager.SPACING_SM, 
                                                     ThemeManager.SPACING_LG));
        item.setBackground(new Color(0, 0, 0, 0));
        return item;
    }

    private static JRadioButtonMenuItem createStyledRadioButtonMenuItem(String text) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(text);
        item.setFont(ThemeManager.getFont(ThemeManager.FontScale.BODY, ThemeManager.FontWeight.REGULAR));
        item.setBorder(BorderFactory.createEmptyBorder(ThemeManager.SPACING_SM, 
                                                     ThemeManager.SPACING_LG, 
                                                     ThemeManager.SPACING_SM, 
                                                     ThemeManager.SPACING_LG));
        item.setBackground(new Color(0, 0, 0, 0));
        return item;
    }

    private static JTabbedPane createTabbedPane() {
        SpecsTab.clearPanels();

        JTabbedPane pane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT) {
            @Override
            public void updateUI() {
                super.updateUI();
                setUI(new ModernTabbedPaneUI());
            }
        };
        pane.setFont(ThemeManager.getFont(ThemeManager.FontScale.BODY, ThemeManager.FontWeight.REGULAR));
        pane.setBorder(BorderFactory.createEmptyBorder(ThemeManager.SPACING_XL, 
                                                     ThemeManager.SPACING_XL, 
                                                     ThemeManager.SPACING_XL, 
                                                     ThemeManager.SPACING_XL));
        pane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        pane.setUI(new ModernTabbedPaneUI());

        for (SpecsTab tab : SpecsTab.getTabs()) {
            String headerTitle = getHeaderTitle(tab.getId());
            JPanel infoPanel = InfoPanel.createModernInfoPanel(headerTitle, tab.getContent(), tab.getIcon());
            SpecsTab.bindPanel(tab.getId(), infoPanel);
            pane.addTab(tab.getTitle(), scaleIcon(tab.getIcon(), 28), infoPanel, tab.getTitle() + " specifications");
        }

        return pane;
    }

    private static String getHeaderTitle(String tabId) {
        return switch (tabId) {
            case "os" -> Specs.getOperatingSystemName() + " " + Specs.getOperatingSystemVersion();
            case "cpu" -> Specs.getCpuName();
            case "gpu" -> Specs.getGpuName();
            case "ram" -> Specs.getRamSize() + " MB";
            default -> tabId.toUpperCase();
        };
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

    private static class ModernTabbedPaneUI extends BasicTabbedPaneUI {
        private static final int TAB_PADDING = ThemeManager.SPACING_LG;
        private static final int TAB_HEIGHT = 48;
        private static final int ARC_SIZE = 10;
        private static final int TAB_GAP = 6;

        @Override
        protected void installDefaults() {
            super.installDefaults();
            tabInsets = new Insets(ThemeManager.SPACING_SM, TAB_PADDING, ThemeManager.SPACING_SM, TAB_PADDING);
        }

        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return TAB_HEIGHT + tabInsets.top + tabInsets.bottom;
        }

        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + TAB_PADDING * 2;
        }

        @Override
        protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
            Rectangle tabRect = rects[tabIndex];
            boolean isSelected = tabIndex == tabPane.getSelectedIndex();
            boolean isRollover = getRolloverTab() == tabIndex;

            ThemeManager.Palette palette = ThemeManager.getPalette();

            // Simple tab background
            if (isSelected) {
                g.setColor(palette.tabSelected);
            } else if (isRollover) {
                g.setColor(ThemeManager.blendColors(palette.tabBackground, palette.tabSelected, 0.3));
            } else {
                g.setColor(palette.tabBackground);
            }

            g.fillRoundRect(tabRect.x + TAB_GAP / 2, tabRect.y + 2, 
                          tabRect.width - TAB_GAP, tabRect.height - 4, ARC_SIZE, ARC_SIZE);

            // Simple border
            g.setColor(palette.border);
            g.drawRoundRect(tabRect.x + TAB_GAP / 2, tabRect.y + 2, 
                          tabRect.width - TAB_GAP - 1, tabRect.height - 5, ARC_SIZE, ARC_SIZE);

            // Text and icon color
            if (isSelected) {
                g.setColor(palette.textPrimary);
            } else {
                g.setColor(palette.textSecondary);
            }

            super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            // Custom border handled in paintTab
        }

        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            // No content border for cleaner look
        }
    }
}
