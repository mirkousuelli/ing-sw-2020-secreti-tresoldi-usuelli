package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.states.*;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Level;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents a game and contains all the information useful for it: the list of players playing the match,
 * the deck that is being used, a list of the chosen Gods, the board,the current (and previous) state, the current
 * player, the number of players in the math, the request that is made and the starter of the game (who is picked later
 * by the Challenger)
 * <p>
 * It uses a state pattern, allowing the game to work according to the current state
 */
public class Game extends Observable<Answer> {

    private final List<Player> players;
    private Deck deck;
    private List<Card> chosenGods;
    private Board board;
    private GameState state;
    private State prevState;
    private int currentPlayer;
    private int numPlayers;
    private ActionToPerform request;
    private int starter;

    /**
     * Constructor of the player, initializing his elements
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public Game() throws ParserConfigurationException, SAXException {

        this.deck = new Deck();
        this.chosenGods = new ArrayList<>();
        this.board = new Board();
        this.currentPlayer = 0;
        this.players = new ArrayList<>();
        this.state = new Start(this);
        prevState = null;

        starter = -1;
    }

    public int getStarter() {
        return starter;
    }

    public void setStarter(int starter) {
        this.starter = starter;
    }

    public List<Card> getChosenGods() {
        return chosenGods;
    }

    public void setChosenGods(List<God> chosenGods) {
        this.chosenGods = chosenGods.stream().map(deck::getCard).collect(Collectors.toList());
    }

    /**
     * Method that removes the God from the list of chosen Gods for the game
     *
     * @param god the God that is removed
     */
    public void removeGod(Card god) {
        chosenGods.remove(god);
    }

    public ActionToPerform getRequest() {
        return request;
    }

    public void setRequest(ActionToPerform request) {
        this.request = request;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return this.numPlayers;
    }

    /**
     * Method that adds the player to the list of players for the game
     *
     * @param nickname the nickname of the player that is added
     */
    public void addPlayer(String nickname) {
        this.players.add(new Player(nickname));
        numPlayers++;
    }

    /**
     * Method that adds the player to the list of players for the game
     *
     * @param player the player that is added
     */
    public void addPlayer(Player player) {
        this.players.add(player);
        numPlayers++;
    }

    public Player getPlayer(String nickname) {
        for (Player p : this.players) {
            if (p.getNickName().equals(nickname))
                return p;
        }
        return null;
    }

    public Player getPlayer(int index) {
        if (index >= 0 && index < numPlayers)
            return this.players.get(index);
        return null;
    }

    public List<Player> getPlayerList() {
        return new ArrayList<>(players);
    }

    public int getIndex(Player player) {
        return this.players.indexOf(player);
    }

    /**
     * Method that assigns the God to the current player
     *
     * @param god the God that is given to the current player
     */
    public void assignCard(God god) {
        this.deck.fetchCard(god);
        this.players.get(this.currentPlayer).setCard(this.deck.popCard(god));
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board newBoard) {
        this.board = newBoard;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck newDeck) {
        this.deck = newDeck;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = this.players.indexOf(currentPlayer);
    }

    public Player getCurrentPlayer() {
        return this.players.get(this.currentPlayer);
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public State getPrevState() {
        return prevState;
    }

    public void setPrevState(State prevState) {
        this.prevState = prevState;
    }

    public void setState(State state) {
        if (!this.state.getName().equals(State.ASK_ADDITIONAL_POWER.toString()))
            prevState = State.parseString(this.state.getName());

        this.state = parseState(state);
    }

    public GameState parseState(State st) {
        GameState s;

        switch (st) {
            case START:
                s = new Start(this);
                break;
            case CHOOSE_CARD:
                s = new ChooseCard(this);
                break;
            case CHOOSE_STARTER:
                s = new ChooseStarter(this);
                break;
            case PLACE_WORKERS:
                s = new PlaceWorkers(this);
                break;
            case CHOOSE_WORKER:
                s = new ChooseWorker(this);
                break;
            case MOVE:
                s = new Move(this);
                break;
            case BUILD:
                s = new Build(this);
                break;
            case ASK_ADDITIONAL_POWER:
                s = new AskAdditionalPower(this);
                break;
            case ADDITIONAL_POWER:
                s = new AdditionalPower(this);
                break;
            case CHANGE_TURN:
                s = new ChangeTurn(this);
                break;
            case VICTORY:
                s = new Victory(this);
                break;
            default:
                s = null;
                break;
        }

        return s;
    }

    /**
     * Method that gets the list of opponents
     *
     * @return the list of opponents players
     */
    public List<Player> getOpponents() {
        return players.stream()
                .filter(p -> !getCurrentPlayer().equals(p))
                .collect(Collectors.toList());
    }

    /**
     * Method that counts the number of complete towers on the board: this is used in order to control when Chronus' win
     * condition is verified
     *
     * @return the number of complete towers on the board
     */
    public int getNumberOfCompleteTower() {
        Cell c;
        int numberOfCompleteTowers = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                c = board.getCell(i, j);
                if (c.isFree() && c.getLevel().equals(Level.DOME) && ((Block) c).getPreviousLevel().equals(Level.TOP))
                    numberOfCompleteTowers++;
            }
        }

        return numberOfCompleteTowers;
    }

    /**
     * Method that removes the select player from the list of players in the game
     *
     * @param player the nickname of the player to remove
     */
    public void removePlayer(String player) {
        if (players.removeIf(p -> p.nickName.equals(player)) && currentPlayer > players.size() - 1)
            currentPlayer--;
    }

    /**
     * Method that resets the game: it cleans the board, removes the list of chosen Gods and the list of players
     * and setting the previous state to start
     */
    public void clean() {
        board.clean();
        chosenGods.clear();
        prevState = State.START;
        parseState(State.START);
        currentPlayer = 0;
        starter = -1;
        players.forEach(Player::reset);
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here it is used to change the current player and to control when the list of Gods is available, then
     * proceeding to notify the players of these changes
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    public ReturnContent gameEngine() {
        ReturnContent returnContent = state.gameEngine();
        ReturnContent rc = null;

        if (!returnContent.getAnswerType().equals(AnswerType.ERROR)) {
            if (returnContent.isChangeTurn()) {
                setState(new ChangeTurn(this));
                 rc = state.gameEngine();

                if (!rc.getAnswerType().equals(AnswerType.ERROR))
                    notify(new Answer<>(AnswerType.CHANGE_TURN, rc.getPayload()));
            }

            if (returnContent.getPayload() != null)
                notify(new Answer<>(returnContent.getAnswerType(), UpdatedPartType.parseString(returnContent.getState().toString()), returnContent.getPayload()));
        }

        if (rc != null && rc.getAnswerType().equals(AnswerType.ERROR))
            return rc;
        else
            return returnContent;
    }
}