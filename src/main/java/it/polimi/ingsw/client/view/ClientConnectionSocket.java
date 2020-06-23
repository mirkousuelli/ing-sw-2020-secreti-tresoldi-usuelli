package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.xml.FileXML;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionSocket<S> extends SantoriniRunnable<S> {

    private ClientView<S> clientView;

    private final Socket socket;
    private final FileXML file;
    private final LinkedList<Answer<S>> buffer;

    private static final Logger LOGGER = Logger.getLogger(ClientConnectionSocket.class.getName());

    ClientConnectionSocket(String ip, int port, ClientView<S> clientView) throws IOException {
        super();
        socket = new Socket(ip, port);
        file = new FileXML(socket);
        this.clientView = clientView;
        buffer = new LinkedList<>();
    }

    ClientConnectionSocket(String ip, int port) throws IOException {
        this(ip, port, null);
    }

    void setClientView(ClientView<S> clientView) {
        this.clientView = clientView;
    }

    Answer<S> getFirstAnswer() {
        Answer<S> answer;

        synchronized (buffer) {
            answer = buffer.remove();
        }

        return answer;
    }

    boolean hasAnswer() {
        boolean ret;

        synchronized (buffer) {
            ret = !buffer.isEmpty();
        }

        return ret;
    }

    private void send(Demand demand) throws IOException {
        if (isActive()) {
            synchronized (file.lockSend) {
                file.send(demand);
            }
        }
    }

    private Thread asyncReadFromSocket() {
        Thread t = new Thread(
                () -> {
                    Answer<S> temp;
                    while (isActive()) {
                        if (socket.isConnected() && !socket.isClosed()) {
                            synchronized (file.lockReceive) {
                                temp = (Answer<S>) file.receive();

                            }

                            if (temp == null) { //server ko
                                LOGGER.info("Server ko!!!");
                                System.exit(1);
                            } else {
                                clientView.freeOnExit(temp.getHeader());
                                LOGGER.info("Queueing...");
                                synchronized (buffer) {
                                    buffer.add(temp);
                                    LOGGER.info("Queued!");
                                    buffer.notifyAll();
                                    LOGGER.info("READ");
                                }
                            }
                        }
                    }
                }
        );

        t.start();
        return t;
    }

    private Thread asyncWriteToSocket() {
        Thread t = new Thread(
                () -> {
                    try {
                        Demand<S> demand;
                        while (isActive()) {
                            synchronized (clientView.lockDemand) {
                                while (!clientView.isChanged()) clientView.lockDemand.wait();
                                clientView.setChanged(false);
                                demand = clientView.getDemand();
                            }

                            synchronized (buffer) {
                                if (hasAnswer()) {
                                    buffer.notifyAll();
                                    LOGGER.info("WRITE");
                                }
                            }

                            LOGGER.info("Sending...");
                            send(demand);
                            LOGGER.info("Sent!");
                        }
                    } catch (Exception e) {
                        if (isActive())
                            LOGGER.log(Level.SEVERE, "Got an unexpected exception, asyncWriteToSocket not working", e);
                        setActive(false);
                        Thread.currentThread().interrupt();
                    }
                }
        );

        t.start();
        return t;
    }

    private Thread consumerThread() {
        Thread t = new Thread(
                () -> {
                    try {
                        while (isActive()) {
                            synchronized (clientView.lockFree) {
                                while (!clientView.isFree()) clientView.lockFree.wait();
                                clientView.setFree(false);
                            }

                            synchronized (buffer) {
                                while (!hasAnswer()) buffer.wait();
                            }

                            LOGGER.info("Consuming...");
                            synchronized (lockAnswer) {
                                setChanged(true);
                                lockAnswer.notifyAll();
                                LOGGER.info("Consumed!");
                            }
                        }
                    } catch (InterruptedException e) {
                        if (isActive())
                            LOGGER.log(Level.SEVERE, "Got an unexpected InterruptedException, consumer not working", e);
                        Thread.currentThread().interrupt();
                        setActive(false);
                    }
                }
        );

        t.start();
        return t;
    }

    @Override
    protected void startThreads() throws InterruptedException {
        Thread read = asyncReadFromSocket();
        Thread write = asyncWriteToSocket();
        Thread consumer = consumerThread();

        read.join();
        write.join();
        consumer.join();

        closeConnection();
    }

    private synchronized void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot close socket", e);
        }
    }
}