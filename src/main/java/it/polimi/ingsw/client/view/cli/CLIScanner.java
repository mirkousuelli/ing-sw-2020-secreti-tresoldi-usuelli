package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that manages how the input is received from the command line interface
 */
public class CLIScanner<S> {

    private ClientModel<S> clientModel;
    private final BufferedReader in;
    private final CLIPrinter<S> out;

    private static final String CONNECT = "Insert your name:\n";
    private static final String CREATE_GAME = "Insert the number of players:\n";
    private static final String CHOOSE_DECK = "Insert the name of one the gods which will be used in this match: [godName]\n";
    private static final String CHOOSE_CARD = "Insert the name of the chosen god [godName]\n";
    private static final String CHOOSE_STARTER = "Insert the name of the starter: [playerName]\n";
    private static final String PLACE_WORKERS = "Insert the initial locations of your worker: [x,y]\n";
    private static final String CHOOSE_WORKERS = "Select a worker: [x,y]\n";
    private static final String ACTION = "Make your action [action x,y]\n";
    private static final String ASK_ADDITIONAL_POWER = "Do you want to use the additional power of your god? [y/n]\n";
    private static final String ADDITIONAL_POWER = "Select a cell: [action x,y]\n";
    private static final String NEW_GAME = "Do you want to play again? [y/n]\n";

    private final Map<DemandType, String> messageMap;
    private final Map<DemandType, Predicate<String>> toRepeatMap;
    private final Map<DemandType, IntPredicate> indexMap;
    private final Map<DemandType, Predicate<String>> toUsePowerMap;
    private final Map<DemandType, Function<String, S>> payloadMap;

    private static final Logger LOGGER = Logger.getLogger(CLIScanner.class.getName());

    /**
     * Constructor which initializes the hash maps used to determine if the data read from the command line is correct according the {@code ClientModel}'s current state
     *
     * @param out an instance of {@code CLIPrinter} to print to the command line when needed
     */
    CLIScanner(CLIPrinter<S> out) {
        in = new BufferedReader(new InputStreamReader(System.in));
        this.out = out;

        messageMap = new EnumMap<>(DemandType.class);
        toRepeatMap = new EnumMap<>(DemandType.class);
        indexMap = new EnumMap<>(DemandType.class);
        toUsePowerMap = new EnumMap<>(DemandType.class);
        payloadMap = new EnumMap<>(DemandType.class);
    }

    /**
     * Method that initializes maps accordingly with the type of demand made by the player
     */
    private void initializeMaps() {
        messageMap.put(DemandType.CONNECT, CONNECT);
        messageMap.put(DemandType.CREATE_GAME, CREATE_GAME);
        messageMap.put(DemandType.CHOOSE_DECK, CHOOSE_DECK);
        messageMap.put(DemandType.CHOOSE_CARD, CHOOSE_CARD);
        messageMap.put(DemandType.CHOOSE_STARTER, CHOOSE_STARTER);
        messageMap.put(DemandType.PLACE_WORKERS, PLACE_WORKERS);
        messageMap.put(DemandType.CHOOSE_WORKER, CHOOSE_WORKERS);
        messageMap.put(DemandType.MOVE, ACTION);
        messageMap.put(DemandType.BUILD, ACTION);
        messageMap.put(DemandType.USE_POWER, ACTION);
        messageMap.put(DemandType.ASK_ADDITIONAL_POWER, ASK_ADDITIONAL_POWER);
        messageMap.put(DemandType.ADDITIONAL_POWER, ADDITIONAL_POWER);
        messageMap.put(DemandType.NEW_GAME, NEW_GAME);

        toRepeatMap.put(DemandType.CREATE_GAME, index -> Integer.parseInt(index) < 2 || Integer.parseInt(index) > 3);
        toRepeatMap.put(DemandType.CHOOSE_DECK, this::checkGod);
        toRepeatMap.put(DemandType.CHOOSE_CARD, this::checkGod);
        toRepeatMap.put(DemandType.CHOOSE_STARTER, this::checkPlayer);
        toRepeatMap.put(DemandType.PLACE_WORKERS, this::checkCell);
        toRepeatMap.put(DemandType.CHOOSE_WORKER, this::checkWorker);
        toRepeatMap.put(DemandType.MOVE, this::isToRepeat);
        toRepeatMap.put(DemandType.BUILD, this::isToRepeat);
        toRepeatMap.put(DemandType.USE_POWER, this::isToRepeat);
        toRepeatMap.put(DemandType.ASK_ADDITIONAL_POWER, value -> !value.equals("y") && !value.equals("n"));
        toRepeatMap.put(DemandType.ADDITIONAL_POWER, this::isToRepeat);
        toRepeatMap.put(DemandType.NEW_GAME, value -> !value.equals("y") && !value.equals("n"));

        indexMap.put(DemandType.CHOOSE_DECK, index -> index < clientModel.getOpponents().size());
        indexMap.put(DemandType.PLACE_WORKERS, index -> index < 1);

        toUsePowerMap.put(DemandType.MOVE, this::isToUsePower);
        toUsePowerMap.put(DemandType.BUILD, this::isToUsePower);
        toUsePowerMap.put(DemandType.USE_POWER, this::isToUsePower);
        toUsePowerMap.put(DemandType.ADDITIONAL_POWER, this::isToUsePower);

        payloadMap.put(DemandType.CONNECT, this::parseString);
        payloadMap.put(DemandType.CREATE_GAME, this::parseString);
        payloadMap.put(DemandType.CHOOSE_DECK, this::parseStringGod);
        payloadMap.put(DemandType.CHOOSE_CARD, this::parseStringGod);
        payloadMap.put(DemandType.CHOOSE_STARTER, this::parseString);
        payloadMap.put(DemandType.PLACE_WORKERS, this::parseStringReducedWorker);
        payloadMap.put(DemandType.CHOOSE_WORKER, this::parseStringReducedDemandCell);
        payloadMap.put(DemandType.MOVE, this::parseCommand);
        payloadMap.put(DemandType.BUILD, this::parseCommand);
        payloadMap.put(DemandType.USE_POWER, this::parseCommand);
        payloadMap.put(DemandType.ASK_ADDITIONAL_POWER, this::parseString);
        payloadMap.put(DemandType.ADDITIONAL_POWER, this::parseCommand);
        payloadMap.put(DemandType.NEW_GAME, this::parseString);
    }

    void setClientModel(ClientModel<S> clientModel) {
        this.clientModel = clientModel;
        initializeMaps();
    }


    /*---------------------------------------------------READER-------------------------------------------------------*/

    /**
     * Creates a message to be sent to the server according to the user's requests
     *
     * @param currentState the {@code ClientModel}'s current state
     * @return the message to send to the server
     */
    Demand<S> requestInput(DemandType currentState) {
        boolean toRepeat;
        boolean toUsePower;
        int i = 0;

        List<S> payloadList = new ArrayList<>();
        S payload;

        String value;

        do {
            toUsePower = false;
            payload = null;

            out.printString(messageMap.get(currentState));

            value = readLine();
            if (value != null)
                toRepeat = repeat(currentState, value);
            else
                return null;

            if (toRepeat)
                out.printError(); //toRepeat because input values are wrong
            else {
                toUsePower = power(currentState, value);
                payload = payload(currentState, value);

                if (payload == null) {
                    out.printError();
                    toRepeat = true; //toRepeat because input values are wrong
                } else {
                    payloadList.add(payload);
                    toRepeat = index(currentState, i); //toRepeat because there are more values to read!
                    if (toRepeat)
                        i++;
                }
            }
        } while (toRepeat);

        skipAdditionalPower(value);
        skipToBuild();
        changeNickname(value);
        stayInCurrentState(currentState, toUsePower);

        return generateDemand(toUsePower, i, payload, payloadList, currentState);
    }

    /**
     * Reads the input from the command line interface
     *
     * @return the string read from the command line interface
     */
    String readLine() {
        String value;

        try {
            value = in.readLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException", e);
            value = null;
        }

        return value;
    }

    /**
     * Method that controls if the action is to repeat
     *
     * @param currentState the current state
     * @param value        the value to check
     * @return {@code true} if the action must be repeated, {@code false} if not
     */
    private boolean repeat(DemandType currentState, String value) {
        boolean toRepeat = false;
        Predicate<String> toRepeatFunction;

        toRepeatFunction = toRepeatMap.get(currentState);
        if (toRepeatFunction != null)
            toRepeat = toRepeatFunction.test(value);

        return toRepeat;
    }

    /**
     * Method that controls if the power can be used
     *
     * @param currentState the current state
     * @param value        the value to check
     * @return {@code true} if the power can be used, {@code false} if not
     */
    private boolean power(DemandType currentState, String value) {
        boolean toUsePower = false;
        Predicate<String> powerFunction;

        powerFunction = toUsePowerMap.get(currentState);
        if (powerFunction != null)
            toUsePower = powerFunction.test(value);

        return toUsePower;
    }

    /**
     * Method that returns the payload for the action
     *
     * @param currentState the current state
     * @param value        the value to check
     * @return the payload for the action
     */
    private S payload(DemandType currentState, String value) {
        S payload = null;
        Function<String, S> payloadFunction;

        payloadFunction = payloadMap.get(currentState);
        if (payloadFunction != null)
            payload = payloadMap.get(currentState).apply(value);

        return payload;
    }

    /**
     * Method that controls if the given index is in the index map
     *
     * @param currentState the current state
     * @param i            the index to check
     * @return {@code true} if the index is in the index map, {@code false} if not
     */
    private boolean index(DemandType currentState, int i) {
        boolean toIncrementIndex = false;
        IntPredicate indexFunction;

        indexFunction = indexMap.get(currentState);
        if (indexFunction != null)
            toIncrementIndex = indexMap.get(currentState).test(i);

        return toIncrementIndex;
    }

    /**
     * Method that generates the demand given the following information
     *
     * @param toUsePower   tells if the power can be used
     * @param i            the index to check
     * @param payload      the payload
     * @param payloadList  the list of payloads
     * @param currentState the current state
     * @return the demand that is generated
     */
    private Demand<S> generateDemand(boolean toUsePower, int i, S payload, List<S> payloadList, DemandType currentState) {
        if (toUsePower)
            return new Demand<>(DemandType.USE_POWER, payload);
        else if (i > 0)
            return new Demand<>(currentState, (S) payloadList);
        else
            return new Demand<>(currentState, payload);
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    /*---------------------------------------------------PARSER-------------------------------------------------------*/
    private S parseString(String string) {
        return (S) (new ReducedMessage(string));
    }

    private S parseStringReducedDemandCell(String string) {
        ReducedAnswerCell cell = getReducedCell(string);

        if (cell == null) return null;

        return (S) (new ReducedDemandCell(cell.getX(), cell.getY()));
    }

    private S parseStringReducedWorker(String string) {
        ReducedAnswerCell cell = getReducedCell(string);

        if (cell == null) return null;

        return (S) (new ReducedWorker(clientModel.getPlayer().getNickname(), cell.getX(), cell.getY(), false));
    }

    private S parseCommand(String string) {
        String[] input = string.split(" ");

        if (input.length != 2) return null;

        return parseStringReducedDemandCell(input[1]);
    }

    private S parseStringGod(String string) {
        return (S) (God.parseString(string));
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    /*---------------------------------------------------CHECKER------------------------------------------------------*/

    /**
     * Method that controls if the given God's name is present in the deck
     *
     * @param godString the name of the God to check
     * @return {@code false} if the God is present in the deck, {@code true} otherwise
     */
    private boolean checkGod(String godString) {
        List<ReducedCard> deck;
        God god = God.parseString(godString);
        if (god == null) return true;

        synchronized (clientModel.lock) {
            deck = clientModel.getDeck();
        }

        return deck.stream()
                .noneMatch(g -> g.getGod().equals(god));
    }

    /**
     * Method that controls if the given worker's name is present in a cell of the board
     *
     * @param workerString the name of the worker to check
     * @return {@code false} if the worker exists, {@code true} otherwise
     */
    private boolean checkWorker(String workerString) {
        ReducedAnswerCell workerCell = getReducedCell(workerString);

        if (workerCell == null) return true;

        return clientModel.getWorkers().stream()
                .filter(w -> w.getOwner().equals(clientModel.getPlayer().getNickname()))
                .noneMatch(w -> w.getX() == workerCell.getX() && w.getY() == workerCell.getY());
    }

    /**
     * Method that controls if the given player's name is present in the game (as opponent)
     *
     * @param player the name of the player to check
     * @return {@code false} if the player is an opponent in the game, {@code true} otherwise
     */
    private boolean checkPlayer(String player) {
        for (ReducedPlayer p : clientModel.getOpponents()) {
            if (p.getNickname().equals(player))
                return false;
        }

        return !clientModel.getPlayer().getNickname().equals(player);
    }

    /**
     * Method that controls if the given cell is present in the game (which means it is not null)
     *
     * @param cellString the name of the cell to check
     * @return {@code false} if the cell is not null, {@code true} otherwise
     */
    private boolean checkCell(String cellString) {
        ReducedAnswerCell cell = getReducedCell(cellString);

        return cell == null;
    }

    /**
     * Method that tells if the player has to repeat the insertion of the action: this can happen if he writes a wrong
     * command, like by not inserting the cell
     *
     * @param string the string inserted
     * @return {@code true} if the user has to re-insert the action to make, {@code false} otherwise
     */
    private boolean isToRepeat(String string) {
        String[] input = string.split(" ");

        if (input.length != 2) return true;

        ReducedAnswerCell cell = getReducedCell(input[1]);

        if (cell == null) return true;

        for (ReducedAction ra : cell.getActionList()) {
            if (input[0].equals(ra.getName())) {
                switch (ra) {
                    case BUILD:
                    case MOVE:
                        return !cell.isFree();

                    case USEPOWER:
                        return false;

                    default:
                        return true;
                }
            }
        }

        return true;
    }

    /**
     * Method that tells if the player has to use his God power
     *
     * @param string the string inserted
     * @return {@code true} if the user has to insert the power and the cell where to use it (for example if he
     *  previously inserted a cell that isn't in the possible actions), {@code false} otherwise
     */
    private boolean isToUsePower(String string) {
        String[] input = string.split(" ");

        if (input.length != 2) return true;

        ReducedAnswerCell cell = getReducedCell(input[1]);

        if (cell == null) return true;

        return cell.getActionList().contains(ReducedAction.USEPOWER) && ReducedAction.USEPOWER.getName().equals(input[0]);
    }

    private ReducedAnswerCell getReducedCell(String cellString) {
        List<Integer> coordinate = stringToInt(cellString);

        if (coordinate.isEmpty()) return null;

        int x = coordinate.get(0);
        int y = coordinate.get(1);

        return clientModel.getCell(x, y);
    }

    private List<Integer> stringToInt(String string) {
        if (string.length() != 3) return new ArrayList<>();

        List<Integer> ret = new ArrayList<>();

        String[] input = string.split(",");

        if (input.length != 2) return new ArrayList<>();
        if (Arrays.stream(input).anyMatch(i -> i.length() != 1)) return new ArrayList<>();

        ret.add(string.charAt(0) - 48);
        ret.add(string.charAt(2) - 48);

        if (clientModel.checkCell(ret.get(0), ret.get(1))) return new ArrayList<>();

        return ret;
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    /**
     * Method that skips the additional power and proceed to the correct state: if the previous one was {@code move}
     * then it goes to {@code build}, if it was {@code build} then it goes to {@code choose_worker}
     *
     * @param value the string inserted by the user
     */
    private void skipAdditionalPower(String value) {
        if (!(clientModel.getCurrentState().equals(DemandType.ASK_ADDITIONAL_POWER))) return;
        if (!value.equals("n")) return;

        if (clientModel.getPrevState().equals(DemandType.MOVE))
            clientModel.setNextState(DemandType.BUILD);
        else if (clientModel.getPrevState().equals(DemandType.BUILD))
            clientModel.setNextState(DemandType.CHOOSE_WORKER);
    }

    /**
     * Method that skips to {@code build} if the previous state was {@code move}
     */
    private void skipToBuild() {
        if (!clientModel.getCurrentState().equals(DemandType.ADDITIONAL_POWER)) return;

        if (clientModel.getPrevState().equals(DemandType.MOVE))
            clientModel.setNextState(DemandType.BUILD);
    }

    /**
     * Method that sets the nickname of the player to the given one
     *
     * @param value the nickname to give to the player
     */
    private void changeNickname(String value) {
        if (!clientModel.getCurrentState().equals(DemandType.CONNECT)) return;

        clientModel.getPlayer().setNickname(value);
    }

    /**
     * Method that allows the player to stay in the current state
     *
     * @param currentState the current state
     * @param toUsePower   tells if there is a power to use
     */
    private void stayInCurrentState(DemandType currentState, boolean toUsePower) {
        if (!currentState.equals(DemandType.MOVE) && !currentState.equals(DemandType.BUILD) && !currentState.equals(DemandType.ADDITIONAL_POWER))
            return;
        if (!toUsePower) return;

        int numOfAdditional = clientModel.getNumberOfAdditional();

        if (numOfAdditional != 0) {
            if (numOfAdditional > 0)
                clientModel.setNumberOfAdditional(numOfAdditional - 1);

            if (numOfAdditional != 1)
                clientModel.setNextState(clientModel.getCurrentState());

            clientModel.setAdditionalPowerUsed(numOfAdditional == 1);
        }
    }
}
