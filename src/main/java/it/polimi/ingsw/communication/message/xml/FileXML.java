package it.polimi.ingsw.communication.message.xml;

import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.xml.network.encoding.DecoderXML;
import it.polimi.ingsw.communication.message.xml.network.encoding.EncoderXML;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class FileXML {
    public final Object lockReceive;
    public final Object lockSend;
    private final ObjectOutputStream outStream;
    private final ObjectInputStream inStream;

    public FileXML(Socket sock) throws IOException {
        outStream = new ObjectOutputStream(sock.getOutputStream());
        inStream = new ObjectInputStream(sock.getInputStream());
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
