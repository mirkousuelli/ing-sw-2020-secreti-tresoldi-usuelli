package it.polimi.ingsw.communication.message.xml.network.encoding;

import it.polimi.ingsw.communication.message.Message;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncoderXML {
    private static final Logger LOGGER = Logger.getLogger(EncoderXML.class.getName());

    public static void encode(Message msg, ObjectOutputStream out) {
        OutputStream memStream = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(memStream);
        encoder.writeObject(msg);
        encoder.close();
        String xmlString = memStream.toString();

        try {
            out.writeObject(xmlString);
            out.flush();
        } catch (Exception e) {
            if (!(e instanceof IOException) && !e.getMessage().equals("Connection reset"))
                LOGGER.log(Level.SEVERE, "Got an unexpected exception", e);
        }
    }
}
