//StressTest.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StressTest {
    private static Timer countdownTimer;
    private static ExecutorService stressExecutor;

    // Method to display the Stress Test window
    public static void showStressTest(JFrame parent, ImageIcon icon) {
        // Create a new window for the stress test
        JFrame stressTestFrame = new JFrame("Stress Test");
        stressTestFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        stressTestFrame.setSize(600, 400);

        // Panel to hold components
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Create a label for the countdown timer
        JLabel timerLabel = new JLabel("Time left : 60 seconds", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Serif", Font.BOLD, 20));
        panel.add(timerLabel, BorderLayout.NORTH);

        // Create a text area to provide user information
        JTextArea infoText = new JTextArea();
        infoText.setText("Welcome to the Stress Test tool!\n" +
                "This test will run CPU-intensive tasks across all available cores " +
                "to maximize CPU usage.");
        infoText.setEditable(false);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        panel.add(new JScrollPane(infoText), BorderLayout.CENTER);

        // Input fields for selecting time duration (with default of 60 seconds)
        JPanel inputPanel = new JPanel(new GridLayout(2, 2)); // Simplified to 2 rows for time field and buttons
        JLabel timeLabel = new JLabel("Test Duration (seconds) : ");
        JTextField timeField = new JTextField("60", 5);  // Default to 60 seconds
        timeLabel.setLabelFor(timeField);
        inputPanel.add(timeLabel);
        inputPanel.add(timeField);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Add a start button
        JButton startButton = new JButton("Start Stress Test");
        startButton.setBackground(new Color(100, 200, 100));
        startButton.setToolTipText("Start the stress test with the selected options.");
        startButton.addActionListener(e -> {
            String timeText = timeField.getText();
            try {
                int duration = Integer.parseInt(timeText);
                startStressTest(duration, timerLabel); // Always use multi-core mode
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(stressTestFrame, "Please enter a valid number for the time duration.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        inputPanel.add(startButton);

        // Add a close button
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(200, 100, 100));
        closeButton.setToolTipText("Stop the stress test and close this window.");
        closeButton.addActionListener(e -> {
            if (stressExecutor != null && !stressExecutor.isShutdown()) {
                stressExecutor.shutdownNow(); // Stop any ongoing stress test
            }
            stressTestFrame.dispose();
        });
        inputPanel.add(closeButton);

        panel.add(inputPanel, BorderLayout.SOUTH); // Button panel at the bottom

        // Set the icon for the window
        stressTestFrame.setIconImage(icon.getImage());

        // Add everything to the main frame
        stressTestFrame.add(panel);
        ThemeManager.applyToWindow(stressTestFrame);
        stressTestFrame.setLocationRelativeTo(parent);
        stressTestFrame.setVisible(true);
    }

    // Method to start the stress test
    private static void startStressTest(int duration, JLabel timerLabel) {
        int numCores = Runtime.getRuntime().availableProcessors(); // Always use all available cores
        stressExecutor = Executors.newFixedThreadPool(numCores);

        // Create and start stress tasks
        for (int i = 0; i < numCores; i++) {
            stressExecutor.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    // Intensive CPU-bound task to utilize as much GHz as possible
                    double x = 0;
                    for (long j = 0; j < Long.MAX_VALUE; j++) {
                        x = Math.sin(x) * Math.cos(x) * Math.tan(x); // Intense floating-point math
                        // Check for interruption after some iterations
                        if (j % 10000000 == 0 && Thread.currentThread().isInterrupted()) {
                            break; // Break out of the loop if interrupted
                        }
                    }
                }
            });
        }

        // Countdown timer (in seconds)
        countdownTimer = new Timer(1000, new ActionListener() {
            int timeRemaining = duration;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeRemaining > 0) {
                    timerLabel.setText("Time left : " + timeRemaining + " seconds");
                    timeRemaining--;
                } else {
                    ((Timer) e.getSource()).stop(); // Stop the countdown
                    stressExecutor.shutdownNow(); // End the stress test
                    timerLabel.setText("Test Complete");
                    JOptionPane.showMessageDialog(null, "Stress Test Complete!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        countdownTimer.start();
    }
}
