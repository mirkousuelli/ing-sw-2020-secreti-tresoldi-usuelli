package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents the deck in the GUI.
 * <p>
 * It contains a list of Gods (chosen by the Challenger) and an index of the current one.
 */
public class JDeck extends JPanel implements ActionListener {

    private final List<JGod> gods;
    private int current;

    /**
     * Constructor of the Deck, given a list of Gods
     *
     * @param list list of cards that will be in this deck
     */
    public JDeck(List<God> list) {
        this();
        setGodList(list);
    }

    /**
     * Constructor of the Deck
     */
    public JDeck() {
        gods = new ArrayList<>();
        setLayout(new GridBagLayout());
        setOpaque(false);
    }

    public int getNum() {
        return gods.size();
    }

    public void addGod(JGod god) {
        gods.add(god);
        setCurrent(god);
    }

    public void addGod(God god) {
        addGod(new JGod(god));
    }

    public void setCurrent(JGod chosen) {
        if (gods.contains(chosen)) {
            if (current < getNum())
                gods.get(current).getMini().disactive();
            current = gods.indexOf(chosen);
            gods.get(current).getMini().active();
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

    /**
     * Method that displays the list of Gods in the deck (in their mini version). It is used after the Challenger
     * picked the cards and each player has to choose one for himself.
     */
    public void showMiniList() {
        int i = 0;
        for (JGod god : gods) {
            GridBagConstraints c = new GridBagConstraints();

            c.gridx = i;
            c.gridy = 0;
            c.weightx = 1;
            c.weighty = 0f;
            c.fill = GridBagConstraints.BOTH;

            add(god.getMini(), c);
            i++;
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

    /**
     * Method that pops the chosen God from the deck and removes it
     *
     * @param god the God to be popped
     * @return the chosen God
     */
    public JGod pop(JGod god) {
        int index = gods.indexOf(god);

        if (index == -1) return god;

        JGod toPop = gods.remove(index);
        remove(toPop.getMini());
        toPop.getMini().disactive();
        if (gods.size() > 0)
            setCurrent(gods.get(0));
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

    @Override
    public void actionPerformed(ActionEvent e) {
        setCurrent(getJGod(((JMini) e.getSource()).getGod()));
    }

    /**
     * Method that cleans the list of Gods in the deck
     */
    public void clean() {
        gods.clear();
        current = 0;

        validate();
        repaint();
    }
}
