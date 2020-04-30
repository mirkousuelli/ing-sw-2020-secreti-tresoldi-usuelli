package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;

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

    public ReducedGame(String lobbyId, Cell[][] board, List<Player> playerList, int currentPlayerIndex, int currentWorkerIndex) {
        this.lobbyId = lobbyId;
        reducedBoard = new ReducedAnswerCell[5][5];
        reducedPlayerList = new ArrayList<>();
        reducedWorkerList = new ArrayList<>();
        this.currentPlayerIndex = currentPlayerIndex;
        this.currentWorkerIndex = currentPlayerIndex * 2 + currentWorkerIndex - 1;


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                reducedBoard[i][j] = new ReducedAnswerCell(i, j);
        }

        for (Player p : playerList) {
            reducedPlayerList.add(new ReducedPlayer(p.nickName, p.getCard().getGod()));
            for (Worker w: p.getWorkers()) {
                ReducedWorker work = new ReducedWorker(w, p.nickName);
                reducedWorkerList.add(work);
                reducedBoard[w.getX()][w.getY()].setWorker(work);
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
