package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.gui.windows.StartWindow;

import javax.swing.*;

public class GUI<S>/* extends ClientView<S>*/ {

    //private final GUIScanner<S> in;
    //private final GUIPrinter<S> out;

    public GUI() {
    }

    public GUI(ClientModel<S> clientModel) {
        //super(clientModel);
        //out = new GUIPrinter<>(System.out, clientModel, this);
        //in = new GUIScanner<>(System.in, out, clientModel);
    }

    public void createAndStartGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new StartWindow("Santorini");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /*@Override
    public void run() {
        //not implemented yet
    }*/
}
