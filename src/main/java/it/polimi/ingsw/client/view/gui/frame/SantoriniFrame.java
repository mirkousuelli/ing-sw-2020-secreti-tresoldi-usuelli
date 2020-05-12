package it.polimi.ingsw.client.view.gui.frame;

import it.polimi.ingsw.client.view.gui.panels.*;
import javax.swing.*;
import java.awt.*;

public class SantoriniFrame extends JFrame {
    private final String TITLE = "Santorini";
    private JPanel main;

    public SantoriniFrame() {
        super();

        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        main = new ManagerPanel();

        getContentPane().add(main);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }
}
