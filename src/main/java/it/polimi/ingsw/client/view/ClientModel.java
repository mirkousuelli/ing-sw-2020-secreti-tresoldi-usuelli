package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientModel<S> implements Runnable {

    private final ClientConnection<S> clientConnection;

    private ReducedAnswerCell[][] reducedBoard;
    private List<God> deck;
    private List<ReducedPlayer> opponents;
    private List<ReducedWorker> workers;
    private String currentPlayer;
    private int currentWorker;
    private final ReducedPlayer player;
    private String lobbyId;


    private final Object lockAnswer;
    private Answer<S> answer;

    private boolean isActive;
    private boolean isChanged;

    private static final Logger LOGGER = Logger.getLogger(ClientModel.class.getName());


    public ClientModel(String playerName, ClientConnection<S> clientConnection) {
        reducedBoard = new ReducedAnswerCell[5][5];
        deck = new ArrayList<>();
        workers = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                reducedBoard[i][j] = new ReducedAnswerCell(i, j);
            }
        }

        player = new ReducedPlayer(playerName);
        player.setColor(Color.RESET);

        this.clientConnection = clientConnection;

        lockAnswer = new Object();

        setActive(false);
        setChanged(false);
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public ReducedPlayer getPlayer() {
        return player;
    }

    public Answer<S> getAnswer() {
        Answer<S> temp;

        synchronized (lockAnswer) {
            temp = answer;
        }

        return temp;
    }

    private void setAnswer(Answer<S> answer) {
        synchronized (lockAnswer) {
            this.answer = answer;
        }
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

    private void updateModel() {
        synchronized (lockAnswer) {
            if (answer.getHeader().equals(AnswerType.SUCCESS))
                updateReduceObjects(answer);
        }
    }

    private synchronized void updateReduceObjects(Answer<S> answer) {
        switch (answer.getContext()) {
            case CONNECT:
                //TODO only print?
                break;

            case RELOAD:
                ReducedGame reducedGame = ((ReducedGame) answer.getPayload());

                reducedBoard = reducedGame.getReducedBoard();
                lobbyId = reducedGame.getLobbyId();
                opponents = reducedGame.getReducedPlayerList();
                currentPlayer = reducedGame.getCurrentPlayerIndex();
                workers = reducedGame.getReducedWorkerList();
                currentWorker = reducedGame.getCurrentWorkerIndex();

                for (ReducedPlayer o : opponents) {
                    deck.add(o.getGod());
                    if (o.getNickname().equals(player.getNickname())) {
                        player.setGod(o.getGod());
                        player.setColor(o.getColor());
                        opponents.remove(o);
                        break;
                    }
                }
                break;

            case CREATE_GAME:
            case JOIN_GAME:
                lobbyId = ((ReduceDemandChoice) answer.getPayload()).getChoice();
                break;

            case ASK_LOBBY:
                //TODO only print?
                break;

            case WAIT:
                //TODO only print?
                break;

            case START:
                currentPlayer = ((List<ReducedPlayer>) answer.getPayload()).get(0).getNickname();//Hp: first one is the chosen one

                opponents = ((List<ReducedPlayer>) answer.getPayload());

                for (ReducedPlayer o : opponents) {
                    if (o.getNickname().equals(player.getNickname())) {
                        opponents.remove(o);
                        break;
                    }
                }
                break;

            case CHOOSE_DECK:
            case CHOOSE_CARD:
                deck = new ArrayList<>((List<God>) answer.getPayload());
                break;

            case CHOOSE_STARTER:
                //TODO only print?
                break;

            case PLACE_WORKERS:
                List<ReducedAnswerCell> reducedAnswerCellList = (List<ReducedAnswerCell>) answer.getPayload();
                if (reducedAnswerCellList.isEmpty()) return;

                for (ReducedAnswerCell c : reducedAnswerCellList) {
                    reducedBoard[c.getX()][c.getY()].setWorker(c.getWorker());
                    workers.add(c.getWorker());
                }

                break;

            case CHOOSE_WORKER:
                /*currentPlayer = player.getNickname();
                workers.addAll((List<ReducedWorker>) answer.getPayload());

                for (ReducedWorker w : (List<ReducedWorker>) answer.getPayload())
                    reducedBoard[w.getX()][w.getY()].setWorker(w);*/
                break;

            case MOVE:
            case BUILD:
            case USE_POWER: //TODO
                updateReducedBoard((List<ReducedAnswerCell>) answer.getPayload());

                workers = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        if(!reducedBoard[i][j].isFree()) {
                            workers.add(reducedBoard[i][j].getWorker());
                        }
                    }
                }
                break;

            case CHANGE_TURN:
                currentPlayer = ((ReducedPlayer) answer.getPayload()).getNickname();
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

    public synchronized boolean isYourTurn() {
       if (currentPlayer == null) return true;

       return player.getNickname().equals(currentPlayer);
    }

    public ReducedAnswerCell[][] getReducedBoard() {
        return reducedBoard;
    }

    public ReducedAnswerCell getReducedCell(String cellString) {
        List<Integer> coord = stringToInt(cellString);
        int x = coord.get(0);
        int y = coord.get(1);

        return !checkCell(x, y) ? reducedBoard[x][y] : null;
    }

    public boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
    }

    public synchronized boolean checkGod(String godString) {
        God god = God.parseString(godString);
        if (god == null) return true;

        return deck.stream()
                    .noneMatch(g -> g.equals(god));
    }

    public synchronized boolean checkWorker(String workerString) {
        List<Integer> coord = stringToInt(workerString);
        int x = coord.get(0);
        int y = coord.get(1);

        if (checkCell(x, y)) return true;

        return workers.stream()
                      .filter(w -> w.getOwner().equals(player.getNickname()))
                      .noneMatch(w -> w.getX() == x && w.getY() == y);
    }

    public synchronized boolean checkPlayer(String player) {
        for (ReducedPlayer p : opponents) {
            if (p.getNickname().equals(player))
                return false;
        }

        return !currentPlayer.equals(player);
    }

    public synchronized boolean evalToRepeat(String string) {
        List<Integer> coord = stringToInt(string);
        int x = coord.get(0);
        int y = coord.get(1);

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

    public synchronized boolean evalToUsePower(String string) {
        List<Integer> coord = stringToInt(string);
        int x = coord.get(0);
        int y = coord.get(1);

        if (checkCell(x, y)) return false;

        return reducedBoard[x][y].getAction().equals(ReducedAction.USEPOWER);
    }

    public synchronized List<God> getDeck() {
        return new ArrayList<>(deck);
    }

    public synchronized List<ReducedPlayer> getOpponents() {
        return new ArrayList<>(opponents);
    }

    public synchronized List<ReducedWorker> getWorkers() {
        return new ArrayList<>(workers);
    }

    public synchronized int getCurrentWorker() {
        return currentWorker;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    private List<Integer> stringToInt(String string) {
        if (string.length() != 3) return new ArrayList<>();

        List<Integer> ret = new ArrayList<>();

        ret.add(0, string.charAt(0) - 48);
        ret.add(1, string.charAt(2) - 48);

        return ret;
    }
}
