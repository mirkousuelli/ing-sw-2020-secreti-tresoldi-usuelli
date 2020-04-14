package it.polimi.ingsw.communication.message.xml.network;

import it.polimi.ingsw.communication.message.*;
import it.polimi.ingsw.communication.message.xml.AnswerXML;
import it.polimi.ingsw.communication.message.xml.DemandXML;
import it.polimi.ingsw.communication.message.xml.MessageXML;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MessageDecoderXML {

    private final String XML_FILE = "src/main/java/it/polimi/ingsw/communication/message/xml/message.xml";

    public MessageDecoderXML(String path) {
        //XML_FILE = path;
    }

    public Message decode() throws IOException {

        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(XML_FILE)));
            MessageXML decoded = (MessageXML) decoder.readObject();

            if (decoded.getHeader() instanceof DemandType) {
                return new Demand((DemandXML) decoded);
            } else if (decoded.getHeader() instanceof AnswerType){
                return new Answer((AnswerXML) decoded);
            }

        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File " + XML_FILE + ".xml not found");
        }

        return null;
    }

}
