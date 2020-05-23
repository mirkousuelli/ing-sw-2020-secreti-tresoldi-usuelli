package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.client.view.gui.component.JWorker;
import it.polimi.ingsw.client.view.gui.component.deck.JDeck;
import it.polimi.ingsw.client.view.gui.component.map.JCell;
import it.polimi.ingsw.client.view.gui.component.map.JCellStatus;
import it.polimi.ingsw.client.view.gui.component.map.JDecorator;
import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ManagerPanel extends JPanel {
    private final CardLayout cardLayout;
    private final List<SantoriniPanel> santoriniPanelList;
    private int currentPanelIndex;
    private final GUI gui;
    private JDeck deck; // da utilizzare quello in JGame
    private JGame game;

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

        /* ------------------------ZONA TESTING---------------------------- */
        santoriniPanelList.add(new GamePanel(cardLayout, this));
        GamePanel gamePanel = (GamePanel)getCurrentPanel();

        JPlayer p = new JPlayer("mirko");
        JWorker w1 = new JWorker(JCellStatus.PLAYER_2_FEMALE, gamePanel.getJMap().getCell(2,2));
        JWorker w2 = new JWorker(JCellStatus.PLAYER_2_MALE, gamePanel.getJMap().getCell(0,0));
        p.setWorkers(w1, w2);

        // PER TESTARE CHOOSE WORKER
        /*p.chooseWorker();
        gamePanel.getJMap().setCurrentWorker(p.getCurrentWorker());*/

        // PER TESTARE MOVE/BUILD/USEPOWER
        gamePanel.getJMap().setCurrentWorker(p.getFemaleWorker());

        List<JCell> around = new ArrayList<>();
        around.add(gamePanel.getJMap().getCell(2,1));
        around.add(gamePanel.getJMap().getCell(1,2));
        around.add(gamePanel.getJMap().getCell(1,1));
        around.add(gamePanel.getJMap().getCell(3,3));
        gamePanel.setPossibleMove(around);
        around.clear();

        around.add(gamePanel.getJMap().getCell(3,2));
        around.add(gamePanel.getJMap().getCell(2,3));
        around.add(gamePanel.getJMap().getCell(3,1));
        around.add(gamePanel.getJMap().getCell(2,2));
        gamePanel.setPossibleUsePowerBuild(around);

        gamePanel.setPossibleMalus(Collections.singletonList(gamePanel.getJMap().getCell(1,3)));

        /* ------------------------------------------------------------------- */

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
