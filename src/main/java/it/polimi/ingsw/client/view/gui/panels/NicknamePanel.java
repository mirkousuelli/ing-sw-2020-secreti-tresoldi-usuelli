package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NicknamePanel extends SantoriniPanel implements ActionListener {

    private static final String imgPath = "menu.png";
    private static final int BUTTON_SIZE = 175;

    private JButton sendButton;
    private JLabel stand;
    private JTextField nickText;
    private JTextField serverAdd;
    private JTextField serverPort;
    private JLabel errorMessage;

    private boolean error = false;

    public NicknamePanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createWaitStand();
        createFormat();
        createSendButton();
        createErrorMessage();

        stand.revalidate();
        stand.repaint();
    }

    private void createErrorMessage() {
        GridBagConstraints a = new GridBagConstraints();
        a.gridx = 0;
        a.gridy = 2;
        a.insets = new Insets(0, -5, 50, 0);

        errorMessage = new JLabel("Nickname already existing!");
        errorMessage.setForeground(Color.RED);
        errorMessage.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        stand.add(errorMessage, a);
        errorMessage.setVisible(false);
    }

    private void createWaitStand() {
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/labels/stand.png"));
        Image img = icon.getImage().getScaledInstance(540, 540, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        stand = new JLabel(icon);
        stand.setOpaque(false);
        stand.setLayout(new GridBagLayout());

        add(stand, new GridBagConstraints());
    }

    private void createFormat() {
        GridBagConstraints a = new GridBagConstraints();
        a.gridx = 0;
        a.gridy = 0;
        a.anchor = GridBagConstraints.WEST;
        a.weightx = 1;
        a.weighty = 0f;
        a.insets = new Insets(250, 40, 0, 0);
        nickText = new JTextField(14);
        nickText.setVisible(true);
        stand.add(nickText, a);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 1;
        c.weighty = 0.1;
        c.insets = new Insets(-135, 0, 0, 70);
        serverAdd = new JTextField(10);
        serverAdd.setVisible(true);
        stand.add(serverAdd, c);

        GridBagConstraints d = new GridBagConstraints();
        d.gridx = 1;
        d.gridy = 2;
        d.anchor = GridBagConstraints.EAST;
        d.weightx = 1;
        d.weighty = 0.1;
        d.insets = new Insets(0, 0, 0, 70);
        serverPort = new JTextField(10);
        serverPort.setVisible(true);
        stand.add(serverPort, d);
    }

    private void createSendButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/send_button.png"));
        Image img = icon.getImage().getScaledInstance(BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        c.gridy = 3;
        c.gridx = 1;
        c.anchor = GridBagConstraints.SOUTH;
        c.weightx = 1;
        c.weighty = 1;

        sendButton = new JButton(icon);
        sendButton.setOpaque(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);

        sendButton.addActionListener(this);
        stand.add(sendButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!e.getSource().equals(sendButton)) return;

        GUI gui = ((ManagerPanel) panels).getGui();

        String name = parseName(nickText.getText());
        String address = parseAddress(serverAdd.getText());
        Integer port = parsePort(serverPort.getText());

        if (name.equals("") || address.equals("") || port == null) {
            errorMessage.setText("Error! Incorrect input...");
            errorMessage.setVisible(true);
            return;
        }

        if (error) {
            error = false;
            gui.getClientModel().getPlayer().setNickname(name);
            gui.generateDemand(DemandType.CONNECT, new ReducedMessage(name));
        } else
            gui.initialRequest(name, address, port);

        sendButton.setEnabled(false);
    }

    @Override
    public void updateFromModel() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (mg.evalDisconnection()) {
            gui.free();
            return;
        }

        if (gui.getClientModel().getCurrentState().equals(DemandType.CREATE_GAME)) {
            mg.addPanel(new NumPlayerPanel(panelIndex, panels));
            panelIndex.next(panels);
        } else if (gui.getClientModel().getCurrentState().equals(DemandType.CONNECT) && gui.getAnswer().getHeader().equals(AnswerType.ERROR)) {
            error = true;
            errorMessage.setText("wrong nickname!");
            errorMessage.setVisible(true);
            sendButton.setEnabled(true);
        } else if (gui.getClientModel().getCurrentState().equals(DemandType.CONNECT) && gui.getAnswer().getHeader().equals(AnswerType.SUCCESS)) {
            mg.addPanel(new WaitingRoomPanel(panelIndex, panels));
            panelIndex.next(panels);
            gui.free();
        }
    }

    private String parseName(String name) {
        if (name == null)
            return "";
        else
            return name;
    }

    private String parseAddress(String address) {
        if (address == null) return "";
        if (address.equals("")) return "127.0.0.1";

        String[] fields = address.split("\\.");

        if (fields.length != 4) return "";

        int field;
        int i = 0;

        do {
            try {
                field = Integer.parseInt(fields[0]);
                if (field < 0 || field > 255)
                    return "";
                i++;
            } catch (NumberFormatException e) {
                return "";
            }
        } while (i < 4);

        return address;
    }

    private Integer parsePort(String portString) {
        if (portString == null) return null;
        if (portString.equals("")) return 1337;

        int port;

        try {
            port = Integer.parseInt(portString);
            if (port < 0)
                return null;
        } catch (NumberFormatException e) {
            return null;
        }

        return port;
    }
}