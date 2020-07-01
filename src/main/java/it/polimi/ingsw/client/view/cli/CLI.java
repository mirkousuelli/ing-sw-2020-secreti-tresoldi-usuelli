package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;

/**
 * Class which communicates which the client via a command line interface
 */
public class CLI<S> extends ClientView<S> {

    private final CLIScanner<S> in;
    private final CLIPrinter<S> out;

    /**
     * Constructor which initializes the cli by defining a printer and a scanner of the command line
     * */
    public CLI() {
        super();
        out = new CLIPrinter<>();
        in = new CLIScanner<>(out);
    }

    @Override
    protected void update() {
        boolean isYourTurn = false;
        Answer<S> answerTemp = getAnswer();

        switch (answerTemp.getHeader()) {
            case ERROR:
                out.printError();
                out.printChanges(clientModel.getCurrentState());
                isYourTurn = true;
                break;

            case DEFEAT:
                out.printEnd(answerTemp.getHeader().toString());
                if (clientModel.isEnded())
                    System.exit(1);
                else {
                    isYourTurn = true;
                    createDemand(new Demand<>(DemandType.NEW_GAME, (S) "close"));
                }
                break;

            case CLOSE:
            case VICTORY:
                out.printEnd(answerTemp.getHeader().toString());
                isYourTurn = true;
                createDemand(new Demand<>(DemandType.NEW_GAME, (S) "close"));
                break;

            case SUCCESS:
                out.printSuccess();
                isYourTurn = out.printChanges(clientModel.getCurrentState());
                break;

            case CHANGE_TURN:
                out.printCurrentPlayer();
                break;

            case RELOAD:
                out.printReload();
                out.printChanges(clientModel.getCurrentState());
                break;

            default:
                LOGGER.info("Not a valid answerType " + answerTemp.getHeader());
                break;
        }

        if (isYourTurn)
            startUI();

        becomeFree();
    }

    /**
     * Performs the input operations with the user and generates the message to send to the server
     */
    private void startUI() {
        if (clientModel.getCurrentState().equals(DemandType.START)) return;

        createDemand(in.requestInput(clientModel.getCurrentState()));
    }

    /**
     * Executes a thread which keeps the client view updated.
     */
    @Override
    protected void startThreads() throws InterruptedException {
        initialRequest();
        out.setClientModel(clientModel);
        in.setClientModel(clientModel);

        Thread read = asyncReadFromModel();

        read.join();
    }

    private void initialRequest() {
        out.printString("Insert your name:\n");
        String name = in.readLine();

        out.printString("Insert the server's ip:\n");
        String ip = in.readLine();

        out.printString("Insert the server's port:\n");
        int port = Integer.parseInt(in.readLine());

        runThreads(name, ip, port);
    }
}
