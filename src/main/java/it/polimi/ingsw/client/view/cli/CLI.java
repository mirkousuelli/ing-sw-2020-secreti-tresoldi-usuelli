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
        boolean isYourTurn;
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
                isYourTurn = out.printChanges(answerTemp.getContext());
                break;

            case CLOSE:
                setActive(false);
                return;

            default:
                throw new NotAValidInputRunTimeException("Not a valid answer");
        }

        if (isYourTurn)
            startUI(answerTemp);

        becomeFree();
    }

    private void startUI(Answer<S> answerTemp) {
        Demand<S> demand = in.requestInput(answerTemp.getContext());

        if (demand.getHeader().equals(DemandType.VICTORY) && ((ReducedMessage) demand.getPayload()).getMessage().equals("n")) {
            setActive(false);
            synchronized (clientModel.lock) {
                clientModel.setActive(false);
            }
        }

        createDemand(demand);
    }

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
        String name = in.nextLine();

        out.printString("Insert the server's ip:\n");
        String ip = in.nextLine();

        out.printString("Insert the server's port:\n");
        int port = Integer.parseInt(in.nextLine());

        runThreads(name, ip, port);
    }
}
