package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.client.view.gui.component.map.JBlockDecorator;
import it.polimi.ingsw.client.view.gui.component.map.JCell;
import it.polimi.ingsw.client.view.gui.component.map.JCellStatus;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.map.Level;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents the panel that handles all panels from their index.
 * <p>
 * It contains the current panel, the game that is being played and the player
 */
public class ManagerPanel extends JPanel {

    private final CardLayout cardLayout;
    private final GUI gui;

    private SantoriniPanel currentPanel;
    private JGame game;
    private JPlayer clientPlayer;

    /**
     * Constructor of the ManagerPanel, which creates the starting panel and then adds the one for nickname.
     *
     * @param gui the interface that is used
     */
    public ManagerPanel(GUI gui) {
        this.gui = gui;
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        game = new JGame();

        StartPanel startPanel = new StartPanel(cardLayout, this);
        addPanel(startPanel);
        addPanel(new NicknamePanel(cardLayout, this));
        cardLayout.show(this, "Card 1");
    }

    public GUI getGui() {
        return gui;
    }

    JGame getGame() {
        return game;
    }

    JPlayer getClientPlayer() {
        return clientPlayer;
    }

    void setClientPlayer(JPlayer clientPlayer) {
        this.clientPlayer = clientPlayer;
    }

    /**
     * Method that adds the given panel
     *
     * @param comp the component that is added
     * @return the component with the added panel
     */
    public Component addPanel(Component comp) {
        currentPanel = (SantoriniPanel) comp;
        return this.add(comp);
    }

    public SantoriniPanel getCurrentPanel() {
        return currentPanel;
    }

    /**
     * Method that allows a game to be reloaded.
     * <p>
     * It sets the players, the cards, the board and the workers, then proceeding to play the game
     */
    public void reload() {
        game = new JGame();

        WaitingRoomPanel.setUpJPlayers(this);
        game.setCurrentPlayer(gui.getClientModel().getCurrentPlayer().getNickname());

        ChooseGodPanel.setGods(this);

        updateBuild(Arrays.stream(gui.getClientModel().getReducedBoard())
                .flatMap(Arrays::stream)
                .filter(rac -> !rac.getLevel().equals(Level.GROUND))
                .collect(Collectors.toList())
        );

        game.getPlayerList().forEach(
                jPlayer -> updateWorkers(jPlayer,
                        Arrays.stream(gui.getClientModel().getReducedBoard())
                                .flatMap(Arrays::stream)
                                .filter(rac -> !rac.isFree())
                                .filter(rac -> rac.getWorker().getOwner().equals(jPlayer.getNickname()))
                                .collect(Collectors.toList()))
        );

        //cardLayout
        if (gui.getClientModel().getCurrentState().equals(DemandType.ASK_ADDITIONAL_POWER) && gui.getClientModel().isYourTurn()) {
            if (gui.getClientModel().getPlayer().getCard().getEffect().equals(Effect.MOVE))
                gui.getClientModel().setPrevState(DemandType.MOVE);
            else if (gui.getClientModel().getPlayer().getCard().getEffect().equals(Effect.BUILD))
                gui.getClientModel().setPrevState(DemandType.BUILD);
        }
        addPanel(new GamePanel(cardLayout, this));

        cardLayout.next(this);
        gui.free();
    }

    private void updateBuild(List<ReducedAnswerCell> reducedAnswerCellList) {
        reducedAnswerCellList.forEach(reducedAnswerCell -> {
            JCell cell = game.getJMap().getCell(reducedAnswerCell.getX(), reducedAnswerCell.getY());
            cell.setStatus(JCellStatus.NONE);

            if (reducedAnswerCell.getLevel().equals(Level.DOME)) {
                while (cell.getStatus().ordinal() < reducedAnswerCell.getPrevLevel().toInt() && !cell.getStatus().equals(JCellStatus.TOP))
                    ((JBlockDecorator) cell).buildUp();

                ((JBlockDecorator) cell).addDecoration(JCellStatus.DOME);
            } else {
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
            } else {
                jPlayer.setUpFemaleWorker(jCellWorker);
                jPlayer.getFemaleWorker().setId(1);
            }

            if (jPlayer.getNickname().equals(gui.getClientModel().getCurrentPlayer().getNickname()) && updatedCell.getWorker().isCurrent()) {
                game.getCurrentPlayer().setCurrentWorker(currentWorker);
                game.getJMap().setCurrentWorker(game.getCurrentPlayer().getCurrentWorker());
            }
        });

    }

    /**
     * Method that controls if there was a disconnection and in that case sets the background so saved
     *
     * @return {@code true} if a player disconnected from the game, {@code false} otherwise
     */
    boolean evalDisconnection() {
        boolean isClosed = gui.getAnswer().getHeader().equals(AnswerType.CLOSE);

        if (isClosed) {
            add(new EndPanel("saved", cardLayout, this));
            cardLayout.next(this);
        }

        return isClosed;
    }

    /**
     * Method that cleans the panels, setting the JPlayer to null, cleaning the game and removing all components from
     * the container. In the end it validates and repaints this component.
     */
    void clean() {
        clientPlayer = null;
        game.clean();

        removeAll();
        revalidate();
        validate();
        repaint();
    }
}
