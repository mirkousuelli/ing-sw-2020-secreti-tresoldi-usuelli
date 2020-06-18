package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;
import it.polimi.ingsw.server.network.Lobby;

import java.util.ArrayList;
import java.util.List;

/**
 * Class the represents the reduced version of the game, which contains all the main pieces of information
 * <p>
 * It contains the board (composed by reduced version of cells), the list of players (in their reduced version), the
 * list of workers (in their reduced version), current player's index, current worker's index and the current state
 */
public class ReducedGame {
    private ReducedAnswerCell[][] reducedBoard;
    private List<ReducedPlayer> reducedPlayerList;
    private List<ReducedWorker> reducedWorkerList;
    private String currentPlayerIndex;
    private int currentWorkerIndex;
    private DemandType currentState;

    /**
     * Constructor of the reduced game, which is initialised starting from the lobby
     *
     * @param lobby the lobby which the pieces of information are obtained from
     */
    public ReducedGame(Lobby lobby) {
        Game loadedGame = lobby.getGame();
        Board board = loadedGame.getBoard();
        List<Player> playerList = loadedGame.getPlayerList();
        String state = loadedGame.getState().getName();

        currentPlayerIndex = loadedGame.getCurrentPlayer().getNickName();
        currentWorkerIndex = loadedGame.getCurrentPlayer().getWorkers().indexOf(loadedGame.getCurrentPlayer().getCurrentWorker());
        reducedBoard = new ReducedAnswerCell[5][5];
        reducedPlayerList = new ArrayList<>(lobby.getReducedPlayerList());
        reducedWorkerList = new ArrayList<>();
        currentState = DemandType.parseString(lobby.getGame().getState().getName());


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                reducedBoard[i][j] = new ReducedAnswerCell(i, j);
                reducedBoard[i][j].setLevel(ILevel.parseInt(board.map[i][j].getLevel().toInt()));
                reducedBoard[i][j].setPrevLevel(ILevel.parseInt(((Block) board.map[i][j]).getPreviousLevel().toInt()));
                reducedBoard[i][j].setAction(ReducedAction.DEFAULT);
            }
        }

        if (currentWorkerIndex > -1) {
            currentWorkerIndex = loadedGame.getCurrentPlayer().getCurrentWorker().getId();
            Cell loc = loadedGame.getCurrentPlayer().getWorker(currentWorkerIndex);
            List<Cell> around = board.getAround(loc);
            List<Cell> action = null;

            if (state.equals("move"))
                action = board.getPossibleMoves(loadedGame.getPlayer(currentPlayerIndex));
            else if (state.equals("build"))
                action = board.getPossibleBuilds(loadedGame.getPlayer(currentPlayerIndex).getCurrentWorker());

            if (action != null)
                action.stream()
                        .filter(around::contains)
                        .map(cell -> reducedBoard[cell.getX()][cell.getY()])
                        .forEach(reducedCell -> reducedCell.setAction(ReducedAction.parseString(state)));
        }

        for (Player p : playerList) {
            for (Worker w : p.getWorkers()) {
                ReducedWorker work = new ReducedWorker(w, p);
                work.setGender(w.isMale());
                reducedWorkerList.add(work);
                reducedBoard[w.getX()][w.getY()].setWorker(work);
            }

            reducedPlayerList.stream()
                    .filter(player -> player.getNickname().equals(p.nickName))
                    .forEach(player -> player.setCard(new ReducedCard(p.getCard())));
        }
    }

    public ReducedGame() {

    }

    public ReducedAnswerCell[][] getReducedBoard() {
        return reducedBoard;
    }

    public void setReducedBoard(ReducedAnswerCell[][] reducedBoard) {
        this.reducedBoard = reducedBoard;
    }

    public List<ReducedPlayer> getReducedPlayerList() {
        return reducedPlayerList;
    }

    public void setReducedPlayerList(List<ReducedPlayer> reducedPlayerList) {
        this.reducedPlayerList = reducedPlayerList;
    }

    public List<ReducedWorker> getReducedWorkerList() {
        return reducedWorkerList;
    }

    public void setReducedWorkerList(List<ReducedWorker> reducedWorkerList) {
        this.reducedWorkerList = reducedWorkerList;
    }

    public String getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(String currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public int getCurrentWorkerIndex() {
        return currentWorkerIndex;
    }

    public void setCurrentWorkerIndex(int currentWorkerIndex) {
        this.currentWorkerIndex = currentWorkerIndex;
    }

    public DemandType getCurrentState() {
        return currentState;
    }

    public void setCurrentState(DemandType currentState) {
        this.currentState = currentState;
    }
}
