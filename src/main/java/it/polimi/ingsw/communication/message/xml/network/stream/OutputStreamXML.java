package it.polimi.ingsw.communication.message.xml.network.stream;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamXML extends ByteArrayOutputStream {
    private DataOutputStream outchannel;

    public OutputStreamXML (OutputStream outchannel) {
        super();
        this.outchannel = new DataOutputStream(outchannel);
    }

    public void send() throws IOException {
        byte[] data = toByteArray();
        outchannel.writeInt(data.length);
        outchannel.flush();
        outchannel.write(data);
        outchannel.flush();
        reset();
    }
}
