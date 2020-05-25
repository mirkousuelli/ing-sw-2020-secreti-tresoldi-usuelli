package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.client.view.gui.component.deck.JDeck;
import it.polimi.ingsw.client.view.gui.component.deck.JGod;
import it.polimi.ingsw.client.view.gui.component.deck.JMini;
import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class ChooseGodPanel extends SantoriniPanel implements ActionListener {
    private static final String imgPath = "menu.png";
    private static final int BUTTON_SIZE = 175;
    private JLayeredPane layers;
    private JLayeredPane godsList;
    private JLabel godsBack;
    private JPanel choice;
    private JButton chooseButton;
    private JDeck deck;
    private God chosenGod;


    public ChooseGodPanel(CardLayout panelIndex, JPanel panels, JDeck deck) {
        super(imgPath, panelIndex, panels);

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.SOUTH;
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

        this.deck = deck;

        createGodsList();
        createChoice();
        createChooseButton();

        loadGods();
        for (JGod god : deck.getList())
            god.getMini().addActionListener(this);
        setChoice(deck, deck.getGod(0));
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

        godsList = new JLayeredPane();
        godsList.setLayout(new OverlayLayout(godsList));
        godsList.setVisible(true);
        godsList.setOpaque(false);
        godsList.setPreferredSize(new Dimension(BackgroundPanel.WIDTH, 180));

        layers.add(godsList, c, 1);

        ImageIcon icon = new ImageIcon("img/labels/gods_menu.png");
        Image img = icon.getImage().getScaledInstance( BackgroundPanel.WIDTH, 180, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        godsBack = new JLabel(icon);
        godsBack.setOpaque(false);
        godsBack.setLayout(new GridBagLayout());

        godsList.add(godsBack);
    }

    private void createChooseButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.SOUTH;
        c.weightx = 1;
        c.weighty = 0f;
        c.ipady = -120;

        ImageIcon icon = new ImageIcon("img/buttons/choose_button.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        chooseButton = new JButton(icon);
        chooseButton.setOpaque(false);
        chooseButton.setContentAreaFilled(false);
        chooseButton.setBorderPainted(false);
        chooseButton.addActionListener(this);
        chooseButton.setName("choose");
        chooseButton.setEnabled(false);

        layers.add(chooseButton, c);
    }

    void createChoice() {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.8;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(30,0,0,0);

        choice = new JPanel(new FlowLayout());
        choice.setVisible(true);
        choice.setOpaque(false);
        choice.setSize(BackgroundPanel.WIDTH, BackgroundPanel.HEIGHT);

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
        chosenGod = god.getGod();
        validate();
        repaint();
    }

    void enableChoose(boolean enable) {
        chooseButton.setEnabled(enable);
    }

    void updateDeck(God godToRemove) {
        deck.pop(deck.getJGod(godToRemove));
        setChoice(deck, deck.getCurrent());
        deck.showMiniList();
    }

    void updateDeck() {
        updateDeck(chosenGod);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(((JButton)e.getSource()).getName()) {
            case "mini":
                JMini obj = (JMini)e.getSource();
                if (deck.getMiniList().contains(obj)) {
                    setChoice(deck, deck.getJGod(obj.getGod()));
                }
                break;

            case "choose":
                ManagerPanel mg = (ManagerPanel) panels;
                GUI gui = mg.getGui();

                mg.getGame().getCurrentPlayer().setGod(deck.getJGod(chosenGod));
                updateDeck();
                chooseButton.setEnabled(false);

                if (gui.getClientModel().isCreator()) {
                    mg.addPanel(new ChooseStarterPanel(panelIndex, panels, mg.getGame()));
                    this.panelIndex.next(this.panels);
                }

                mg.getGui().generateDemand(DemandType.CHOOSE_CARD, chosenGod);
                break;
        }
    }

    @Override
    public void updateFromModel() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        List<ReducedCard> reducedCardList = gui.getClientModel().getDeck();
        List<God> gods = reducedCardList.stream().map(ReducedCard::getGod).collect(Collectors.toList());

        mg.getGame().setCurrentPlayer(gui.getClientModel().getCurrentPlayer().getNickname());

        if (gods.size() < gui.getClientModel().getNumberOfPlayers() && !gods.isEmpty()) {
            God godToRemove = deck.getList().stream()
                    .filter(jGod -> !gods.contains(jGod.getGod()))
                    .reduce(null, (a, b) -> a != null ? a : b)
                    .getGod();

            updateDeck(godToRemove);
        }

        if (gui.getClientModel().getCurrentState().equals(DemandType.PLACE_WORKERS)) {
            mg.addPanel(new WaitingRoomPanel(panelIndex, panels));
            this.panelIndex.next(this.panels);
        }

        if (!gui.getClientModel().isCreator()) return;
        gui.free();
    }
}