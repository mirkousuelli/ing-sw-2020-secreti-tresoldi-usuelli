package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnectionSocket;
import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientModel<S> extends SantoriniRunnable<S> {

    private ClientConnectionSocket<S> clientConnection;
    private ReducedAnswerCell[][] reducedBoard;
    private List<ReducedCard> deck;
    private List<ReducedPlayer> opponents;
    private List<ReducedWorker> workers;
    private String currentPlayer;
    private final ReducedPlayer player;
    private boolean isReloaded;
    private static final Logger LOGGER = Logger.getLogger(ClientModel.class.getName());


    public ClientModel(String playerName, ClientConnectionSocket<S> clientConnection) {
        super();
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

        isReloaded = false;
    }

    public void setClientConnection(ClientConnectionSocket<S> clientConnection) {
        this.clientConnection = clientConnection;
    }

    public ClientConnectionSocket<S> getClientConnection() {
        ClientConnectionSocket<S> ret;

        synchronized (lock) {
            ret = clientConnection;
        }

        return ret;
    }

    public synchronized String getCurrentPlayer() {
        return currentPlayer;
    }

    public ReducedPlayer getPlayer() {
        ReducedPlayer ret;

        synchronized (player) {
            ret = player;
        }

        return ret;
    }

    private Thread asyncReadFromConnection() {
        Thread t = new Thread(
                () -> {
                    try {
                        Answer<S> temp;
                        while (isActive()) {
                            synchronized (clientConnection.lockAnswer) {
                                while (!clientConnection.isChanged()) clientConnection.lockAnswer.wait();
                                if (!clientConnection.isActive()) {
                                    setActive(false);
                                    break;
                                }
                                clientConnection.setChanged(false);
                                temp = clientConnection.getAnswer();
                            }

                            LOGGER.info("Receiving...");
                            synchronized (lockAnswer) {
                                setAnswer(temp);
                                updateModel();
                                setChanged(true);
                                LOGGER.info("Received: " + getAnswer().getHeader() + " " + getAnswer().getContext());
                                lockAnswer.notifyAll();
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
    protected void startThreads() throws InterruptedException {
        Thread read = asyncReadFromConnection();
        read.join();
    }

    private void updateModel() {
        if (isReloaded) {
            isReloaded = false;
        }
        synchronized (lockAnswer) {
            if (answer.getHeader().equals(AnswerType.CLOSE)) {
                setActive(false);
                return;
            }

            if (!answer.getHeader().equals(AnswerType.ERROR))
                updateReduceObjects(answer);
        }
    }

    private synchronized void updateReduceObjects(Answer<S> answer) {
        switch (answer.getContext()) {
            case RELOAD:
                ReducedGame reducedGame = ((ReducedGame) answer.getPayload());

                reducedBoard = reducedGame.getReducedBoard();
                opponents = reducedGame.getReducedPlayerList();
                currentPlayer = reducedGame.getCurrentPlayerIndex();
                workers = reducedGame.getReducedWorkerList();

                for (ReducedPlayer o : opponents) {
                    deck.add(o.getCard());
                    if (o.getNickname().equals(player.getNickname())) {
                        player.setCard(o.getCard());
                        player.setColor(o.getColor());
                        opponents.remove(o);
                        break;
                    }
                }

                isReloaded = true;
                break;

            case CREATE_GAME:
            case CONNECT:
                player.setColor(((ReducedMessage) answer.getPayload()).getMessage());
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
            case AVAILABLE_GODS:
                deck = new ArrayList<>((List<ReducedCard>) answer.getPayload());
                break;

            case CHOOSE_CARD:
            case CHOOSE_STARTER:
                ReducedPlayer rp = ((ReducedPlayer) answer.getPayload());
                if (rp.getCard() == null) return;

                deck.removeIf(card -> card.getGod().equals(rp.getCard().getGod()));

                for (ReducedPlayer p : opponents) {
                    if (p.getNickname().equals(rp.getNickname())) {
                        p.setCard(rp.getCard());
                        return;
                    }
                }
                player.setCard(rp.getCard());
                break;

            case MOVE:
            case BUILD:
            case USE_POWER:
            case ASK_ADDITIONAL_POWER:
            case PLACE_WORKERS:
            case CHOOSE_WORKER:
            case VICTORY:
            case DEFEAT:
                List<ReducedAnswerCell> reducedAnswerCellList = (List<ReducedAnswerCell>) answer.getPayload();
                if (reducedAnswerCellList.isEmpty()) break;

                //reset board
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        reducedBoard[i][j].resetAction();
                    }
                }

                updateReducedBoard(reducedAnswerCellList);

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

    public synchronized ReducedAnswerCell[][] getReducedBoard() {
        return reducedBoard;
    }

    public synchronized ReducedAnswerCell getReducedCell(String cellString) {
        List<Integer> coord = stringToInt(cellString);

        if (coord.isEmpty()) return null;

        int x = coord.get(0);
        int y = coord.get(1);

        return !checkCell(x, y) ? reducedBoard[x][y] : null;
    }

    public synchronized boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
    }

    public synchronized boolean checkGod(String godString) {
        God god = God.parseString(godString);
        if (god == null) return true;

        return deck.stream()
                    .noneMatch(g -> g.getGod().equals(god));
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
        String[] input = string.split(" ");
        if (input.length > 2) return true;

        List<Integer> coord = stringToInt(input[1]);
        int x = coord.get(0);
        int y = coord.get(1);

        if (checkCell(x, y)) return true;

        for (ReducedAction ra : reducedBoard[x][y].getActionList()) {
            if (input[0].equals(ra.getName())) {
                switch (ra) {
                    case BUILD:
                    case MOVE:
                        return !reducedBoard[x][y].isFree();

                    case DEFAULT:
                        return true;

                    case USEPOWER:
                        return false;

                    default:
                        throw new NotAValidInputRunTimeException("Not a valid turn");
                }
            }
        }

        return true;
    }

    public synchronized boolean evalToUsePower(String string) {
        String[] input = string.split(" ");

        List<Integer> coord = stringToInt(input[1]);
        int x = coord.get(0);
        int y = coord.get(1);

        return reducedBoard[x][y].getActionList().contains(ReducedAction.USEPOWER) && ReducedAction.USEPOWER.getName().equals(input[0]);
    }

    public synchronized List<ReducedCard> getDeck() {
        return new ArrayList<>(deck);
    }

    public synchronized List<ReducedPlayer> getOpponents() {
        return new ArrayList<>(opponents);
    }

    public synchronized List<ReducedWorker> getWorkers() {
        return new ArrayList<>(workers);
    }

    private synchronized List<Integer> stringToInt(String string) {
        if (string.length() != 3) return new ArrayList<>();

        List<Integer> ret = new ArrayList<>();

        ret.add(0, string.charAt(0) - 48);
        ret.add(1, string.charAt(2) - 48);

        return ret;
    }
}
