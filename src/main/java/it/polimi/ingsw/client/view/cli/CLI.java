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
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLI<S> extends ClientView<S> {

    private final Scanner in;
    final SantoriniPrintStream out;
    private static final Logger LOGGER = Logger.getLogger(CLI.class.getName());

    public CLI(ReducedPlayer player, ClientConnection<S> clientConnection) {
        super(player, clientConnection);
        in = new Scanner(System.in);
        out = new SantoriniPrintStream(System.out);
    }

    @Override
    public void update(ClientModel<S> clientModel) {
        if (clientModel.getState().equals(AnswerType.START)) {
            printOpponents(out, clientModel);
            return;
        }

        if (clientModel.getState().equals(AnswerType.ERROR))
            printError();

        if(clientModel.isYourTurn(player.getNickname())) {
            if (clientModel.getState().equals(AnswerType.DEFEAT) || clientModel.getState().equals(AnswerType.VICTORY)) {
                try {
                    endGame();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Got an IOException.", e);
                }

                CLIPrinter.printString(out, "Game ended\n");
                return;
            }

            startUI(clientModel);
        }
        else {
            if (clientModel.getTurn().equals(Turn.BUILD) || clientModel.getTurn().equals(Turn.MOVE))
                printChanges(out, clientModel);

            if (clientModel.getState().equals(AnswerType.DEFEAT) || clientModel.getState().equals(AnswerType.VICTORY))
                printOpponents(out, clientModel);
        }
    }

    private void startUI(ClientModel<S> clientModel) {
        DemandType demandType;
        S payload;
        String nextLine;
        boolean toRepeat;
        boolean toUsePower = false;
        int i;
        God god;
        List<God> chosenDeck;
        List<ReducedDemandCell> initialWorkerPosition;
        List<Integer> coord;

        Turn turn = clientModel.getTurn();


        switch (turn) {
            case CHOOSE_DECK:
                chosenDeck = new ArrayList<>();
                printCards(out, clientModel);

                i = 0;
                do {
                    CLIPrinter.printString(out, "Insert the name of one the gods which will be used in this match [godName]\n");
                    nextLine = in.nextLine();
                    god = God.parseString(nextLine);

                    if (god != null && !clientModel.checkGod(god)) {
                        chosenDeck.add(god);
                        i++;
                    }
                    else
                        printError();
                } while (i <= 2);

                payload = (S) chosenDeck;
                break;

            case CHOOSE_CARD:
                do {
                    printCards(out, clientModel);
                    CLIPrinter.printString(out, "Insert the name of the chosen god [godName]\n");
                    nextLine = in.nextLine();
                    god = God.parseString(nextLine);

                    toRepeat = god == null || clientModel.checkGod(god);

                    if (toRepeat)
                        printError();
                } while (toRepeat);

                payload = (S) god;
                break;

            case PLACE_WORKERS:
                initialWorkerPosition = new ArrayList<>();

                i = 0;
                printChanges(out, clientModel);
                do {
                    CLIPrinter.printString(out, "Insert the initial locations of your worker [x,y]\n");
                    nextLine = in.nextLine();
                    coord = stringToInt(nextLine);

                    if (!coord.isEmpty() && clientModel.getReducedCell(coord.get(0), coord.get(1)).isFree()) {
                        initialWorkerPosition.add(new ReducedDemandCell(coord.get(0), coord.get(1)));
                        toRepeat = false;
                        i++;
                    }
                    else {
                        toRepeat = true;
                        printError();
                    }
                } while (toRepeat || i <= 1);

                payload = (S) initialWorkerPosition;
                break;

            case CHOOSE_WORKER:
                do {
                    printChanges(out, clientModel);
                    CLIPrinter.printString(out, "Select a worker[x,y]\n");
                    nextLine = in.nextLine();
                    coord = stringToInt(nextLine);

                    toRepeat = coord.isEmpty() || clientModel.checkWorker(coord.get(0), coord.get(1));

                    if (toRepeat)
                        printError();
                } while (toRepeat);

                payload = (S) new ReducedDemandCell(coord.get(0), coord.get(1));
                break;

            case MOVE:
            case BUILD:
                do {
                    printChanges(out, clientModel);
                    CLIPrinter.printString(out, "Make your action [x,y]\n");
                    nextLine = in.nextLine();
                    coord = stringToInt(nextLine);

                    toRepeat = coord.isEmpty() || clientModel.evalToRepeat(coord.get(0), coord.get(1));

                    if (toRepeat)
                        printError();
                } while (toRepeat);

                toUsePower = clientModel.evalToUsePower(coord.get(0), coord.get(1));

                payload = (S) new ReducedDemandCell(coord.get(0), coord.get(1));
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
        List<ReducedPlayer> players = new ArrayList<>(clientModel.getOpponents());
        players.add(player);

        CLIPrinter.printBoard(out, clientModel.getReducedBoard(), players);
        CLIPrinter.printPossibleActions(out, clientModel.getReducedBoard());
        printCards(out, clientModel);
    }

    private void printOpponents(SantoriniPrintStream out, ClientModel<S> clientModel) {
        CLIPrinter.printOpponents(out, clientModel.getOpponents());
    }

    private void printCards(SantoriniPrintStream out, ClientModel<S> clientModel) {
        CLIPrinter.printGods(out, clientModel.getReducedGodList());
    }

    private List<Integer> stringToInt(String string) {
        if (string.length() != 3) return new ArrayList<>();

        List<Integer> ret = new ArrayList<>();

        ret.add((int) string.charAt(0) - 48);
        ret.add((int) string.charAt(2) - 48);

        return ret;
    }
}
