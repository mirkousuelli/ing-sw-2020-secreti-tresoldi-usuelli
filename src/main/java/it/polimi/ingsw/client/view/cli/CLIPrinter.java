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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CLIPrinter<S> {

    private final ClientModel<S> clientModel;
    private final ClientView<S> clientView;
    private final PrintStream out;
    private final EnumMap<DemandType, String> stringMap;
    private final EnumMap<DemandType, Consumer<String>> initialMap;
    private final EnumMap<DemandType, Runnable> changesMap;
    private final EnumMap<DemandType, Boolean> allowScanner;

    private static final String CONNECT = "Connected!\n";
    private static final String RELOAD = "Reloaded!\n";
    private static final String CREATEGAME = "Waiting other players...\n";

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
        allowScanner = new EnumMap<>(DemandType.class);

        stringMap.put(DemandType.CONNECT, CONNECT);
        stringMap.put(DemandType.RELOAD, RELOAD);
        stringMap.put(DemandType.CREATE_GAME, CREATEGAME);

        initialMap.put(DemandType.CONNECT, this::printString);

        changesMap.put(DemandType.RELOAD, this::printAll);
        changesMap.put(DemandType.START, this::printStart);
        changesMap.put(DemandType.CHOOSE_DECK, this::printAvailableGods);
        changesMap.put(DemandType.CHOOSE_CARD, this::printAvailableGods);
        changesMap.put(DemandType.AVAILABLE_GODS, this::printAvailableGods);
        changesMap.put(DemandType.CHOOSE_STARTER, this::printGods);
        changesMap.put(DemandType.PLACE_WORKERS, this::printBoard);
        changesMap.put(DemandType.CHOOSE_WORKER, this::printBoard);
        changesMap.put(DemandType.MOVE, this::printAll);
        changesMap.put(DemandType.BUILD, this::printAll);
        changesMap.put(DemandType.ASK_ADDITIONAL_POWER, this::printAll);
        changesMap.put(DemandType.CHANGE_TURN, this::printCurrentPlayer);

        allowScanner.put(DemandType.CONNECT, false);
        allowScanner.put(DemandType.RELOAD, false);
        allowScanner.put(DemandType.START, false);
        allowScanner.put(DemandType.CHANGE_TURN, false);
        allowScanner.put(DemandType.AVAILABLE_GODS, false);
    }

    public void printLogo() {
        out.println(LOGO);
    }

    public void printString(String message) {
        out.print(message);
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

        for (int j = 4; j >= 0; j--) {
            System.out.print(j + " ");
            for (int i = 0; i < 5; i++)
                printCell(board[i][j], opponents);
            out.print("\n");
        }
        out.print("  ");
        for (int i = 0; i < 5; i++) {
            out.print(" " + i + " ");
        }
        out.print("\n\n");

        printOpponents();
    }

    private void printCell(ReducedAnswerCell cell, List<ReducedPlayer> opponents) {
        if (!cell.isFree()) {
            out.print(opponents.stream()
                    .filter(opponent -> opponent.getNickname().equals(cell.getWorker().getOwner()))
                    .map(ReducedPlayer::getColor)
                    .map(Color::parseString)
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

    private void printAvailableGods() {
        Map<Integer, String> numberedDeck = IntStream
                .range(0, clientModel.getDeck().size()) // IntStream
                .boxed() // Stream<Integer>
                .collect(Collectors.toMap(i -> i, i -> clientModel.getDeck().get(i).getGod().toString().toLowerCase() + ": " + clientModel.getDeck().get(i).getDescription()));

        out.print("Available Gods: \n");

        out.println(numberedDeck.entrySet().stream()
                .map(g -> g.getKey() + ") " + g.getValue())
                .reduce(null, (a, b) -> a != null
                ? a + ".\n" + b
                : b
                )
         + "\n");
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
                .map(opponent -> Color.parseString(opponent.getColor()) + opponent.getCard().getGod().toString() + Color.RESET)
                .reduce("none", (a, b) -> !a.equals("none")
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
            god = clientModel.getPlayer().getCard().getGod();
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
                                                 .filter(x -> !x.getActionList().contains(ReducedAction.DEFAULT))
                                                 .collect(Collectors.toList());

        List<ReducedAction> reducedActions = new ArrayList<>();
        for (ReducedAnswerCell c : cellList) {
            for (ReducedAction ra : c.getActionList()) {
                if (!reducedActions.contains(ra))
                    reducedActions.add(ra);
            }
        }

        out.print("Action");
        if (reducedActions.size() >= 2) out.print("s");
        out.println(": ");
        for (ReducedAction action : reducedActions) {
            out.print(action.toString() + ": ");


            out.println(
                    cellList.stream()
                            .filter(c -> c.getActionList().contains(action))
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
        if (clientModel.isYourTurn())
            printPossibleActions();
        printGods();
    }

    private void printCurrentPlayer() {
        if (clientModel.isYourTurn())
            printString("It is your turn!\n");
        else
            printString(clientModel.getCurrentPlayer()+ " is the current player!\n");
    }

    public void printError() {
        out.println("Error, try again");
    }

    public void printSuccess() {
        out.println("Done!");
    }

    public void printEnd(String context) {
        if (clientModel.isYourTurn())
            out.println("It is your " + context + "!");
        else
            out.println("It is" + clientModel.getCurrentPlayer() + context + "!");
    }

    public boolean printChanges(DemandType demandType) {
        Boolean ret;
        Consumer<String> initlaFunct = initialMap.get(demandType);
        Runnable changesMapFunct = changesMap.get(demandType);

           if (initlaFunct != null)
               initlaFunct.accept(stringMap.get(demandType));
           else if (changesMapFunct != null) {
               changesMapFunct.run();
           }

           ret = allowScanner.get(demandType);
           if (ret == null)
               return clientModel.isYourTurn();
           else
               return ret;
    }
}

