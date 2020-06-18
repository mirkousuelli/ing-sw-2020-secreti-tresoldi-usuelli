package it.polimi.ingsw.client.view.gui.frame;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.panels.ManagerPanel;

import javax.swing.*;
import java.awt.*;

public class SantoriniFrame extends JFrame {
    private static final String TITLE = "Santorini";
    private final JPanel main;

    public SantoriniFrame(GUI gui) {
        super();

        setTitle(SantoriniFrame.TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        main = new ManagerPanel(gui);

        getContentPane().add(main);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    public ManagerPanel getMain() {
        return (ManagerPanel) main;
    }
}
