package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.client.view.cli.CLIPrinter;
import it.polimi.ingsw.client.view.cli.SantoriniPrintStream;
import it.polimi.ingsw.client.view.cli.Turn;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.payload.ReducedCell;
import it.polimi.ingsw.server.model.cards.God;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLI<S> extends ClientView<S> {

    private final Scanner in;
    private final SantoriniPrintStream out;

    private final ReducedCell[][] reducedBoard;
    private List<God> reducedGodList;
    private List<String> opponents;
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
        turn = Turn.WAIT;
        defeated = false;
        victorious = false;
    }

    @Override
    public void startUI() {
        Demand<S> demand = null;

        try {
            switch (turn) {
                case START:
                    CLIPrinter.printLogo(out);
                    if (opponents.size() == 1)
                        CLIPrinter.printString("Your opponent is:", true);
                    else
                        System.out.println("Your opponents are:");
                    CLIPrinter.printOpponents(out);
                    break;

                case CHOOSE_DECK:
                    CLIPrinter.printGods(out);
                    CLIPrinter.printString("Insert the name of the gods which will used in this match [godName, godName", false);
                    if (opponents.size() == 2) CLIPrinter.printString(", godName", false);
                    CLIPrinter.printString("]", true);
                    break;

                case CHOOSE_CARD:
                    CLIPrinter.printGods(out);
                    CLIPrinter.printString("Insert the name of the chosen god [godName]", true);
                    break;

                case PLACE_WORKERS:
                    CLIPrinter.printString("Insert the initial locations of your workers [(x,y), (x,y)]", true);
                    break;

                case CHOOSE_WORKER:
                    CLIPrinter.printBoard(out);
                    CLIPrinter.printWorkers(out);
                    CLIPrinter.printString("Insert which worker will be used [1, 2]", true);
                    break;

                case MOVE:
                    CLIPrinter.printBoard(out);
                    CLIPrinter.printPossibleActions(out);
                    CLIPrinter.printString("Insert the cell to move the worker to [(x,y)]", true);
                    break;

                case BUILD:
                    CLIPrinter.printBoard(out);
                    CLIPrinter.printPossibleActions(out);
                    CLIPrinter.printString("Insert the cell to build up [(x,y)]", true);
                    break;

                case CONFIRM:
                    CLIPrinter.printBoard(out);
                    CLIPrinter.printString("Do you want to confirm your action? [Y/N]", true);
                    break;

                default:
                    throw new RuntimeException("Not a valid turn");
            }
        } catch(RuntimeException e) {
            System.out.println(e.getMessage());
        }

        notify(demand);
    }

    @Override
    public void update(Answer<S> answer) {
        switch (answer.getHeader()) {
            case ERROR:
                CLIPrinter.printString(answer.getPayload().toString(), true);
                /*if (isYourTurn(nickName))*/ startUI();
                break;

            case SUCCESS:
                CLIPrinter.printString("Your action has been done!", true);
                if (isYourTurn(nickName) && !turn.equals(Turn.WAIT)) {
                    turn = Turn.parseInt(turn.toInt() + 1);
                    startUI();
                }
                break;

            case DEFEAT:
                CLIPrinter.printString("You lose!", true);
                defeated = true;
                endGame();
                break;

            case VICTORY:
                CLIPrinter.printString("You win!", true);
                victorious = true;
                endGame();
                break;

            case START:
                opponents = new ArrayList<>((List<String>) answer.getPayload());
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
}
