package it.polimi.ingsw.communication.message.xml.network.encoding;

import it.polimi.ingsw.communication.message.*;
import java.beans.XMLEncoder;
import java.io.*;

public class EncoderXML {

    private final String XML_FILE;

    public EncoderXML(String path) {
        XML_FILE = path;
    }

    public void encode(Message message) throws IOException {
        try{
            FileOutputStream fos = new FileOutputStream(XML_FILE);
            XMLEncoder encoder = new XMLEncoder(fos);

            encoder.setExceptionListener(e -> System.out.println("Exception! :" + e.toString()));

            encoder.writeObject(message);
            encoder.close();
            fos.close();

        }catch(FileNotFoundException fileNotFound){
            System.out.println("ERROR: While Creating or Opening the File" + XML_FILE + ".xml");
        }
    }
}
