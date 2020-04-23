package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.cards.God;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLI<S> extends ClientView<S> {

    private final Scanner in;
    final SantoriniPrintStream out;

    public CLI(String nickName, ClientConnection<S> clientConnection) {
        super(nickName, clientConnection);
        in = new Scanner(System.in);
        out = new SantoriniPrintStream(System.out);
    }

    @Override
    public void update(ClientModel<S> clientModel) {
        if (clientModel.getState().equals(AnswerType.ERROR))
            printError();

        if(clientModel.isYourTurn(nickName)) {
            if (clientModel.getState().equals(AnswerType.DEFEAT) || clientModel.getState().equals(AnswerType.VICTORY)) {
                try {
                    endGame();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                CLIPrinter.printString(out, "Game ended\n");
                return;
            }

            startUI(clientModel);
        }
        else {
            if (clientModel.getTurn().equals(Turn.BUILD) || clientModel.getTurn().equals(Turn.MOVE))
                printChanges(out, clientModel);


            if (clientModel.getState().equals(AnswerType.START) || clientModel.getState().equals(AnswerType.DEFEAT) || clientModel.getState().equals(AnswerType.VICTORY))
                printOpponents(out, clientModel);
        }
    }

    private void startUI(ClientModel<S> clientModel) {
        DemandType demandType;
        S payload;
        String nextLine;
        int x;
        int y;
        boolean toRepeat = false;
        boolean toUsePower = false;
        God god;
        List<God> chosenDeck;
        List<ReducedDemandCell> initialWorkerPosition;

        Turn turn = clientModel.getTurn();


        switch (turn) {
            case CHOOSE_DECK:
                chosenDeck = new ArrayList<>();
                CLIPrinter.printGods(out);

                x = 0;
                do {
                    CLIPrinter.printString(out, "Insert the name of one the gods which will be used in this match [godName]\n");
                    nextLine = in.nextLine();
                    god = God.parseString(nextLine);

                    if (god != null && !chosenDeck.contains(god)) {
                        chosenDeck.add(god);
                        x++;
                    }
                    else
                        printError();
                } while (x <= 2);

                payload = (S) chosenDeck;
                break;

            case CHOOSE_CARD:
                do {
                    CLIPrinter.printGods(out);
                    CLIPrinter.printString(out, "Insert the name of the chosen god [godName]\n");
                    nextLine = in.nextLine();
                    god = God.parseString(nextLine);

                    toRepeat = god != null && !clientModel.getReducedGodList().contains(god);

                    if (toRepeat)
                        printError();
                } while (toRepeat);

                payload = (S) god;
                break;

            case PLACE_WORKERS:
                do {
                    initialWorkerPosition = new ArrayList<>();
                    CLIPrinter.printString(out, "Insert the initial locations of your worker [x,y]\n");
                    x = in.nextInt();
                    y = in.nextInt();

                    if (clientModel.checkCell(x, y))
                        initialWorkerPosition.add(new ReducedDemandCell(x, y));
                    else {
                        toRepeat = true;
                        printError();
                    }
                } while (toRepeat);

                payload = (S) initialWorkerPosition;
                break;

            case CHOOSE_WORKER:
                do {
                    CLIPrinter.printBoard(out, clientModel.getReducedBoard(), clientModel.getOpponents());
                    CLIPrinter.printWorkers(out);
                    CLIPrinter.printString(out, "Select a worker[1, 2]\n");
                    nextLine = in.nextLine();

                    toRepeat = !nextLine.equals("1") && !nextLine.equals("2");

                    if (toRepeat)
                        printError();
                } while (toRepeat);

                payload = (S) nextLine;
                break;

            case MOVE:
            case BUILD:
                do {
                    printChanges(out, clientModel);
                    CLIPrinter.printString(out, "Make your action [x,y]\n");
                    x = in.nextInt();
                    y = in.nextInt();

                    toRepeat = clientModel.checkCell(x, y);

                    if (toRepeat)
                        printError();
                } while (toRepeat);

                if (clientModel.getReducedCell(x, y).getAction().equals(ReducedAction.USEPOWER))
                    toUsePower = true;

                payload = (S) new ReducedDemandCell(x, y);
                break;

            case CONFIRM:
                do {
                    printChanges(out, clientModel);
                    CLIPrinter.printString(out, "Do you want to confirm your action? [Y/N]\n");
                    nextLine = in.nextLine().toLowerCase();

                    toRepeat = !nextLine.equals("y") && !nextLine.equals("n");

                    if (toRepeat)
                        printError();
                } while (toRepeat);

                payload = (S) nextLine;
                break;

            default:
                throw new NotAValidTurnRunTimeException("Not a valid turn");
        }

        if (toUsePower)
            demandType = DemandType.USE_POWER;
        else
            demandType = turn.toDemandType();

        notify(new Demand<>(demandType, payload));
    }

    private void printError() {
        CLIPrinter.printString(out, "Error, try again\n");
    }

    private void printChanges(SantoriniPrintStream out, ClientModel<S> clientModel) {
        CLIPrinter.printBoard(out, clientModel.getReducedBoard(), clientModel.getOpponents());
        CLIPrinter.printPossibleActions(out, clientModel.getReducedBoard());
    }

    private void printOpponents(SantoriniPrintStream out, ClientModel<S> clientModel) {
        CLIPrinter.printOpponents(out, clientModel.getOpponents());
    }
}
