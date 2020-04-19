package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.client.view.cli.CLIPrinter;
import it.polimi.ingsw.client.view.cli.SantoriniPrintStream;
import it.polimi.ingsw.client.view.cli.Turn;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.MessageCell;
import it.polimi.ingsw.communication.message.payload.ReducedCell;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.communication.message.payload.ReducedWorker;
import it.polimi.ingsw.server.model.cards.God;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLI<S> extends ClientView<S> {

    private final Scanner in;
    private final SantoriniPrintStream out;

    private final ReducedCell[][] reducedBoard;
    private List<God> reducedGodList;
    private List<ReducedPlayer> opponents;
    private List<ReducedWorker> workers;
    private String currentPlayer;
    private Turn turn;
    private boolean defeated;
    private boolean victorious;

    public CLI(String nickName, ClientConnection<S> clientConnection) {
        super(nickName, clientConnection);
        in = new Scanner(System.in);
        out = new SantoriniPrintStream(System.out);

        reducedBoard = new ReducedCell[5][5];
        reducedGodList = new ArrayList<>();
        opponents = new ArrayList<>();
        workers = new ArrayList<>();
        turn = Turn.WAIT;
        defeated = false;
        victorious = false;
    }

    @Override
    public void startUI() {
        Demand<S> demand = null;
        String nextLine = "1";
        int x = 0;
        int y = 0;

        try {
            switch (turn) {
                case START:
                    CLIPrinter.printLogo(out);
                    if (opponents.size() == 1)
                        CLIPrinter.printString(out, "Your opponent is:", true);
                    else
                        CLIPrinter.printString(out, "Your opponents are:", true);
                    CLIPrinter.printOpponents(out, opponents);
                    break;

                case CHOOSE_DECK:
                    List<God> chosenDeck = new ArrayList<>();
                    God god = null;

                    do {
                        if (god != null && !chosenDeck.contains(god)) CLIPrinter.printString(out, "Error, try again", true);
                        CLIPrinter.printGods(out);
                        CLIPrinter.printString(out, "Insert the name of the gods which will be used in this match [godName]", true);
                        nextLine = in.nextLine();
                        god = God.parseString(nextLine);
                        if (god != null && !chosenDeck.contains(god)) chosenDeck.add(god);
                    } while (chosenDeck.size() != opponents.size());
                    break;

                case CHOOSE_CARD:
                    God chosenGod = null;

                    do {
                        if (chosenGod != null && !reducedGodList.contains(chosenGod))
                            CLIPrinter.printString(out, "Error, try again", true);

                        CLIPrinter.printGods(out);
                        CLIPrinter.printString(out, "Insert the name of the chosen god [godName]", true);
                        nextLine = in.nextLine();
                        chosenGod =  God.parseString(nextLine);
                    } while (!reducedGodList.contains(chosenGod));

                    demand = (Demand<S>) new Demand<>(DemandType.CHOOSE_CARD, chosenGod);
                    break;

                case PLACE_WORKERS:
                    List<MessageCell> initialWorkerPosition = new ArrayList<>();

                    do {
                        if (checkCell(x, y)) CLIPrinter.printString(out, "Error, try again", true);
                        CLIPrinter.printString(out, "Insert the initial locations of your worker [x,y]", true);
                        x = in.nextInt();
                        y = in.nextInt();
                        if (checkCell(x, y)) initialWorkerPosition.add(new MessageCell(x, y));
                    } while (checkCell(x, y) || initialWorkerPosition.size() != 2);
                    break;

                case CHOOSE_WORKER:
                    do {
                        if (!nextLine.equals("1") && !nextLine.equals("2")) CLIPrinter.printString(out, "Error, try again", true);
                        CLIPrinter.printBoard(out, reducedBoard, opponents);
                        CLIPrinter.printWorkers(out);
                        CLIPrinter.printString(out, "Select a worker[1, 2]", true);
                        nextLine = in.nextLine();
                    } while (!nextLine.equals("1") && !nextLine.equals("2"));

                    demand = (Demand<S>) new Demand<>(DemandType.CHOOSE_WORKER, nextLine);
                    break;

                case MOVE:
                    do {
                        if (checkCell(x, y)) CLIPrinter.printString(out, "Error, try again", true);
                        CLIPrinter.printBoard(out, reducedBoard, opponents);
                        CLIPrinter.printPossibleActions(out, reducedBoard);
                        CLIPrinter.printString(out, "Make your move [x,y]", true);
                        x = in.nextInt();
                        y = in.nextInt();
                    } while (checkCell(x, y));

                    demand = (Demand<S>) new Demand<>(DemandType.MOVE, new MessageCell(x, y));
                    break;

                case BUILD:
                    do {
                        if (checkCell(x, y)) CLIPrinter.printString(out, "Error, try again", true);
                        CLIPrinter.printBoard(out, reducedBoard, opponents);
                        CLIPrinter.printPossibleActions(out, reducedBoard);
                        CLIPrinter.printString(out, "Insert the cell to build up [x,y]", true);
                        x = in.nextInt();
                        y = in.nextInt();
                    } while (checkCell(x, y));

                    demand = (Demand<S>) new Demand<>(DemandType.BUILD, new MessageCell(x, y));
                    break;

                case CONFIRM:
                    do {
                        if (!nextLine.equals("y") && !nextLine.equals("n")) CLIPrinter.printString(out, "Error, try again", true);
                        CLIPrinter.printBoard(out, reducedBoard, opponents);
                        CLIPrinter.printString(out, "Do you want to confirm your action? [Y/N]", true);
                        nextLine = in.nextLine().toLowerCase();
                    } while (!nextLine.equals("y") && !nextLine.equals("n"));

                    demand = (Demand<S>) new Demand<>(DemandType.CONFIRM, nextLine);
                    break;

                default:
                    throw new RuntimeException("Not a valid turn");
            }

            notify(demand);

        } catch(RuntimeException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void update(Answer<S> answer) {
        switch (answer.getHeader()) {
            case ERROR:
                CLIPrinter.printString(out, answer.getPayload().toString(), true);
                /*if (isYourTurn(nickName))*/ startUI();
                break;

            case SUCCESS:
                CLIPrinter.printString(out, "Your action has been done!", true);
                if (isYourTurn(nickName) && !turn.equals(Turn.WAIT)) {
                    turn = Turn.parseInt(turn.toInt() + 1);
                    startUI();
                }
                break;

            case DEFEAT:
                CLIPrinter.printString(out, "You lose!", true);
                defeated = true;
                try {
                    endGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case VICTORY:
                CLIPrinter.printString(out, "You win!", true);
                victorious = true;
                try {
                    endGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case START:
                opponents = new ArrayList<>((List<ReducedPlayer>) answer.getPayload());
                opponents.remove(nickName);
                break;

            case CHOOSE_DECK:
            case CHOOSE_CARD:
                reducedGodList = new ArrayList<>((List<God>) answer.getPayload());
                startUI();
                break;

            case CHOOSE_STARTER:
                startUI();
                break;
        }
    }

    private boolean isYourTurn(String player) {
        return currentPlayer.equals(player);
    }

    private boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
    }
}
