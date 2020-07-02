package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.cards.gods.God;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that manages how the {@code ClientModel}'s data are displayed on a command line interface
 */
public class CLIPrinter<S> {

    private ClientModel<S> clientModel;
    private final PrintStream out = new PrintStream(System.out, true);
    private final EnumMap<DemandType, String> stringMap;
    private final EnumMap<DemandType, Consumer<String>> initialMap;
    private final EnumMap<UpdatedPartType, Runnable> changesMap;

    private static final String CONNECT = "Connected!\n";
    private static final String START = "Starting!!\n";
    private static final String RELOAD = "Reloaded!\n";

    private static final String LOGO = "\n" +
            "  ______                             _       _ \n" +
            " / _____)             _             (_)     (_)\n" +
            "( (____  _____ ____ _| |_ ___   ____ _ ____  _ \n" +
            " \\____ \\(____ |  _ (_   _) _ \\ / ___) |  _ \\| |\n" +
            " _____) ) ___ | | | || || |_| | |   | | | | | |\n" +
            "(______/\\_____|_| |_| \\__)___/|_|   |_|_| |_|_|\n" +
            "                                               \n\n";

    /**
     * Constructor which initializes the hash maps used to determine the correct data to print according the {@code ClientModel}'s current state
     */
    CLIPrinter() {
        stringMap = new EnumMap<>(DemandType.class);
        initialMap = new EnumMap<>(DemandType.class);
        changesMap = new EnumMap<>(UpdatedPartType.class);

        stringMap.put(DemandType.CONNECT, CONNECT);
        stringMap.put(DemandType.START, START);

        initialMap.put(DemandType.CONNECT, this::printString);
        initialMap.put(DemandType.START, this::printStart);

        changesMap.put(UpdatedPartType.GOD, this::printAvailableGods);
        changesMap.put(UpdatedPartType.CARD, this::printAvailableGods);
        changesMap.put(UpdatedPartType.PLAYER, this::printGods);
        changesMap.put(UpdatedPartType.WORKER, this::printBoard);
        changesMap.put(UpdatedPartType.BOARD, this::printAll);
    }

    void setClientModel(ClientModel<S> clientModel) {
        this.clientModel = clientModel;
    }

    /**
     * Method that prints Santorini's logo
     */
    void printLogo() {
        out.println(LOGO);
    }

    /**
     * Method that prints the selected message
     *
     * @param message the message to print
     */
    void printString(String message) {
        out.print(message);
    }

    /**
     * Method that prints the given message, the logo of the game and the opponents' name on the command line
     *
     * @param message the message to print
     */
    void printStart(String message) {
        out.println(message);
        printLogo();
        printOpponents();
    }

    /**
     * Method that prints the board in a 5x5 matrix
     */
    private void printBoard() {
        ReducedAnswerCell[][] board;
        List<ReducedPlayer> opponents;

        synchronized (clientModel.lock) {
            board = clientModel.getReducedBoard();
            opponents = clientModel.getOpponents();
            opponents.add(clientModel.getPlayer());
        }

        for (int j = 4; j >= 0; j--) {
            out.print(j + " ");
            for (int i = 0; i < 5; i++)
                printCell(board[i][j], opponents);
            out.print("\n");
        }
        out.print("  ");
        for (int i = 0; i < 5; i++)
            out.print(" " + i + " ");
        out.print("\n\n");

        printOpponents();
    }

    /**
     * Method that prints the given cell with its current level, where is represented by a number between 0 and 4
     *
     * @param cell      the cell to print
     * @param opponents the list of opponents
     */
    private void printCell(ReducedAnswerCell cell, List<ReducedPlayer> opponents) {
        if (!cell.isFree()) {
            out.print(opponents.stream()
                    .filter(opponent -> opponent.getNickname().equals(cell.getWorker().getOwner()))
                    .map(ReducedPlayer::getColor)
                    .map(Color::parseString)
                    .reduce(Color.RESET, (a, b) -> !a.equals(Color.RESET)
                            ? a
                            : b)
            );
        }
        out.print("[" + cell.getLevel().toInt() + "]" + Color.RESET);

    }

    /**
     * Method that prints opponents' nickname, followed by the player's one
     */
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
                        : b)
        );

        out.println("You: " + Color.parseString(player.getColor()) + player.getNickname() + Color.RESET + "\n");
    }

    /**
     * Method that prints the available Gods
     */
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
                        : b)
                + "\n"
        );
    }

    /**
     * Method that prints the God(s) that belongs to the opponent(s), followed by the one owned by the player
     */
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

    /**
     * Method that prints the card that the player has
     */
    private void printYourCard() {
        God god;
        String color;

        synchronized (clientModel.lock) {
            god = clientModel.getPlayer().getCard().getGod();
            color = Color.parseString(clientModel.getPlayer().getColor());
        }

        out.print("Your card: " + color + god + Color.RESET + "\n");
    }

    /**
     * Method that prints the possible actions that the player can make
     */
    private void printPossibleActions() {
        ReducedAnswerCell[][] reducedBoard;

        synchronized (clientModel.lock) {
            reducedBoard = clientModel.getReducedBoard();
        }

        List<ReducedAnswerCell> cellList = Arrays.stream(reducedBoard)
                .flatMap(Arrays::stream)
                .filter(x -> !x.getActionList().contains(ReducedAction.DEFAULT))
                .collect(Collectors.toList());

        List<ReducedAction> reducedActions = cellList.stream()
                .map(ReducedAnswerCell::getActionList)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        out.print("Action");
        if (reducedActions.size() >= 2) out.print("s");
        out.println(": ");
        for (ReducedAction action : reducedActions) {
            out.print(action.toString() + ": ");
            out.println(
                    cellList.stream()
                            .filter(c -> c.getActionList().contains(action))
                            .map(c -> "(" + c.getX() + ", " + c.getY() + ")")
                            .reduce(null, (a, b) -> a != null
                                    ? a + ", " + b
                                    : b
                            )
            );
        }
    }

    /**
     * Method that prints both all possible actions and the Gods (with the corresponding owner)
     */
    private void printAll() {
        printBoard();
        if (clientModel.isYourTurn() && !clientModel.isReloaded() && !clientModel.getCurrentState().equals(DemandType.ASK_ADDITIONAL_POWER))
            printPossibleActions();
        printGods();
    }

    /**
     * Method that prints a message telling a player that it is his turn (during player's turn)
     */
    void printCurrentPlayer() {
        boolean isYourTurn = false;
        ReducedPlayer currentPlayer;

        synchronized (clientModel.lock) {
            if (clientModel.isYourTurn())
                isYourTurn = true;
            currentPlayer = clientModel.getCurrentPlayer();
        }

        if (isYourTurn)
            printString("It is your turn!\n");
        else
            printString(Color.RESET + Color.parseString(currentPlayer.getColor()) + currentPlayer.getNickname() + Color.RESET + " is the current player!\n");
    }

    /**
     * Method that prints a message of error
     */
    void printError() {
        out.println("Error, try again");
    }

    /**
     * Method that prints a message of success
     */
    void printSuccess() {
        out.println("Done!");
    }

    /**
     * Method that prints a message advising the player that a game has been reloaded
     */
    void printReload() {
        out.print(RELOAD);
    }

    void printLineSeparators() {
        printString("----------------------------------------------------------------------------------------------\n\n");
    }

    /**
     * Method that prints the end of the game: it can be a message of win or defeat if the game ended, or it tells that
     * the game has been successfully saved (if a player disconnected from the game)
     *
     * @param context the context that change the type of message that is print
     */
    void printEnd(String context) {
        switch (context.toLowerCase()) {
            case "victory":
            case "defeat":
                ReducedPlayer player = clientModel.getPlayer(((ReducedPlayer) clientModel.getAnswer().getPayload()).getNickname());

                if (clientModel.isEnded())
                    out.println("It's your " + context + "!");
                else
                    out.println("It's " + Color.RESET + Color.parseString(player.getColor()) + player.getNickname() + Color.RESET + "'s " + context + "!");
                break;

            case "close":
                out.println("game saved and closed!");
                break;

            default:
                break;
        }
    }

    /**
     * Prints the changes in the {@code ClientModel} done by the last message received from the server
     */
    boolean printChanges(DemandType demandType) {
        boolean ret;
        Consumer<String> initialFunct = initialMap.get(demandType);
        Runnable changesMapFunct = changesMap.get(clientModel.getAnswer().getContext());

        if (changesMapFunct != null)
            changesMapFunct.run();
        else if (initialFunct != null)
            initialFunct.accept(stringMap.get(demandType));

        synchronized (clientModel.lock) {
            ret = clientModel.isYourTurn();
        }

        return ret;
    }
}

