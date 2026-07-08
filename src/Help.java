//Help.java

import javax.swing.*;
import java.awt.*;

public class Help {
    // Method to display the help window
    public static void showHelp(JFrame parent, ImageIcon icon) {
        // Create a new window for help
        JFrame helpFrame = new JFrame("Help");
        helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        helpFrame.setSize(600, 400); // Set the size of the window

        // Create a text area to hold the help content
        JTextArea helpText = new JTextArea();
        helpText.setEditable(false); // Make the text area non-editable
        helpText.setLineWrap(true); // Enable line wrapping
        helpText.setWrapStyleWord(true); // Wrap at word boundaries

        // Add help text with explanations
        String helpContent = "Help Information:\n\n" +
                "This application provides system specifications:\n\n" +
                "1. CPU : Central Processing Unit. It is the primary component of a computer that performs most of the processing inside a computer.\n" +
                "2. GPU : Graphics Processing Unit. It is responsible for rendering images, animations, and video for the computer's screen.\n" +
                "3. RAM : Random Access Memory. It is the memory used by the CPU to store data that is being processed.\n" +
                "4. VRAM : Video RAM. It is a special type of RAM used specifically to store image data for the GPU.\n\n" +
                "You can view detailed information about your CPU, GPU, RAM, and more in the main application window.\n";

        helpText.setText(helpContent); // Set the help text in the text area

        // Add the help text to the window
        helpFrame.add(new JScrollPane(helpText), BorderLayout.CENTER); // Use JScrollPane to allow scrolling if needed

        // Add a close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> helpFrame.dispose()); // Close the help window
        helpFrame.add(closeButton, BorderLayout.SOUTH);

        helpFrame.setIconImage(icon.getImage());
        ThemeManager.applyToWindow(helpFrame);
        helpFrame.setLocationRelativeTo(parent);
        helpFrame.setVisible(true);
    }
}
