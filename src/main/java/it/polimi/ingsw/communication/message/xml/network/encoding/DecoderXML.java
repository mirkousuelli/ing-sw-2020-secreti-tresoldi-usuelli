package it.polimi.ingsw.communication.message.xml.network.encoding;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DecoderXML {
    private static final Logger LOGGER = Logger.getLogger(DecoderXML.class.getName());

    public static Message decode(ObjectInputStream in) {
        String xmlString;

        try {
            xmlString = (String) in.readObject();
        } catch (Exception e) {
            if (!(e instanceof EOFException) && !(e instanceof SocketException) && !e.getMessage().equals("Connection reset"))
                LOGGER.log(Level.SEVERE, "Got an unexpected exception", e);
            return null;
        }

        XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
        Message message = (Message) decoder.readObject();

        if (message.getHeader() instanceof DemandType)
            return new Demand((Demand) message);
        else if (message.getHeader() instanceof AnswerType)
            return new Answer((Answer) message);

        return null;
    }
}
