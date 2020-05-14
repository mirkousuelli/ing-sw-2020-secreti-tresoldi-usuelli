package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.network.ClientConnectionSocket;
import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLI<S> extends ClientView<S> {

    private final CLIScanner<S> in;
    private final CLIPrinter<S> out;
    private static final Logger LOGGER = Logger.getLogger(CLI.class.getName());

    public CLI(ClientModel<S> clientModel) {
        super(clientModel);
        out = new CLIPrinter<>(System.out, clientModel);
        in = new CLIScanner<>(System.in, out, clientModel);

    }

    public CLI() {
        this(null);
    }

    private void update() {
        boolean isYourTurn;
        Answer<S> answerTemp = getAnswer();

        switch (answerTemp.getHeader()) {
            case ERROR:
                out.printError();
                isYourTurn = true;
                break;

            case DEFEAT:
            case VICTORY:
                out.printEnd(answerTemp.getHeader().toString());
                if(clientModel.isYourTurn())
                    endGame();
                return;

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

        setFree(true);
        synchronized (lockFree) {
            lockFree.notifyAll();
        }
    }

    private void startUI(Answer<S> answerTemp) {
        Demand<S> demand = in.requestInput(answerTemp.getContext());

        setDemand(demand);
        setChanged(true);

        synchronized (lockDemand) {
            lockDemand.notifyAll();
        }
    }

    public Thread asyncReadFromModel() {
        Thread t = new Thread(
                () -> {
                    try {
                        Answer<S> temp;
                        while (isActive()) {
                            synchronized (clientModel.lockAnswer) {
                                while (!clientModel.isChanged()) clientModel.lockAnswer.wait();
                                clientModel.setChanged(false);
                                temp = clientModel.getAnswer();
                            }

                            LOGGER.info("Receiving...");
                            synchronized (lockAnswer) {
                                setAnswer(temp);
                                LOGGER.info("Received!");
                                update();
                            }
                        }
                    } catch (Exception e){
                        setActive(false);
                        LOGGER.log(Level.SEVERE, "Got an exception", e);
                    }
                }
        );

        t.start();
        return t;
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
        ClientConnectionSocket clientConnectionSocket = null;
        ClientModel clientModel;
        String name;
        String ip;
        int port;

        out.printString("Insert your name:\n");
        name = in.nextLine();
        out.printString("Insert the server's ip:\n");
        ip = in.nextLine();
        out.printString("Insert the server's port:\n");
        port = Integer.parseInt(in.nextLine());

        try {
            clientConnectionSocket = new ClientConnectionSocket(ip, port);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException");
        }

        clientModel = new ClientModel(name, clientConnectionSocket);
        setClientModel(clientModel);
        clientConnectionSocket.setClientView(this);

        setInitialRequest();

        new Thread(
                clientConnectionSocket
        ).start();

        new Thread(
                clientModel
        ).start();
    }

    private void setInitialRequest() {
        setFree(false);

        synchronized (clientModel.lock) {
            setDemand(new Demand<>(DemandType.CONNECT, (S) (new ReducedMessage(clientModel.getPlayer().getNickname()))));
            setChanged(true);
        }

        setFree(true);

        synchronized (lockDemand) {
            lockDemand.notifyAll();
        }
    }
}
