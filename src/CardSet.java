// CardSet.java
// Small holder passed to GUI/Refresh so they can update the four spec
// cards (and the status bar) directly by reference, instead of the old
// approach of pulling components back out of a JPanel by index.

import javax.swing.*;

public class CardSet {
    public final SpecCard osCard;
    public final SpecCard cpuCard;
    public final SpecCard gpuCard;
    public final SpecCard ramCard;
    public final JLabel statusLabel;

    public CardSet(SpecCard osCard, SpecCard cpuCard, SpecCard gpuCard, SpecCard ramCard, JLabel statusLabel) {
        this.osCard = osCard;
        this.cpuCard = cpuCard;
        this.gpuCard = gpuCard;
        this.ramCard = ramCard;
        this.statusLabel = statusLabel;
    }
}
