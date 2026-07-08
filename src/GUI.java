//GUI.java

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GUI {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static ImageIcon icon;
    private static JFrame jframe;
    private static CardSet cards;

    static ImageIcon osIcon;
    static ImageIcon cpuIcon;
    static ImageIcon ramIcon;
    static ImageIcon gpuIcon;

    public static void main(String[] args) {

        // Apply the saved (or default) FlatLaf theme before any component is created.
        ThemeManager.applyStartupTheme();

        Upload uploadHandler = new Upload();

        // Load icons once at startup
        loadIcons();

        // Load the application icon
        icon = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("/icon/Icon 128x128.png")));

        // Create the main window
        jframe = new JFrame("Specs");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setMinimumSize(new Dimension(760, 520));
        jframe.setSize(1200, 700);
        jframe.setLocationRelativeTo(null); // Center the window on the screen
        jframe.setIconImage(icon.getImage()); // Set the window's icon

        // ---- Menu bar (all original menu items are preserved) ----
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
        autoRefreshMenuItem.addActionListener(e -> Refresh.showAutoRefreshDialog(jframe, cards));
        settingsMenu.add(autoRefreshMenuItem);
        settingsMenu.add(buildThemeMenu());

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

        jframe.setJMenuBar(menuBar);

        // ---- Root layout ----
        JPanel root = new JPanel(new MigLayout("insets 0, fill, wrap 1", "[grow,fill]", "[]0[]0[grow,fill]0[]"));

        root.add(buildHeader(), "growx");
        root.add(buildToolbar(uploadHandler), "growx");

        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        statusLabel.setFont(statusLabel.getFont().deriveFont(11f));

        // ---- Card grid ----
        JPanel cardGrid = new JPanel(new MigLayout(
                "insets 20, wrap 2, gap 18 18, fillx",
                "[grow,fill][grow,fill]",
                "[grow,fill][grow,fill]"));
        cardGrid.setOpaque(false);

        SpecCard osCard = new SpecCard("Operating System",
                osIcon, new Color(0x2F80ED));
        SpecCard cpuCard = new SpecCard("CPU",
                cpuIcon, new Color(0x9B51E0));
        SpecCard gpuCard = new SpecCard("GPU",
                gpuIcon, new Color(0x27AE60));
        SpecCard ramCard = new SpecCard("RAM",
                ramIcon, new Color(0xF2994A));

        cardGrid.add(osCard);
        cardGrid.add(cpuCard);
        cardGrid.add(gpuCard);
        cardGrid.add(ramCard);

        JScrollPane scrollPane = new JScrollPane(cardGrid);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        root.add(scrollPane, "grow");
        root.add(statusLabel, "growx");

        cards = new CardSet(osCard, cpuCard, gpuCard, ramCard, statusLabel);

        jframe.add(root);

        // Populate the cards with the current specs
        applySpecs();

        jframe.setVisible(true);

        // Start the timer for auto-refresh with the default interval
        Refresh.startAutoRefresh(cards);
    }

    private static JComponent buildHeader() {
        JPanel header = new JPanel(new MigLayout("insets 16 20 16 20, fillx", "[]push[]"));
        header.putClientProperty(FlatClientProperties.STYLE, "arc:0");

        JPanel titleBlock = new JPanel(new MigLayout("insets 0, wrap 1"));
        titleBlock.setOpaque(false);
        JLabel title = new JLabel("Specs");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        JLabel subtitle = new JLabel("Live overview of your PC's hardware");
        subtitle.setFont(subtitle.getFont().deriveFont(12f));
        subtitle.setForeground(Color.GRAY);
        titleBlock.add(title);
        titleBlock.add(subtitle);

        header.add(titleBlock);
        return header;
    }

    private static JComponent buildToolbar(Upload uploadHandler) {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        toolBar.add(toolbarButton("\u27F3 Refresh", "Refresh system specs now", e -> refreshSpecs()));
        toolBar.add(toolbarButton("\u23F1 Auto Refresh", "Configure automatic refresh interval",
                e -> Refresh.showAutoRefreshDialog(jframe, cards)));
        toolBar.add(toolbarButton("\u2601 Validate", "Send or save specs for comparison",
                e -> uploadHandler.uploadSpecs()));
        toolBar.addSeparator();
        toolBar.add(toolbarButton("\u26A1 Stress Test", "Run a CPU stress test",
                e -> StressTest.showStressTest(jframe, icon)));
        toolBar.add(toolbarButton("\u25D1 Theme", "Change the app's color theme", e -> showThemePopup(toolBar)));
        toolBar.addSeparator();
        toolBar.add(toolbarButton("\u2753 Help", "Open the help window", e -> Help.showHelp(jframe, icon)));

        return toolBar;
    }

    private static JButton toolbarButton(String text, String tooltip, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        button.addActionListener(listener);
        return button;
    }

    private static void showThemePopup(Component invoker) {
        JPopupMenu popup = new JPopupMenu();
        String current = ThemeManager.getCurrentThemeName();
        ButtonGroup group = new ButtonGroup();
        for (String themeName : ThemeManager.THEMES.keySet()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(themeName, themeName.equals(current));
            item.addActionListener(e -> applyThemeAndRefreshUi(themeName));
            group.add(item);
            popup.add(item);
        }
        popup.show(invoker, 0, invoker.getHeight());
    }

    private static JMenu buildThemeMenu() {
        JMenu themeMenu = new JMenu("Theme");
        String current = ThemeManager.getCurrentThemeName();
        ButtonGroup group = new ButtonGroup();
        for (String themeName : ThemeManager.THEMES.keySet()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(themeName, themeName.equals(current));
            item.addActionListener(e -> applyThemeAndRefreshUi(themeName));
            group.add(item);
            themeMenu.add(item);
        }
        return themeMenu;
    }

    private static void applyThemeAndRefreshUi(String themeName) {
        ThemeManager.apply(themeName);
        SwingUtilities.updateComponentTreeUI(jframe);
    }

    // Method to refresh system specifications
    private static void refreshSpecs() {
        applySpecs();
        cards.statusLabel.setText("Last updated: " + LocalTime.now().format(TIME_FMT));
    }

    private static void applySpecs() {
        cards().osCard.setInfo("Operating System : " + Specs.getOperatingSystemName()
                + "\nVersion : " + Specs.getOperatingSystemVersion());

        cards().cpuCard.setInfo("CPU : " + Specs.getCpuName()
                + "\nCores : " + Specs.getCpuCores()
                + "\nThreads : " + Specs.getCpuThreads());

        long vram = Long.parseLong(Specs.getGpuVram());
        cards().gpuCard.setInfo("GPU : " + Specs.getGpuName()
                + "\nVram : " + (vram <= 0L ? "Shared" : vram + " MB"));

        long total = Specs.getRamSize();
        long used = Specs.getRamUsed();
        long free = Specs.getRamFree();
        cards().ramCard.setInfo("RAM (Total) : " + total + " MB"
                + "\nRAM (Used) : " + used + " MB"
                + "\nRAM (Free) : " + free + " MB");
        int percent = total > 0 ? (int) Math.round((used * 100.0) / total) : 0;
        cards().ramCard.setProgress(percent, percent + "% used");
    }

    private static CardSet cards() {
        return cards;
    }

    // Method to load icons once
    private static void loadIcons() {
        if (osIcon == null) {
            osIcon = InfoPanel.getOsIcon();
        }
        if (cpuIcon == null) {
            cpuIcon = InfoPanel.getCpuIcon(Specs.getCpuName());
        }
        if (ramIcon == null) {
            ramIcon = InfoPanel.getRamIcon();
        }
        if (gpuIcon == null) {
            gpuIcon = InfoPanel.getGpuIcon(Specs.getGpuName());
        }
    }
}
