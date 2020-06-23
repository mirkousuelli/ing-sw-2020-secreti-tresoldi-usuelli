package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.deck.JCard;
import it.polimi.ingsw.client.view.gui.component.deck.JDeck;
import it.polimi.ingsw.client.view.gui.component.deck.JGod;
import it.polimi.ingsw.client.view.gui.component.deck.JMini;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ChooseGodPanel extends SantoriniPanel implements ActionListener {
    private static final String imgPath = "menu.png";
    private static final int BUTTON_SIZE = 175;

    private final JPanel layers;
    private JLayeredPane godsList;
    private JLabel godsBack;
    private JPanel choice;
    private JButton chooseButton;
    private final JDeck deck;
    private JCard retro;
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

        layers = new JPanel();
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

    private void loadGods() {
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

    private void createGodsList() {
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
        godsList.setPreferredSize(new Dimension(BackgroundPanel.WIDTH, 130));

        layers.add(godsList, c);

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/labels/gods_menu.png"));
        Image img = icon.getImage().getScaledInstance(BackgroundPanel.WIDTH, 130, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
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

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/choose_button.png"));
        Image img = icon.getImage().getScaledInstance(BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        chooseButton = new JButton(icon);
        chooseButton.setOpaque(false);
        chooseButton.setContentAreaFilled(false);
        chooseButton.setBorderPainted(false);
        chooseButton.addActionListener(this);
        chooseButton.setName("choose");
        chooseButton.setEnabled(false);

        layers.add(chooseButton, c);
    }

    private void createChoice() {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.8;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(30, 0, 0, 0);

        choice = new JPanel();
        choice.setLayout(new GridBagLayout());
        choice.setVisible(true);
        choice.setOpaque(false);
        choice.setSize(BackgroundPanel.WIDTH, BackgroundPanel.HEIGHT);

        retro = new JCard();
        layers.add(choice, c);
    }

    private void setChoice(JDeck deck, JGod god) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0f;
        c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 20);

        retro.removeAll();
        choice.removeAll();
        JLabel text = new JLabel();
        text.setOpaque(false);
        text.setPreferredSize(new Dimension(130, 250));
        String description = "<html>" + god.getDescription() + "</html>";
        text.setText(description);
        text.setForeground(Color.WHITE);
        text.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        text.setHorizontalTextPosition(JLabel.CENTER);
        text.setVerticalTextPosition(JLabel.CENTER);
        retro.add(text, new GridBagConstraints());
        choice.add(retro, c);

        c.gridx = 1;
        c.insets = new Insets(0, 20, 0, 0);
        choice.add(god.getCard(), c);

        deck.setCurrent(god);
        chosenGod = god.getGod();
        validate();
        repaint();
    }

    void enableChoose(boolean enable) {
        chooseButton.setEnabled(enable);
    }

    private void updateDeck(God godToRemove) {
        boolean found = false;
        int i = 0;

        while (!found) {
            if (deck.getMini(i).getGod().equals(godToRemove))
                found = true;
            else
                i++;
        }
        deck.getComponent(i).setVisible(false);


        deck.pop(deck.getJGod(godToRemove));
        if (!deck.getList().isEmpty()) {
            deck.setCurrent(deck.getGod(0));
            setChoice(deck, deck.getCurrent());
            deck.showMiniList();
        }
    }

    private void updateDeck() {
        updateDeck(chosenGod);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (((JButton) e.getSource()).getName()) {
            case "mini":
                JMini obj = (JMini) e.getSource();
                if (deck.getMiniList().contains(obj)) {
                    setChoice(deck, deck.getJGod(obj.getGod()));
                }
                break;

            case "choose":
                ManagerPanel mg = (ManagerPanel) panels;
                GUI gui = mg.getGui();
                God god = chosenGod;

                chooseButton.setEnabled(false);
                mg.getGame().getCurrentPlayer().setJCard(new JCard(god));
                mg.getClientPlayer().setJCard(mg.getGame().getCurrentPlayer().getJCard());
                updateDeck();

                if (gui.getClientModel().isCreator()) {
                    removeAllComponents();

                    ReducedCard reducedCard = new ReducedCard();
                    reducedCard.setGod(god);
                    gui.getClientModel().getCurrentPlayer().setCard(reducedCard);

                    ChooseGodPanel.setGods((ManagerPanel) panels);
                    mg.addPanel(new ChooseStarterPanel(panelIndex, panels, mg.getGame()));
                    ((ChooseStarterPanel) mg.getCurrentPanel()).addPlayers(mg.getGame().getPlayerList());
                    panelIndex.next(panels);
                }

                mg.getGui().generateDemand(DemandType.CHOOSE_CARD, god);
                break;

            default:
                break;
        }
    }

    private void removeAllComponents() {
        retro.removeAll();
        choice.removeAll();
        godsList.removeAll();
        godsBack.removeAll();
        layers.removeAll();

        ManagerPanel mg = (ManagerPanel) panels;
        JDeck newDeck = new JDeck();
        for (JGod god : mg.getGame().getJDeck().getList()) {
            newDeck.addGod(god.getGod());
            newDeck.getJGod(god.getGod()).setDescription(god.getDescription());
        }

        mg.getGame().setJDeck(newDeck);
    }

    @Override
    public void updateFromModel() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (mg.evalDisconnection()) {
            gui.free();
            return;
        }

        if (gui.getAnswer().getHeader().equals(AnswerType.CHANGE_TURN)) {
            mg.getGame().setCurrentPlayer(gui.getClientModel().getCurrentPlayer().getNickname());
            gui.free();
            return;
        }

        chooseButton.setEnabled(gui.getClientModel().isYourTurn());

        if (gui.getAnswer().getContext().equals(UpdatedPartType.CARD)) {
            List<ReducedCard> reducedCardList = (List<ReducedCard>) gui.getAnswer().getPayload();

            if (reducedCardList.size() > 1) { //safety check, cannot happen normally!
                gui.free();
                return;
            }

            if (mg.getGame().getJDeck().getList().size() == gui.getClientModel().getDeck().size()) {
                gui.free();
                return;
            }

            updateDeck(reducedCardList.get(0).getGod()); //remove from JDeck the gods chosen by the previous player
        }

        if (gui.getAnswer().getContext().equals(UpdatedPartType.WORKER)) {
            removeAllComponents();
            ChooseGodPanel.setGods((ManagerPanel) panels);
            mg.addPanel(new GamePanel(panelIndex, panels));
            mg.getCurrentPanel().updateFromModel();
            panelIndex.next(panels);
            return;
        }

        if (!gui.getClientModel().isYourTurn())
            gui.free();
    }

    static void setGods(ManagerPanel mg) {
        GUI gui = mg.getGui();

        List<ReducedPlayer> playerList = gui.getClientModel().getOpponents();
        playerList.add(gui.getClientModel().getPlayer());

        playerList.forEach(p -> {
                    mg.getGame().getPlayer(p.getNickname()).setJCard(new JCard(p.getCard().getGod()));

                    if (p.getNickname().equals(mg.getClientPlayer().getNickname()))
                        mg.getClientPlayer().setJCard(mg.getGame().getPlayer(p.getNickname()).getJCard());
                }
        );
    }
}