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
        turn = Turn.WAIT;
    }

    @Override
    public void startUI() {
        DemandType header;
        S payload;
        String nextLine = "1";
        int x;
        int y;


        if(turn.equals(Turn.START) || turn.equals(Turn.CHOOSE_DECK)) {
            CLIPrinter.printLogo(out);
            if (opponents.size() == 1)
                CLIPrinter.printString(out, "Your opponent is:", true);
            else
                CLIPrinter.printString(out, "Your opponents are:", true);
            CLIPrinter.printOpponents(out, opponents);
        }

        if (!turn.equals(Turn.START) && !turn.equals(Turn.WAIT)){
            switch (turn) {
                case CHOOSE_DECK:
                    List<God> chosenDeck = new ArrayList<>();
                    God god;

                    do {
                        CLIPrinter.printGods(out);
                        CLIPrinter.printString(out, "Insert the name of one the gods which will be used in this match [godName]", true);
                        nextLine = in.nextLine();
                        god = God.parseString(nextLine);


                        if (god != null && !chosenDeck.contains(god))
                            chosenDeck.add(god);
                        else
                            CLIPrinter.printString(out, "Error, try again", true);

                    } while (chosenDeck.size() != opponents.size());

                    //-------------------------------------------TODO demand deck
                    header = DemandType.CONFIRM;
                    payload = (S) nextLine;
                    break;

                case CHOOSE_CARD:
                    God chosenGod;

                    do {
                        CLIPrinter.printGods(out);
                        CLIPrinter.printString(out, "Insert the name of the chosen god [godName]", true);
                        nextLine = in.nextLine();
                        chosenGod = God.parseString(nextLine);

                        if (chosenGod != null && !reducedGodList.contains(chosenGod))
                            CLIPrinter.printString(out, "Error, try again", true);

                    } while (!reducedGodList.contains(chosenGod));

                    header = DemandType.CHOOSE_CARD;
                    payload = (S) chosenGod;
                    break;

                case PLACE_WORKERS:
                    List<MessageCell> initialWorkerPosition = new ArrayList<>();

                    do {
                        CLIPrinter.printString(out, "Insert the initial locations of your worker [x,y]", true);
                        x = in.nextInt();
                        y = in.nextInt();

                        if (checkCell(x, y))
                            initialWorkerPosition.add(new MessageCell(x, y));
                        else
                            CLIPrinter.printString(out, "Error, try again", true);

                    } while (checkCell(x, y) || initialWorkerPosition.size() != 2);

                    header = DemandType.CONFIRM; //--------------------------- TODO demand place workers
                    payload = (S) nextLine;
                    break;

                case CHOOSE_WORKER:
                    do {
                        CLIPrinter.printBoard(out, reducedBoard, opponents);
                        CLIPrinter.printWorkers(out);
                        CLIPrinter.printString(out, "Select a worker[1, 2]", true);
                        nextLine = in.nextLine();

                        if (!nextLine.equals("1") && !nextLine.equals("2"))
                            CLIPrinter.printString(out, "Error, try again", true);

                    } while (!nextLine.equals("1") && !nextLine.equals("2"));

                    header = DemandType.CHOOSE_WORKER;
                    payload = (S) nextLine;
                    break;

                case ACTION:
                    do {
                        CLIPrinter.printBoard(out, reducedBoard, opponents);
                        CLIPrinter.printPossibleActions(out, reducedBoard);
                        CLIPrinter.printString(out, "Make your action [x,y]", true);
                        x = in.nextInt();
                        y = in.nextInt();

                        if (checkCell(x, y))
                            CLIPrinter.printString(out, "Error, try again", true);

                    } while (checkCell(x, y));

                    header = DemandType.BUILD;
                    payload = (S) new MessageCell(x, y);
                    break;

                case CONFIRM:
                    do {
                        CLIPrinter.printBoard(out, reducedBoard, opponents);
                        CLIPrinter.printString(out, "Do you want to confirm your action? [Y/N]", true);
                        nextLine = in.nextLine().toLowerCase();

                        if (!nextLine.equals("y") && !nextLine.equals("n"))
                            CLIPrinter.printString(out, "Error, try again", true);

                    } while (!nextLine.equals("y") && !nextLine.equals("n"));

                    header = DemandType.CONFIRM;
                    payload = (S) nextLine;
                    break;

                default:
                    throw new RuntimeException("Not a valid turn");
            }

            notify(new Demand<>(header, payload));
        }

    }

    @Override
    public void update(Answer<S> answer) {
        switch (answer.getHeader()) {
            case ERROR:
                CLIPrinter.printString(out, answer.getPayload().toString(), true);
                startUI();
                break;

            case SUCCESS:
                CLIPrinter.printString(out, "Your action has been done!", true);
                turn = Turn.parseInt(turn.toInt() + 1); //--------------- TODO parseTurn
                startUI();
                break;

            case DEFEAT:
            case VICTORY:
                CLIPrinter.printString(out, "You " + answer.getHeader().toString() + "!", true);
                try {
                    endGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case START:
                opponents = new ArrayList<>((List<ReducedPlayer>) answer.getPayload());
                break;

            case CHOOSE_DECK:
                opponents = new ArrayList<>((List<ReducedPlayer>) answer.getPayload());
            case CHOOSE_CARD:
                reducedGodList = new ArrayList<>((List<God>) answer.getPayload());
                startUI();
                break;

            case CHOOSE_STARTER:
            case UPDATE:
                startUI();
                break;
        }

        updateReduceObjects(answer);

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

    private boolean checkCell(int x, int y) {
        return x < 0 || x > 4 || y < 0 || y > 4;
    }
}
