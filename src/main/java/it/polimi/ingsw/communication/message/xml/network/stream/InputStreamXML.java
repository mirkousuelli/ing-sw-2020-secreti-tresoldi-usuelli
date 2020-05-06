package it.polimi.ingsw.communication.message.xml.network.stream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamXML extends ByteArrayInputStream {
    private DataInputStream inchannel;

    public InputStreamXML (InputStream inchannel) {
        super(new byte[2]);
        this.inchannel = new DataInputStream(inchannel);
    }

    public void receive() throws IOException {
        int i = inchannel.readInt();
        byte[] data = new byte[i];
        inchannel.read(data, 0, i);
        this.buf = data;
        this.count = i;
        this.mark = 0;
        this.pos = 0;
    }
}
