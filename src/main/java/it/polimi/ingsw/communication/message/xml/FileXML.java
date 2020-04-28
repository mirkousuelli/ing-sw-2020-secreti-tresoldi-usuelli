package it.polimi.ingsw.communication.message.xml;

import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.xml.network.encoding.DecoderXML;
import it.polimi.ingsw.communication.message.xml.network.encoding.EncoderXML;
import it.polimi.ingsw.communication.message.xml.network.serialization.DeserializerXML;
import it.polimi.ingsw.communication.message.xml.network.serialization.SerializerXML;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

public class FileXML {
    private final EncoderXML encoder;
    private final DecoderXML decoder;
    private final SerializerXML sender;
    private final DeserializerXML receiver;
    public final Object lockReceive;
    public final Object lockSend;

    public FileXML(String pathFile, Socket sock) throws FileNotFoundException {
        encoder = new EncoderXML(pathFile);
        decoder = new DecoderXML(pathFile);
        sender = new SerializerXML(pathFile, sock);
        receiver = new DeserializerXML(pathFile, sock);

        lockReceive = new Object();
        lockSend = new Object();

    }

    public void send(Message message) throws IOException {
        synchronized (lockSend) {
            encoder.encode(message);
            sender.write();
        }
    }

    public Message receive() throws IOException {
        synchronized (lockReceive) {
            receiver.read();
            return decoder.decode();
        }
    }
}
