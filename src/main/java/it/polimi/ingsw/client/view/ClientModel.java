package it.polimi.ingsw.client.view;


import it.polimi.ingsw.client.view.cli.NotAValidTurnRunTimeException;
import it.polimi.ingsw.client.view.cli.Turn;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedCell;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.communication.message.payload.ReducedWorker;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.model.cards.God;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientModel<S> extends Observable<ClientModel<S>> implements Observer<Answer<S>> {

    private final ReducedCell[][] reducedBoard;
    private List<God> reducedGodList;
    private List<ReducedPlayer> opponents;
    private List<ReducedWorker> workers;
    private String currentPlayer;
    private String currentWorker;
    private Turn turn;
    private AnswerType state;
    private ReducedPlayer player;


    public ClientModel(ReducedPlayer player) {
        reducedBoard = new ReducedCell[5][5];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                reducedBoard[i][j] = new ReducedCell(i, j);
            }
        }

        this.player = player;
        turn = Turn.START;
    }

    @Override
    public void update(Answer<S> answer) {
        state = answer.getHeader();

        if (state.equals(AnswerType.START)) {
            currentPlayer = ((List<ReducedPlayer>) answer.getPayload()).get(0).getNickname();//Hp: first one is the chosen one

            opponents = ((List<ReducedPlayer>) answer.getPayload()).stream()
                                                                   .filter(p -> !p.getNickname().equals(player.getNickname()))
                                                                   .collect(Collectors.toList());
        }

        if (state.equals(AnswerType.SUCCESS))
            updateReduceObjects(answer);

        notify(this);

        nextTurn(answer.getContext());
    }

    private void updateReduceObjects(Answer<S> answer) {
        switch (turn) {
            case CHOOSE_DECK:
            case CHOOSE_CARD:
                reducedGodList = new ArrayList<>((List<God>) answer.getPayload());
                break;

            case PLACE_WORKERS:
                workers = new ArrayList<>((List<ReducedWorker>) answer.getPayload());

                for (ReducedWorker w : workers)
                    reducedBoard[w.getX()][w.getY()].setWorker(w);
                break;

            case CHOOSE_WORKER:
                currentPlayer = player.getNickname();
                workers.addAll((List<ReducedWorker>) answer.getPayload());

                for (ReducedWorker w : (List<ReducedWorker>) answer.getPayload())
                    reducedBoard[w.getX()][w.getY()].setWorker(w);
                break;

            case MOVE:
            case BUILD:
                updateReducedBoard((List<ReducedCell>) answer.getPayload());
                break;

            case CONFIRM:
                currentPlayer = answer.getPayload().toString();
                break;

            default:
                throw new NotAValidTurnRunTimeException("Not a valid turn");
        }
    }

    private void updateReducedBoard(List<ReducedCell> cells) {
        for (ReducedCell c : cells) {
            reducedBoard[c.getX()][c.getY()] = c;
        }
    }

    public boolean isYourTurn(String player) {
        return currentPlayer.equals(player);
    }

    private boolean nextTurn(DemandType demandType) {
        if (demandType == null) return false;
        turn = Turn.parseDemandType(demandType);

        return true;
    }

    public ReducedCell[][] getReducedBoard() {
        return reducedBoard;
    }

    public ReducedCell getReducedCell(int x, int y) {
        return !checkCell(x, y) ? reducedBoard[x][y] : null;
    }

    public boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
    }

    public boolean checkGod(God god) {
        if (god == null) return true;

        return reducedGodList.stream()
                             .noneMatch(g -> g.equals(god));
    }

    public boolean checkWorker(int x, int y) {
        if (checkCell(x, y)) return true;

        return workers.stream()
                      .filter(w -> w.getOwner().equals(player.getNickname()))
                      .noneMatch(w -> w.getX() == x && w.getY() == y);
    }

    public boolean evalToRepeat(int x, int y) {
        if (checkCell(x, y)) return true;

        switch (reducedBoard[x][y].getAction()) {
            case BUILD:
            case MOVE:
                return !reducedBoard[x][y].isFree();

            case DEFAULT:
                return true;

            case USEPOWER:
                return false;

            default:
                throw  new NotAValidTurnRunTimeException("Not a valid turn");
        }
    }

    public boolean evalToUsePower(int x, int y) {
        if (checkCell(x, y)) return false;

        return reducedBoard[x][y].getAction().equals(ReducedAction.USEPOWER);
    }

    public List<God> getReducedGodList() {
        return new ArrayList<>(reducedGodList);
    }

    public List<ReducedPlayer> getOpponents() {
        return new ArrayList<>(opponents);
    }

    public List<ReducedWorker> getWorkers() {
        return new ArrayList<>(workers);
    }

    public String getCurrentWorker() {
        return currentWorker;
    }

    public Turn getTurn() {
        return turn;
    }

    public AnswerType getState() {
        return state;
    }
}
