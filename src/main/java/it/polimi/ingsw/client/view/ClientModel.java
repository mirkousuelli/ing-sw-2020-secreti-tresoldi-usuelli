package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnectionSocket;
import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ClientModel<S> extends SantoriniRunnable {

    private final ClientConnectionSocket<S> clientConnection;
    private ReducedAnswerCell[][] reducedBoard;
    private List<ReducedCard> deck;
    private List<ReducedPlayer> opponents;
    private List<ReducedWorker> workers;
    private String currentPlayer;
    private String prevPlayer = null;
    private final ReducedPlayer player;

    private boolean isCreator = false;
    private boolean isInitializing = true;
    private boolean isReloaded;
    private boolean isNewGame = false;

    private DemandType nextState = DemandType.CONNECT;
    private DemandType currentState = DemandType.CONNECT;
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

    public ClientConnectionSocket<S> getClientConnection() {
        ClientConnectionSocket<S> ret;

        synchronized (lock) {
            ret = clientConnection;
        }

        return ret;
    }

    public int getNumberOfPlayers() {
        int numOfPl;

        synchronized (lock) {
            numOfPl = opponents.size() + 1;
        }

        return numOfPl;
    }

    public ReducedPlayer getPlayer() {
        ReducedPlayer ret;

        synchronized (lock) {
            ret = player;
        }

        return ret;
    }

    public DemandType getCurrentState() {
        return currentState;
    }

    public boolean isReloaded() {
        boolean ret;

        synchronized (lock) {
            ret = isReloaded;
        }

        return ret;
    }

    private Thread asyncReadFromConnection() {
        Thread t = new Thread(
                () -> {
                    try {
                        while (isActive()) {
                            synchronized (lockAnswer) {
                                while (!clientConnection.isChanged()) lockAnswer.wait();
                                clientConnection.setChanged(false);
                            }

                            LOGGER.info("Receiving...");
                            synchronized (lockAnswer) {
                                updateModel();
                                LOGGER.info("updated!");
                                LOGGER.info("curr: " + currentState);
                                LOGGER.info("next: " + nextState);
                            }
                        }
                    } catch (Exception e){
                        if (!(e instanceof InterruptedException))
                            LOGGER.log(Level.SEVERE, "Got an exception", e);
                    }
                }
        );
        t.start();
        return t;
    }

    @Override
    protected void startThreads(Thread watchDogThread) throws InterruptedException {
        Thread read = asyncReadFromConnection();
        watchDogThread.join();
        read.interrupt();
    }

    private synchronized void updateStateInitial(Answer<S> answerTemp) {
        if (!isInitializing) return;

        if (currentState.equals(DemandType.CONNECT) && answerTemp.getHeader().equals(AnswerType.CHANGE_TURN)) {
            isCreator = true;
            currentPlayer = player.getNickname();
            answerTemp.setHeader(AnswerType.SUCCESS);
        }

        updateNextState();
    }

    private synchronized void updateNextState() {
        nextState = DemandType.getNextState(currentState, isCreator);
    }

    private void updateModel() {
        Answer answerTemp;

        synchronized (lockAnswer) {
            answerTemp = getAnswer();
        }

        if (!isCreator && isInitializing)
            currentState = nextState;

        if (isNewGame) {
            currentState = DemandType.CONNECT;
            isNewGame = false;
            isInitializing = true;
            deck.clear();
            opponents.clear();
            workers.clear();
        }

        updateStateInitial(answerTemp);

        if (answerTemp.getHeader().equals(AnswerType.CHANGE_TURN))
            updateCurrentPlayer();
        else {
            if (isYourTurn()) {
                if (!isReloaded) {
                    if (nextState.ordinal() >= DemandType.USE_POWER.ordinal())
                        currentState = DemandType.CHOOSE_WORKER;
                    else
                        currentState = nextState;
                }
                else
                    isReloaded = false;
            }

            if (answerTemp.getHeader().equals(AnswerType.SUCCESS)) {
                if (isInitializing)
                    updateReducedObjectsInitialize(answerTemp);
                else
                    updateReduceObjects(answerTemp);
            }

            if (answerTemp.getHeader().equals(AnswerType.VICTORY)) {
                currentState = DemandType.NEW_GAME;
                nextState = DemandType.START;
                isNewGame = true;
            }

            if (answerTemp.getHeader().equals(AnswerType.RELOAD))
                reloadGame();

            updateNextState();
        }

        synchronized (lockAnswer) {
            setChanged(true);
            lockAnswer.notifyAll();
        }
    }

    private synchronized void reloadGame() {
        ReducedGame reducedGame = ((ReducedGame) getAnswer().getPayload());

        reducedBoard = reducedGame.getReducedBoard();
        opponents = reducedGame.getReducedPlayerList();
        currentPlayer = reducedGame.getCurrentPlayerIndex();
        workers = reducedGame.getReducedWorkerList();
        isInitializing = false;

        if (isYourTurn())
            currentState = reducedGame.getCurrentState();
        else
            currentState = DemandType.CHOOSE_WORKER;

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
    }

    private synchronized void updateCurrentPlayer() {
        prevPlayer = currentPlayer;
        currentPlayer = ((ReducedPlayer) getAnswer().getPayload()).getNickname();
    }

    private synchronized void updateReducedObjectsInitialize(Answer answerTemp) {
        switch (currentState) {
            case CREATE_GAME:
            case CONNECT:
                player.setColor(((ReducedMessage) answerTemp.getPayload()).getMessage());
                break;

            case START:
                currentPlayer = ((List<ReducedPlayer>) answerTemp.getPayload()).get(0).getNickname(); //Hp: first one is the chosen one

                opponents = ((List<ReducedPlayer>) answerTemp.getPayload());

                for (ReducedPlayer o : opponents) {
                    if (o.getNickname().equals(player.getNickname())) {
                        opponents.remove(o);
                        break;
                    }
                }

                isInitializing = false;
                break;

            default:
                throw new NotAValidInputRunTimeException("Not a valid turn: " + currentState);
        }
    }

    private synchronized void updateReduceObjects(Answer answer) {
        switch (answer.getContext()) {
            case GOD:
            case PLAYER:
            case CARD:
                List<ReducedCard> reducedCardList = ((List<ReducedCard> ) answer.getPayload());

                if (reducedCardList == null || reducedCardList.isEmpty()) return;
                if (deck.isEmpty() || deck.size() > opponents.size() + 1) {
                    deck = reducedCardList;
                    return;
                }

                Set<God> gods = reducedCardList.stream()
                        .map(ReducedCard::getGod)
                        .collect(Collectors.toSet());

                List<ReducedCard> chosenList = deck.stream()
                        .filter(card -> !gods.contains(card.getGod()))
                        .collect(Collectors.toList());

                if (chosenList.size() > 1) return;

                ReducedCard chosen;
                if (chosenList.isEmpty()) {
                    chosen = reducedCardList.get(0);
                    prevPlayer = currentPlayer;
                }
                else
                    chosen= chosenList.get(0);

                ReducedPlayer current = opponents.stream()
                                                  .filter(p -> p.getNickname().equals(prevPlayer))
                                                  .reduce(null, (a, b) -> a != null
                                                          ? a
                                                          : b
                                                  );

                if (current == null)
                    current = player;

                current.setCard(chosen);
                deck.remove(chosen);
                break;

            case WORKER:
            case BOARD:
                List<ReducedAnswerCell> reducedAnswerCellList = (List<ReducedAnswerCell>) answer.getPayload();
                if (reducedAnswerCellList.isEmpty()) return;

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
       if (currentPlayer == null) return false;

       return player.getNickname().equals(currentPlayer);
    }

    public synchronized boolean isCreator() {
        return isCreator;
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

    public boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
    }

    public boolean checkGod(String godString) {
        List<ReducedCard> dk;
        God god = God.parseString(godString);
        if (god == null) return true;

        synchronized (lock) {
            dk = deck;
        }

        return dk.stream()
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

    public synchronized ReducedPlayer getCurrentPlayer() {
        if (player.getNickname().equals(currentPlayer)) return player;

        for (ReducedPlayer o : opponents) {
            if (o.getNickname().equals(currentPlayer))
                return o;
        }

        return null;
    }
}
