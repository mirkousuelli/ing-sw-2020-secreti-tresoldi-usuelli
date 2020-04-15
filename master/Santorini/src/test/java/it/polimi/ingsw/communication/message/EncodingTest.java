package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.xml.network.ReceiverXML;
import it.polimi.ingsw.communication.message.xml.network.SenderXML;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class EncodingTest {
    private final String FILE = "src/test/java/it/polimi/ingsw/communication/message/message_sent.xml";
    private final SenderXML encoderXML = new SenderXML(FILE);
    private final ReceiverXML decoderXML = new ReceiverXML(FILE);
    private final String payload = "1234";

    @Test
    public void AnswerTest() throws IOException
    {
        Message toSend;
        Message decoded;

        for (AnswerType header : AnswerType.values()) {
            for (DemandType context : DemandType.values()) {
                // test
                toSend = new Answer<>(header, context, this.payload);
                encoderXML.encode(toSend);
                decoded = decoderXML.decode();

                assertEquals(decoded.getHeader(), toSend.getHeader());
                assertEquals(((Answer)decoded).getContext(), ((Answer)toSend).getContext());
                assertEquals(decoded.getPayload(), toSend.getPayload());
            }
        }
    }

    @Test
    public void DemandTest() throws IOException
    {
        Message toSend;
        Message decoded;

        for (DemandType header : DemandType.values()) {
            // test
            toSend = new Demand<>(header, this.payload);
            encoderXML.encode(toSend);
            decoded = decoderXML.decode();

            assertEquals(decoded.getHeader(), toSend.getHeader());
            assertEquals(decoded.getPayload(), toSend.getPayload());
        }
    }
}
