package it.polimi.ingsw.communication.message.xml.network.serialization;

import it.polimi.ingsw.communication.message.xml.network.stream.InputStreamXML;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DeserializerXML {
    private final int EOF = -1;
    public final static int FILE_SIZE = 6022386;

    private final File file;
    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;
    private InputStreamXML is;
    //private InputStream is;
    private Socket sock;
    public DeserializerXML(String pathFile, Socket sock) throws FileNotFoundException {
        this.file = new File (pathFile);
        this.sock = sock;
    }

    public void read() throws IOException {
        int bytesRead;
        int current = 0;

        //byte[] myByteArray = new byte[FILE_SIZE];
        byte[] myByteArray = new byte[FILE_SIZE];
        //byte[] correctArray = new byte[FILE_SIZE];
        fos = new FileOutputStream(file, false);
        bos = new BufferedOutputStream(fos);

        try {
            // receive file
            //is = new DataInputStream(sock.getInputStream());
            //is = sock.getInputStream();
            is = new InputStreamXML(sock.getInputStream());
            //int len = is.readInt();
            //System.out.println("RICEVUTO LEN: " + len);

            //do {
                bytesRead = is.read(myByteArray, current, (myByteArray.length - current));
                if (bytesRead >= 0)
                    current += bytesRead;
                System.out.println("RICEVUTO current: " + current);
                System.out.println("RICEVUTO bytesRead: " + bytesRead);
                System.out.println("RICEVUTO length: " + myByteArray.length);
                System.out.println("RICEVUTO contenuto: " + myByteArray);
            //} while (bytesRead > EOF);
            /*do {
                bytesRead = is.read(myByteArray, current, (myByteArray.length - current));
                if (bytesRead >= 0)
                    current += bytesRead;
            } while (bytesRead > EOF);*/
            /*(int j = len - 1;
            if (bytesRead != len) {
                System.out.println("ricevuta lunghezza diversa");
                for (int i = myByteArray.length - 1; i > myByteArray.length - len; i--) {
                    correctArray[j] = myByteArray[i];
                    j--;
                }
            } else {
                correctArray = myByteArray;
            }*/

            bos.write(myByteArray, 0, myByteArray.length);
            bos.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            //sock.shutdownInput();
            bos.close();
            fos.close();
        }
    }
}
