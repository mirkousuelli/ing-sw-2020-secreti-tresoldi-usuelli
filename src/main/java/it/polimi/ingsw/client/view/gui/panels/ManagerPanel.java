package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.deck.JDeck;
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

        deck = new JDeck(Arrays.asList(God.values()));

        santoriniPanelList.add(new StartPanel(cardLayout, this));
        santoriniPanelList.add(new NicknamePanel(cardLayout, this));
        santoriniPanelList.add(new NumPlayerPanel(cardLayout, this));
        santoriniPanelList.add(new WaitingRoomPanel(cardLayout, this));
        santoriniPanelList.add(new ChooseCardsPanel(cardLayout, this, deck));
        santoriniPanelList.add(new ChooseGodPanel(cardLayout, this, deck));
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
