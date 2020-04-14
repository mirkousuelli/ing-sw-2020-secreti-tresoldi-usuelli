package it.polimi.ingsw.communication.message.xml.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamXML extends ByteArrayInputStream {

    private DataInputStream inChannel;

    public InputStreamXML(InputStream inChannel) {
        super(new byte[2]);
        this.inChannel = new DataInputStream(inChannel);
    }

    public void receive() throws IOException {
        int i = inChannel.readInt();
        byte[] data = new byte[i];
        inChannel.read(data, 0, i);
        this.buf = data;
        this.count = i;
        this.mark = 0;
        this.pos = 0;
    }

}
