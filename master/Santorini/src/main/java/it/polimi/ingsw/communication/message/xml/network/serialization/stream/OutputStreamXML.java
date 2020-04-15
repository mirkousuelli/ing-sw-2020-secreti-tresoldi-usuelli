package it.polimi.ingsw.communication.message.xml.network.serialization.stream;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamXML extends ByteArrayOutputStream {

    private final DataOutputStream outChannel;

    public OutputStreamXML(OutputStream outChannel) {
        super();
        this.outChannel = new DataOutputStream(outChannel);
    }

    public void send() throws IOException {
        byte[] data = toByteArray();
        outChannel.writeInt(data.length);
        outChannel.write(data);
        reset();
    }

}
