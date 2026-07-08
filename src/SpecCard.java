// SpecCard.java
// A modern "card" component used to present one category of hardware
// information (OS / CPU / GPU / RAM). Replaces the old plain
// JPanel + monospaced JTextPane combo with a rounded, FlatLaf-styled
// card built with MigLayout, an accent stripe, nicer typography and
// (optionally) a usage progress bar.

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class SpecCard extends JPanel {

    private final JLabel iconLabel = new JLabel();
    private final JLabel titleLabel = new JLabel();
    private final JEditorPane contentPane = new JEditorPane("text/html", "");
    private JProgressBar progressBar;

    public SpecCard(String title, ImageIcon icon, Color accent) {
        super(new MigLayout("insets 16 18 16 18, fillx, wrap 1", "[grow,fill]"));

        // Rounded corners via FlatLaf's styling client property.
        putClientProperty(FlatClientProperties.STYLE, "arc:16");
        setBorder(new MatteBorder(0, 4, 0, 0, accent));

        titleLabel.setText(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        iconLabel.setIcon(scale(icon, 36, 36));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        JPanel header = new JPanel(new MigLayout("insets 0, fillx"));
        header.setOpaque(false);
        header.add(iconLabel);
        header.add(titleLabel);

        contentPane.setEditable(false);
        contentPane.setFocusable(false);
        contentPane.setOpaque(false);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        add(header, "growx");
        add(contentPane, "growx");
    }

    /**
     * Accepts the same "Label : value\nLabel : value" strings the rest of
     * the app already builds, and renders them as a clean two-tone list
     * instead of a raw monospaced blob.
     */
    public void setInfo(String info) {
        StringBuilder html = new StringBuilder("<html><body style='font-family:sans-serif;font-size:12px;'>");
        for (String rawLine : info.split("\n")) {
            String line = rawLine.trim();
            if (line.isEmpty()) continue;
            int idx = line.indexOf(" : ");
            html.append("<div style='margin-bottom:5px;'>");
            if (idx > 0) {
                String label = escape(line.substring(0, idx));
                String value = escape(line.substring(idx + 3));
                html.append("<span style='color:#888888;'>").append(label).append(" : </span>")
                        .append("<b>").append(value).append("</b>");
            } else {
                html.append(escape(line));
            }
            html.append("</div>");
        }
        html.append("</body></html>");
        contentPane.setText(html.toString());
    }

    public void setCardIcon(ImageIcon icon) {
        iconLabel.setIcon(scale(icon, 36, 36));
    }

    /** Shows (creating if needed) a labeled usage bar under the card content, e.g. for RAM usage. */
    public void setProgress(int percent, String caption) {
        if (progressBar == null) {
            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            add(progressBar, "growx, gaptop 10");
            revalidate();
        }
        progressBar.setValue(Math.max(0, Math.min(100, percent)));
        progressBar.setString(caption);
    }

    private static ImageIcon scale(ImageIcon icon, int w, int h) {
        if (icon == null || icon.getIconWidth() <= 0) return icon;
        Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
