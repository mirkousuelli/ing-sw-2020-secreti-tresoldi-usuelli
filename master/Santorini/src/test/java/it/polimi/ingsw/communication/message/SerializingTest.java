package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.xml.network.ReceiverXML;
import it.polimi.ingsw.communication.message.xml.network.SenderXML;
import it.polimi.ingsw.communication.message.xml.network.serialization.DeserializeXML;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializingTest {
    private final String SEND_FILE = "src/test/java/it/polimi/ingsw/communication/message/message_sent.xml";
    private final String RECEIVE_FILE = "src/test/java/it/polimi/ingsw/communication/message/message_received.xml";
    private final SenderXML senderXML = new SenderXML(SEND_FILE);
    private final ReceiverXML receiverXML = new ReceiverXML(RECEIVE_FILE);
    private final String payload = "1234";

    @Test
    public void AnswerTest() throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException {
        Message toSend;
        Message toRead;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        for (AnswerType header : AnswerType.values()) {
            for (DemandType context : DemandType.values()) {
                // test
                toSend = new Answer<>(header, context, this.payload);
                senderXML.send(toSend, os);

                try {
                    toRead = receiverXML.receive(is);
                } catch (EOFException e){
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    throw new EOFException();
                }

                assertEquals(toRead.getHeader(), toSend.getHeader());
                assertEquals(((Answer)toRead).getContext(), ((Answer)toSend).getContext());
                assertEquals(toRead.getPayload(), toSend.getPayload());

            }
        }
    }

    @Test
    public void DemandTest() throws IOException, SAXException, ParserConfigurationException, TransformerConfigurationException {
        Message toSend;
        Message toRead;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        for (DemandType header : DemandType.values()) {
            // test
            toSend = new Demand<>(header, this.payload);
            senderXML.send(toSend, os);
            toRead = receiverXML.receive(is);

            assertEquals(toRead.getHeader(), toSend.getHeader());
            assertEquals(toRead.getPayload(), toSend.getPayload());
        }
    }
}
