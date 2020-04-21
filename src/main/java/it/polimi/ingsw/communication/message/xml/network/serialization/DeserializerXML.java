package it.polimi.ingsw.communication.message.xml.network.serialization;

import java.io.*;
import java.net.Socket;

public class DeserializerXML {
    private final int EOF = -1;
    public final static int FILE_SIZE = 6022386;

    private final File file;
    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;
    private InputStream is;
    private Socket sock;

    public DeserializerXML(String pathFile, Socket sock) throws FileNotFoundException {
        this.file = new File (pathFile);
        this.sock = sock;
    }

    public void read() throws IOException {
        int bytesRead;
        int current = 0;

        byte[] myByteArray = new byte[FILE_SIZE];
        fos = new FileOutputStream(file);
        bos = new BufferedOutputStream(fos);

        try {
            // receive file
            is = sock.getInputStream();
            bytesRead = is.read(myByteArray, 0, myByteArray.length);
            current = bytesRead;

            do {
                bytesRead = is.read(myByteArray, current, (myByteArray.length - current));
                if (bytesRead >= 0)
                    current += bytesRead;
            } while (bytesRead > EOF);

            bos.write(myByteArray, 0, current);
            bos.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (bos != null) bos.close();
            if (fos != null) fos.close();
            //if (is != null) is.close();
        }
    }
}