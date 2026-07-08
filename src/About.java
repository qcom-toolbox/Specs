//About.java

import javax.swing.*;
import java.util.Objects;

public class About {
    public static void showAbout(JFrame parent) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(GUI.class.getResource("/icon/Icon 128x128.png")));
        JOptionPane.showMessageDialog(parent,
                "Specs\nVersion 2.1.0 Beta\n© 2026",
                "About",
                JOptionPane.INFORMATION_MESSAGE,
                icon);
    }
}
