package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;
import it.polimi.ingsw.server.network.message.Lobby;

import java.util.ArrayList;
import java.util.List;

public class ReducedGame {

    private String lobbyId;
    private ReducedAnswerCell[][] reducedBoard;
    private List<ReducedPlayer> reducedPlayerList;
    private List<ReducedWorker> reducedWorkerList;
    private int currentPlayerIndex;
    private int currentWorkerIndex;

    public ReducedGame() {}

    public ReducedGame(Lobby lobby) {
        Game loadedGame = lobby.getGame();
        Board board = loadedGame.getBoard();
        List<Player> playerList = loadedGame.getPlayerList();
        String state = loadedGame.getState().getName();
        int currWork;

        currentPlayerIndex = loadedGame.getPlayerList().indexOf(loadedGame.getCurrentPlayer());
        currWork = loadedGame.getCurrentPlayer().getWorkers().indexOf(loadedGame.getCurrentPlayer().getCurrentWorker());
        currentWorkerIndex = currentPlayerIndex * 2 + currWork;
        lobbyId = lobby.getID();
        reducedBoard = new ReducedAnswerCell[5][5];
        reducedPlayerList = new ArrayList<>(lobby.getReducedPlayerList());
        reducedWorkerList = new ArrayList<>();


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                reducedBoard[i][j] = new ReducedAnswerCell(i, j);
                reducedBoard[i][j].setLevel(ReducedLevel.parseInt(board.map[i][j].getLevel().toInt()));
                reducedBoard[i][j].setAction(ReducedAction.DEFAULT);
            }
        }

        if (currWork > -1) {
            Cell loc = playerList.get(currentPlayerIndex).getWorkers().get(currWork).getLocation();
            List<Cell> around = board.getAround(loc);
            List<Cell> action = null;
            if (state.equals("move"))
                action = board.getPossibleMoves(playerList.get(currentPlayerIndex));
            if (state.equals("build"))
                action = board.getPossibleBuilds(playerList.get(currentPlayerIndex).getWorkers().get(currWork));

            if (action != null) {
                for (Cell c : around) {
                    for (Cell cell : action) {
                        if (c.equals(cell))
                            reducedBoard[c.getX()][c.getY()].setAction(ReducedAction.parseString(state));
                    }
                }
            }
        }

        for (Player p : playerList) {
            for (Worker w : p.getWorkers()) {
                ReducedWorker work = new ReducedWorker(w, p.nickName);
                reducedWorkerList.add(work);
                reducedBoard[w.getX()][w.getY()].setWorker(work);
            }

            for (ReducedPlayer rp : reducedPlayerList) {
                if (rp.getNickname().equals(p.nickName))
                    rp.setGod(p.getCard().getGod());
            }
        }
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
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

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public int getCurrentWorkerIndex() {
        return currentWorkerIndex;
    }

    public void setCurrentWorkerIndex(int currentWorkerIndex) {
        this.currentWorkerIndex = currentWorkerIndex;
    }
}
