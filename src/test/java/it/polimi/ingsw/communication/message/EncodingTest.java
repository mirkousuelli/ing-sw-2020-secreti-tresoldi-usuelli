package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.communication.message.xml.network.encoding.DecoderXML;
import it.polimi.ingsw.communication.message.xml.network.encoding.EncoderXML;
import it.polimi.ingsw.server.model.cards.gods.God;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                //ReducedAnswerCell cell = new ReducedAnswerCell(1,2, ReducedLevel.GROUND, context,);
                /*cell.setX(1);
                cell.setY(2);*/
                List<God> list = new ArrayList<>();
                list.add(God.APOLLO);
                list.add(God.ARTEMIS);
                list.add(God.DEMETER);
                toEncode = new Answer<>(header, context, new ReducedDeck(list));
                encoderXML.encode(toEncode);
                toDecode = decoderXML.decode();

                assertEquals(toDecode.getHeader(), toEncode.getHeader());
                assertEquals(((Answer)toDecode).getContext(), ((Answer)toEncode).getContext());
                //assertEquals(toDecode.getPayload(), toEncode.getPayload());
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
