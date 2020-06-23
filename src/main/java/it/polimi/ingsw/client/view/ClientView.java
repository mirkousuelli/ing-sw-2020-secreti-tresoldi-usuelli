package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ClientView<S> extends SantoriniRunnable<S> {

    protected ClientModel<S> clientModel;

    private boolean isFree = false;
    final Object lockFree;

    protected static final Logger LOGGER = Logger.getLogger(ClientView.class.getName());
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    public ClientView(ClientModel<S> clientModel) {
        super();
        this.clientModel = clientModel;

        lockFree = new Object();
    }

    public ClientView() {
        this(null);
    }

    private void setClientModel(ClientModel<S> clientModel) {
        this.clientModel = clientModel;
    }

    public ClientModel<S> getClientModel() {
        return clientModel;
    }

    boolean isFree() {
        boolean ret;

        synchronized (lockFree) {
            ret = isFree;
        }

        return ret;
    }

    void setFree(boolean free) {
        synchronized (lockFree) {
            isFree = free;
        }
    }

    protected void setInitialRequest() {
        synchronized (clientModel.lock) {
            setDemand(new Demand<>(DemandType.CONNECT, (S) (new ReducedMessage(clientModel.getPlayer().getNickname()))));
            setChanged(true);
        }

        becomeFree();
    }

    protected void createDemand(Demand<S> demand) {
        if (demand == null) return;

        setDemand(demand);
        setChanged(true);

        synchronized (lockDemand) {
            lockDemand.notifyAll();
        }
    }

    protected Thread asyncReadFromModel() {
        Thread t = new Thread(
                () -> {
                    try {
                        while (isActive()) {
                            synchronized (clientModel.lockAnswer) {
                                while (!clientModel.isChanged()) clientModel.lockAnswer.wait();
                            }

                            clientModel.setChanged(false);
                            setAnswer(clientModel.getAnswer());

                            LOGGER.info("Receiving...");
                            synchronized (lockAnswer) {
                                LOGGER.info("Received!");
                                update();
                            }
                        }
                    } catch (InterruptedException e) {
                        if (isActive())
                            LOGGER.log(Level.SEVERE, "Got an unexpected InterruptedException, asyncReadFromModel not working", e);
                        Thread.currentThread().interrupt();
                        setActive(false);
                    }
                }
        );

        t.start();
        return t;
    }

    protected void runThreads(String name, String ip, int port) {
        ClientConnectionSocket<S> clientConnectionSocket = null;

        try {
            clientConnectionSocket = new ClientConnectionSocket<>(ip, port);
            clientConnectionSocket.setClientView(this);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException", e);
            System.exit(1);
        }

        setClientModel(new ClientModel<>(name, clientConnectionSocket));
        setInitialRequest();

        executor.execute(clientConnectionSocket);
        executor.execute(clientModel);
    }

    protected void becomeFree() {
        setFree(true);

        synchronized (lockFree) {
            lockFree.notifyAll();
        }
    }

    void freeOnExit(AnswerType answerType) {
        switch (answerType) {
            case CLOSE:
            case DEFEAT:
            case VICTORY:
                becomeFree();
                break;

            default:
                break;
        }
    }

    protected abstract void update();
}
