package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.xml.FileXML;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionSocket<S> implements ClientConnection<S>, Runnable {
    private final Socket socket;
    private final FileXML file;

    private ClientView<S> clientView;
    private boolean isActive;
    private boolean isChanged;
    private Answer<S> answer;

    private final String FILEXML = "src/main/java/it/polimi/ingsw/client/network/message/message.xml";
    private static final Logger LOGGER = Logger.getLogger(ClientConnectionSocket.class.getName());

    public ClientConnectionSocket(String ip, int port, ClientView<S> clientView) throws IOException {
        socket = new Socket(ip, port);
        file = new FileXML(FILEXML, socket);
        this.clientView = clientView;

        setActive(false);
        setChanged(false);
    }

    public ClientConnectionSocket(String ip, int port) throws IOException {
        this(ip, port, null);
    }

    @Override
    public void setClientView(ClientView<S> clientView) {
        this.clientView = clientView;
    }

    @Override
    public synchronized Answer<S> getAnswer() {
        return answer;
    }

    private synchronized void setAnswer(Answer<S> answer) {
        this.answer = answer;
    }

    @Override
    public synchronized boolean isActive() {
        return isActive;
    }

    private synchronized void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public synchronized boolean isChanged() {
        return isChanged;
    }

    @Override
    public synchronized void setChanged(boolean changed) {
        isChanged = changed;
    }

    private Thread asyncReadFromSocket() {
        Thread t = new Thread(
                () -> {
                        try {
                            Answer<S> temp;
                            while (isActive()) {
                                synchronized (file.lockReceive) {
                                    try {
                                        temp = (Answer<S>) file.receive();
                                    }
                                    catch(IOException e) {
                                        LOGGER.log(Level.SEVERE, "Got an IOException", e);
                                        break;
                                    }
                                }

                                LOGGER.info("Receiving...");
                                synchronized (this) {
                                    setAnswer(temp);
                                    LOGGER.info("Received!");

                                    setChanged(true);
                                    this.notifyAll();
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
                                    demand = clientView.fetchDemand();
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

    @Override
    public void run() {
        setActive(true);
        setChanged(false);

        try {
            Thread read = asyncReadFromSocket();
            Thread write = asyncWriteToSocket();
            read.join();
            write.join();
        } catch (InterruptedException | NoSuchElementException e) {
            LOGGER.log(Level.SEVERE, "Connection closed from the client side", e);
        } finally {
            setActive(false);
            closeConnection();
        }
    }

    @Override
    public synchronized void closeConnection() {
        try {
            socket.close();
            isActive = false;
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot close socket", e);
        }
    }
}