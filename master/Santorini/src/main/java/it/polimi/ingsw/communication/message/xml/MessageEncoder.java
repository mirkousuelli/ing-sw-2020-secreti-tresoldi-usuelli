package it.polimi.ingsw.communication.message.xml;

import it.polimi.ingsw.communication.message.*;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class MessageEncoder {

    final static String FILE_MESSAGE_XML = "src/main/java/it/polimi/ingsw/communication/message/xml/message.xml";

    public static void main(String[] args) throws IOException
    {
        boolean demand = false;
        Message decoded;

        if (demand) {
            Message message = new Demand<>(DemandType.JOIN_GAME, "1234");
            MessageXML toSend = message.messageToXML();

            encode ( toSend );
            decoded = decode();
        } else {
            Message message = new Answer<>(AnswerType.SUCCESS, DemandType.JOIN_GAME, "1234");
            MessageXML toSend = message.messageToXML();

            encode ( toSend );
            decoded = decode();
        }
    }

    public static void encode(MessageXML message) throws IOException
    {
        /*FileOutputStream fos = new FileOutputStream("message.xml");
        XMLEncoder encoder = new XMLEncoder(fos);

        encoder.setExceptionListener(new ExceptionListener() {
            public void exceptionThrown(Exception e) {
                System.out.println("Exception! :" + e.toString());
            }
        });

        encoder.writeObject(message);
        encoder.close();
        fos.close();*/

        try{
            FileOutputStream fos = new FileOutputStream(FILE_MESSAGE_XML);
            XMLEncoder encoder = new XMLEncoder(fos);
            encoder.setExceptionListener(new ExceptionListener() {
                public void exceptionThrown(Exception e) {
                    System.out.println("Exception! :"+e.toString());
                }
            });
            encoder.writeObject(message);
            encoder.close();
            fos.close();

        }catch(FileNotFoundException fileNotFound){
            System.out.println("ERROR: While Creating or Opening the File message.xml");
        }
    }

    public static Message decode() throws IOException {

        /*FileInputStream fis = new FileInputStream("message.xml");
        XMLDecoder decoder = new XMLDecoder(fis);
        Demand decodedMessage = (Demand) decoder.readObject();

        decoder.close();
        fis.close();

        return decodedMessage;*/

        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(FILE_MESSAGE_XML)));
            MessageXML decoded = (MessageXML) decoder.readObject();

            if (decoded.getHeader() instanceof DemandType) {
                return new Demand((DemandXML) decoded);
            } else  if (decoded.getHeader() instanceof AnswerType){
                return new Answer((AnswerXML) decoded);
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File message.xml not found");
        }

        return null;
    }

}
