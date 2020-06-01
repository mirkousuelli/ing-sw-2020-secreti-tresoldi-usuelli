package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;

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
                isYourTurn = true;//out.printChanges(clientModel.getCurrentState());
                break;

            case DEFEAT:
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
        if (clientModel.getCurrentState().equals(DemandType.START)) return;

        if (clientModel.isYourTurn() || getAnswer().getHeader().equals(AnswerType.VICTORY) || getAnswer().getHeader().equals(AnswerType.ERROR))
            createDemand(in.requestInput(clientModel.getCurrentState()));
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
        String name = in.readLine();

        out.printString("Insert the server's ip:\n");
        String ip = in.readLine();

        out.printString("Insert the server's port:\n");
        int port = Integer.parseInt(in.readLine());

        runThreads(name, ip, port);
    }
}
