package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CLIPrinter<S> {

    private ClientModel<S> clientModel;
    private final PrintStream out;
    private final EnumMap<DemandType, String> stringMap;
    private final EnumMap<DemandType, Consumer<String>> initialMap;
    private final EnumMap<UpdatedPartType, Runnable> changesMap;

    private static final String CONNECT = "Connected!\n";
    private static final String START = "Starting!!\n";
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

    public CLIPrinter(PrintStream out, ClientModel<S> clientModel) {
        this.out = out;
        this.clientModel = clientModel;

        stringMap = new EnumMap<>(DemandType.class);
        initialMap = new EnumMap<>(DemandType.class);
        changesMap = new EnumMap<>(UpdatedPartType.class);

        stringMap.put(DemandType.CONNECT, CONNECT);
        stringMap.put(DemandType.START, START);
        stringMap.put(DemandType.CREATE_GAME, CREATEGAME);

        initialMap.put(DemandType.CONNECT, this::printString);
        initialMap.put(DemandType.START, this::printStart);
        initialMap.put(DemandType.CREATE_GAME, this::printString);

        changesMap.put(UpdatedPartType.GOD, this::printAvailableGods);
        changesMap.put(UpdatedPartType.CARD, this::printAvailableGods);
        changesMap.put(UpdatedPartType.PLAYER, this::printGods);
        changesMap.put(UpdatedPartType.WORKER, this::printBoard);
        changesMap.put(UpdatedPartType.BOARD, this::printAll);
    }

    public void setClientModel(ClientModel<S> clientModel) {
        this.clientModel = clientModel;
    }

    public void printLogo() {
        out.println(LOGO);
    }

    public void printString(String message) {
        out.print(message);
    }

    public void printStart(String message) {
        out.println(message);
        printLogo();
        printOpponents();
    }

    private void printBoard() {
        ReducedAnswerCell[][] board;
        List<ReducedPlayer> opponents;

        synchronized (clientModel.lock) {
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
        ReducedPlayer player;

        synchronized (clientModel.lock) {
            opponents = clientModel.getOpponents();
            player = clientModel.getPlayer();
        }

        if (opponents.isEmpty()) return;

        out.print("Opponent");
        if (opponents.size() == 2) out.print("s");
        out.print(": ");

        out.println(opponents.stream()
                .filter(p -> !p.getNickname().equals(player.getColor()))
                .map(opponent -> Color.parseString(opponent.getColor()) + opponent.getNickname() + Color.RESET)
                .reduce(null, (a, b) -> a != null
                        ? a + ", " + b
                        : b
                )
        );

        out.println("You: "+ Color.parseString(player.getColor()) + player.getNickname() + Color.RESET + "\n");
    }

    private void printAvailableGods() {
        List<ReducedCard> deck;

        synchronized (clientModel.lock) {
            deck = clientModel.getDeck();
        }

        Map<Integer, String> numberedDeck = IntStream
                .range(0, deck.size()) // IntStream
                .boxed() // Stream<Integer>
                .collect(Collectors.toMap(i -> i, i -> deck.get(i).getGod().toString().toLowerCase() + ": " + deck.get(i).getDescription()));

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

        synchronized (clientModel.lock) {
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

        synchronized (clientModel.lock) {
            god = clientModel.getPlayer().getCard().getGod();
            color = Color.parseString(clientModel.getPlayer().getColor());
        }

        out.print("Your card: " + color + god + Color.RESET + "\n");
    }

    private void printPossibleActions() {
        ReducedAnswerCell[][] reducedBoard;

        synchronized (clientModel.lock) {
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
        if (clientModel.isYourTurn() && !clientModel.isReloaded())
            printPossibleActions();
        printGods();
    }

    public void printCurrentPlayer() {
        boolean isYourTurn = false;
        ReducedPlayer currentPlayer;

        synchronized (clientModel.lock) {
            if (clientModel.isYourTurn())
                isYourTurn = true;
            currentPlayer= clientModel.getCurrentPlayer();
        }

        if (isYourTurn)
            printString("It is your turn!\n");
        else
            printString(Color.RESET + Color.parseString(currentPlayer.getColor()) + currentPlayer.getNickname() + Color.RESET + " is the current player!\n");
    }

    public void printError() {
        out.println("Error, try again");
    }

    public void printSuccess() {
        out.println("Done!");
    }

    public void printReload() {
        out.println(RELOAD);
    }

    public void printEnd(String context) {
        if (clientModel.isYourTurn())
            out.println("It's your " + context + "!");
        else
            out.println("It's " + clientModel.getCurrentPlayer().getNickname() + "'s " + context + "!");
    }

    public boolean printChanges(DemandType demandType) {
        boolean ret;
        Consumer<String> initlaFunct = initialMap.get(demandType);
        Runnable changesMapFunct = changesMap.get(UpdatedPartType.parseString(demandType.toString()));

           if (initlaFunct != null)
               initlaFunct.accept(stringMap.get(demandType));
           else if (changesMapFunct != null) {
               changesMapFunct.run();
           }

           synchronized (clientModel.lock) {
               ret = clientModel.isYourTurn();
           }

           return ret;
    }
}

