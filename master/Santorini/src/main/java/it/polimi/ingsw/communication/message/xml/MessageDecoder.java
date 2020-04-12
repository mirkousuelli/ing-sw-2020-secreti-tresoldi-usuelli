package it.polimi.ingsw.communication.message.xml;

import it.polimi.ingsw.communication.message.Message;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.IOException;

public class MessageDecoder {

    public Message deserializeFromXML() throws IOException {

        FileInputStream fis = new FileInputStream("message.xml");
        XMLDecoder decoder = new XMLDecoder(fis);
        Message decodedMessage = (Message) decoder.readObject();

        decoder.close();
        fis.close();

        return decodedMessage;
    }

}
