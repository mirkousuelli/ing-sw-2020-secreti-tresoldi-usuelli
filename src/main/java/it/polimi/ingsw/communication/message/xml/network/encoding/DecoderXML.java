package it.polimi.ingsw.communication.message.xml.network.encoding;

import it.polimi.ingsw.communication.message.*;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import java.beans.XMLDecoder;
import java.io.*;

public class DecoderXML {

    private final String XML_FILE;

    public DecoderXML(String path) {
        XML_FILE = path;
    }

    public Message decode() {

        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(XML_FILE)));
            Message decoded = (Message) decoder.readObject();

            if (decoded.getHeader() instanceof DemandType) {
                return new Demand((Demand) decoded);
            } else if (decoded.getHeader() instanceof AnswerType){
                return new Answer((Answer) decoded);
            }

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File " + XML_FILE + ".xml not found");
        }

        return null;
    }
}
