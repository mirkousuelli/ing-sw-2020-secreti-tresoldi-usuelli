package it.polimi.ingsw.client.view.gui.frame;

import it.polimi.ingsw.client.view.gui.panels.*;
import it.polimi.ingsw.server.model.game.Game;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SantoriniFrame extends JFrame implements ActionListener {
    private final String TITLE = "Santorini";
    JPanel cards;
    JButton button1;
    JButton button2;
    JButton button3;
    JButton button4;
    JButton button5;
    JButton button6;
    JButton button7;
    JButton button8;
    JButton button9;
    JButton button10;
    CardLayout cardLayout;

    public SantoriniFrame() {
        super();

        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        //setMinimumSize(new Dimension(BackgroundPanel.WIDTH, BackgroundPanel.HEIGHT));
        //setMaximumSize(new Dimension(1920, 1080));

        button1 = new JButton("NEXT");
        button2 = new JButton("NEXT");
        button3 = new JButton("NEXT");
        button4 = new JButton("NEXT");
        button5 = new JButton("NEXT");
        button6 = new JButton("NEXT");
        button7 = new JButton("NEXT");
        button8 = new JButton("NEXT");
        button9 = new JButton("NEXT");
        button10 = new JButton("NEXT");

        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
        button4.addActionListener(this);
        button5.addActionListener(this);
        button6.addActionListener(this);
        button7.addActionListener(this);
        button8.addActionListener(this);
        button9.addActionListener(this);
        button10.addActionListener(this);

        //Create the cards
        JPanel card1 = new StartPanel();
        card1.add(button1);

        JPanel card3 = new NumPlayerPanel();
        card3.add(button3);

        JPanel card4 = new WaitingRoomPanel();
        card4.add(button4);

        JPanel card5 = new ChooseCardsPanel();
        //card5.add(button5);

        JPanel card6 = new ChooseGodPanel();
        card6.add(button6);

        JPanel card7 = new GamePanel();
        //card7.add(button7);

        JPanel card8 = new EndPanel(EndPanel.DEFEAT);
        card8.add(button8);

        JPanel card9 = new EndPanel(EndPanel.LOST);
        card9.add(button9);

        JPanel card10 = new EndPanel(EndPanel.VICTORY);
        card10.add(button10);

        cards = new JPanel(new CardLayout());

        cards.add(card1, BorderLayout.CENTER);
        //cards.add(card2, BorderLayout.CENTER);
        cards.add(card3, BorderLayout.CENTER);
        cards.add(card4, BorderLayout.CENTER);
        cards.add(card5, BorderLayout.CENTER);
        cards.add(card6, BorderLayout.CENTER);
        cards.add(card7, BorderLayout.CENTER);
        cards.add(card8, BorderLayout.CENTER);
        cards.add(card9, BorderLayout.CENTER);
        cards.add(card10, BorderLayout.CENTER);

        cardLayout = (CardLayout) cards.getLayout();
        cardLayout.show(cards, "Card 1");

        getContentPane().add(cards);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
    }

    public void actionPerformed(ActionEvent e) {
        cardLayout.next(cards);
    }
}
