package it.polimi.ingsw.communication.message.xml.network.encoding;

import it.polimi.ingsw.communication.message.*;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.xml.network.object.AnswerXML;
import it.polimi.ingsw.communication.message.xml.network.object.DemandXML;
import it.polimi.ingsw.communication.message.xml.network.object.MessageXML;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

    /*public Message receive(InputStream in) throws IOException {
        try {
            Document doc = DeserializeXML.receive(in);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result =  new StreamResult(new File(XML_FILE));
            transformer.transform(source, result);

            return this.decode();
        } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }*/
}
