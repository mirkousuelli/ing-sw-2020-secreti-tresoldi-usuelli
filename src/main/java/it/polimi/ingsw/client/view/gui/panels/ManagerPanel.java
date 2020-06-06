package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.client.view.gui.component.JWorker;
import it.polimi.ingsw.client.view.gui.component.map.JBlockDecorator;
import it.polimi.ingsw.client.view.gui.component.map.JCell;
import it.polimi.ingsw.client.view.gui.component.map.JCellStatus;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedLevel;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerPanel extends JPanel {
    private final CardLayout cardLayout;
    private SantoriniPanel currentPanel;
    private final GUI gui;
    private JGame game;
    private JPlayer clientPlayer;

    public ManagerPanel(GUI gui) {
        this.gui = gui;
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        game = new JGame();

        addPanel(new StartPanel(cardLayout, this));
        addPanel(new NicknamePanel(cardLayout, this));
        cardLayout.show(this, "Card 1");
    }

    public GUI getGui() {
        return gui;
    }

    public JGame getGame() {
        return game;
    }

    public JPlayer getClientPlayer() {
        return clientPlayer;
    }

    public void setClientPlayer(JPlayer clientPlayer) {
        this.clientPlayer = clientPlayer;
    }

    public Component addPanel(Component comp) {
        currentPanel = (SantoriniPanel) comp;
        return this.add(comp);
    }

    public SantoriniPanel getCurrentPanel() {
        return currentPanel;
    }

    public void reload() {
        game = new JGame();

        //players
        WaitingRoomPanel.setUpJPlayers(this);
        game.setCurrentPlayer(gui.getClientModel().getCurrentPlayer().getNickname());

        //cards
        ChooseGodPanel.setGods(this);

        //board
        updateBuild(Arrays.stream(gui.getClientModel().getReducedBoard())
                .flatMap(Arrays::stream)
                .filter(ReducedAnswerCell::isFree)
                .filter(rac -> rac.getLevel().toInt() > 0)
                .collect(Collectors.toList())
        );

        //workers
        game.getPlayerList().forEach(
                jPlayer -> updateWorkers(jPlayer,
                        Arrays.stream(gui.getClientModel().getReducedBoard())
                                .flatMap(Arrays::stream)
                                .filter(rac -> !rac.isFree())
                                .filter(rac -> rac.getWorker().getOwner().equals(jPlayer.getNickname()))
                                .collect(Collectors.toList()))
        );

        //cardLayout
        addPanel(new GamePanel(cardLayout, this));

        //start
        this.cardLayout.next(this);
        gui.free();
    }

    private void updateBuild(List<ReducedAnswerCell> reducedAnswerCellList) {
        reducedAnswerCellList.forEach(reducedAnswerCell -> {
            JCell cell = game.getJMap().getCell(reducedAnswerCell.getX(), reducedAnswerCell.getY());
            cell.setStatus(JCellStatus.NONE);

            if (reducedAnswerCell.getLevel().equals(ReducedLevel.DOME)) {
                while (cell.getStatus().ordinal() < reducedAnswerCell.getPrevLevel().toInt() && !cell.getStatus().equals(JCellStatus.TOP))
                    ((JBlockDecorator) cell).buildUp();

                ((JBlockDecorator) cell).addDecoration(JCellStatus.DOME);
            }
            else {
                while (cell.getStatus().ordinal() < reducedAnswerCell.getLevel().toInt() && !cell.getStatus().equals(JCellStatus.TOP))
                    ((JBlockDecorator) cell).buildUp();
            }
        });
    }

    private void updateWorkers(JPlayer jPlayer, List<ReducedAnswerCell> reducedAnswerCellList) {
        reducedAnswerCellList.forEach(updatedCell -> {
            JCell jCellWorker = game.getJMap().getCell(updatedCell.getX(), updatedCell.getY());
            boolean currentWorker = false;

            if (updatedCell.getWorker().isGender()) {
                jPlayer.setUpMaleWorker(jCellWorker);
                jPlayer.getMaleWorker().setId(2);
                currentWorker = true;
            }
            else {
                jPlayer.setUpFemaleWorker(jCellWorker);
                jPlayer.getFemaleWorker().setId(1);
            }

            if (jPlayer.getNickname().equals(gui.getClientModel().getCurrentPlayer().getNickname()) && updatedCell.getWorker().isCurrent()) {
                game.getCurrentPlayer().setCurrentWorker(currentWorker);
                game.getJMap().setCurrentWorker(game.getCurrentPlayer().getCurrentWorker());
            }
        });

    }
}
