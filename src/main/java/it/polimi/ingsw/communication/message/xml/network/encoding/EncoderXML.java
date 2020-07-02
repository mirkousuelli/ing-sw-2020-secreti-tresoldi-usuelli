package it.polimi.ingsw.communication.message.xml.network.encoding;

import it.polimi.ingsw.communication.message.Message;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that represents the encoder which has to encode the message that is being sent
 */
public class EncoderXML {
    private static final Logger LOGGER = Logger.getLogger(EncoderXML.class.getName());

    /**
     * Method that encodes the given message
     *
     * @param msg the message to encode
     * @param out the object output stream that serializes objects
     */
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
