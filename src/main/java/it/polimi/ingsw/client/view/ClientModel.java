package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Reduced version of the Model-View-Controller's Model. It contains only the information needed to present the model to the user.
 */
public class ClientModel<S> extends SantoriniRunnable<S> {

    private final ClientConnectionSocket<S> clientConnection;
    private ReducedAnswerCell[][] reducedBoard;
    private List<ReducedCard> deck;
    private List<ReducedPlayer> opponents;
    private List<ReducedWorker> workers;
    private String currentPlayer;
    private String prevPlayer = null;
    private final ReducedPlayer player;
    private int numberOfAdditional = 0;

    private boolean isInitializing = true;
    private boolean isReloaded;
    private boolean isNewGame = false;
    private boolean isYourEnding = false;
    private boolean additionalPowerUsed = true;

    private DemandType nextState = DemandType.CONNECT;
    private DemandType currentState = DemandType.CONNECT;
    private DemandType prevState = DemandType.CONNECT;

    private static final int DIM = 5;

    /**
     * Constructor which initializes the client model by setting its attributes to an initial state.
     *
     * @param playerName       player's name
     * @param clientConnection the middleman between the player and the server
     */
    ClientModel(String playerName, ClientConnectionSocket<S> clientConnection) {
        super();

        reducedBoard = new ReducedAnswerCell[DIM][DIM];
        deck = new ArrayList<>();
        workers = new ArrayList<>();
        opponents = new ArrayList<>();

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

    /**
     * Method that defines an asynchronous read from the connection
     *
     * @return the thread that is reading
     */
    private Thread asyncReadFromConnection() {
        Thread t = new Thread(
                () -> {
                    try {
                        Answer<S> answerTemp;
                        while (isActive()) {
                            synchronized (clientConnection.lockAnswer) {
                                while (!clientConnection.isChanged()) clientConnection.lockAnswer.wait();
                                clientConnection.setChanged(false);
                            }

                            synchronized (clientConnection.lockAnswer) {
                                while (!clientConnection.hasAnswer()) clientConnection.lockAnswer.wait();
                                clientConnection.setChanged(false);
                            }

                            answerTemp = clientConnection.getFirstAnswer();
                            setAnswer(answerTemp);

                            synchronized (lockAnswer) {
                                updateModel();
                                setChanged(true);
                                lockAnswer.notifyAll();
                            }
                        }
                    } catch (InterruptedException e) {
                        if (isActive())
                            LOGGER.log(Level.SEVERE, "Got an unexpected InterruptedException, asyncReadFromConnection not working", e);
                        Thread.currentThread().interrupt();
                        setActive(false);
                    }
                }
        );
        t.start();
        return t;
    }

    /**
     * Executes a thread which keeps the client model updated. It fetches all the answers sent by the server from {@code ClientConnectionSocket} and updates consequently the client model
     */
    @Override
    protected void startThreads() throws InterruptedException {
        Thread read = asyncReadFromConnection();

        read.join();
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    /*-------------------------------------------------------UPDATE---------------------------------------------------*/

    /**
     * Updates the client model. The update varies according to the answer received
     */
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
                changeTurn();
                break;

            case ERROR:
                break;

            case CLOSE:
                clearAll();
                break;

            case SUCCESS:
                updateCurrentState();
                checkIsCreator(answerTemp);
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
                defeat();
                break;

            default:
                LOGGER.info("Not a valid answerType " + answerTemp.getHeader());
                break;
        }

        additionalPower();
    }

    /**
     * Method that allows the player to use his God power when it is an additional move/build
     */
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

    /**
     * Method that resets all attributes of this class to default values
     */
    private void clearAll() {
        prevState = DemandType.CONNECT;
        currentState = DemandType.NEW_GAME;
        nextState = DemandType.CONNECT;
        isNewGame = false;
        isInitializing = true;
        isReloaded = false;
        currentPlayer = null;

        Arrays.stream(reducedBoard)
                .flatMap(Arrays::stream)
                .forEach(ReducedAnswerCell::clear);

        deck.clear();
        opponents.clear();
        workers.clear();
    }

    /**
     * Method that, during the user's turn, updates the current state
     */
    private void updateCurrentState() {
        if (isYourTurn()) {
            if (!isReloaded) {
                if (!currentState.equals(DemandType.ASK_ADDITIONAL_POWER))
                    prevState = currentState;

                if (nextState.ordinal() >= DemandType.USE_POWER.ordinal())
                    currentState = DemandType.CHOOSE_WORKER;
                else
                    currentState = nextState;
            } else
                isReloaded = false;
        } else if (currentState.equals(DemandType.NEW_GAME))
            currentState = DemandType.CONNECT;
    }

    /**
     * Method that update the state with the next one
     */
    private synchronized void updateNextState() {
        nextState = DemandType.getNextState(currentState, player.isCreator());
    }

    /**
     * Method that permit the game to be reloaded
     */
    private synchronized void reloadGame() {
        ReducedGame reducedGame = ((ReducedGame) getAnswer().getPayload());

        reducedBoard = reducedGame.getReducedBoard();
        opponents = reducedGame.getReducedPlayerList();
        currentPlayer = reducedGame.getCurrentPlayerIndex();
        prevPlayer = currentPlayer;
        workers = reducedGame.getReducedWorkerList();
        isInitializing = false;

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

        if (isYourTurn()) {
            currentState = reducedGame.getCurrentState();
            if (currentState.equals(DemandType.ASK_ADDITIONAL_POWER))
                prevState = DemandType.parseString(player.getCard().getEffect().toString().toLowerCase());
        } else
            currentState = DemandType.CHOOSE_WORKER;

        isReloaded = true;
    }

    /**
     * Method that removes from the game the player that lost. If in the game there are still two players the game can
     * continue with just these players
     */
    private synchronized void defeat() {
        String playerToRemove = ((ReducedPlayer) getAnswer().getPayload()).getNickname();
        if (playerToRemove.equals(player.getNickname())) //if it is your defeat
            isYourEnding = true; //then it is your ending
        else { //else remove the defeated player and continue the game
            opponents.removeIf(o -> o.getNickname().equals(playerToRemove));
            workers.removeIf(reducedWorker -> reducedWorker.getOwner().equals(playerToRemove));
        }

        if (isYourTurn()) {
            currentState = DemandType.CHOOSE_WORKER;
            updateNextState();
        }
    }

    /**
     * Method that changes the current player and sets the next state to {@code choose_worker}
     */
    private synchronized void changeTurn() {
        updateCurrentPlayer();
        additionalPowerUsed = true;
        numberOfAdditional = player.getCard() != null
                ? player.getCard().getNumberOfAdditional()
                : 0;

        if (isYourTurn() && !isInitializing && currentState.ordinal() > DemandType.MOVE.ordinal())
            nextState = DemandType.CHOOSE_WORKER;
    }

    /**
     * Method that updates the current player when the turn is changed
     */
    private synchronized void updateCurrentPlayer() {
        if (!currentPlayer.equals(((ReducedPlayer) getAnswer().getPayload()).getNickname())) {
            prevPlayer = currentPlayer;
            currentPlayer = ((ReducedPlayer) getAnswer().getPayload()).getNickname();
        }
    }

    /**
     * Method that updates the objects passed as parameter
     *
     * @param answerTemp the objects to update
     */
    private synchronized void updateReduceObjects(Answer<S> answerTemp) {
        if (isInitializing)
            updateReducedObjectsInitialize(answerTemp);
        else
            updateReduceObjectsInGame(answerTemp);
    }

    /**
     * Method that updates the objects at the beginning of the game, after the connection or the start of the game
     *
     * @param answerTemp the objects to update
     */
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
                if (answerTemp.getContext() != null && answerTemp.getContext().equals(UpdatedPartType.PLAYER)) {
                    player.setCreator(((ReducedPlayer) answerTemp.getPayload()).isCreator());

                    if (player.isCreator()) {
                        currentState = DemandType.CREATE_GAME;
                        currentPlayer = player.getNickname();
                    }
                    return;
                }

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
                LOGGER.info("Not a valid answerType " + answerTemp.getHeader());
                break;
        }
    }

    /**
     * Method that updates the objects during the game, controlling the type of the objects to update
     *
     * @param answer the objects to update
     */
    private synchronized void updateReduceObjectsInGame(Answer<S> answer) {
        switch (answer.getContext()) {
            case GOD:
            case PLAYER:
            case CARD:
                List<ReducedCard> reducedCardList = ((List<ReducedCard>) answer.getPayload());

                if (reducedCardList == null || reducedCardList.isEmpty())
                    return; //safety check, cannot happen normally!
                if (deck.isEmpty() || deck.size() > opponents.size() + 1) { //happens only to the creator during chooseDeck
                    deck = reducedCardList;
                    return;
                }

                ReducedCard chosen;
                if (deck.size() == 1) //if the chosen card is not present in deck
                    prevPlayer = currentPlayer;

                chosen = reducedCardList.get(0); //picks the card chosen by the prev player
                ReducedPlayer current = opponents.stream() //finds prev player within the opponents
                        .filter(p -> p.getNickname().equals(prevPlayer))
                        .reduce(player, (a, b) -> a != player //accumulator -> if the prev player is not an opponent, then it must be you!
                                ? a
                                : b
                        );

                current.setCard(chosen); //assigns to the prev player the card he chose
                deck.removeIf(card -> card.getGod().equals(chosen.getGod())); //removes the chosen card from the deck
                break;

            case WORKER:
            case BOARD:
                List<ReducedAnswerCell> reducedAnswerCellList = (List<ReducedAnswerCell>) answer.getPayload();

                //resets board
                Arrays.stream(reducedBoard)
                        .flatMap(Arrays::stream)
                        .forEach(ReducedAnswerCell::resetAction);

                if (reducedAnswerCellList.isEmpty()) return; //safety check, cannot happen normally!

                updateReducedBoard(reducedAnswerCellList);

                //resets and sets workers
                workers = new ArrayList<>();
                Arrays.stream(reducedBoard)
                        .flatMap(Arrays::stream)
                        .filter(reducedAnswerCell -> !reducedAnswerCell.isFree())
                        .map(ReducedAnswerCell::getWorker)
                        .forEach(workers::add);
                break;

            default:
                LOGGER.info("Not a valid answer context " + answer.getContext());
                break;
        }
    }

    /**
     * Method that updates the board with the given cells, that represents the one that have changed
     *
     * @param cells the list of cells that are updated
     */
    private synchronized void updateReducedBoard(List<ReducedAnswerCell> cells) {
        for (ReducedAnswerCell c : cells) {
            reducedBoard[c.getX()][c.getY()] = c;
        }
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    /*------------------------------------------------------GET-------------------------------------------------------*/
    public synchronized ReducedAnswerCell[][] getReducedBoard() {
        ReducedAnswerCell[][] reducedAnswerCellsCopy = new ReducedAnswerCell[DIM][DIM];

        Arrays.stream(reducedBoard).forEach(reducedBoardRow -> System.arraycopy(reducedBoardRow, 0, reducedAnswerCellsCopy[reducedBoardRow[0].getX()], 0, DIM));
        return reducedAnswerCellsCopy;
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
        return getPlayer(currentPlayer);
    }

    public synchronized ReducedPlayer getPlayer(String name) {
        if (player.getNickname().equals(name)) return player;

        for (ReducedPlayer o : opponents) {
            if (o.getNickname().equals(currentPlayer))
                return o;
        }

        return null;
    }

    public synchronized String getPrevPlayer() {
        return prevPlayer;
    }

    public synchronized int getNumberOfAdditional() {
        return numberOfAdditional;
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    /*------------------------------------------------------SET-------------------------------------------------------*/
    public synchronized void setPrevState(DemandType prevState) {
        this.prevState = prevState;
    }

    public synchronized void setNextState(DemandType nextState) {
        this.nextState = nextState;
    }

    public synchronized void setAdditionalPowerUsed(boolean additionalPowerUsed) {
        this.additionalPowerUsed = additionalPowerUsed;
    }

    public synchronized void setNumberOfAdditional(int numberOfAdditional) {
        this.numberOfAdditional = numberOfAdditional;
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

    public synchronized boolean isEnded() {
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

    /**
     * Verifies if the given cell is within the board
     *
     * @param x cell's row
     * @param y cell's column
     * @return {@code true} if the coordinates belong to a cell on the board (which means that both coordinates must be
     * between 0 and 5), {@code false} otherwise
     */
    public boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
    }

    /**
     * Verifies if the player is chosen to be the creator
     *
     * @param answer The answer sent by the server
     */
    private synchronized void checkIsCreator(Answer<S> answer) {
        if (isInitializing && answer.getContext() != null && answer.getContext().equals(UpdatedPartType.PLAYER) && currentState.equals(DemandType.START)) {
            player.setCreator(true);
            nextState = DemandType.CREATE_GAME;
            currentPlayer = player.getNickname();
        }
    }
    /*----------------------------------------------------------------------------------------------------------------*/
}
