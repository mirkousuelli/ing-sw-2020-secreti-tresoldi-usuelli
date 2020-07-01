package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * Abstract class presenting the {@code ClientModel}'s data to the user
 */
public abstract class ClientView<S> extends SantoriniRunnable<S> {

    protected ClientModel<S> clientModel;

    private boolean isFree = false;
    final Object lockFree;

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    /**
     * Constructor which initializes the client view
     */
    protected ClientView() {
        super();
        lockFree = new Object();
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

    /**
     * Method used by the classes implementing the client view to send a message to the server via the {@code ClientConnection}
     *
     * @param demand the message to send to the server
     */
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

                            synchronized (lockAnswer) {
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

    /**
     * When a client view starts
     *
     * @param name user's name
     * @param ip   server's ip
     * @param port server's port
     */
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

    /**
     * Method used by a client view to report to the {@code ClientConnection}'s consumer thread that a new message can be consumed and the client view is free to receive and handle it
     */
    protected void becomeFree() {
        setFree(true);

        synchronized (lockFree) {
            lockFree.notifyAll();
        }
    }

    /**
     * Performs the necessary input and output actions with the user when it is needed (i.e. there is a new answer from the server, the view requires a certain input to continue its execution)
     */
    protected abstract void update();
}
