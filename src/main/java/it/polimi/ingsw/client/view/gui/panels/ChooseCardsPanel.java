package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.client.view.gui.component.deck.JDeck;
import it.polimi.ingsw.client.view.gui.component.deck.JGod;
import it.polimi.ingsw.client.view.gui.component.deck.JMini;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChooseCardsPanel extends SantoriniPanel implements ActionListener {
    private static final String imgPath = "menu.png";
    private static final int BUTTON_SIZE = 175;
    int numPlayer = 3;
    private JButton activeButton;
    private JButton removeButton;
    private JLayeredPane godsList;
    private JLabel choice;
    private JLayeredPane chosenList;
    private JLabel godsBack;
    private JLabel cloudBack;
    private JDeck deck;
    private JDeck chosenDeck;
    private JLayeredPane layers;

    public ChooseCardsPanel(CardLayout panelIndex, JPanel panels, JDeck deck) {
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

        this.deck = deck;
        chosenDeck = new JDeck();
        loadGods();
        for (JGod god : deck.getList())
            god.getMini().addActionListener(this);
        setChoice(deck, deck.getGod(0));

        createActiveButton();
        createRemoveButton();
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

        chosenList = new JLayeredPane();
        chosenList.setLayout(new GridBagLayout());
        chosenList.setVisible(true);
        chosenList.setOpaque(false);
        chosenList.setPreferredSize(new Dimension(BackgroundPanel.WIDTH, 130));

        layers.add(chosenList, c, 0);

        ImageIcon icon = new ImageIcon("img/labels/clouds.png");
        Image img = icon.getImage().getScaledInstance( BackgroundPanel.WIDTH, 130, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        cloudBack = new JLabel(icon);
        cloudBack.setOpaque(false);
        cloudBack.setLayout(new GridBagLayout());

        chosenList.add(cloudBack);
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
        chosenDeck.setCurrent(chosenDeck.getGod(chosenDeck.getNum() - 1));
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

    void setChoice(JDeck deck, JGod god) {
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

    private void createActiveButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.gridheight = 2;
        c.anchor = GridBagConstraints.NORTHEAST;
        c.weightx = 1;
        c.weighty = 1;

        ImageIcon icon = new ImageIcon("img/buttons/choose_button.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        activeButton = new JButton(icon);
        activeButton.setOpaque(false);
        activeButton.setContentAreaFilled(false);
        activeButton.setBorderPainted(false);
        activeButton.addActionListener(this);
        activeButton.setName("choose");

        cloudBack.add(activeButton, c);
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
        removeButton.addActionListener(this);
        removeButton.setName("remove");

        cloudBack.add(removeButton, c);
    }

    void enableChoose(boolean enable) {
        activeButton.setEnabled(enable);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(((JButton)e.getSource()).getName()) {
            case "send":
                //changing panel
                ManagerPanel mg = (ManagerPanel) panels;
                mg.getGame().setDeck(chosenDeck);
                mg.addPanel(new ChooseGodPanel(panelIndex, panels, mg.getGame().getDeck()));
                ((ChooseGodPanel) mg.getCurrentPanel()).enableChoose(false);
                this.panelIndex.next(this.panels);
                mg.getGui().generateDemand(DemandType.CHOOSE_DECK, chosenDeck.getGodList());
                break;

            case "choose":
                loadChosen(deck.pop(deck.getCurrent()));
                setChoice(deck, deck.getCurrent());
                deck.showMiniList();
                if (numPlayer == chosenDeck.getNum()) {
                    ImageIcon icon = new ImageIcon("img/buttons/send_button.png");
                    Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(img);
                    activeButton.setIcon(icon);
                    activeButton.setName("send");
                }
                // TODO: get god's description from model
                break;

            case "remove":
                deck.addGod(chosenDeck.pop(chosenDeck.getCurrent()));
                if (chosenDeck.getNum() > 0)
                    chosenDeck.setCurrent(chosenDeck.getGod(0));
                deck.showMiniList();
                chosenDeck.showMiniList();
                if (activeButton.getName().equalsIgnoreCase("send")) {
                    ImageIcon icon = new ImageIcon("img/buttons/choose_button.png");
                    Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(img);
                    activeButton.setIcon(icon);
                    activeButton.setName("choose");
                }
                repaint();
                validate();
                break;

            case "mini":
                JMini obj = (JMini)e.getSource();
                if (deck.getMiniList().contains(obj)) {
                    setChoice(deck, deck.getJGod(obj.getGod()));
                }
                else if (chosenDeck.getMiniList().contains(obj)) {
                    setChoice(chosenDeck, chosenDeck.getJGod(obj.getGod()));
                }
                break;
            default:
                break;
        }
    }
}