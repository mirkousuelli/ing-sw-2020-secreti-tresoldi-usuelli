package it.polimi.ingsw.client.view.gui.panels;
import it.polimi.ingsw.client.view.gui.button.deck.JCard;
import it.polimi.ingsw.client.view.gui.button.deck.JDeck;
import it.polimi.ingsw.client.view.gui.button.deck.JGod;
import it.polimi.ingsw.client.view.gui.button.deck.JMini;
import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ChooseCardsPanel extends SantoriniPanel implements ActionListener {
    private static final String imgPath = "menu.png";
    private static final int BUTTON_SIZE = 175;
    private JButton sendButton;
    private JButton removeButton;
    private JButton chooseButton;
    private JLayeredPane godsList;
    private JLabel choice;
    private JLayeredPane choosenList;
    private JLabel godsBack;
    private JLabel cloudBack;
    private JDeck deck;
    private JDeck chosenDeck;
    private JLayeredPane layers;

    public ChooseCardsPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;

        layers = new JLayeredPane();
        layers.setPreferredSize(new Dimension(BackgroundPanel.WIDTH, BackgroundPanel.HEIGHT));
        layers.setOpaque(false);
        layers.setVisible(true);
        layers.setLayout(new GridBagLayout());
        add(layers, c);

        createChosenList();
        createGodsList();
        createChoice();

        deck = new JDeck(Arrays.asList(God.values()));
        chosenDeck = new JDeck();
        loadGods();
        for (JGod god : deck.getList())
            god.getMini().addActionListener(this);
        setChoice(deck.getJGod(God.APOLLO));

        createSendButton();
        createRemoveButton();
        createChooseButton();
    }

    void createChosenList() {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0f;
        c.fill = GridBagConstraints.BOTH;

        choosenList = new JLayeredPane();
        choosenList.setLayout(new GridBagLayout());
        choosenList.setVisible(true);
        choosenList.setOpaque(false);
        choosenList.setPreferredSize(new Dimension(BackgroundPanel.WIDTH, 130));

        layers.add(choosenList, c, 0);

        ImageIcon icon = new ImageIcon("img/labels/clouds.png");
        Image img = icon.getImage().getScaledInstance( BackgroundPanel.WIDTH, 130, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        cloudBack = new JLabel(icon);
        cloudBack.setOpaque(false);
        cloudBack.setLayout(new GridBagLayout());

        choosenList.add(cloudBack);
    }

    void loadChosen(JGod god) {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0f;
        c.fill = GridBagConstraints.BOTH;

        chosenDeck.addGod(god);
        cloudBack.add(chosenDeck, c);
        chosenDeck.showMiniList();
    }

    void createChoice() {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.3;
        c.fill = GridBagConstraints.BOTH;

        choice = new JLabel();
        choice.setLayout(new GridBagLayout());
        choice.setVisible(true);
        choice.setOpaque(false);

        layers.add(choice, c, 1);
    }

    void setChoice(JGod god) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;

        choice.removeAll();
        choice.add(god.getCard(), c);
        deck.setCurrent(god);
        validate();
        repaint();
    }

    void createGodsList() {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.SOUTH;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0f;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0,0,-20,0);

        godsList = new JLayeredPane();
        godsList.setLayout(new OverlayLayout(godsList));
        godsList.setVisible(true);
        godsList.setOpaque(false);
        godsList.setPreferredSize(new Dimension(BackgroundPanel.WIDTH, 180));

        layers.add(godsList, c, 0);

        ImageIcon icon = new ImageIcon("img/labels/gods_menu.png");
        Image img = icon.getImage().getScaledInstance( BackgroundPanel.WIDTH, 155, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        godsBack = new JLabel(icon);
        godsBack.setOpaque(false);
        godsBack.setLayout(new GridBagLayout());

        godsList.add(godsBack);
    }

    void loadGods() {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.SOUTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0f;
        c.weighty = 0f;
        c.fill = GridBagConstraints.BOTH;

        godsBack.add(deck, c);
        deck.showMiniList();
    }

    private void createSendButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.weightx = 1;
        c.weighty = 1;

        ImageIcon icon = new ImageIcon("img/buttons/send_button.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        sendButton = new JButton(icon);
        sendButton.setOpaque(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);
        sendButton.addActionListener(this);
        sendButton.setName("send");

        cloudBack.add(sendButton, c);
    }

    private void createRemoveButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1;
        c.weighty = 1;

        ImageIcon icon = new ImageIcon("img/buttons/remove_button.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        removeButton = new JButton(icon);
        removeButton.setOpaque(false);
        removeButton.setContentAreaFilled(false);
        removeButton.setBorderPainted(false);

        cloudBack.add(removeButton, c);
    }

    private void createChooseButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 3;
        c.anchor = GridBagConstraints.SOUTH;
        c.weightx = 1;
        c.weighty = 1;

        ImageIcon icon = new ImageIcon("img/buttons/choose_button.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        chooseButton = new JButton(icon);
        chooseButton.setOpaque(false);
        chooseButton.setContentAreaFilled(false);
        chooseButton.setBorderPainted(false);
        chooseButton.addActionListener(this);
        chooseButton.setName("choose");

        cloudBack.add(chooseButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(((JButton)e.getSource()).getName()) {
            case "send":
                this.panelIndex.next(this.panels);
                break;
            case "choose":
                //System.out.println(deck.pop(deck.getCurrent()).getGod().toString());
                loadChosen(deck.pop(deck.getCurrent()));
                setChoice(deck.getCurrent());
                deck.showMiniList();
                // TODO: get god's description from model
                // gui.getModel()
                break;
            case "mini":
                setChoice(deck.getJGod(((JMini)e.getSource()).getGod()));
                break;
            default:
                break;
        }
    }
}