package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReduceDemandChoice;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

public class CLIScanner<S> {

    private final Scanner in;
    private final CLIPrinter<S> out;

    private static final String CREATEJOINGAME = "Do you want to create a lobby or join an existing one: 1-create, 2-join\n";
    private static final String CREATEGAME = "Insert the number of players\n";
    private static final String ASKLOBBY = "Insert a lobby's id:\n";
    private static final String CHOOSEDECK = "Insert the name of one the gods which will be used in this match [godName]\n";
    private static final String CHOOSECARD = "Insert the name of the chosen god [godName]\n";
    private static final String PLACEWORKERS = "Insert the initial locations of your worker [x,y]\n";
    private static final String CHOOSEWORKERS = "Select a worker[x,y]\n";
    private static final String ACTION = "Make your action [x,y]\n";
    private static final String CONFIRM = "Do you want to confirm your action? [Y/N]\n";

    private final Map<DemandType, String> messageMap;
    private final Map<DemandType, Function<String, Boolean>> toRepeatMap;
    private final Map<DemandType, Function<Integer, Boolean>> indexMap;
    private final Map<DemandType, Function<String, Boolean>> toUsePowerMap;
    private final Map<DemandType, Function<String, S>> payloadMap;
    private final ClientModel<S> clientModel;

    public CLIScanner(InputStream inputStream, CLIPrinter<S> out, ClientModel<S> clientModel) {
        in = new Scanner(inputStream);
        this.out = out;
        this.clientModel = clientModel;


        messageMap = new EnumMap<>(DemandType.class);
        toRepeatMap = new EnumMap<>(DemandType.class);
        indexMap = new EnumMap<>(DemandType.class);
        toUsePowerMap = new EnumMap<>(DemandType.class);
        payloadMap = new EnumMap<>(DemandType.class);

        messageMap.put(DemandType.CONNECT, CREATEJOINGAME);
        messageMap.put(DemandType.CREATE_GAME, CREATEGAME);
        messageMap.put(DemandType.ASK_LOBBY, ASKLOBBY);
        messageMap.put(DemandType.CHOOSE_DECK, CHOOSEDECK);
        messageMap.put(DemandType.CHOOSE_CARD, CHOOSECARD);
        messageMap.put(DemandType.PLACE_WORKERS, PLACEWORKERS);
        messageMap.put(DemandType.CHOOSE_WORKER, CHOOSEWORKERS);
        messageMap.put(DemandType.MOVE, ACTION);
        messageMap.put(DemandType.BUILD, ACTION);
        messageMap.put(DemandType.USE_POWER, ACTION);
        messageMap.put(DemandType.CONFIRM, CONFIRM);

        toRepeatMap.put(DemandType.CREATE_GAME, index -> Integer.parseInt(index) < 2 || Integer.parseInt(index) > 3);
        toRepeatMap.put(DemandType.CHOOSE_DECK, clientModel::checkGod);
        toRepeatMap.put(DemandType.CHOOSE_CARD, clientModel::checkGod);
        toRepeatMap.put(DemandType.PLACE_WORKERS, value -> clientModel.getReducedCell(value).isFree());
        toRepeatMap.put(DemandType.CHOOSE_WORKER, clientModel::checkWorker);
        toRepeatMap.put(DemandType.MOVE, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.BUILD, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.USE_POWER, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.CONFIRM, value -> !value.equals("y") && !value.equals("n"));

        indexMap.put(DemandType.CHOOSE_DECK, index -> index <= 2);

        toUsePowerMap.put(DemandType.USE_POWER, clientModel::evalToUsePower);

        payloadMap.put(DemandType.CONNECT, this::parseString);
        payloadMap.put(DemandType.CREATE_GAME, this::parseString);
        payloadMap.put(DemandType.ASK_LOBBY, this::parseString);
        payloadMap.put(DemandType.CHOOSE_DECK, this::parseStringGod);
        payloadMap.put(DemandType.CHOOSE_CARD, this::parseStringGod);
        payloadMap.put(DemandType.PLACE_WORKERS, this::parseStringReducedDemandCell);
        payloadMap.put(DemandType.CHOOSE_WORKER, this::parseStringReducedDemandCell);
        payloadMap.put(DemandType.MOVE, this::parseStringReducedDemandCell);
        payloadMap.put(DemandType.BUILD, this::parseStringReducedDemandCell);
        payloadMap.put(DemandType.USE_POWER, this::parseStringReducedDemandCell);
        payloadMap.put(DemandType.CONFIRM, this::parseString);
    }


    private S parseString(String string) {
        return (S) (new ReduceDemandChoice(string));
    }

    private S parseStringReducedDemandCell(String string) {
        int x = string.charAt(0) - 48;
        int y = string.charAt(2) - 48;

        return (S) (new ReducedDemandCell(x, y));
    }

    private S parseStringGod(String string) {
        return (S) (God.parseString(string));
    }

    public Demand<S> requestInput(DemandType demandType) {
        boolean toRepeat = false;
        boolean toUsePower = false;
        boolean incrementIndex = false;
        int i = 0;
        String value;
        S payload = null;

        Function <String, Boolean> toRepeatFunction;
        Function <Integer, Boolean> indexFunction;
        Function <String, Boolean> powerFunction;
        Function <String, S> payloadFunction;

        do {
            out.printString(messageMap.get(demandType));
            value = in.nextLine();

            toRepeatFunction = toRepeatMap.get(demandType);
            if (toRepeatFunction != null)
                toRepeat = toRepeatFunction.apply(value);

            powerFunction = toUsePowerMap.get(demandType);
            if (powerFunction != null)
                toUsePower = powerFunction.apply(value);

            indexFunction = indexMap.get(demandType);
            if (indexFunction != null)
                incrementIndex = indexMap.get(demandType).apply(i);

            if (incrementIndex)
                i++;

            if (toRepeat)
                out.printError();
            else {
                payloadFunction = payloadMap.get(demandType);
                if (payloadFunction != null)
                    payload = payloadMap.get(demandType).apply(value);
            }
        } while (toRepeat || incrementIndex);

        if (toUsePower)
            return new Demand<>(DemandType.USE_POWER, payload);

        return new Demand<>(demandType, payload);
    }
}
