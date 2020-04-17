package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.client.view.cli.Turn;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.payload.ReducedCell;
import it.polimi.ingsw.server.model.cards.God;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLI<S> extends ClientView<S> {

    private final Scanner input;

    private final ReducedCell[][] reducedBoard;
    private List<God> reducedGodList;
    private List<String> opponents;
    private String currentPlayer;
    private Turn turn;
    private boolean defeated;
    private boolean victorious;

    private static final String logo = "SANTORINI";

    public CLI(String nickName, ClientConnection<S> clientConnection){
        super(nickName, clientConnection);
        this.input = new Scanner(System.in);

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
                    System.out.println(logo);
                    if (opponents.size() == 1) System.out.print("Your opponent is:");
                    else System.out.println("Your opponents are:");
                    System.out.println(opponents);
                case CHOOSE_DECK:
                    displayGods();
                    System.out.print("Insert the name of the gods which will used in this match [godName, godName");
                    if (opponents.size() == 2) System.out.print(", godName");
                    System.out.println("]");
                    break;
                case CHOOSE_CARD:
                    displayGods();
                    System.out.println("Insert the name of the chosen god [godName]");
                    break;
                case PLACE_WORKERS:
                    System.out.println("Insert the initial locations of your workers [(x,y), (x,y)]");
                    break;
                case CHOOSE_WORKER:
                    displayBoard();
                    displayWorkers();
                    System.out.println("Insert which worker will be used [1, 2]");
                    break;
                case MOVE:
                    displayBoard();
                    System.out.println("Insert the cell to move the worker to [(x,y)]");
                    break;
                case BUILD:
                    displayBoard();
                    System.out.println("Insert the cell to build up [(x,y)]");
                    break;
                case CONFIRM:
                    displayBoard();
                    System.out.println("Do you want to confirm your action? [Y/N]");
                    break;
                default:
                    throw new RuntimeException("Not a valid turn");
            }
        } catch(RuntimeException e){
            System.out.println(e.getMessage());
        }

        notify(demand);
    }

    @Override
    public void update(Answer<S> answer) {
        switch (answer.getHeader()) {
            case ERROR:
                System.out.println(answer.getPayload().toString());
                /*if (isYourTurn(nickName))*/ startUI();
                break;
            case SUCCESS:
                System.out.println("Your action has been done!");
                if (isYourTurn(nickName) && !turn.equals(Turn.WAIT)){
                    turn = Turn.parseInt(turn.toInt() + 1);
                    startUI();
                }
                break;
            case DEFEAT:
                System.out.println("You lose!");
                defeated = true;
                endGame();
                break;
            case VICTORY:
                System.out.println("You win!");
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

    public void displayBoard() {

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.println(reducedBoard[i][j].toString());
                //TODO ReducedBoard.toString
            }
        }
    }

    public void displayWorkers() {
        //TODO displayWorker
    }

    public void displayGods() {
        for (God g : reducedGodList)
            System.out.println(g.toString());
        //TODO ReducedGod.toString
    }

    public boolean isYourTurn(String player) {
        return currentPlayer.equals(player);
    }

}
