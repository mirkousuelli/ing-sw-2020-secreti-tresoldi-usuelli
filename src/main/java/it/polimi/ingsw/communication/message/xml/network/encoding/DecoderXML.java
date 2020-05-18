package it.polimi.ingsw.communication.message.xml.network.encoding;

import it.polimi.ingsw.communication.message.*;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import java.beans.XMLDecoder;
import java.io.*;

public class DecoderXML {
    public static Message decode(ObjectInputStream in) {
        try {
            String xmlString;
            try {
                xmlString = (String) in.readObject();
            } catch (EOFException e) {
                return null;
            }
            XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
            Message message = (Message) decoder.readObject();

            if (message.getHeader() instanceof DemandType) {
                return new Demand((Demand) message);
            } else if (message.getHeader() instanceof AnswerType){
                return new Answer((Answer) message);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
