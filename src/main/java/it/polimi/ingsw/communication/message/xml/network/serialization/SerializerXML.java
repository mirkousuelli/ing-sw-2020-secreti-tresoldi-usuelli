package it.polimi.ingsw.communication.message.xml.network.serialization;

import it.polimi.ingsw.communication.message.xml.network.stream.OutputStreamXML;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SerializerXML {
    private final int EOF = -1;

    private final File file;
    private FileInputStream fis;
    private BufferedInputStream bis;
    //private OutputStream os;
    private OutputStreamXML os;
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
                System.out.println("INVIATO Byte read: " + end);
                if (end != this.EOF) {
                    try {
                        // send file
                        os = new OutputStreamXML(sock.getOutputStream());
                        //os = sock.getOutputStream();
                        //System.out.println("SERIALIZER length: " + myByteArray.length);
                        //os.writeInt(myByteArray.length);
                        //os.flush();

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
            //sock.shutdownInput();
            bis.close();
            fis.close();
        }
    }
}
