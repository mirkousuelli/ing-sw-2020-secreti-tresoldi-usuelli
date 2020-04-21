package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.client.view.cli.CLIPrinter;
import it.polimi.ingsw.client.view.cli.SantoriniPrintStream;
import it.polimi.ingsw.client.view.cli.Turn;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
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
    //private ReducedGod card; TODO ReducedGod
    private String currentPlayer;
    private Turn turn;

    public CLI(String nickName, ClientConnection<S> clientConnection) {
        super(nickName, clientConnection);
        in = new Scanner(System.in);
        out = new SantoriniPrintStream(System.out);

        reducedBoard = new ReducedCell[5][5];
        reducedGodList = new ArrayList<>();
        opponents = new ArrayList<>();
        workers = new ArrayList<>();
    }

    @Override
    protected void startUI() {
        S payload;
        String nextLine;
        int x;
        int y;
        boolean toRepeat = false;
        God god;
        List<God> chosenDeck = new ArrayList<>();
        List<MessageCell> initialWorkerPosition = new ArrayList<>();
        int i;

        if (turn.equals(Turn.START)) {
            printStartInfo(out);
            return;
        }

        do {
            switch (turn) {
                case CHOOSE_DECK:
                    CLIPrinter.printGods(out);

                    i = 0;
                    do {
                        CLIPrinter.printString(out, "Insert the name of one the gods which will be used in this match [godName]\n");
                        nextLine = in.nextLine();
                        god = God.parseString(nextLine);

                        if (god != null && !chosenDeck.contains(god)) {
                            chosenDeck.add(god);
                            i++;
                        }
                    } while (i <= 2);

                    payload = (S) chosenDeck;
                    break;

                case CHOOSE_CARD:
                    CLIPrinter.printGods(out);
                    CLIPrinter.printString(out, "Insert the name of the chosen god [godName]\n");
                    nextLine = in.nextLine();
                    god = God.parseString(nextLine);

                    toRepeat = god != null && !reducedGodList.contains(god);

                    payload = (S) god;
                    break;

                case PLACE_WORKERS:
                    CLIPrinter.printString(out, "Insert the initial locations of your worker [x,y]\n");
                    x = in.nextInt();
                    y = in.nextInt();

                    if (checkCell(x, y))
                        initialWorkerPosition.add(new MessageCell(x, y));
                    else
                        toRepeat = true;

                    payload = (S) initialWorkerPosition;
                    break;

                case CHOOSE_WORKER:
                    CLIPrinter.printBoard(out, reducedBoard, opponents);
                    CLIPrinter.printWorkers(out);
                    CLIPrinter.printString(out, "Select a worker[1, 2]\n");
                    nextLine = in.nextLine();

                    toRepeat = !nextLine.equals("1") && !nextLine.equals("2");

                    payload = (S) nextLine;
                    break;

                case MOVE:
                case BUILD:
                case USEPOWER:
                    CLIPrinter.printBoard(out, reducedBoard, opponents);
                    CLIPrinter.printPossibleActions(out, reducedBoard);
                    CLIPrinter.printString(out, "Make your action [x,y]\n");
                    x = in.nextInt();
                    y = in.nextInt();

                    toRepeat = checkCell(x, y);

                    payload = (S) new MessageCell(x, y);
                    break;

                case CONFIRM:
                    CLIPrinter.printBoard(out, reducedBoard, opponents);
                    CLIPrinter.printString(out, "Do you want to confirm your action? [Y/N]\n");
                    nextLine = in.nextLine().toLowerCase();

                    toRepeat = !nextLine.equals("y") && !nextLine.equals("n");

                    payload = (S) nextLine;
                    break;

                default:
                    throw new RuntimeException("Not a valid turn");
            }

            if (toRepeat)
                CLIPrinter.printString(out, "Error, try again\n");
        } while (toRepeat);

        notify(new Demand<>(turn.toDemandType(), payload));
    }

    @Override
    public void update(Answer<S> answer) {
        if (answer.getHeader().equals(AnswerType.DEFEAT) || answer.getHeader().equals(AnswerType.VICTORY)) {
            CLIPrinter.printString(out, "You " + answer.getHeader().toString() + "!\n");
            try {
                endGame();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return;
        }

        switch (answer.getHeader()) {
            case ERROR:
                CLIPrinter.printString(out, answer.getPayload().toString() + "\n");
                break;

            case SUCCESS:
                CLIPrinter.printString(out, "Your action has been done!\n");
                turn = Turn.parseInt(turn.toInt() + 1); //--------------- TODO parseTurn
                break;

            case START:
                opponents = new ArrayList<>((List<ReducedPlayer>) answer.getPayload());
                break;

            case CHOOSE_DECK:
            case CHOOSE_CARD:
                reducedGodList = new ArrayList<>((List<God>) answer.getPayload());
                break;

            default:
                throw new RuntimeException("Not a valid update");
        }

        updateReduceObjects(answer);
        startUI();
    }

    private void updateReduceObjects(Answer<S> answer) {
        //TODO uptRedObj
    }

    private void updateReducedBoard(List<ReducedCell> cells) {
        for (ReducedCell c : cells) {
            reducedBoard[c.getX()][c.getY()] = c;
        }
    }

    private void updateReducedPlayer() {
        //TODO uptRedPl
    }

    private boolean isYourTurn(String player) {
        return currentPlayer.equals(player);
    }

    private void printStartInfo(SantoriniPrintStream out) {
        CLIPrinter.printLogo(out);
        if (opponents.size() == 1)
            CLIPrinter.printString(out, "Your opponent is:\n");
        else
            CLIPrinter.printString(out, "Your opponents are:\n");
        CLIPrinter.printOpponents(out, opponents);

    }

    private boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
    }
}
