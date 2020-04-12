package it.polimi.ingsw.communication.message.xml;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.DemandType;
import it.polimi.ingsw.communication.message.Message;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class MessageEncoder {

    public static void main(String[] args) throws IOException
    {
        Demand<Integer> message = new Demand<Integer>(DemandType.JOIN_GAME, 1234);

        //Before
        System.out.println(message);
        encode ( message );
        Demand<Integer> messageDecoded = (Demand<Integer>) decode();
        //After
        System.out.println(messageDecoded);
    }

    public static void encode(Demand message) throws IOException
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
            XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("message.xml")));
            encoder.writeObject(message);
            encoder.close();
        }catch(FileNotFoundException fileNotFound){
            System.out.println("ERROR: While Creating or Opening the File message.xml");
        }
    }

    public static Demand decode() throws IOException {

        /*FileInputStream fis = new FileInputStream("message.xml");
        XMLDecoder decoder = new XMLDecoder(fis);
        Demand decodedMessage = (Demand) decoder.readObject();

        decoder.close();
        fis.close();

        return decodedMessage;*/
        Demand decoded = null;
        try {
            XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream("message.xml")));
            decoded = (Demand) decoder.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: File message.xml not found");
        }

        return decoded;
    }

}
