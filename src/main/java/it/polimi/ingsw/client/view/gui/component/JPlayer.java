package it.polimi.ingsw.client.view.gui.component;

import it.polimi.ingsw.client.view.gui.component.deck.JCard;
import it.polimi.ingsw.client.view.gui.component.deck.JGod;
import it.polimi.ingsw.client.view.gui.component.map.JBlockDecorator;
import it.polimi.ingsw.client.view.gui.component.map.JCell;
import it.polimi.ingsw.client.view.gui.component.map.JCellStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JPlayer extends JButton implements ActionListener {
    private final String nickname;
    private final int id;
    private JCard card;
    private JWorker femaleWorker;
    private JWorker maleWorker;
    private boolean currentWorker;
    private boolean chooseWorker;
    private final String tagPath;
    private final static String activePath = "img/labels/chosen_player.png";
    private boolean active;
    private JLabel text;

    public static final int SIZE_X = 400;
    public static final int SIZE_Y = 100;
    public static final int FONT_SIZE = 35;

    public static final int CARD_SIZE_X = 150;
    public static final int CARD_SIZE_Y = 35;
    public static final int CARD_FONT_SIZE = 15;

    private int xSize;
    private int ySize;
    private int fontSize;


    public JPlayer(String nickname, int index) {
        this.nickname = nickname;
        this.id = index;
        this.chooseWorker = false;
        this.active = false;
        this.xSize = SIZE_X;
        this.ySize = SIZE_Y;
        this.fontSize = FONT_SIZE;

        setPreferredSize(new Dimension(xSize, ySize));
        this.tagPath = "img/workers/worker_" + (id + 1) + "/tag.png";
        ImageIcon icon = new ImageIcon(tagPath);
        Image img = icon.getImage().getScaledInstance(xSize, ySize, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(img));
        setOpaque(false);
        setLayout(new GridBagLayout());
        setContentAreaFilled(false);
        setBorderPainted(false);
        setName("player");

        text = new JLabel(this.nickname);
        text.setForeground(Color.WHITE);
        text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));
        text.setOpaque(false);
        add(text, new GridBagConstraints());

        this.femaleWorker = null;
        this.maleWorker = null;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setJCard(JCard card) {
        this.card = card;
    }

    public JCard getJCard() {
        return this.card;
    }

    public void setWorkers(JWorker female, JWorker male) {
        this.femaleWorker = female;
        this.maleWorker = male;

        this.femaleWorker.getLocation().addActionListener(this);
        this.maleWorker.getLocation().addActionListener(this);
    }

    public void setUpWorker(JCell position) {
        if (femaleWorker == null)
            femaleWorker = new JWorker(JCellStatus.getWorkerType(this.id, true), position);
        else if (maleWorker == null)
            maleWorker = new JWorker(JCellStatus.getWorkerType(this.id, false), position);
    }

    public JWorker getFemaleWorker() {
        return this.femaleWorker;
    }

    public JWorker getMaleWorker() {
        return this.maleWorker;
    }

    public void chooseWorker() {
        ((JBlockDecorator) this.maleWorker.getLocation()).addDecoration(JCellStatus.CHOOSE_WORKER);
        ((JBlockDecorator) this.femaleWorker.getLocation()).addDecoration(JCellStatus.CHOOSE_WORKER);
        chooseWorker = true;
    }

    public JWorker getCurrentWorker() {
        return (currentWorker) ? maleWorker : femaleWorker;
    }

    public void active() {
        this.active = true;

        JLabel activeLabel = new JLabel();
        ImageIcon icon = new ImageIcon(activePath);
        Image img = icon.getImage().getScaledInstance(xSize, ySize, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        activeLabel.setIcon(icon);
        activeLabel.setLayout(new GridBagLayout());
        activeLabel.add(text, new GridBagConstraints());
        add(activeLabel, new GridBagConstraints());
        validate();
        repaint();
    }

    public void disactive() {
        this.active = false;

        removeAll();
        add(text, new GridBagConstraints());
        validate();
        repaint();
    }

    public void setCardViewSize(boolean view) {
        if (view) {
            this.xSize = CARD_SIZE_X;
            this.ySize = CARD_SIZE_Y;
            this.fontSize = CARD_FONT_SIZE;
        } else {
            this.xSize = SIZE_X;
            this.ySize = SIZE_Y;
            this.fontSize = FONT_SIZE;
        }

        setPreferredSize(new Dimension(xSize, ySize));
        ImageIcon icon = new ImageIcon(tagPath);
        Image img = icon.getImage().getScaledInstance(xSize, ySize, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(img));
        text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, fontSize));

        revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (chooseWorker) {
            currentWorker = ((JBlockDecorator) e.getSource()).getWorker().equals(maleWorker.getPawn().getDecoration());
            ((JBlockDecorator) this.maleWorker.getLocation()).removeDecoration();
            ((JBlockDecorator) this.femaleWorker.getLocation()).removeDecoration();
            chooseWorker = false;
        }
    }
}
