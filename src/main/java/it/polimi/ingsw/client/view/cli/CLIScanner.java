package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final Map<DemandType, Function<String, Boolean>> toRepeatMap;
    private final Map<DemandType, Function<Integer, Boolean>> indexMap;
    private final Map<DemandType, Function<String, Boolean>> toUsePowerMap;
    private final Map<DemandType, Function<String, S>> payloadMap;

    private static final Logger LOGGER = Logger.getLogger(CLIScanner.class.getName());

    CLIScanner(InputStream inputStream, CLIPrinter<S> out, ClientModel<S> clientModel) {
        in = new BufferedReader(new InputStreamReader(inputStream));
        this.out = out;
        this.clientModel = clientModel;


        messageMap = new EnumMap<>(DemandType.class);
        toRepeatMap = new EnumMap<>(DemandType.class);
        indexMap = new EnumMap<>(DemandType.class);
        toUsePowerMap = new EnumMap<>(DemandType.class);
        payloadMap = new EnumMap<>(DemandType.class);

        if (clientModel != null)
            initializeMaps();
    }

    void initializeMaps() {
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
        toRepeatMap.put(DemandType.NEW_GAME, value -> !value.equals("y") && !value.equals("n"));
        toRepeatMap.put(DemandType.ASK_ADDITIONAL_POWER, value -> !value.equals("y") && !value.equals("n"));

        indexMap.put(DemandType.CHOOSE_DECK, index -> index < clientModel.getOpponents().size());
        indexMap.put(DemandType.PLACE_WORKERS, index -> index < 1);

        toUsePowerMap.put(DemandType.MOVE, this::isToUsePower);
        toUsePowerMap.put(DemandType.BUILD, this::isToUsePower);
        toUsePowerMap.put(DemandType.USE_POWER, this::isToUsePower);

        payloadMap.put(DemandType.CONNECT, this::parseString);
        payloadMap.put(DemandType.CREATE_GAME, this::parseString);
        payloadMap.put(DemandType.CHOOSE_DECK, this::parseStringGod);
        payloadMap.put(DemandType.CHOOSE_CARD, this::parseStringGod);
        payloadMap.put(DemandType.CHOOSE_STARTER, this::parseString);
        payloadMap.put(DemandType.PLACE_WORKERS, this::parseStringReducedDemandCell);
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



    Demand<S> requestInput(DemandType currentState) {
        boolean toRepeat;
        boolean toUsePower;
        boolean incrementIndex;
        int i = 0;
        List<S> payloadList = new ArrayList<>();
        S payload;
        String value;

        Function <String, Boolean> toRepeatFunction;
        Function <Integer, Boolean> indexFunction;
        Function <String, Boolean> powerFunction;
        Function <String, S> payloadFunction;

        do {
            toRepeat = false;
            toUsePower = false;
            incrementIndex = false;
            payload = null;

            out.printString(messageMap.get(currentState));

            value = readLine();
            if (value == null) return null;

            toRepeatFunction = toRepeatMap.get(currentState);
            if (toRepeatFunction != null)
                toRepeat = toRepeatFunction.apply(value);

            if (toRepeat)
                out.printError();
            else {
                powerFunction = toUsePowerMap.get(currentState);
                if (powerFunction != null)
                    toUsePower = powerFunction.apply(value);

                payloadFunction = payloadMap.get(currentState);
                if (payloadFunction != null) {
                    payload = payloadMap.get(currentState).apply(value);
                    payloadList.add(payload);
                }

                if (payload == null)
                    toRepeat = true;
                else {
                    indexFunction = indexMap.get(currentState);
                    if (indexFunction != null)
                        incrementIndex = indexMap.get(currentState).apply(i);

                    if (incrementIndex)
                        i++;
                }
            }
        } while (toRepeat || incrementIndex);

        skipAdditionalPower(value);
        skipToBuild();

        if (toUsePower)
            return new Demand<>(DemandType.USE_POWER, payload);

        if (i > 0)
            return new Demand<>(currentState, (S) payloadList);

        return new Demand<>(currentState, payload);
    }

    String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException", e);
        }

        return null;
    }



    private S parseString(String string) {
        return (S) (new ReducedMessage(string));
    } //OK

    private S parseStringReducedDemandCell(String string) {
        ReducedAnswerCell cell = getReducedCell(string);

        if (cell == null) return null;

        return (S) (new ReducedDemandCell(cell.getX(), cell.getY()));
    } //OK

    private S parseCommand(String string) {
        String[] input = string.split(" ");

        if (input.length != 2) return null;

        return parseStringReducedDemandCell(input[1]);
    } //OK

    private S parseStringGod(String string) {
        return (S) (God.parseString(string));
    } //OK



    private boolean checkGod(String godString) {
        List<ReducedCard> deck;
        God god = God.parseString(godString);
        if (god == null) return true;

        synchronized (clientModel.lock) {
            deck = clientModel.getDeck();
        }

        return deck.stream()
                .noneMatch(g -> g.getGod().equals(god));
    } //OK

    private boolean checkWorker(String workerString) {
        ReducedAnswerCell workerCell = getReducedCell(workerString);

        if (workerCell == null) return true;

        return clientModel.getWorkers().stream()
                .filter(w -> w.getOwner().equals(clientModel.getPlayer().getNickname()))
                .noneMatch(w -> w.getX() == workerCell.getX() && w.getY() == workerCell.getY());
    } //OK

    private boolean checkPlayer(String player) {
        for (ReducedPlayer p : clientModel.getOpponents()) {
            if (p.getNickname().equals(player))
                return false;
        }

        return !clientModel.getPlayer().getNickname().equals(player);
    } //OK

    private boolean checkCell(String cellString) {
        ReducedAnswerCell cell = getReducedCell(cellString);

        return  cell == null;
    } //OK

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
    } //OK

    private boolean isToUsePower(String string) {
        String[] input = string.split(" ");

        ReducedAnswerCell cell = getReducedCell(input[1]);

        if (cell == null) return true;

        return cell.getActionList().contains(ReducedAction.USEPOWER) && ReducedAction.USEPOWER.getName().equals(input[0]);
    } //OK

    private ReducedAnswerCell getReducedCell(String cellString) {
        List<Integer> coordinate = stringToInt(cellString);

        if (coordinate == null) return null;

        int x = coordinate.get(0);
        int y = coordinate.get(1);

        return clientModel.getCell(x, y);
    } //OK

    private List<Integer> stringToInt(String string) {
        if (string.length() != 3) return null;

        List<Integer> ret = new ArrayList<>();

        String[] input = string.split(",");

        if (input.length != 2) return null;
        if (Arrays.stream(input).anyMatch(i -> i.length() != 1)) return null;

        ret.add(string.charAt(0) - 48);
        ret.add(string.charAt(2) - 48);

        if(clientModel.checkCell(ret.get(0), ret.get(1))) return null;

        return ret;
    } //OK



    private void skipAdditionalPower(String value) {
        if (!(clientModel.getCurrentState().equals(DemandType.ASK_ADDITIONAL_POWER))) return;
        if (!value.equals("n")) return;

        if (clientModel.getPrevState().equals(DemandType.MOVE))
            clientModel.setNextState(DemandType.BUILD);

        if (clientModel.getPrevState().equals(DemandType.BUILD))
            clientModel.setNextState(DemandType.CHOOSE_WORKER);
    }

    private void skipToBuild() {
        if (!clientModel.getCurrentState().equals(DemandType.ADDITIONAL_POWER)) return;

        if (clientModel.getPrevState().equals(DemandType.MOVE))
            clientModel.setNextState(DemandType.BUILD);
    }
}
