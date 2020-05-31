package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
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
        toRepeatMap.put(DemandType.CHOOSE_DECK, clientModel::checkGod);
        toRepeatMap.put(DemandType.CHOOSE_CARD, clientModel::checkGod);
        toRepeatMap.put(DemandType.CHOOSE_STARTER, clientModel::checkPlayer);
        toRepeatMap.put(DemandType.PLACE_WORKERS, value -> !clientModel.getReducedCell(value).isFree());
        toRepeatMap.put(DemandType.CHOOSE_WORKER, clientModel::checkWorker);
        toRepeatMap.put(DemandType.MOVE, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.BUILD, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.USE_POWER, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.NEW_GAME, value -> !value.equals("y") && !value.equals("n"));
        toRepeatMap.put(DemandType.ASK_ADDITIONAL_POWER, value -> !value.equals("y") && !value.equals("n"));

        indexMap.put(DemandType.CHOOSE_DECK, index -> index < clientModel.getOpponents().size());
        indexMap.put(DemandType.PLACE_WORKERS, index -> index < 1);

        toUsePowerMap.put(DemandType.MOVE, clientModel::evalToUsePower);
        toUsePowerMap.put(DemandType.BUILD, clientModel::evalToUsePower);
        toUsePowerMap.put(DemandType.USE_POWER, clientModel::evalToUsePower);

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

    private S parseString(String string) {
        return (S) (new ReducedMessage(string));
    }

    private S parseStringReducedDemandCell(String string) {
        int x = string.charAt(0) - 48;
        int y = string.charAt(2) - 48;

        if(clientModel.checkCell(x, y)) return null;

        return (S) (new ReducedDemandCell(x, y));
    }

    private S parseCommand(String string) {
        String[] input = string.split(" ");

        if (input.length != 2) return null;

        return parseStringReducedDemandCell(input[1]);
    }

    private S parseStringGod(String string) {
        return (S) (God.parseString(string));
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
