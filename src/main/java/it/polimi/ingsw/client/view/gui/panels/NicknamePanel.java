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
    private JComboBox connectType;
    private JComboBox serverAdd;
    private JComboBox serverPort;
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
        a.insets = new Insets(0,-5,50,0);

        errorMessage = new JLabel("Nickname already existing!");
        errorMessage.setForeground(Color.RED);
        errorMessage.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        stand.add(errorMessage, a);
        errorMessage.setVisible(false);
    }

    public void createWaitStand() {
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/labels/stand.png"));
        Image img = icon.getImage().getScaledInstance( 540, 540, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        stand = new JLabel(icon);
        stand.setOpaque(false);
        stand.setLayout(new GridBagLayout());

        add(stand, new GridBagConstraints());
    }

    void createFormat() {
        GridBagConstraints a = new GridBagConstraints();
        a.gridx = 0;
        a.gridy = 0;
        a.anchor = GridBagConstraints.WEST;
        a.weightx = 1;
        a.weighty = 0f;
        a.insets = new Insets(250,30,0,0);
        nickText = new JTextField(14);
        nickText.setVisible(true);
        stand.add(nickText, a);

        GridBagConstraints b = new GridBagConstraints();
        b.gridx = 1;
        b.gridy = 0;
        b.anchor = GridBagConstraints.EAST;
        b.weightx = 1;
        b.weighty = 0.1;
        b.insets = new Insets(90,0,0,130);
        connectType = new JComboBox();
        connectType.addItem("Socket");
        connectType.setVisible(true);
        stand.add(connectType, b);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 1;
        c.weighty = 0.1;
        c.insets = new Insets(-30,0,0,110);
        serverAdd = new JComboBox();
        serverAdd.addItem("127.0.0.1");
        serverAdd.setVisible(true);
        stand.add(serverAdd, c);

        GridBagConstraints d = new GridBagConstraints();
        d.gridx = 1;
        d.gridy = 2;
        d.anchor = GridBagConstraints.EAST;
        d.weightx = 1;
        d.weighty = 0.1;
        d.insets = new Insets(45,0,0,137);
        serverPort = new JComboBox();
        serverPort.addItem("1337");
        serverPort.setVisible(true);
        stand.add(serverPort, d);
    }

    public void createSendButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/send_button.png"));
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

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
        GUI gui = ((ManagerPanel) panels).getGui();
        String name = nickText.getText();

        if (!e.getSource().equals(sendButton)) return;
        if (name == null || name.equals("")) {
            errorMessage.setText("Error!, incorrect nickname");
            errorMessage.setVisible(true);
            return;
        }

        if (error) {
            gui.getClientModel().getPlayer().setNickname(name);
            gui.generateDemand(DemandType.CONNECT, new ReducedMessage(name));
            error = false;
        }
        else
            gui.initialRequest(
                    name,
                    (String) serverAdd.getSelectedItem(),
                    serverPort.getSelectedItem() == null
                            ? Integer.parseInt((String) serverPort.getItemAt(0))
                            : Integer.parseInt((String) serverPort.getSelectedItem())
            );

        sendButton.setEnabled(false);
    }

    @Override
    public void updateFromModel() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (gui.getClientModel().getCurrentState().equals(DemandType.CREATE_GAME)) {
            mg.addPanel(new NumPlayerPanel(panelIndex, panels));
            this.panelIndex.next(this.panels);
        }
        else if (gui.getClientModel().getCurrentState().equals(DemandType.CONNECT) && gui.getAnswer().getHeader().equals(AnswerType.ERROR)) {
            error = true;
            errorMessage.setText("Nickname already existing!");
            errorMessage.setVisible(true);
            sendButton.setEnabled(true);
        }
        else if (gui.getClientModel().getCurrentState().equals(DemandType.CONNECT) && gui.getAnswer().getHeader().equals(AnswerType.SUCCESS)) {
            mg.addPanel(new WaitingRoomPanel(panelIndex, panels));
            this.panelIndex.next(this.panels);
            gui.free();
        }
    }
}