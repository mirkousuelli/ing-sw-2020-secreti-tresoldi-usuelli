package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.view.cli.NotAValidTurnRunTimeException;
import it.polimi.ingsw.client.view.cli.Turn;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedCell;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.communication.message.payload.ReducedWorker;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.model.cards.God;

import java.util.ArrayList;
import java.util.List;

public class ClientModel<S> extends Observable<ClientModel<S>> implements Observer<Answer<S>> {

    private final ReducedCell[][] reducedBoard;
    private List<God> reducedGodList;
    private List<ReducedPlayer> opponents;
    private List<ReducedWorker> workers;
    private String currentPlayer;
    private String currentWorker;
    private Turn turn;
    private AnswerType state;

    public ClientModel() {
        reducedBoard = new ReducedCell[5][5];
    }

    @Override
    public void update(Answer<S> answer) {
        state = answer.getHeader();

        if (state.equals(AnswerType.START))
            opponents = new ArrayList<>((List<ReducedPlayer>) answer.getPayload());

        if (state.equals(AnswerType.SUCCESS))
            updateReduceObjects(answer);

        notify(this);
    }

    private void updateReduceObjects(Answer<S> answer) {
        switch (turn) {
            case CHOOSE_DECK:
            case CHOOSE_CARD:
                reducedGodList = new ArrayList<>((List<God>) answer.getPayload());
                break;

            case PLACE_WORKERS:
                workers = new ArrayList<>((List<ReducedWorker>) answer.getPayload());
                break;

            case CHOOSE_WORKER:
                currentWorker = answer.getPayload().toString();
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

        nextTurn(answer.getContext());
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
        return checkCell(x, y) ? reducedBoard[x][y] : null;
    }

    public boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
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
