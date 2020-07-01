package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Class that represents the panel that is shown after the player started the game. It contains the fields where he has
 * to insert his nickname, server IP and port.
 * <p>
 * It extends {@link SantoriniPanel}
 */
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

    /**
     * Constructor of the panel where the player has to fill the fields in order to be able to play.
     * The nickname is unique, so two different players cannot have the same nickname.
     *
     * @param panelIndex the index of the panel
     * @param panels     the panels used
     */
    public NicknamePanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createWaitStand();
        createFormat();
        createSendButton();
        createErrorMessage();

        stand.revalidate();
        stand.repaint();
    }

    /**
     * Function which prints out if the name already exists after having received the answer from the server.
     */
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

    /**
     * Function which creates the main label in the center of the monitor in order to make the initial form available.
     */
    private void createWaitStand() {
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/labels/stand.png"));
        Image img = icon.getImage().getScaledInstance(540, 540, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        stand = new JLabel(icon);
        stand.setOpaque(false);
        stand.setLayout(new GridBagLayout());

        add(stand, new GridBagConstraints());
    }

    /**
     * Function which defines above the main label displaying the stand, the initial form to be compiled.
     */
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

    /**
     * Function which creates the button used to send the previous compiled form in order to get connected with the
     * the server.
     */
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

    /**
     * Function used to avoid null string pointer in lambda expressions.
     */
    private String parseName(String name) {
        if (name == null)
            return "";
        else
            return name;
    }

    /**
     * Function which check ip address semantic and if void, set as localhost
     *
     * @param address   server's ip chosen
     */
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

    /**
     * Function which parse the socket port and if void it is set has 1337 by default
     *
     * @param portString   server's port
     */
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