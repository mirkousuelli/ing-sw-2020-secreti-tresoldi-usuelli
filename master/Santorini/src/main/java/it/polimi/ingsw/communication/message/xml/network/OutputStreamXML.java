package it.polimi.ingsw.communication.message.xml.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamXML extends ByteArrayOutputStream {

    private DataOutputStream outChannel;

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

    /*public void flush() throws IOException {
        this.outChannel.flush();
    }*/

}
