//Refresh.java

import javax.swing.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Refresh {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static ScheduledExecutorService autoRefreshExecutor;
    private static int refreshInterval = 5; // Default interval (seconds)

    // Start the auto-refresh
    public static void startAutoRefresh(CardSet cards) {
        stopAutoRefresh(); // Ensure any previous executor is stopped
        autoRefreshExecutor = Executors.newSingleThreadScheduledExecutor();

        autoRefreshExecutor.scheduleAtFixedRate(() -> refreshSpecs(cards), 0, refreshInterval, TimeUnit.SECONDS);
    }

    private static void refreshSpecs(CardSet cards) {
        try {
            SwingUtilities.invokeLater(() -> {
                cards.osCard.setInfo("Operating System : " + Specs.getOperatingSystemName()
                        + "\nVersion : " + Specs.getOperatingSystemVersion());

                cards.cpuCard.setInfo("CPU : " + Specs.getCpuName()
                        + "\nCores : " + Specs.getCpuCores()
                        + "\nThreads : " + Specs.getCpuThreads());

                long vram = Long.parseLong(Specs.getGpuVram());
                cards.gpuCard.setInfo("GPU : " + Specs.getGpuName()
                        + "\nVram : " + (vram <= 0L ? "Shared" : vram + " MB"));

                long total = Specs.getRamSize();
                long used = Specs.getRamUsed();
                long free = Specs.getRamFree();
                cards.ramCard.setInfo("RAM (Total) : " + total + " MB"
                        + "\nRAM (Used) : " + used + " MB"
                        + "\nRAM (Free) : " + free + " MB");
                int percent = total > 0 ? (int) Math.round((used * 100.0) / total) : 0;
                cards.ramCard.setProgress(percent, percent + "% used");

                cards.statusLabel.setText("Last updated: " + LocalTime.now().format(TIME_FMT));

                // Ask the garbage collector to release unused memory after the update
                System.gc();
            });
        } catch (Exception e) {
            System.err.println("Unexpected error while updating system specs: " + e.getMessage());
        }
    }

    // Display Auto Refresh dialog
    public static void showAutoRefreshDialog(JFrame parent, CardSet cards) {
        String[] options = {"1 second", "5 seconds", "10 seconds", "15 seconds", "60 seconds", "Custom", "Disabled"};
        String selected = (String) JOptionPane.showInputDialog(
                parent,
                "Select Auto Refresh Interval:",
                "Auto Refresh",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        );

        if (selected != null) {
            switch (selected) {
                case "1 second" -> setAutoRefresh(1, cards);
                case "5 seconds" -> setAutoRefresh(5, cards);
                case "10 seconds" -> setAutoRefresh(10, cards);
                case "15 seconds" -> setAutoRefresh(15, cards);
                case "60 seconds" -> setAutoRefresh(60, cards);
                case "Custom" -> handleCustomInterval(parent, cards);
                case "Disabled" -> stopAutoRefresh();
            }
        }
    }

    // Handle custom interval
    private static void handleCustomInterval(JFrame parent, CardSet cards) {
        String input = JOptionPane.showInputDialog(parent, "Enter custom interval in seconds (only numbers):");
        if (input != null) {
            try {
                int customInterval = Integer.parseInt(input);
                if (customInterval > 0) {
                    setAutoRefresh(customInterval, cards);
                } else {
                    JOptionPane.showMessageDialog(parent, "Enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "Invalid input. Auto refresh disabled.", "Error", JOptionPane.ERROR_MESSAGE);
                stopAutoRefresh();
            }
        }
    }

    // Set auto-refresh interval
    private static void setAutoRefresh(int interval, CardSet cards) {
        if (interval > 0) {
            refreshInterval = interval;
            startAutoRefresh(cards);
        } else {
            stopAutoRefresh();
        }
    }

    // Stop any active auto-refresh executor
    private static void stopAutoRefresh() {
        if (autoRefreshExecutor != null && !autoRefreshExecutor.isShutdown()) {
            autoRefreshExecutor.shutdownNow();
        }
    }
}
