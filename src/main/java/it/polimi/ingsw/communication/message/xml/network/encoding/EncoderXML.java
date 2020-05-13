package it.polimi.ingsw.communication.message.xml.network.encoding;

import it.polimi.ingsw.communication.message.*;
import java.beans.XMLEncoder;
import java.io.*;

public class EncoderXML {
    public static void encode(Message msg, ObjectOutputStream out) throws IOException {
        OutputStream memStream = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(memStream);
        encoder.writeObject(msg);
        encoder.close();
        String xmlString = memStream.toString();

        out.writeObject(xmlString);
        out.flush();
    }
}
