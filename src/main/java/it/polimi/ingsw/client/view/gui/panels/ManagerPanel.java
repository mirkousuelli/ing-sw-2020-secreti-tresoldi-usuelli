package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.button.deck.JDeck;
import it.polimi.ingsw.client.view.gui.button.map.JCell;
import it.polimi.ingsw.client.view.gui.button.map.JCellStatus;
import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManagerPanel extends JPanel {
    private final CardLayout cardLayout;
    private final List<SantoriniPanel> santoriniPanelList;
    private int currentPanelIndex;
    private final GUI gui;
    private JDeck deck;

    enum SantoriniPanelEnum {
        START,
        NICKNAME,
        NUM_PLAYERS,
        WAITING,
        CHOOSE_CARDS,
        CHOOSE_GOD,
        GAME,
        END_DEFEAT,
        END_VICTORY,
        END_SAVE;

        static SantoriniPanelEnum parseString(String string) {
            switch (string) {
                case "start":
                    return START;
                case "nickName":
                    return NICKNAME;
                case "numOfPlayers":
                    return NUM_PLAYERS;
                case "waiting":
                    return WAITING;
                case "chooseCards":
                    return CHOOSE_CARDS;
                case "chooseGod":
                    return CHOOSE_GOD;
                case "game":
                    return GAME;
                case "endDefeat":
                    return END_DEFEAT;
                case "endVictory":
                    return END_VICTORY;
                case "endSave":
                    return END_SAVE;
                default:
                    return null;
            }
        }
    }

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
        around.add(curr.getCell(2,1));
        around.add(curr.getCell(1,2));
        around.add(curr.getCell(1,1));
        around.add(curr.getCell(3,3));
        around.add(curr.getCell(3,2));
        around.add(curr.getCell(2,3));
        around.add(curr.getCell(3,1));
        around.add(curr.getCell(1,3));

        curr.possibleBuild(around);
        curr.setPlayer(curr.getCell(2,2), JCellStatus.PLAYER_3_FEMALE);
        /* --------------------------------------------------------------------------------------------*/
        santoriniPanelList.add(new StartPanel(cardLayout, this));
        santoriniPanelList.add(new NicknamePanel(cardLayout, this));
        santoriniPanelList.add(new NumPlayerPanel(cardLayout, this));
        santoriniPanelList.add(new WaitingRoomPanel(cardLayout, this));
        santoriniPanelList.add(new ChooseCardsPanel(cardLayout, this, deck));
        santoriniPanelList.add(new ChooseGodPanel(cardLayout, this));
        santoriniPanelList.add(new GamePanel(cardLayout, this));
        santoriniPanelList.add(new EndPanel(EndPanel.DEFEAT, cardLayout, this));
        santoriniPanelList.add(new EndPanel(EndPanel.VICTORY, cardLayout, this));
        santoriniPanelList.add(new EndPanel(EndPanel.SAVE, cardLayout, this));

        currentPanelIndex = 0;

        add(santoriniPanelList.get(0));
        add(santoriniPanelList.get(1));
        cardLayout.show(this, "Card 1");
    }

    public GUI getGui() {
        return gui;
    }

    public SantoriniPanel getCurrentPanel() {
        return santoriniPanelList.get(currentPanelIndex);
    }

    public int getCurrentPanelIndex() {
        return currentPanelIndex;
    }

    public void setCurrentPanelIndex(String currentPanelString) {
        SantoriniPanelEnum santoriniPanelEnum = SantoriniPanelEnum.parseString(currentPanelString);
        if (santoriniPanelEnum == null) return;

        this.currentPanelIndex = santoriniPanelEnum.ordinal();
    }

    public List<SantoriniPanel> getSantoriniPanelList() {
        return santoriniPanelList;
    }
}
