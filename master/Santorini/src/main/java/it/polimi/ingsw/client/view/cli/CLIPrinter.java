package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedCell;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CLIPrinter {

    private static final String logo = "\n" +
            "  ______                             _       _ \n" +
            " / _____)             _             (_)     (_)\n" +
            "( (____  _____ ____ _| |_ ___   ____ _ ____  _ \n" +
            " \\____ \\(____ |  _ (_   _) _ \\ / ___) |  _ \\| |\n" +
            " _____) ) ___ | | | || || |_| | |   | | | | | |\n" +
            "(______/\\_____|_| |_| \\__)___/|_|   |_|_| |_|_|\n" +
            "                                               \n\n";

    private static final String verticalWall = "---";//"\uD834\uDF6C";
    private static final String horizontalWall = "|";//"\uD834\uDF63";
    private static final String worker = "^";//"\uD83D\uDD74";

    public CLIPrinter() {
        throw new IllegalStateException("Utility class");
    }

    public static void printLogo(SantoriniPrintStream out) {
        out.println(logo);
    }

    public static void printString(SantoriniPrintStream out, String message, boolean newLine) {
        out.print(message);
        if (newLine) out.print("\n");
    }

    public static void printBoard(SantoriniPrintStream out, ReducedCell[][] board, List<ReducedPlayer> opponents) {
        List<ReducedCell> occupiedCell = new ArrayList<>();

        //printVerticalWalls(out);
        for (int i = 4; i >= 0; i--) {
            //out.print(horizontalWall);
            for (int j = 0; j <5; j++) {
                if(printCell(out, board[i][j], opponents))
                    occupiedCell.add(board[i][j]);
            }
            //out.print(horizontalWall + "\n");
            out.print("\n");
        }
        //printVerticalWalls(out);
        out.print("\n");
        printOccupiedCell(out, occupiedCell);
    }

    private static void printVerticalWalls(SantoriniPrintStream out) {
        for (int i = 0; i < 5; i++) out.print(verticalWall);
        out.print("\n");
    }

    /*
    private static void printHorizontalWalls(SantoriniPrintStream out) {
        for (int i = 0; i < 5; i++) out.print(horizontalWall);
        out.print("\n");
    }*/

    private static boolean printCell(SantoriniPrintStream out, ReducedCell cell, List<ReducedPlayer> opponents) {
        boolean occupied = false;

        if (cell.isFree()) {
            if (cell.getColor() != null) out.print(Color.parseString(cell.getColor()));
            out.print("[" + cell.getLevel().toInt() + "]" + Color.RESET);
        }
        else {
            String workerColor = Color.RESET;
            for (ReducedPlayer player : opponents) {
                if (player.getNickname().equals(cell.getWorker().getOwner())) {
                    workerColor = player.getColor();
                    occupied = true;
                }
            }
            out.print(Color.parseString(workerColor) + "[" + worker + "]" + Color.RESET);
        }

        return occupied;
    }

    private static void printOccupiedCell(SantoriniPrintStream out, List<ReducedCell> cellList) {
        for (ReducedCell c : cellList) {
            out.print("cell: (" + c.getX() + ", " + c.getY() + ")\t");
            out.print("level: " + c.getLevel().toString() + "\t");
            out.println("occupied by: " + c.getWorker().getOwner());
        }

        out.print("\n");
    }

    public static void printOpponents(SantoriniPrintStream out, List<ReducedPlayer> opponents) {
        out.print("Opponent");
        if (opponents.size() == 2) out.print("s");
        out.print(": ");

        out.println(opponents.stream()
                .map(opponent -> Color.parseString(opponent.getColor()) + opponent.getNickname() + Color.RESET)
                .reduce(null, (a, b) -> a != null
                                                ? a + ", " + b
                                                : b
                )
        );

        out.print("\n");
    }

    public static void printWorkers(SantoriniPrintStream out) {
        //TODO printWorkers
    }

    public static void printGods(SantoriniPrintStream out) {
        //TODO printGods
    }

    public static void printPossibleActions(SantoriniPrintStream out, ReducedCell[][] reducedBoard) {
        List<ReducedCell> cellList = Arrays.stream(reducedBoard).flatMap(Arrays::stream).filter(x -> x.getAction() != ReducedAction.DEFAULT).collect(Collectors.toList());
        List<ReducedAction> reducedActions = cellList.stream().map(ReducedCell::getAction).distinct().collect(Collectors.toList());

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
}
