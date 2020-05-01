package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CLIPrinter<S> {

    private final ClientModel<S> clientModel;
    private final ClientView<S> clientView;
    private final PrintStream out;
    private final Map<DemandType, Function<String, Boolean>> yourTurnMap;
    private final EnumMap<DemandType, String> stringMap;
    private final EnumMap<DemandType, Consumer<String>> initialMap;
    private final EnumMap<DemandType, Runnable> changesMap;

    private static final String CONNECT = "Connected!\n";
    private static final String RELOAD = "Reloaded!\n";
    private static final String CONFIRM = "Confirmed!\n";
    private static final String WAIT = "Waiting other players...\n";
    private static final String ASKLOBBY = "Ask your friend his lobby's id!\n";

    private static final String LOGO = "\n" +
            "  ______                             _       _ \n" +
            " / _____)             _             (_)     (_)\n" +
            "( (____  _____ ____ _| |_ ___   ____ _ ____  _ \n" +
            " \\____ \\(____ |  _ (_   _) _ \\ / ___) |  _ \\| |\n" +
            " _____) ) ___ | | | || || |_| | |   | | | | | |\n" +
            "(______/\\_____|_| |_| \\__)___/|_|   |_|_| |_|_|\n" +
            "                                               \n\n";

    public CLIPrinter(PrintStream out, ClientModel<S> clientModel, ClientView<S> clientView) {
        this.out = out;
        this.clientModel = clientModel;
        this.clientView = clientView;

        stringMap = new EnumMap<>(DemandType.class);
        initialMap = new EnumMap<>(DemandType.class);
        changesMap = new EnumMap<>(DemandType.class);
        yourTurnMap = new EnumMap<>(DemandType.class);

        yourTurnMap.put(DemandType.CHOOSE_DECK, clientModel::isYourTurn);
        yourTurnMap.put(DemandType.CHOOSE_CARD, clientModel::isYourTurn);
        yourTurnMap.put(DemandType.PLACE_WORKERS, clientModel::isYourTurn);
        yourTurnMap.put(DemandType.CHOOSE_WORKER, clientModel::isYourTurn);
        yourTurnMap.put(DemandType.MOVE, clientModel::isYourTurn);
        yourTurnMap.put(DemandType.BUILD, clientModel::isYourTurn);
        yourTurnMap.put(DemandType.USE_POWER, clientModel::isYourTurn);
        yourTurnMap.put(DemandType.CONFIRM, clientModel::isYourTurn);

        stringMap.put(DemandType.CONNECT, CONNECT);
        stringMap.put(DemandType.RELOAD, RELOAD);
        stringMap.put(DemandType.CONFIRM, CONFIRM);
        stringMap.put(DemandType.WAIT, WAIT);
        stringMap.put(DemandType.ASK_LOBBY, ASKLOBBY);

        initialMap.put(DemandType.CONNECT, this::printString);
        initialMap.put(DemandType.CONFIRM, this::printString);
        initialMap.put(DemandType.WAIT, this::printString);
        initialMap.put(DemandType.ASK_LOBBY, this::printString);

        changesMap.put(DemandType.RELOAD, this::printAll);
        changesMap.put(DemandType.CREATE_GAME, this::printLobby);
        changesMap.put(DemandType.JOIN_GAME, this::printLobby);
        changesMap.put(DemandType.START, this::printStart);
        changesMap.put(DemandType.CHOOSE_DECK, this::printGods);
        changesMap.put(DemandType.CHOOSE_CARD, this::printGods);
        changesMap.put(DemandType.PLACE_WORKERS, this::printBoard);
        changesMap.put(DemandType.CHOOSE_WORKER, this::printBoard);
        changesMap.put(DemandType.MOVE, this::printAll);
        changesMap.put(DemandType.BUILD, this::printAll);
    }

    public void printLogo() {
        out.println(LOGO);
    }

    public void printString(String message) {
        out.print(message);
    }

    public void printLobby() {
        String id;

        synchronized (clientModel) {
            id = clientModel.getLobbyId();
        }

        out.println("Your lobby number is: " + id);
    }

    public void printStart() {
        printLogo();
        printOpponents();
    }

    private void printBoard() {
        ReducedAnswerCell[][] board;
        List<ReducedPlayer> opponents;

        synchronized (clientModel) {
            board = clientModel.getReducedBoard();
            opponents = clientModel.getOpponents();
            opponents.add(clientModel.getPlayer());
        }

        for (int i = 4; i >= 0; i--) {
            for (int j = 0; j < 5; j++)
                printCell(board[i][j], opponents);
            out.print("\n");
        }
        out.print("\n");

        printOpponents();
    }

    private void printCell(ReducedAnswerCell cell, List<ReducedPlayer> opponents) {
        if (!cell.isFree()) {
            out.print(opponents.stream()
                    .filter(opponent -> opponent.getNickname().equals(cell.getWorker().getOwner()))
                    .map(ReducedPlayer::getColor)
                    .map(Color::parseString)
                    .filter(Objects::nonNull)
                    .reduce(Color.RESET, (a, b) -> !a.equals(Color.RESET)
                            ? a
                            : b
                    )
            );
        }
        out.print("[" + cell.getLevel().toInt() + "]" + Color.RESET);

    }

    private void printOpponents() {
        List<ReducedPlayer> opponents;

        synchronized (clientModel) {
            opponents = clientModel.getOpponents();
        }

        if (opponents.isEmpty()) return;

        out.print("Opponent");
        if (opponents.size() == 2) out.print("s");
        out.print(": ");

        out.println(opponents.stream()
                .filter(p -> !p.getNickname().equals(clientModel.getPlayer().getColor()))
                .map(opponent -> Color.parseString(opponent.getColor()) + opponent.getNickname() + Color.RESET)
                .reduce(null, (a, b) -> a != null
                        ? a + ", " + b
                        : b
                )
        );

        out.println("You: "+ Color.parseString(clientModel.getPlayer().getColor()) + clientModel.getPlayer().getNickname() + Color.RESET + "\n");
    }

    private void printGods() {
        List<ReducedPlayer> playerList;

        synchronized (clientModel) {
            playerList = clientModel.getOpponents();
        }

        if (playerList.isEmpty()) return;

        out.print("Opponent card");
        if (playerList.size() == 2) out.print("s");
        out.print(": ");

        out.println(playerList.stream()
                .map(opponent -> Color.parseString(opponent.getColor()) + opponent.getGod() + Color.RESET)
                .reduce(null, (a, b) -> a != null
                        ? a + ", " + b
                        : b
                )
        );

        printYourCard();
    }

    private void printYourCard() {
        God god;
        String color;

        synchronized (clientModel) {
            god = clientModel.getPlayer().getGod();
            color = Color.parseString(clientModel.getPlayer().getColor());
        }

        out.print("Your card: " + color + god + Color.RESET + "\n");
    }

    private void printPossibleActions() {
        ReducedAnswerCell[][] reducedBoard;

        synchronized (clientModel) {
            reducedBoard = clientModel.getReducedBoard();
        }

        List<ReducedAnswerCell> cellList = Arrays.stream(reducedBoard)
                                                 .flatMap(Arrays::stream)
                                                 .filter(x -> x.getAction() != ReducedAction.DEFAULT)
                                                 .collect(Collectors.toList());

        List<ReducedAction> reducedActions = cellList.stream()
                                                     .map(ReducedAnswerCell::getAction)
                                                     .distinct()
                                                     .collect(Collectors.toList());

        out.print("Action");
        if (reducedActions.size() >= 2) out.print("s");
        out.println(": ");
        for (ReducedAction action : reducedActions) {
            out.print(action.toString() + ": ");

            out.println(
                    cellList.stream()
                            .filter(c -> c.getAction().equals(action))
                            .map(c -> "(" + c.getX() + ", " +c.getY() + ")")
                            .reduce(null, (a, b) -> a != null
                                    ? a + ", " + b
                                    : b
                            )
            );
        }
    }

    private void printAll() {
        printBoard();
        printPossibleActions();
        printGods();
    }

    public void printError() {
        out.println("Error, try again");
    }

    public void printSuccess() {
        out.println("Done!");
    }

    public void printEnd(String player, String context) {
        out.println(player + context + "!");
    }

    public boolean printChanges(DemandType demandType) {
        Function<String, Boolean> yourTurnFunct = yourTurnMap.get(demandType);
        Consumer<String> initlaFunct = initialMap.get(demandType);

        if (initlaFunct != null)
            initlaFunct.accept(stringMap.get(demandType));
        else
            changesMap.get(demandType).run();

       if (yourTurnFunct != null)
           return yourTurnFunct.apply(clientModel.getPlayer().getNickname());
       else
           return true;
    }
}

