package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.client.view.gui.component.JWorker;
import it.polimi.ingsw.client.view.gui.component.deck.JCard;
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
    private JGame game;
    private JPlayer clientPlayer;

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

        game = new JGame();

        /* ------ */
        game.getJDeck().setGodList(Arrays.asList(God.values()));

        game.addPlayer("Mirko", 0);
        game.addPlayer("Alessia", 1);
        game.addPlayer("Haze", 2);
        clientPlayer = game.getPlayer(1);

        game.getPlayer(0).setJCard(new JCard(God.CHRONUS));
        game.getPlayer(1).setJCard(new JCard(God.TRITON));
        game.getPlayer(2).setJCard(new JCard(God.ZEUS));


        santoriniPanelList.add(new GamePanel(cardLayout, this, game, clientPlayer));
        game.setCurrentPlayer(clientPlayer);

        GamePanel panel = (GamePanel) getCurrentPanel();

        //game.getJMap().workersPositioning(); ---> POSITIONING

        List<JCell> around = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                around.add(game.getJMap().getCell(i,j));
        panel.setPossibleBuild(around);
        around.clear();
        around.add(game.getJMap().getCell(3,1));
        around.add(game.getJMap().getCell(1,3));
        around.add(game.getJMap().getCell(3,3));
        around.add(game.getJMap().getCell(1,1));
        panel.setPossibleUsePowerBuild(around);
        /* ------ */

        santoriniPanelList.add(new StartPanel(cardLayout, this));
        santoriniPanelList.add(new NicknamePanel(cardLayout, this));
        santoriniPanelList.add(new NumPlayerPanel(cardLayout, this));
        santoriniPanelList.add(new WaitingRoomPanel(cardLayout, this));
        santoriniPanelList.add(new ChooseCardsPanel(cardLayout, this, game.getJDeck()));
        santoriniPanelList.add(new ChooseGodPanel(cardLayout, this, game.getJDeck()));
        //santoriniPanelList.add(new GamePanel(cardLayout, this));
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
