package it.polimi.ingsw.client.view.gui.frame;

import it.polimi.ingsw.client.view.gui.panels.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SantoriniFrame extends JFrame implements ActionListener {
    private final String TITLE = "Santorini";
    private SantoriniPanel currentPanel;
    JPanel cards;
    JButton button1, button2, button3, button4, button5, button6;
    CardLayout cardLayout;

    public SantoriniFrame() {
        super();

        //currentPanel = new StartPanel();
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        //getContentPane().add((JPanel) currentPanel);

        button1 = new JButton("PLAY");
        button2 = new JButton("CREATE");
        button3 = new JButton("TURN");
        button4 = new JButton("PLAY AGAIN");
        button5 = new JButton("PLAY AGAIN");
        button6 = new JButton("PLAY AGAIN");

        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
        button4.addActionListener(this);
        button5.addActionListener(this);
        button6.addActionListener(this);

        //Create the cards
        JPanel card1 = new StartPanel();
        card1.add(button1);

        JPanel card2 = new NicknamePanel();
        card2.add(button2);

        JPanel card3 = new GamePanel();
        card3.add(button3);

        JPanel card4 = new EndPanel(EndPanel.DEFEAT);
        card4.add(button4);

        JPanel card5 = new EndPanel(EndPanel.LOST);
        card5.add(button5);

        JPanel card6 = new EndPanel(EndPanel.VICTORY);
        card6.add(button6);

        //Create the panel that contains the "cards".

        cards = new JPanel(new CardLayout());

        cards.add(card1, "Card 1");
        cards.add(card2, "Card 2");
        cards.add(card3, "Card 3");
        cards.add(card4, "Card 4");
        cards.add(card5, "Card 5");
        cards.add(card6, "Card 6");

        getContentPane().add(cards);
        cardLayout = (CardLayout) cards.getLayout();
        cardLayout.show(cards, "Card 1");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    public void actionPerformed(ActionEvent e) {
        cardLayout.next(cards);
    }
}
