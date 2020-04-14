package it.polimi.ingsw.communication.message.xml.network;

import it.polimi.ingsw.communication.message.*;
import it.polimi.ingsw.communication.message.xml.MessageXML;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class MessageEncoderXML {

    private final String XML_FILE; // = "src/main/java/it/polimi/ingsw/communication/message/xml/message.xml";

    public MessageEncoderXML(String path) {
        XML_FILE = path;
    }

    public void encode(Message message) throws IOException
    {
        try{
            FileOutputStream fos = new FileOutputStream(XML_FILE);
            XMLEncoder encoder = new XMLEncoder(fos);
            MessageXML toEncode = message.messageToXML();

            encoder.setExceptionListener(new ExceptionListener() {
                public void exceptionThrown(Exception e) {
                    System.out.println("Exception! :"+e.toString());
                }
            });


            encoder.writeObject(toEncode);
            encoder.close();
            fos.close();

        }catch(FileNotFoundException fileNotFound){
            System.out.println("ERROR: While Creating or Opening the File" + XML_FILE + ".xml");
        }
    }

    /*public static void main(String[] args) throws IOException
    {
        boolean demand = false;
        Message decoded;

        if (demand) {
            Message toSend = new Demand<>(DemandType.JOIN_GAME, "1234");

            encode ( toSend.messageToXML() );
            decoded = decode();
        } else {
            Message toSend = new Answer<>(AnswerType.SUCCESS, DemandType.JOIN_GAME, "1234");

            encode ( toSend.messageToXML() );
            decoded = decode();
        }
    }*/

}
