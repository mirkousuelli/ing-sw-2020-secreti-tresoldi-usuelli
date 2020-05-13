package it.polimi.ingsw.communication.message.xml;

import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.xml.network.encoding.DecoderXML;
import it.polimi.ingsw.communication.message.xml.network.encoding.EncoderXML;
import java.io.*;
import java.net.Socket;

public class FileXML {
    public final Object lockReceive;
    public final Object lockSend;
    private final ObjectInputStream inStream;
    private final ObjectOutputStream outStream;

    public FileXML(Socket sock) throws IOException {
        inStream = new ObjectInputStream(sock.getInputStream());
        outStream = new ObjectOutputStream(sock.getOutputStream());
        lockReceive = new Object();
        lockSend = new Object();
    }

    public void send(Message message) throws IOException {
        synchronized (lockSend) {
            EncoderXML.encode(message, outStream);
        }
    }

    public Message receive() {
        synchronized (lockReceive) {
            return DecoderXML.decode(inStream);
        }
    }
}
