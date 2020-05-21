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
 * Class that represents a game and contains all the information useful for it.
 * It uses a state pattern, allowing the game to work according to the current state
 */
public class Game extends Observable<Answer> {

    private List<Player> players;
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
     * @param god God that is removed
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
        /* @getter
         * it gets the board for the game
         */
        return board;
    }

    public void setBoard(Board newBoard) {
        this.board = newBoard;
    }

    public Deck getDeck() {
        /* @getter
         * it gets the deck of cards in use
         */
        return deck;
    }

    public void setDeck(Deck newDeck) {
        this.deck = newDeck;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        /* @setter
         * it sets the current player to the designated one
         */
        this.currentPlayer = this.players.indexOf(currentPlayer);
    }

    public Player getCurrentPlayer() {
        /* @getter
         * it gets the current player
         */
        return this.players.get(this.currentPlayer);
    }

    public GameState getState() {
        /* @getter
         * it gets the current state of the game
         */
        return state;
    }

    public void setState(GameState state) {
        /* @setter
         * it sets the current state to the designated one
         */
        this.state = state;
    }

    public State getPrevState() {
        return prevState;
    }

    public void setState(State state) {
        prevState = State.parseString(this.state.getName());

        this.state = parseState(state);
    }

    public GameState parseState(State st) {
        GameState state;

        switch (st) {
            case START:
                state = new Start(this);
                break;
            case CHOOSE_CARD:
                state = new ChooseCard(this);
                break;
            case CHOOSE_STARTER:
                state = new ChooseStarter(this);
                break;
            case PLACE_WORKERS:
                state = new PlaceWorkers(this);
                break;
            case CHOOSE_WORKER:
                state = new ChooseWorker(this);
                break;
            case MOVE:
                state = new Move(this);
                break;
            case BUILD:
                state = new Build(this);
                break;
            case ADDITIONAL_POWER:
                state = new AdditionalPower(this);
                break;
            case CHANGE_TURN:
                state = new ChangeTurn(this);
                break;
            case VICTORY:
                state = new Victory(this);
                break;
            default:
                state = null;
                break;
        }

        return state;
    }

    public List<Player> getOpponents() {
        return players.stream().filter(p -> !getCurrentPlayer().equals(p)).collect(Collectors.toList());
    }

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

    public void removePlayer(String player) {
        players.removeIf(p -> p.nickName.equals(player));
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     *     In here it is used to change the current player and to control when the list of Gods is available, then
     *     proceeding to notify the players of these changes
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
                    notify(new Answer(AnswerType.CHANGE_TURN, rc.getPayload()));
            }

            notify(new Answer(returnContent.getAnswerType(), UpdatedPartType.parseString(returnContent.getState().toString()), returnContent.getPayload()));
        }

        if (rc != null && rc.getAnswerType().equals(AnswerType.ERROR))
            return rc;
        else
            return returnContent;
    }
}