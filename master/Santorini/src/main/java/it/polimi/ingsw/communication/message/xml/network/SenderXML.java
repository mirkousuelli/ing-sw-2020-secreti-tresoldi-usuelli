package it.polimi.ingsw.communication.message.xml.network;

import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SenderXML {

    public static void send(Document toSend, OutputStream channel) throws TransformerConfigurationException, IOException {
        OutputStreamXML out = new OutputStreamXML(channel);

        StreamResult sr = new StreamResult(out);
        DOMSource ds = new DOMSource(toSend);
        Transformer tf = TransformerFactory.newInstance().newTransformer();

        try {
            tf.transform(ds, sr);
        } catch (TransformerException ex) {
            Logger.getLogger(SenderXML.class.getName()).log(Level.SEVERE, null, ex);
        }

        out.send();
    }
}
