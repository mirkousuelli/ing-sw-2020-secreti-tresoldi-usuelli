package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

public class CLIScanner<S> {

    private final Scanner in;
    private final CLIPrinter<S> out;

    private static final String createJoinGame = "Do you want to create a lobby or join an existing one: 1-create, 2-join\n";
    private static final String createGame = "Insert the number of players\n";
    private static final String askLobby = "Insert a lobby's id:\n";
    private static final String chooseDeck = "Insert the name of one the gods which will be used in this match [godName]\n";
    private static final String chooseCard = "Insert the name of the chosen god [godName]\n";
    private static final String placeWorkers = "Insert the initial locations of your worker [x,y]\n";
    private static final String chooseWorker = "Select a worker[x,y]\n";
    private static final String action = "Make your action [x,y]\n";
    private static final String confirm = "Do you want to confirm your action? [Y/N]\n";
    private static final String exceptionMessage = "Not a valid turn";

    private final Map<DemandType, String> messageMap;
    private final Map<DemandType, Function<String, Boolean>> toRepeatMap;
    private final Map<DemandType, Function<Integer, Boolean>> indexMap;
    private final Map<DemandType, Function<String, Boolean>> toUsePowerMap;
    private final ClientModel<S> clientModel;

    public CLIScanner(InputStream inputStream, CLIPrinter<S> out, ClientModel<S> clientModel) {
        in = new Scanner(inputStream);
        this.out = out;
        this.clientModel = clientModel;


        messageMap = new EnumMap<>(DemandType.class);
        toRepeatMap = new EnumMap<>(DemandType.class);
        indexMap = new EnumMap<>(DemandType.class);
        toUsePowerMap = new EnumMap<>(DemandType.class);

        messageMap.put(DemandType.CONNECT, createJoinGame);
        messageMap.put(DemandType.CREATE_GAME, createGame);
        messageMap.put(DemandType.ASK_LOBBY, askLobby);
        messageMap.put(DemandType.CHOOSE_DECK, chooseDeck);
        messageMap.put(DemandType.CHOOSE_CARD, chooseCard);
        messageMap.put(DemandType.PLACE_WORKERS, placeWorkers);
        messageMap.put(DemandType.CHOOSE_WORKER, chooseWorker);
        messageMap.put(DemandType.MOVE, action);
        messageMap.put(DemandType.BUILD, action);
        messageMap.put(DemandType.USE_POWER, action);
        messageMap.put(DemandType.CONFIRM, confirm);

        toRepeatMap.put(DemandType.CREATE_GAME, index -> {return Integer.parseInt(index) < 2 || Integer.parseInt(index) > 3;});
        toRepeatMap.put(DemandType.CHOOSE_DECK, clientModel::checkGod);
        toRepeatMap.put(DemandType.CHOOSE_CARD, clientModel::checkGod);
        toRepeatMap.put(DemandType.PLACE_WORKERS, value -> {return clientModel.getReducedCell(value).isFree();});
        toRepeatMap.put(DemandType.CHOOSE_WORKER, clientModel::checkWorker);
        toRepeatMap.put(DemandType.MOVE, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.BUILD, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.USE_POWER, clientModel::evalToRepeat);
        toRepeatMap.put(DemandType.CONFIRM, value -> {return !value.equals("y") && !value.equals("n");});

        indexMap.put(DemandType.CHOOSE_DECK, index -> {return index <= 2;});

        toUsePowerMap.put(DemandType.USE_POWER, clientModel::evalToUsePower);
    }

    public Demand<S> requestInput(DemandType demandType) {
        boolean toRepeat = false;
        boolean toUsePower = false;
        boolean incrementIndex = false;
        int i = 0;
        String value;

        Function <String, Boolean> toRepeatFunction ;
        Function <Integer, Boolean> indexFunction;
        Function <String, Boolean> powerFunction;

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
        } while (toRepeat || incrementIndex);

        if (toUsePower)
            return new Demand<>(DemandType.USE_POWER, (S) value);

        return new Demand<>(demandType, (S) value);
    }
}
