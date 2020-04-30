package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;

import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLI<S> extends ClientView<S> {

    private final CLIScanner<S> in;
    private final CLIPrinter<S> out;
    private static final Logger LOGGER = Logger.getLogger(CLI.class.getName());

    public CLI(String playerName, ClientModel<S> clientModel) {
        super(playerName, clientModel);
        out = new CLIPrinter<>(System.out, clientModel, this);
        in = new CLIScanner<>(System.in, out, clientModel);
    }

    private synchronized void update() {
        switch (answer.getHeader()) {
            case ERROR:
                out.printError();
                break;

            case DEFEAT:
            case VICTORY:
                out.printEnd(answer.getPayload().toString(), answer.getHeader().toString());
                if(clientModel.isYourTurn())
                    endGame();
                return;

            case SUCCESS:
                out.printSuccess();
                out.printChanges(answer.getContext());
                break;
        }

        if (!answer.getContext().equals(DemandType.WAIT) && !answer.getContext().equals(DemandType.RELOAD) &&
            !answer.getContext().equals(DemandType.JOIN_GAME) && !answer.getContext().equals(DemandType.START))
            startUI();
    }

    private synchronized void startUI() {
        Demand<S> demand;

        demand = in.requestInput(answer.getContext());

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
                            synchronized (clientModel) {
                                while (!clientModel.isChanged()) clientModel.wait();
                                clientModel.setChanged(false);
                                temp = clientModel.getAnswer();
                            }

                            LOGGER.info("Receiving...");
                            synchronized (this) {
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
    public void run() {
        setActive(true);
        setChanged(false);

        try {
            setInitialRequest();
            Thread read = asyncReadFromModel();
            read.join();
        } catch (InterruptedException | NoSuchElementException e) {
            LOGGER.log(Level.SEVERE, "CLI closed", e);
        } finally {
            setActive(false);
        }
    }
}
