package it.polimi.ingsw.client.view.gui.button.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class JDeck extends JPanel implements ActionListener {
    private ArrayList<JGod> gods;
    private int current;

    public JDeck(List<God> list) {
        this.gods = new ArrayList<>();

        int i = 0;
        for (God god : list) {
            this.gods.add(new JGod(god));
            this.gods.get(i).getMini().addActionListener(this);
            i++;
        }

        setLayout(new GridBagLayout());
        setOpaque(false);
    }

    public void setCurrent(JGod chosen) {
        this.gods.get(current).getMini().disactive();
        this.current = this.gods.indexOf(chosen);
        this.gods.get(current).getMini().active();
    }

    public JGod getGod(int i) {
        return this.gods.get(i);
    }

    public JCard getCard(int i) {
        return this.gods.get(i).getCard();
    }

    public JMini getMini(int i) {
        return this.gods.get(i).getMini();
    }

    public void showMiniList() {
        int i = 0;
        for (JGod god : gods) {
            if (!god.getGod().toString().equalsIgnoreCase("poseidon")) {
                GridBagConstraints c = new GridBagConstraints();

                c.gridx = i;
                c.gridy = 0;
                c.weightx = 1;
                c.weighty = 0f;
                c.fill = GridBagConstraints.BOTH;

                add(god.getMini(), c);
                i++;
            }
        }
    }

    public JGod getJGod(God god) {
        for (JGod g : gods) {
            if (g.getMini().getGod().equals(god))
                return g;
        }
        return null;
    }

    public List<JGod> getList() {
        return gods;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setCurrent(getJGod(((JMini) e.getSource()).getGod()));
    }
}
