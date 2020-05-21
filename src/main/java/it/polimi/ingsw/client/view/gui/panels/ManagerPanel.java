package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.button.deck.JDeck;
import it.polimi.ingsw.client.view.gui.button.map.JBlockDecorator;
import it.polimi.ingsw.client.view.gui.button.map.JCell;
import it.polimi.ingsw.client.view.gui.button.map.JCellStatus;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManagerPanel extends JPanel {
    private CardLayout cardLayout;
    private final List<SantoriniPanel> santoriniPanelList;
    private int currentPanelIndex;
    private final GUI gui;
    private JDeck deck;

    public ManagerPanel(GUI gui) {
        this.gui = gui;
        santoriniPanelList = new ArrayList<>();
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        /* x Fabio
         * ho pensato che sia di gran lunga meglio tenere JDeck qui, perchè lo devono usufruire 3 diversi panel
         * ed è inutile avercene uno interno a ciascuno e passare ogni volta una lista di dei, glielo passiamo
         * noi direttamente dal manager panel
         */

        /* --------------------------------------------------------------------------------------------*/
        deck = new JDeck(Arrays.asList(God.values())); //  TODO : aggiungere i god scelti dal creatore

        santoriniPanelList.add(new GamePanel(cardLayout, this));
        GamePanel curr = (GamePanel)getCurrentPanel();

        List<JCell> around = new ArrayList<>();
        around.add(curr.getJMap().getCell(2,1));
        around.add(curr.getJMap().getCell(1,2));
        around.add(curr.getJMap().getCell(1,1));
        around.add(curr.getJMap().getCell(3,3));
        around.add(curr.getJMap().getCell(3,2));
        around.add(curr.getJMap().getCell(2,3));
        around.add(curr.getJMap().getCell(3,1));
        around.add(curr.getJMap().getCell(1,3));

        curr.getJMap().possibleMove(around);
        curr.getJMap().setPlayer(curr.getJMap().getCell(2,2), JCellStatus.PLAYER_3_FEMALE);
        /* --------------------------------------------------------------------------------------------*/
        santoriniPanelList.add(new StartPanel(cardLayout, this));
        santoriniPanelList.add(new NicknamePanel(cardLayout, this));
        santoriniPanelList.add(new NumPlayerPanel(cardLayout, this));
        santoriniPanelList.add(new WaitingRoomPanel(cardLayout, this));
        //santoriniPanelList.add(new ChooseCardsPanel(cardLayout, this, Arrays.asList(God.values())));
        //santoriniPanelList.add(new ChooseGodPanel(cardLayout, this)); TODO : aggiungere i god scelti dal creatore
        santoriniPanelList.add(new GamePanel(cardLayout, this));
        santoriniPanelList.add(new EndPanel(EndPanel.DEFEAT, cardLayout, this));

        currentPanelIndex = 0;

        add(santoriniPanelList.get(0));
        add(santoriniPanelList.get(1));
        cardLayout.show(this, "Card 1");
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public GUI getGui() {
        return gui;
    }

    public SantoriniPanel getCurrentPanel() {
        return santoriniPanelList.get(currentPanelIndex);
    }

    public void updateCurrentPanel() {
        currentPanelIndex++;
    }

    public int getCurrentPanelIndex() {
        return currentPanelIndex;
    }

    public void setCurrentPanelIndex(int currentPanelIndex) {
        this.currentPanelIndex = currentPanelIndex;
    }

    public List<SantoriniPanel> getSantoriniPanelList() {
        return santoriniPanelList;
    }
}
