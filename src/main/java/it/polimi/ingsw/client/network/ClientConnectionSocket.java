package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.SantoriniRunnable;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.xml.FileXML;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionSocket<S> extends SantoriniRunnable {

    private final Socket socket;
    private final FileXML file;
    private ClientView<S> clientView;
    private final LinkedList<Answer<S>> buffer;
    private static final Logger LOGGER = Logger.getLogger(ClientConnectionSocket.class.getName());

    public ClientConnectionSocket(String ip, int port, ClientView<S> clientView) throws IOException {
        super();
        socket = new Socket(ip, port);
        file = new FileXML(socket);
        this.clientView = clientView;
        buffer = new LinkedList<>();
    }

    public ClientConnectionSocket(String ip, int port) throws IOException {
        this(ip, port, null);
    }

    public void setClientView(ClientView<S> clientView) {
        this.clientView = clientView;
    }

    @Override
    public Answer<S> getAnswer() {
        Answer<S> answer;

        synchronized (buffer) {
            answer = buffer.removeFirst();
        }

        return answer;
    }

    public boolean hasAnswer() {
        boolean ret;

        synchronized (buffer) {
            ret = !buffer.isEmpty();
        }

        return ret;
    }

    private Thread asyncReadFromSocket() {
        Thread t = new Thread(
                () -> {
                        try {
                            Answer temp;
                            while (isActive()) {
                                if (socket.isConnected() && !socket.isClosed()) {
                                    synchronized (file.lockReceive) {
                                        temp = (Answer<S>) file.receive();
                                    }

                                    if (temp == null) {
                                        setActive(false);
                                        break;
                                    }

                                    LOGGER.info("Queueing...");
                                    synchronized (buffer) {
                                        buffer.addLast(temp);
                                        LOGGER.info("Queued!");
                                        buffer.notifyAll();
                                        LOGGER.info("READ");
                                    }
                                }
                            }
                        } catch (Exception e){
                            LOGGER.log(Level.SEVERE, "Got an IOException", e);
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
                            Demand demand;
                            while (isActive()) {
                                synchronized (lockDemand) {
                                    while (!clientView.isChanged()) lockDemand.wait();
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
                                if (isActive()) {
                                    synchronized (file.lockSend) {
                                        try {
                                            file.send(demand);
                                        } catch (IOException e) {
                                            LOGGER.log(Level.SEVERE, "Got an IOException", e);
                                            break;
                                        }
                                    }
                                }
                                LOGGER.info("Sent!");
                            }
                        } catch (Exception e) {
                            if (!(e instanceof InterruptedException))
                                LOGGER.log(Level.SEVERE, "Got an exception, asyncWrite not working", e);
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
                                setAnswer(getAnswer());
                                setChanged(true);
                                lockAnswer.notifyAll();
                                LOGGER.info("Consumed!");
                            }
                        }
                    } catch(Exception e) {
                        if (!(e instanceof InterruptedException))
                            LOGGER.log(Level.SEVERE, "Got an exception, asyncWrite not working", e);
                    }
                }
        );

        t.start();
        return t;
    }

    @Override
    protected void startThreads(Thread watchDogThread) throws InterruptedException {
        Thread read = asyncReadFromSocket();
        Thread write = asyncWriteToSocket();
        Thread consumer = consumerThread();
        watchDogThread.join();
        read.interrupt();
        write.interrupt();
        consumer.interrupt();
    }

    public synchronized void closeConnection() {
        try {
            socket.close();
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot close socket", e);
        }
    }
}