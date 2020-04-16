package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.xml.FileXML;
import it.polimi.ingsw.communication.message.xml.network.encoding.DecoderXML;
import it.polimi.ingsw.communication.message.xml.network.encoding.EncoderXML;
import org.junit.jupiter.api.Test;

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializationTest {
    public final static int SOCKET_PORT = 13267;
    public final static String SERVER = "127.0.0.1";
    private final String SEND_FILE = "src/test/java/it/polimi/ingsw/communication/message/message_sent.xml";
    private final String RECEIVE_FILE = "src/test/java/it/polimi/ingsw/communication/message/message_received.xml";
    private FileXML sendFile;
    private FileXML receiveFile;
    private final String payload = "1234";
    private ServerSocket serverSocket;
    private Socket handlerSocket;
    private Socket clientSocket;

    @Test
    public void AnswerTest() throws IOException
    {
        Message toSend;
        Message toReceive;

        serverSocket = new ServerSocket(SOCKET_PORT);
        clientSocket = new Socket(SERVER, SOCKET_PORT);
        handlerSocket = serverSocket.accept();

        sendFile = new FileXML(SEND_FILE, handlerSocket);
        receiveFile = new FileXML(RECEIVE_FILE, clientSocket);

        //for (AnswerType header : AnswerType.values()) {
            //for (DemandType context : DemandType.values()) {
                // test
                toSend = new Answer<>(AnswerType.SUCCESS, DemandType.BUILD, this.payload);
                sendFile.write(toSend);
                toReceive = receiveFile.read();

                assertEquals(toReceive.getHeader(), toSend.getHeader());
                assertEquals(((Answer)toReceive).getContext(), ((Answer)toSend).getContext());
                assertEquals(toReceive.getPayload(), toSend.getPayload());
            //}
        //}
        clientSocket.close();
        handlerSocket.close();
        serverSocket.close();
    }

    /*@Test
    public void DemandTest() throws IOException
    {
        Message toEncode;
        Message toDecode;

        //for (DemandType header : DemandType.values()) {
            // test
            toEncode = new Demand<>(header, this.payload);
            encoderXML.encode(toEncode);
            toDecode = decoderXML.decode();

            assertEquals(toDecode.getHeader(), toEncode.getHeader());
            assertEquals(toDecode.getPayload(), toEncode.getPayload());
        //}
    }*/
}
