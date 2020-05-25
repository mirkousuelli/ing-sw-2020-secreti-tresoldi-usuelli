package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JDeck extends JPanel implements ActionListener {
    private List<JGod> gods;
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

    public JDeck() {
        this.gods = new ArrayList<>();
        setLayout(new GridBagLayout());
        setOpaque(false);
    }

    public int getNum() {
        return this.gods.size();
    }

    public void addGod(JGod god) {
        this.gods.add(god);
        this.setCurrent(god);
    }

    public void setCurrent(JGod chosen) {
        if (gods.contains(chosen)) {
            if (current < this.getNum())
                this.gods.get(current).getMini().disactive();
            this.current = this.gods.indexOf(chosen);
            this.gods.get(current).getMini().active();
        }
    }

    public JGod getCurrent() {
        return this.gods.get(current);
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
        validate();
        repaint();
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

    public List<JMini> getMiniList() {
        List<JMini> list = new ArrayList<>();

        for (JGod god : gods) {
            list.add(god.getMini());
        }

        return list;
    }

    public JGod pop(JGod god) {
        JGod toPop = this.gods.remove(this.gods.indexOf(god));
        remove(toPop.getMini());
        toPop.getMini().disactive();
        if (this.gods.size() > 0)
            setCurrent(this.gods.get(0));
        return toPop;
    }

    public List<God> getGodList() {
        return gods.stream().map(JGod::getGod).collect(Collectors.toList());
    }

    public void setGodList(List<God> gods) {
        int i = 0;
        for (God god : gods) {
            this.gods.add(new JGod(god));
            this.gods.get(i).getMini().addActionListener(this);
            i++;
        }
    }

    public void removeGod(God god) {
        if (gods.get(current).getGod().equals(god))
            current = (current + 1) % gods.size();

        gods.removeIf(jg -> jg.getGod().equals(god));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setCurrent(getJGod(((JMini) e.getSource()).getGod()));
    }
}
