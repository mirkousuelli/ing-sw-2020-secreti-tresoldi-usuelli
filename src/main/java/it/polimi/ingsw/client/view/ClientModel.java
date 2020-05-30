package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnectionSocket;
import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;

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
    private boolean isYourEnding = false;
    private boolean additionalPowerUsed = true;

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

    public synchronized DemandType getCurrentState() {
        return currentState;
    }

    public synchronized DemandType getNextState() {
        return nextState;
    }

    public synchronized void setNextState(DemandType nextState) {
        this.nextState = nextState;
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
                                setAnswer(clientConnection.getAnswer());
                            }

                            LOGGER.info("Receiving...");
                            synchronized (lockAnswer) {
                                updateModel();
                                LOGGER.info("updated!");
                                LOGGER.info(() -> "curr: " + currentState);
                                LOGGER.info(() -> "next: " + nextState);
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
    protected void startThreads() throws InterruptedException {
        Thread read = asyncReadFromConnection();
        read.join();
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

        System.out.println("MODEL " + getCurrentState() + " " + getAnswer().getHeader() + " " + getAnswer().getContext());

        if (!isCreator && isInitializing)
            currentState = nextState;

        if (isNewGame) {
            currentState = DemandType.START;
            if (isCreator)
                nextState = DemandType.CHOOSE_DECK;
            else
                nextState = DemandType.CHOOSE_CARD;
            isNewGame = false;
            isInitializing = true;
            isReloaded = false;
            currentPlayer = null;
            reducedBoard = new ReducedAnswerCell[5][5];
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    reducedBoard[i][j] = new ReducedAnswerCell(i, j, null);
                }
            }
            deck.clear();
            opponents.clear();
            workers.clear();
        }

        updateStateInitial(answerTemp);

        if (answerTemp.getHeader().equals(AnswerType.CHANGE_TURN)) {
            updateCurrentPlayer();
            additionalPowerUsed = true;
        }
        else if (!answerTemp.getHeader().equals(AnswerType.ERROR)) {
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
            else if (answerTemp.getHeader().equals(AnswerType.VICTORY)) {
                currentState = DemandType.NEW_GAME;
                nextState = DemandType.START;
                isNewGame = true;
                isYourEnding = player.getNickname().equals(((ReducedPlayer) getAnswer().getPayload()).getNickname());
            }
            else if (answerTemp.getHeader().equals(AnswerType.RELOAD))
                reloadGame();
            else if (answerTemp.getHeader().equals(AnswerType.DEFEAT)) {
                String playerToRemove = ((ReducedPlayer) getAnswer().getPayload()).getNickname();
                if (!playerToRemove.equals(player.getNickname()))
                    opponents.removeIf( o -> o.getNickname().equals(playerToRemove));
            }

            updateNextState();
        }

        if (!additionalPowerUsed && player != null && player.getCard() != null && player.getCard().isAdditionalPower() && currentState.equals(DemandType.BUILD) && player.getCard().getEffect().equals(Effect.MOVE)) {
            currentState = DemandType.ASK_ADDITIONAL_POWER;
            nextState = DemandType.BUILD;
            additionalPowerUsed = true;
        }

        if (!additionalPowerUsed && player != null && player.getCard() != null && player.getCard().isAdditionalPower() && nextState.equals(DemandType.CHOOSE_WORKER) && player.getCard().getEffect().equals(Effect.BUILD)) {
            nextState = DemandType.ASK_ADDITIONAL_POWER;
            additionalPowerUsed = true;
        }

        if (currentState.equals(DemandType.MOVE))
            additionalPowerUsed = false;

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
        if (!currentPlayer.equals(((ReducedPlayer) getAnswer().getPayload()).getNickname())) {
            prevPlayer = currentPlayer;
            currentPlayer = ((ReducedPlayer) getAnswer().getPayload()).getNickname();
        }
    }

    private synchronized void updateReducedObjectsInitialize(Answer answerTemp) {
        switch (currentState) {
            case CREATE_GAME:
            case CONNECT:
                player.setColor(((ReducedPlayer) answerTemp.getPayload()).getColor());
                isCreator = ((ReducedPlayer) answerTemp.getPayload()).isCreator() || currentState.equals(DemandType.CREATE_GAME);
                player.setCreator(isCreator);
                break;

            case START:
                currentPlayer = ((List<ReducedPlayer>) answerTemp.getPayload()).get(0).getNickname(); //Hp: first one is the chosen one

                opponents = ((List<ReducedPlayer>) answerTemp.getPayload());

                for (ReducedPlayer o : opponents) {
                    if (o.getNickname().equals(player.getNickname())) {
                        isCreator = o.isCreator();
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

                //workers.forEach(w -> System.out.println(w.getX() + "," + w.getY() + " " + w.getOwner() + " " + w.isGender() + " " + w.getId()));
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

    public synchronized boolean isYours() {
        return isYourEnding;
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

    public synchronized ReducedAnswerCell getCell(int x, int y) {
        if (checkCell(x, y)) return null;

        return reducedBoard[x][y];
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

    public synchronized String getPrevPlayer() {
        return prevPlayer;
    }
}
