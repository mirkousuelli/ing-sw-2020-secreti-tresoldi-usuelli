package it.polimi.ingsw.communication.message.xml.network.serialization;

import java.io.*;
import java.net.Socket;

public class SerializerXML {
    private final int EOF = -1;

    private final File file;
    private FileInputStream fis;
    private BufferedInputStream bis;
    private OutputStream os;
    private final Socket sock;

    public SerializerXML(String pathFile, Socket sock) throws FileNotFoundException {
        this.file = new File (pathFile);
        this.sock = sock;
    }

    public void write() throws IOException {
        int end;

        byte [] myByteArray  = new byte [(int)file.length()];
        fis = new FileInputStream(file);
        bis = new BufferedInputStream(fis);

        try {
            do {
                end = bis.read(myByteArray,0,myByteArray.length);
                if (end != this.EOF) {
                    try {
                        // send file
                        os = sock.getOutputStream();

                        os.write(myByteArray,0,myByteArray.length);
                        os.flush();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } while (end != this.EOF);
        }
        finally {
            if (bis != null) bis.close();
            if (fis != null) fis.close();
            //if (os != null) os.close();
        }
    }
}
