package it.polimi.ingsw.client.view.cli;

import java.io.OutputStream;
import java.io.PrintStream;

public class SantoriniPrintStream extends PrintStream {

    public SantoriniPrintStream(OutputStream out) {
        super(out, true);
    }
}
