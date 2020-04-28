package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.client.view.cli.Color;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.client.view.cli.Turn;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.communication.message.payload.ReducedWorker;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ClientModel<S> implements Runnable {

    private final ReducedAnswerCell[][] reducedBoard;
    private List<God> reducedGodList;
    private List<ReducedPlayer> opponents;
    private List<ReducedWorker> workers;
    private String currentPlayer;
    private String currentWorker;
    private Turn turn;
    private AnswerType state;
    private final ReducedPlayer player;

    private final ClientConnection<S> clientConnection;
    private Answer<S> answer;
    private boolean isActive;
    private boolean isChanged;

    private static final Logger LOGGER = Logger.getLogger(ClientModel.class.getName());


    public ClientModel(String playerName, ClientConnection<S> clientConnection) {
        reducedBoard = new ReducedAnswerCell[5][5];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                reducedBoard[i][j] = new ReducedAnswerCell(i, j);
            }
        }

        player = new ReducedPlayer(playerName);
        player.setColor(Color.RESET);
        turn = Turn.START;

        this.clientConnection = clientConnection;

        setActive(false);
        setChanged(false);

    }
    
    public ClientModel(ReducedPlayer player, ClientConnection<S> clientConnection) {
        this(player.getNickname(), clientConnection);
        player.setColor(player.getColor());
    }

    public synchronized Answer<S> getAnswer() {
        return answer;
    }

    private synchronized void setAnswer(Answer<S> answer) {
        this.answer = answer;
    }

    private synchronized boolean isActive() {
        return isActive;
    }

    private synchronized void setActive(boolean active) {
        isActive = active;
    }

    public synchronized boolean isChanged() {
        return isChanged;
    }

    public synchronized void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    private Thread asyncReadFromConnection() {
        Thread t = new Thread(
                () -> {
                    try {
                        Answer<S> temp;
                        while (isActive()) {
                            synchronized (clientConnection) {
                                while (!clientConnection.isChanged()) clientConnection.wait();
                                clientConnection.setChanged(false);
                                temp = clientConnection.getAnswer();
                            }

                            LOGGER.info("Receiving...");
                            synchronized (this) {
                                setAnswer(temp);
                                updateModel();
                                setChanged(true);
                                LOGGER.info("Received: " + getAnswer().getHeader() + " " + getAnswer().getContext());
                                this.notifyAll();
                            }
                        }
                    } catch (Exception e){
                        setActive(false);
                        LOGGER.log(Level.SEVERE, "Got an exception", e);
                    }
                }
        );
        t.start();
        return t;
    }

    @Override
    public void run() {
        setActive(true);
        setChanged(false);

        try {
            Thread read = asyncReadFromConnection();
            read.join();
        } catch (InterruptedException | NoSuchElementException e) {
            LOGGER.log(Level.SEVERE, "Connection closed from the client side", e);
        } finally {
            setActive(false);
        }
    }

    private synchronized void updateModel() {
        state = answer.getHeader();

        if (state.equals(AnswerType.START)) {
            currentPlayer = ((List<ReducedPlayer>) answer.getPayload()).get(0).getNickname();//Hp: first one is the chosen one

            opponents = ((List<ReducedPlayer>) answer.getPayload()).stream()
                    .filter(p -> !p.getNickname().equals(player.getNickname()))
                    .collect(Collectors.toList());
        }

        if (state.equals(AnswerType.SUCCESS) && !answer.getContext().equals(DemandType.CONNECT))
            updateReduceObjects(answer);

        if (!nextTurn(answer.getContext()))
            throw new NotAValidInputRunTimeException("Not a valid turn");
    }

    private synchronized void updateReduceObjects(Answer<S> answer) {
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
                updateReducedBoard((List<ReducedAnswerCell>) answer.getPayload());
                break;

            case CONFIRM:
                currentPlayer = answer.getPayload().toString();
                break;

            default:
                throw new NotAValidInputRunTimeException("Not a valid turn");
        }
    }

    private synchronized void updateReducedBoard(List<ReducedAnswerCell> cells) {
        for (ReducedAnswerCell c : cells) {
            reducedBoard[c.getX()][c.getY()] = c;
        }
    }

    public synchronized boolean isYourTurn(String player) {
        return currentPlayer.equals(player);
    }

    private synchronized boolean nextTurn(DemandType demandType) {
        if (demandType == null) return false;
        turn = Turn.parseDemandType(demandType);

        return true;
    }

    public ReducedAnswerCell[][] getReducedBoard() {
        return reducedBoard;
    }

    public ReducedAnswerCell getReducedCell(int x, int y) {
        return !checkCell(x, y) ? reducedBoard[x][y] : null;
    }

    public synchronized boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
    }

    public synchronized boolean checkGod(God god) {
        if (god == null) return true;

        return reducedGodList.stream()
                             .noneMatch(g -> g.equals(god));
    }

    public synchronized boolean checkWorker(int x, int y) {
        if (checkCell(x, y)) return true;

        return workers.stream()
                      .filter(w -> w.getOwner().equals(player.getNickname()))
                      .noneMatch(w -> w.getX() == x && w.getY() == y);
    }

    public synchronized boolean evalToRepeat(int x, int y) {
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
                throw  new NotAValidInputRunTimeException("Not a valid turn");
        }
    }

    public synchronized boolean evalToUsePower(int x, int y) {
        if (checkCell(x, y)) return false;

        return reducedBoard[x][y].getAction().equals(ReducedAction.USEPOWER);
    }

    public synchronized List<God> getReducedGodList() {
        return new ArrayList<>(reducedGodList);
    }

    public synchronized List<ReducedPlayer> getOpponents() {
        return new ArrayList<>(opponents);
    }

    public synchronized List<ReducedWorker> getWorkers() {
        return new ArrayList<>(workers);
    }

    public synchronized String getCurrentWorker() {
        return currentWorker;
    }

    public synchronized Turn getTurn() {
        return turn;
    }

    public synchronized AnswerType getState() {
        return state;
    }
}
