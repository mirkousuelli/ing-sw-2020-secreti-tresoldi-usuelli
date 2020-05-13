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
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionSocket<S> extends SantoriniRunnable<S> implements ClientConnection<S> {
    private final Socket socket;
    private final FileXML file;

    private ClientView<S> clientView;
    private final List<Answer<S>> buffer;

    private static final Random random = new SecureRandom();
    private final String path = "src/main/java/it/polimi/ingsw/client/network/message/message" + random.nextInt() + ".xml";
    private static final Logger LOGGER = Logger.getLogger(ClientConnectionSocket.class.getName());

    public ClientConnectionSocket(String ip, int port, ClientView<S> clientView) throws IOException, ParserConfigurationException, SAXException {
        super();
        socket = new Socket(ip, port);
        file = new FileXML(socket);
        this.clientView = clientView;
        buffer = new LinkedList<>();
    }

    public ClientConnectionSocket(String ip, int port) throws IOException, ParserConfigurationException, SAXException {
        this(ip, port, null);
    }

    @Override
    public void setClientView(ClientView<S> clientView) {
        synchronized (this) {
            this.clientView = clientView;
        }
    }

    @Override
    public Answer<S> getAnswer() {
        Answer<S> answer;

        synchronized (buffer) {
            answer = buffer.remove(0);
        }

        return answer;
    }

    @Override
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
                            Answer<S> temp;
                            while (isActive()) {
                                synchronized (file.lockReceive) {
                                    temp = (Answer<S>) file.receive();
                                }

                                if(temp.getHeader().equals(AnswerType.CLOSE))
                                    setActive(false);

                                LOGGER.info("Queueing...");
                                synchronized (buffer) {
                                    buffer.add(temp);
                                    LOGGER.info("Queued!");
                                    buffer.notifyAll();
                                    LOGGER.info("READ");
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
                                synchronized (file.lockSend) {
                                    try {
                                        file.send(demand);
                                    }
                                    catch(IOException e) {
                                        LOGGER.log(Level.SEVERE, "Got an IOException", e);
                                        break;
                                    }
                                }
                                LOGGER.info("Sent!");
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
                                while (!clientView.isFree()) clientView.lockFree.wait();
                                clientView.setFree(false);
                            }

                            synchronized (buffer) {
                                while (!hasAnswer()) buffer.wait();
                            }

                            LOGGER.info("Consuming...");
                            synchronized (this) {
                                setChanged(true);
                                this.notifyAll();
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

    @Override
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