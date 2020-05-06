package it.polimi.ingsw.communication.message.xml;

import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.xml.network.encoding.DecoderXML;
import it.polimi.ingsw.communication.message.xml.network.encoding.EncoderXML;
import it.polimi.ingsw.communication.message.xml.network.serialization.DeserializerXML;
import it.polimi.ingsw.communication.message.xml.network.serialization.ReceiverXML;
import it.polimi.ingsw.communication.message.xml.network.serialization.SenderXML;
import it.polimi.ingsw.communication.message.xml.network.serialization.SerializerXML;
import it.polimi.ingsw.communication.message.xml.network.stream.InputStreamXML;
import it.polimi.ingsw.communication.message.xml.network.stream.OutputStreamXML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileXML {
    private final EncoderXML encoder;
    private final DecoderXML decoder;
    //private final SenderXML sender;
    //private final ReceiverXML receiver;
    //private final SerializerXML sender;
    //private final DeserializerXML receiver;
    public final Object lockReceive;
    public final Object lockSend;
    Document doc;
    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    private String pathFile;
    private final InputStream inStream;
    private final OutputStream outStream;

    public FileXML(String pathFile, Socket sock) throws IOException, ParserConfigurationException, SAXException {
        this.pathFile = pathFile;
        encoder = new EncoderXML(pathFile);
        decoder = new DecoderXML(pathFile);
        //sender = new SerializerXML(pathFile, sock);
        //receiver = new DeserializerXML(pathFile, sock);
        factory = DocumentBuilderFactory.newInstance();
        //factory.setIgnoringElementContentWhitespace(true);
        builder = factory.newDocumentBuilder();

        inStream = sock.getInputStream();
        outStream = sock.getOutputStream();

        lockReceive = new Object();
        lockSend = new Object();

    }

    public void send(Message message) throws IOException, TransformerConfigurationException, SAXException {
        synchronized (lockSend) {
            encoder.encode(message);
            doc = builder.parse(pathFile);
            SenderXML.send(doc, outStream);
        }
    }

    public Message receive() throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException {
        synchronized (lockReceive) {
            doc = ReceiverXML.receive(inStream);

            Transformer tr = TransformerFactory.newInstance().newTransformer();

            try {
                tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(pathFile)));
            } catch (TransformerException ex) {
                Logger.getLogger(ReceiverXML.class.getName()).log(Level.SEVERE, null, ex);
            }


            return decoder.decode();
        }
    }
}
