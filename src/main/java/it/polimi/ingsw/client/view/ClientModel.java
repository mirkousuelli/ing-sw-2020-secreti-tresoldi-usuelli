package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnectionSocket;
import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.communication.message.Answer;
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

public class ClientModel<S> extends SantoriniRunnable<S> {

    private final ClientConnectionSocket<S> clientConnection;
    private ReducedAnswerCell[][] reducedBoard;
    private List<ReducedCard> deck;
    private List<ReducedPlayer> opponents;
    private List<ReducedWorker> workers;
    private String currentPlayer;
    private String prevPlayer = null;
    private final ReducedPlayer player;

    private boolean isInitializing = true;
    private boolean isReloaded;
    private boolean isNewGame = false;
    private boolean isYourEnding = false;
    private boolean additionalPowerUsed = true;

    private DemandType nextState = DemandType.CONNECT;
    private DemandType currentState = DemandType.CONNECT;
    private DemandType prevState = DemandType.CONNECT;

    private static final Logger LOGGER = Logger.getLogger(ClientModel.class.getName());
    private static final int DIM = 5;

    public ClientModel(String playerName, ClientConnectionSocket<S> clientConnection) {
        super();

        reducedBoard = new ReducedAnswerCell[DIM][DIM];
        deck = new ArrayList<>();
        workers = new ArrayList<>();

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                reducedBoard[i][j] = new ReducedAnswerCell(i, j);
            }
        }

        player = new ReducedPlayer(playerName);
        player.setColor(Color.RESET);

        this.clientConnection = clientConnection;

        isReloaded = false;
    }

    /*------------------------------------------------------THREAD----------------------------------------------------*/
    private Thread asyncReadFromConnection() {
        Thread t = new Thread (
                () -> {
                    try {
                        while (isActive()) {
                            synchronized (clientConnection.lockAnswer) {
                                while (!clientConnection.isChanged()) clientConnection.lockAnswer.wait();
                            }

                            clientConnection.setChanged(false);
                            setAnswer(clientConnection.getFirstAnswer());

                            LOGGER.info("Receiving...");
                            synchronized (lockAnswer) {
                                updateModel();
                                setChanged(true);
                                lockAnswer.notifyAll();
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
    /*----------------------------------------------------------------------------------------------------------------*/



    /*-------------------------------------------------------UPDATE---------------------------------------------------*/
    private void updateModel() {
        Answer<S> answerTemp;

        synchronized (lockAnswer) {
            answerTemp = getAnswer();
        }

        if (!player.isCreator() && isInitializing)
            currentState = nextState;

        if (isNewGame)
            clearAll();

        switch (answerTemp.getHeader()) {
            case CHANGE_TURN:
                updateCurrentPlayer();
                additionalPowerUsed = true;
                break;

            case ERROR:
                break;

            case SUCCESS:
                updateCurrentState();
                if (isInitializing)
                    updateReducedObjectsInitialize(answerTemp);
                else
                    updateReduceObjects(answerTemp);
                updateNextState();
                break;

            case RELOAD:
                updateCurrentState();
                reloadGame();
                break;

            case VICTORY:
                updateCurrentState();
                currentState = DemandType.NEW_GAME;
                nextState = DemandType.START;
                isNewGame = true;
                isYourEnding = player.getNickname().equals(((ReducedPlayer) getAnswer().getPayload()).getNickname());
                updateNextState();
                break;

            case DEFEAT:
                updateCurrentState();
                String playerToRemove = ((ReducedPlayer) getAnswer().getPayload()).getNickname();
                if (!playerToRemove.equals(player.getNickname()))
                    opponents.removeIf(o -> o.getNickname().equals(playerToRemove));
                updateNextState();
                break;

            case CLOSE:
                break;

            default:
                throw new NotAValidInputRunTimeException("Not a valid answerType" + answerTemp.getHeader());
        }

        additionalPower();
    }

    private void additionalPower() {
        if (additionalPowerUsed || player.getCard() == null || !player.getCard().isAdditionalPower()) {
            if (currentState.equals(DemandType.MOVE)) //reset additionalPowerUsed
                additionalPowerUsed = false;

            return;
        }

        Effect effect = player.getCard().getEffect();
        if ((currentState.equals(DemandType.BUILD) && effect.equals(Effect.MOVE)) ||
                (currentState.equals(DemandType.CHOOSE_WORKER) && effect.equals(Effect.BUILD))) { //additionalMovePower || additionalBuildPower
            currentState = DemandType.ASK_ADDITIONAL_POWER;
            nextState = DemandType.ADDITIONAL_POWER;
            additionalPowerUsed = true;
        }
    }

    private void clearAll() {
        currentState = DemandType.CONNECT;
        isNewGame = false;
        isInitializing = true;
        isReloaded = false;
        currentPlayer = null;
        reducedBoard = new ReducedAnswerCell[DIM][DIM];

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                reducedBoard[i][j] = new ReducedAnswerCell(i, j, null);
            }
        }

        deck.clear();
        opponents.clear();
        workers.clear();
    }

    private void updateCurrentState() {
        if (isYourTurn()) {
            if (!isReloaded) {
                if (!currentState.equals(DemandType.ASK_ADDITIONAL_POWER))
                    prevState = currentState;

                if (nextState.ordinal() >= DemandType.USE_POWER.ordinal())
                    currentState = DemandType.CHOOSE_WORKER;
                else
                    currentState = nextState;
            }
            else
                isReloaded = false;
        }
    }

    private synchronized void updateNextState() {
        nextState = DemandType.getNextState(currentState, player.isCreator());
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
                player.setCreator(o.isCreator());
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

    private synchronized void updateReducedObjectsInitialize(Answer<S> answerTemp) {
        switch (currentState) {
            case CONNECT:
                player.setCreator(((ReducedPlayer) answerTemp.getPayload()).isCreator());

                if (player.isCreator()) {
                    currentState = DemandType.CREATE_GAME;
                    currentPlayer = player.getNickname();
                }
                break;

            case START:
                currentPlayer = ((List<ReducedPlayer>) answerTemp.getPayload()).get(0).getNickname(); //Hp: first one is the chosen one
                opponents = ((List<ReducedPlayer>) answerTemp.getPayload());

                for (ReducedPlayer o : opponents) {
                    if (o.isCreator())
                        currentPlayer = o.getNickname();

                    if (o.getNickname().equals(player.getNickname())) {
                        player.setColor(o.getColor());
                        player.setCreator(o.isCreator());
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

    private synchronized void updateReduceObjects(Answer<S> answer) {
        switch (answer.getContext()) {
            case GOD:
            case PLAYER:
            case CARD:
                List<ReducedCard> reducedCardList = ((List<ReducedCard>) answer.getPayload());

                if (reducedCardList == null || reducedCardList.isEmpty()) return; //safety check, cannot happen normally!
                if (deck.isEmpty() || deck.size() > opponents.size() + 1) { //happens only to the creator during chooseDeck
                    deck = reducedCardList;
                    return;
                }

                Set<God> gods = reducedCardList.stream() //creates a set of the chosen gods by the prev player (it must be only one)
                        .map(ReducedCard::getGod)
                        .collect(Collectors.toSet());

                List<ReducedCard> chosenList = deck.stream() //contains only the corresponding card of the god chosen by the previous player (it must be only one)
                        .filter(card -> !gods.contains(card.getGod()))
                        .collect(Collectors.toList());

                if (chosenList.size() > 1) return; //safety check, cannot happen normally!

                ReducedCard chosen;
                if (chosenList.isEmpty()) { //if the chosen card is not present in deck
                    chosen = reducedCardList.get(0); //then pick it form the answer
                    prevPlayer = currentPlayer;
                }
                else
                    chosen = chosenList.get(0); //picks the card chosen by the prev player

                ReducedPlayer current = opponents.stream() //finds prev player within the opponents
                        .filter(p -> p.getNickname().equals(prevPlayer))
                        .reduce(null, (a, b) -> a != null
                                ? a
                                : b
                        );

                if (current == null) //if the prev player is not an opponent, then it must be you!
                    current = player;

                current.setCard(chosen); //assigns to the prev player the card he chose
                deck.remove(chosen); //removes the chosen card from the deck
                break;

            case WORKER:
            case BOARD:
                List<ReducedAnswerCell> reducedAnswerCellList = (List<ReducedAnswerCell>) answer.getPayload();

                //resets board
                for (int i = 0; i < DIM; i++) {
                    for (int j = 0; j < DIM; j++) {
                        reducedBoard[i][j].resetAction();
                    }
                }

                if (reducedAnswerCellList.isEmpty()) return; //safety check, cannot happen normally!

                updateReducedBoard(reducedAnswerCellList);

                //resets and sets workers
                workers = new ArrayList<>();
                for (int i = 0; i < DIM; i++) {
                    for (int j = 0; j < DIM; j++) {
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
    /*----------------------------------------------------------------------------------------------------------------*/



    /*------------------------------------------------------GET-------------------------------------------------------*/
    public synchronized ReducedAnswerCell[][] getReducedBoard() {
        return reducedBoard;
    }

    public synchronized ReducedAnswerCell getCell(int x, int y) {
        if (checkCell(x, y)) return null;

        return reducedBoard[x][y];
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

    public synchronized DemandType getPrevState() {
        return prevState;
    }

    public synchronized DemandType getCurrentState() {
        return currentState;
    }

    public synchronized DemandType getNextState() {
        return nextState;
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
    /*----------------------------------------------------------------------------------------------------------------*/



    /*------------------------------------------------------SET-------------------------------------------------------*/
    public synchronized void setNextState(DemandType nextState) {
        this.nextState = nextState;
    }
    /*----------------------------------------------------------------------------------------------------------------*/



    /*---------------------------------------------------PREDICATE----------------------------------------------------*/
    public synchronized boolean isYourTurn() {
        if (currentPlayer == null) return false;

        return player.getNickname().equals(currentPlayer);
    }

    public synchronized boolean isCreator() {
        return player.isCreator();
    }

    public synchronized boolean IsEnded() {
        return isYourEnding;
    }

    public boolean isReloaded() {
        boolean ret;

        synchronized (lock) {
            ret = isReloaded;
        }

        return ret;
    }
    /*----------------------------------------------------------------------------------------------------------------*/



    /*-----------------------------------------------------SUPPORT----------------------------------------------------*/
    public boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
    }
    /*----------------------------------------------------------------------------------------------------------------*/
}
