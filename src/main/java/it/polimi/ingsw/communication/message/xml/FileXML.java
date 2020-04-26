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
    private Message msg;

    public FileXML(String pathFile, Socket sock) throws FileNotFoundException {
        this.encoder = new EncoderXML(pathFile);
        this.decoder = new DecoderXML(pathFile);
        this.sender = new SerializerXML(pathFile, sock);
        this.receiver = new DeserializerXML(pathFile, sock);
    }

    public void send(Message message) throws IOException {
        this.msg = message;
        this.encoder.encode(message);
        this.sender.write();
    }

    public Message receive() throws IOException {
        this.receiver.read();
        return this.decoder.decode();
    }

    public Message getMessage() {
        return this.msg;
    }
    
    public boolean isChanged() throws IOException {
        Message toMatch = this.receive();

        if (this.msg.equals(toMatch)) {
            return false;
        } else {
            this.msg = toMatch;
            return true;
        }
    }

}
