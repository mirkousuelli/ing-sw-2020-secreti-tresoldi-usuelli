package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ChooseCardsPanel extends SantoriniPanel {
    private static final String imgPath = "menu.png";
    private static final int BUTTON_SIZE = 200;
    private static final int GOD_X = 70;
    private static final int GOD_Y = 100;
    private JButton sendButton;
    private JButton removeButton;
    private JButton chooseButton;
    private JPanel godsList;
    private JPanel choice;
    private JPanel choosenList;
    private JButton[] gods;


    public ChooseCardsPanel() {
        super(imgPath);

        createChosenList();
        createChoice();
        createGodsList();

        //loadGods();
    }

    void createChosenList() {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.NORTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0f;
        c.fill = GridBagConstraints.BOTH;

        choosenList = new JPanel(new BorderLayout());
        choosenList.setVisible(true);
        choosenList.setOpaque(false);
        choosenList.setBackground(Color.BLUE);

        add(choosenList, c);

        JLabel background = new JLabel(BackgroundPanel.getScaledImage(
                new ImageIcon("img/labels/clouds.png"), BackgroundPanel.WIDTH, 120));
        background.setOpaque(false);

        choosenList.add(background, BorderLayout.NORTH);
    }

    void createChoice() {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.3;
        c.fill = GridBagConstraints.BOTH;

        choice = new JPanel(new FlowLayout());
        choice.setVisible(true);
        choice.setOpaque(false);
        choice.setBackground(Color.GREEN);

        add(choice, c);
    }

    void createGodsList() {
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.SOUTH;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0f;
        c.fill = GridBagConstraints.BOTH;

        choosenList = new JPanel(new BorderLayout());
        choosenList.setVisible(true);
        choosenList.setOpaque(false);
        choosenList.setBackground(Color.RED);

        add(choosenList, c);

        JLabel background = new JLabel(BackgroundPanel.getScaledImage(
                new ImageIcon("img/labels/gods_menu.png"), BackgroundPanel.WIDTH, 120));
        background.setOpaque(false);

        choosenList.add(background, BorderLayout.SOUTH);
    }

    /*void loadGods() {
        gods = new JButton[God.values().length];
        God[] array = God.values();
        for (int i = 0; i < array.length; i++) {
            String path = "img/cards/" + array[i].toString().toLowerCase() + "/mini.png";
            System.out.println(path);
            //gods[i] = new JButton("CIAO");//new JButton(BackgroundPanel.getScaledImage(new ImageIcon(path), GOD_X, GOD_Y));

            godsList.add(new JButton("CIAO"));
        }
    }*/

    /*private void createSendButton() {
        sendButton = new JButton(BackgroundPanel.getScaledImage(
                new ImageIcon("img/buttons/send_button.png"), BUTTON_SIZE, BUTTON_SIZE));
        sendButton.setOpaque(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);

        choosenList.add(sendButton, BorderLayout.NORTH);
    }

    private void createRemoveButton() {
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0,-300,0,0);

        removeButton = new JButton(BackgroundPanel.getScaledImage(
                new ImageIcon("img/buttons/remove_button.png"), BUTTON_SIZE, BUTTON_SIZE));
        removeButton.setOpaque(false);
        removeButton.setContentAreaFilled(false);
        removeButton.setBorderPainted(false);

        add(removeButton, c);
    }

    private void createChooseButton() {
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0,0,0,-300);

        chooseButton = new JButton(BackgroundPanel.getScaledImage(
                new ImageIcon("img/buttons/choose_button.png"), BUTTON_SIZE, BUTTON_SIZE));
        chooseButton.setOpaque(false);
        chooseButton.setContentAreaFilled(false);
        chooseButton.setBorderPainted(false);

        add(chooseButton, c);
    }*/
}