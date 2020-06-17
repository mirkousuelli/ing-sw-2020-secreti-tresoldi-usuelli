package it.polimi.ingsw.client.view.gui.frame;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.panels.ManagerPanel;

import javax.swing.*;
import java.awt.*;

public class SantoriniFrame extends JFrame {

    private final String TITLE = "Santorini";
    private JPanel main;

    public SantoriniFrame(GUI gui) {
        super();

        setTitle(TITLE);
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
