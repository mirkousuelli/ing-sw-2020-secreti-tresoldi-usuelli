package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.SantoriniRunnable;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.xml.FileXML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionSocket<S> extends SantoriniRunnable<S> {

    private final Socket socket;
    private final FileXML file;
    private ClientView<S> clientView;
    private final List<Answer<S>> buffer;
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
            answer = buffer.remove(0);
        }

        return answer;
    }

    public boolean hasAnswer() {
        if (!isActive()) return true;
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
                            Answer<S> temp;
                            while (isActive()) {
                                if (socket.isConnected() && !socket.isClosed()) {
                                    synchronized (file.lockReceive) {
                                        temp = (Answer<S>) file.receive();
                                    }

                                    if (temp == null) {
                                        setActive(false);
                                        synchronized (clientView.lockFree) {
                                            clientView.lockFree.notifyAll();
                                        }
                                        synchronized (clientView.lockDemand) {
                                            lockDemand.notifyAll();
                                        }
                                        break;
                                    }

                                    if (temp.getHeader().equals(AnswerType.CLOSE))
                                        setActive(false);

                                    LOGGER.info("Queueing...");
                                    synchronized (buffer) {
                                        buffer.add(temp);
                                        LOGGER.info("Queued!");
                                        buffer.notifyAll();
                                        LOGGER.info("READ");
                                    }

                                    if (!clientView.isActive() || socket.isClosed()) {
                                        setActive(false);
                                        synchronized (buffer) {
                                            buffer.notifyAll();
                                        }
                                    }
                                }
                            }
                        } catch (Exception e){
                            setActive(false);
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
                                    while (!isActive() ||!clientView.isChanged()) clientView.lockDemand.wait();
                                    if (!isActive())
                                        break;
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
                                if (socket.isConnected() && !socket.isClosed()) {
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

                                if (!clientView.isActive() || socket.isClosed()) {
                                    setActive(false);
                                    synchronized (buffer) {
                                        buffer.notifyAll();
                                    }
                                }
                            }
                        } catch(Exception e) {
                            setActive(false);
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
                                while (!isActive() || !clientView.isFree()) clientView.lockFree.wait();
                                if (!isActive())
                                    break;
                                clientView.setFree(false);
                            }

                            synchronized (buffer) {
                                while (!hasAnswer()) buffer.wait();
                                if (!clientView.isActive() || socket.isClosed()) {
                                    setActive(false);
                                    synchronized (lockAnswer) {
                                        lockAnswer.notifyAll();
                                    }
                                }
                            }

                            LOGGER.info("Consuming...");
                            synchronized (lockAnswer) {
                                setChanged(true);
                                lockAnswer.notifyAll();
                                LOGGER.info("Consumed!");
                            }
                        }
                    } catch(Exception e) {
                        setActive(false);
                        LOGGER.log(Level.SEVERE, "Got an exception, asyncWrite not working", e);
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
    }

    public synchronized void closeConnection() {
        try {
            socket.close();
            setActive(false);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot close socket", e);
        }
    }
}