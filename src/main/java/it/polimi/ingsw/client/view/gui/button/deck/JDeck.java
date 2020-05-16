package it.polimi.ingsw.client.view.gui.button.deck;

import javax.swing.*;
import java.util.ArrayList;

public class JDeck {
    private ArrayList<JGod> gods;
    private JLabel selected;
    private String selPath = "img/labels/selected.png";
    private int current;

    public JDeck() {
        this.gods = new ArrayList<>();
        this.selected = new JLabel(new ImageIcon(selPath));
    }

    public void setCurrent(JGod chosen) {
        this.current = this.gods.indexOf(chosen);
    }

    public JGod getGod(int i) {
        return this.gods.get(i);
    }
}
