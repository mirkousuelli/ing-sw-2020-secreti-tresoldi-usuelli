package it.polimi.ingsw.communication.message.xml.network.encoding;

import it.polimi.ingsw.communication.message.*;
import it.polimi.ingsw.communication.message.xml.network.object.MessageXML;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.beans.ExceptionListener;
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
            MessageXML toEncode = message.messageToXML();

            encoder.setExceptionListener(new ExceptionListener() {
                public void exceptionThrown(Exception e) {
                    System.out.println("Exception! :" + e.toString());
                }
            });

            encoder.writeObject(toEncode);
            encoder.close();
            fos.close();

        }catch(FileNotFoundException fileNotFound){
            System.out.println("ERROR: While Creating or Opening the File" + XML_FILE + ".xml");
        }
    }

    /*public void send(Message message, OutputStream out) throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException {
        this.encode(message);

        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        Document doc = docBuilder.parse(XML_FILE);

        SerializeXML.send(doc, out);
    }*/

}
