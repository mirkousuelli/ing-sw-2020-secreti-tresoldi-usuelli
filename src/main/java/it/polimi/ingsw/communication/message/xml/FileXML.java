package it.polimi.ingsw.communication.message.xml;

import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.xml.network.encoding.DecoderXML;
import it.polimi.ingsw.communication.message.xml.network.encoding.EncoderXML;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class that represents the FileXML that is created in ClientConnectionSocket and ServerClientHandlerSocket
 */
public class FileXML {
    public final Object lockReceive;
    public final Object lockSend;
    private final ObjectOutputStream outStream;
    private final ObjectInputStream inStream;

    /**
     * Constructor of the FileXML given the socket
     *
     * @param sock the socket which the file is created from
     * @throws IOException if there was an error whit I/O
     */
    public FileXML(Socket sock) throws IOException {
        outStream = new ObjectOutputStream(sock.getOutputStream());
        inStream = new ObjectInputStream(sock.getInputStream());
        lockReceive = new Object();
        lockSend = new Object();
    }

    /**
     * Method that sends the given message by encoding it
     *
     * @param message the message that is sent
     */
    public void send(Message message) {
        synchronized (lockSend) {
            EncoderXML.encode(message, outStream);
        }
    }

    /**
     * Method that receives the message by decoding it
     *
     * @return the message received
     */
    public Message receive() {
        synchronized (lockReceive) {
            return DecoderXML.decode(inStream);
        }
    }
}
