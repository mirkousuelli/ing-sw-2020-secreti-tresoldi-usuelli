package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

public class CLI<S> extends ClientView<S> {

    private final CLIScanner<S> in;
    private final CLIPrinter<S> out;

    public CLI(ClientModel<S> clientModel) {
        super(clientModel);
        out = new CLIPrinter<>(System.out, clientModel);
        in = new CLIScanner<>(System.in, out, clientModel);

    }

    public CLI() {
        this(null);
    }

    @Override
    protected void update() {
        boolean isYourTurn = false;
        Answer<S> answerTemp = getAnswer();

        switch (answerTemp.getHeader()) {
            case ERROR:
                out.printError();
                isYourTurn = true;
                break;

            case DEFEAT:
                out.printEnd(answerTemp.getHeader().toString());
                if(clientModel.isYourTurn())
                    endGame();
                return;

            case VICTORY:
                out.printEnd(answerTemp.getHeader().toString());
                isYourTurn = true;
                break;

            case SUCCESS:
                out.printSuccess();
                isYourTurn = out.printChanges(clientModel.getCurrentState());
                break;

            case CLOSE:
                setActive(false);
                return;

            case CHANGE_TURN:
                out.printCurrentPlayer();
                if (clientModel.getCurrentState().equals(DemandType.CREATE_GAME))
                    isYourTurn = out.printChanges(clientModel.getCurrentState());
                break;

            case RELOAD:
                out.printReload();
                out.printChanges(clientModel.getCurrentState());
                isYourTurn = false;
                break;

            default:
                throw new NotAValidInputRunTimeException("Not a valid answer");
        }

        if (isYourTurn)
            startUI();

        becomeFree();
    }

    private void startUI() {
        if (clientModel.getCurrentState().equals(DemandType.START))
            return;
        Demand<S> demand = in.requestInput(clientModel.getCurrentState());

        /*if (demand.getHeader().equals(DemandType.VICTORY) && ((ReducedMessage) demand.getPayload()).getMessage().equals("n")) {
            setActive(false);
            synchronized (clientModel.lock) {
                clientModel.setActive(false);
            }
        }*/

        if (clientModel.isYourTurn())
            createDemand(demand);
    }

    @Override
    protected void startThreads(Thread watchDogThread) throws InterruptedException {
        initialRequest();
        out.setClientModel(clientModel);
        in.setClientModel(clientModel);
        Thread read = asyncReadFromModel();
        watchDogThread.join();
        read.interrupt();
    }

    private void initialRequest() {
        out.printString("Insert your name:\n");
        String name = in.getValue();

        out.printString("Insert the server's ip:\n");
        String ip = in.getValue();

        out.printString("Insert the server's port:\n");
        int port = Integer.parseInt(in.getValue());

        runThreads(name, ip, port);
    }
}
