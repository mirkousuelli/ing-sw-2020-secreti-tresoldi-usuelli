package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
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

public class Game extends Observable<Answer> {
    /* @class
     * it contains all the information useful for the game that is being played
     */
    private List<Player> players;
    private Deck deck;
    private List<God> chosenGods;
    private Board board;
    private GameState state;
    private State prevState;
    private int currentPlayer;
    private int numPlayers;
    private ActionToPerform request;
    private int starter;

    public Game() throws ParserConfigurationException, SAXException {
        /* @constructor
         * it creates a new game, initialising its state to start
         */
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

    public List<God> getChosenGods() {
        return chosenGods;
    }

    public void setChosenGods(List<God> choosenGods) {
        this.chosenGods = choosenGods;
    }

    public void removeGod(God god) {
        chosenGods.remove(god);
    }

    public void addGod(God god) {
        chosenGods.add(god);
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

    public void addPlayer(String nickname) {
        this.players.add(new Player(nickname));
        numPlayers++;
    }

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

        switch (state) {
            case START:
                this.state = new Start(this);
                break;
            case CHOOSE_CARD:
                this.state = new ChooseCard(this);
                break;
            case CHOOSE_STARTER:
                this.state = new ChooseStarter(this);
                break;
            case PLACE_WORKERS:
                this.state = new PlaceWorkers(this);
                break;
            case CHOOSE_WORKER:
                this.state = new ChooseWorker(this);
                break;
            case MOVE:
                this.state = new Move(this);
                break;
            case BUILD:
                this.state = new Build(this);
                break;
            case ADDITIONAL_POWER:
                this.state = new AdditionalPower(this);
                break;
            case CHANGE_TURN:
                this.state = new ChangeTurn(this);
                break;
            case VICTORY:
                this.state = new Victory(this);
                break;
            default:
                break;
        }
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

    public ReturnContent gameEngine() {
        ReturnContent returnContent = state.gameEngine();
        boolean availableGods = returnContent.isAvailableGods();

        if (!returnContent.getAnswerType().equals(AnswerType.ERROR)) {
            if (returnContent.isChangeTurn()) {
                setState(new ChangeTurn(this));
                ReturnContent rc = state.gameEngine();

                notify(new Answer(rc.getAnswerType(), DemandType.CHANGE_TURN, rc.getPayload()));
            }

            if (availableGods)
                notify(new Answer(AnswerType.SUCCESS, DemandType.AVAILABLE_GODS, deck.popChosenGods(chosenGods)));

            notify(new Answer(returnContent.getAnswerType(), DemandType.parseString(returnContent.getState().toString()), returnContent.getPayload()));
        }

        return returnContent;
    }
}