package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.xml.network.encoding.DecoderXML;
import it.polimi.ingsw.communication.message.xml.network.encoding.EncoderXML;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class EncodingTest {
    private final String FILE = "src/test/java/it/polimi/ingsw/communication/message/message_sent.xml";
    private final EncoderXML encoderXML = new EncoderXML(FILE);
    private final DecoderXML decoderXML = new DecoderXML(FILE);
    private final String payload = "1234";

    @Test
    public void AnswerTest() throws IOException
    {
        Message toEncode;
        Message toDecode;

        for (AnswerType header : AnswerType.values()) {
            for (DemandType context : DemandType.values()) {
                // test
                toEncode = new Answer<>(header, context, this.payload);
                encoderXML.encode(toEncode);
                toDecode = decoderXML.decode();

                assertEquals(toDecode.getHeader(), toEncode.getHeader());
                assertEquals(((Answer)toDecode).getContext(), ((Answer)toEncode).getContext());
                assertEquals(toDecode.getPayload(), toEncode.getPayload());
            }
        }
    }

    @Test
    public void DemandTest() throws IOException
    {
        Message toEncode;
        Message toDecode;

        for (DemandType header : DemandType.values()) {
            // test
            toEncode = new Demand<>(header, this.payload);
            encoderXML.encode(toEncode);
            toDecode = decoderXML.decode();

            assertEquals(toDecode.getHeader(), toEncode.getHeader());
            assertEquals(toDecode.getPayload(), toEncode.getPayload());
        }
    }
}
