//Refresh.java

import javax.swing.*;

public class Refresh {
    private static java.util.concurrent.ScheduledExecutorService autoRefreshExecutor;
    private static int refreshInterval = 5;

    public static void startAutoRefresh() {
        stopAutoRefresh();
        autoRefreshExecutor = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();

        autoRefreshExecutor.scheduleAtFixedRate(
                () -> SwingUtilities.invokeLater(Refresh::refreshSpecs),
                0,
                refreshInterval,
                java.util.concurrent.TimeUnit.SECONDS
        );
    }

    private static void refreshSpecs() {
        try {
            GUI.refreshSpecs();
            System.gc();
        } catch (Exception e) {
            System.err.println("Unexpected error while updating system specs: " + e.getMessage());
        }
    }

    public static void showAutoRefreshDialog(JFrame parent) {
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
                case "1 second" -> setAutoRefresh(1);
                case "5 seconds" -> setAutoRefresh(5);
                case "10 seconds" -> setAutoRefresh(10);
                case "15 seconds" -> setAutoRefresh(15);
                case "60 seconds" -> setAutoRefresh(60);
                case "Custom" -> handleCustomInterval(parent);
                case "Disabled" -> stopAutoRefresh();
            }
        }
    }

    private static void handleCustomInterval(JFrame parent) {
        String input = JOptionPane.showInputDialog(parent, "Enter custom interval in seconds (only numbers):");
        if (input != null) {
            try {
                int customInterval = Integer.parseInt(input);
                if (customInterval > 0) {
                    setAutoRefresh(customInterval);
                } else {
                    JOptionPane.showMessageDialog(parent, "Enter a positive number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "Invalid input. Auto refresh disabled.", "Error", JOptionPane.ERROR_MESSAGE);
                stopAutoRefresh();
            }
        }
    }

    private static void setAutoRefresh(int interval) {
        if (interval > 0) {
            refreshInterval = interval;
            startAutoRefresh();
        } else {
            stopAutoRefresh();
        }
    }

    private static void stopAutoRefresh() {
        if (autoRefreshExecutor != null && !autoRefreshExecutor.isShutdown()) {
            autoRefreshExecutor.shutdownNow();
        }
    }
}
