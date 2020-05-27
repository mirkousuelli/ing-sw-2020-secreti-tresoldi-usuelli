package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.client.view.gui.component.JWorker;
import it.polimi.ingsw.client.view.gui.component.deck.JCard;
import it.polimi.ingsw.client.view.gui.component.map.*;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GamePanel extends SantoriniPanel implements ActionListener {

    private static final String imgPath = "map.png";
    private JGame game;
    private JPlayer clientPlayer;
    private JPanel right;
    private JCard cardButton;
    private JPanel left;
    private JButton quitButton;
    private JButton powerButton;

    public GamePanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        this.game = ((ManagerPanel)panels).getGame();
        this.clientPlayer = ((ManagerPanel)panels).getClientPlayer();

        createRightSection();
        createPowerButton();
        createQuitButton();
        createCardSection();
        createMap();
        createLeftSection();

        int enemy = 0;
        for (JPlayer p : this.game.getPlayerList()) {
            if (!p.equals(this.clientPlayer))
                createEnemySection(p, enemy++);
            p.setCardViewSize(true);
        }

        game.getJMap().setGamePanel(this);
    }

    void createMap() {
        GridBagConstraints mapCon = new GridBagConstraints();

        mapCon.anchor = GridBagConstraints.CENTER;
        mapCon.gridx = 1;
        mapCon.gridy = 0;
        mapCon.gridwidth = 1;
        mapCon.gridheight = 2;
        mapCon.weightx = 0.05;
        mapCon.weighty = 0.0975;
        mapCon.fill = GridBagConstraints.BOTH;
        mapCon.insets = new Insets(70,30,85,70);

        add(this.game.getJMap(), mapCon);
    }

    void createRightSection() {
        GridBagConstraints rightCon = new GridBagConstraints();

        rightCon.anchor = GridBagConstraints.WEST;
        rightCon.gridx = 0;
        rightCon.gridy = 0;
        rightCon.gridwidth = 1;
        rightCon.gridheight = 2;
        rightCon.weightx = 0.1;
        rightCon.weighty = 1;
        rightCon.fill = GridBagConstraints.BOTH;

        right = new JPanel(new GridLayout(2,1));
        right.setVisible(true);
        right.setOpaque(false);
        right.setLayout(new GridBagLayout());

        add(right, rightCon);
    }

    void createQuitButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon("img/buttons/quit_button.png");
        Image img = icon.getImage().getScaledInstance( 100, 100, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0,20,-70,0);

        quitButton = new JButton(icon);
        quitButton.setPreferredSize(new Dimension(100,50));
        quitButton.setOpaque(false);
        quitButton.setContentAreaFilled(false);
        quitButton.setBorderPainted(false);
        right.add(quitButton, c);

        //quitButton.addActionListener(this); TODO : da mettere verso il SAVED PANEL
    }

    void createPowerButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon("img/buttons/power_off.png");
        Image img = icon.getImage().getScaledInstance( 180, 45, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0f;
        c.weighty = 0f;
        c.insets = new Insets(0,20,0,0);

        powerButton = new JButton(icon);
        powerButton.setOpaque(false);
        powerButton.setContentAreaFilled(false);
        powerButton.setBorderPainted(false);
        powerButton.addActionListener(this);
        powerButton.setName("off");
        powerButton.setEnabled(false);
        this.game.getJMap().powerButtonManager(powerButton);
        right.add(powerButton, c);
    }

    void activePowerButton(boolean active) {
        powerButton.setEnabled(true);

        if (active) {
            ImageIcon icon = new ImageIcon("img/buttons/power_on.png");
            Image img = icon.getImage().getScaledInstance( 180, 45, Image.SCALE_SMOOTH);
            powerButton.setIcon(new ImageIcon(img));
            powerButton.setName("on");
        } else {
            ImageIcon icon = new ImageIcon("img/buttons/power_off.png");
            Image img = icon.getImage().getScaledInstance( 180, 45, Image.SCALE_SMOOTH);
            powerButton.setIcon(new ImageIcon(img));
            powerButton.setName("off");
        }

        revalidate();
    }

    private void createCardSection() {
        GridBagConstraints cardCon = new GridBagConstraints();
        GridBagConstraints playerCon = new GridBagConstraints();

        cardCon.gridx = 0;
        cardCon.gridy = 1;
        cardCon.insets = new Insets(60,20,0,0);
        playerCon.insets = new Insets(125,0,0,0);

        cardButton = this.clientPlayer.getJCard();
        clientPlayer.setCardViewSize(true);
        cardButton.add(clientPlayer, playerCon);
        right.add(cardButton, cardCon);
    }

    void createLeftSection() {
        GridBagConstraints leftCon = new GridBagConstraints();

        leftCon.anchor = GridBagConstraints.EAST;
        leftCon.gridx = 2;
        leftCon.gridy = 0;
        leftCon.gridwidth = 1;
        leftCon.gridheight = 2;
        leftCon.weightx = 0.1;
        leftCon.weighty = 1;
        leftCon.fill = GridBagConstraints.BOTH;

        left = new JPanel(new GridLayout(2,1));
        left.setVisible(true);
        left.setOpaque(false);
        left.setLayout(new GridBagLayout());

        add(left, leftCon);
    }

    private void createEnemySection(JPlayer player, int i) {
        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraints playerCon = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = i;
        if (i > 0)
            c.insets = new Insets(10,0,0,0);
        playerCon. insets = new Insets(125,0,0,0);

        JCard card = player.getJCard();
        player.setCardViewSize(true);
        card.add(player, playerCon);
        left.add(player.getJCard(), c);
    }

    public void setPossibleMove(List<JCell> where) {
        this.game.getJMap().setPossibleMove(where);
        cardButton.applyNormal();
    }

    public void setPossibleBuild(List<JCell> where) {
        this.game.getJMap().setPossibleBuild(where);
        cardButton.applyNormal();
    }

    public void setPossibleUsePowerMove(List<JCell> where) {
        this.game.getJMap().setPossibleUsePowerMove(where);
        activePowerButton(true);
    }

    public void setPossibleUsePowerBuild(List<JCell> where) {
        this.game.getJMap().setPossibleUsePowerBuild(where);
        activePowerButton(true);
    }

    public void setPossibleMalus(List<JCell> where) {
        this.game.getJMap().setPossibleMalus(where);
        cardButton.applyNormal();
    }

    public void generateDemand(List<JCell> chosenJCells) {
        GUI gui = ((ManagerPanel) panels).getGui();
        JMap map = game.getJMap();
        DemandType currentState;

        if (!gui.getClientModel().isYourTurn()) return;

        List<ReducedDemandCell> payload = chosenJCells.stream()
                .map(jCell -> new ReducedDemandCell(jCell.getXCoordinate(), jCell.getYCoordinate()))
                .collect(Collectors.toList());

        if (powerButton.isEnabled())
            currentState = DemandType.USE_POWER;
        else
            currentState = gui.getClientModel().getCurrentState();

        gui.generateDemand(currentState, payload.size() > 1
                ? payload
                : payload.get(0)
        );

        map.removeDecoration(JCellStatus.toJCellStatus(currentState));
        map.removeDecoration(JCellStatus.toJCellStatus(DemandType.USE_POWER));
    }

    public void generateDemand(JCell chosenJCell) {
        List<JCell> chosenCells = new ArrayList<>();

        chosenCells.add(chosenJCell);
        generateDemand(chosenCells);
    }

    private void setJCellLAction(List<ReducedAnswerCell> reducedAnswerCellList, DemandType currentState) {
        GUI gui = ((ManagerPanel) panels).getGui();
        JMap map = game.getJMap();

        if (reducedAnswerCellList.isEmpty()) return;

        List<JCell> jCellList = reducedAnswerCellList.stream()
                .map(rac -> map.getCell(rac.getX(), rac.getY()))
                .collect(Collectors.toList());

        switch (currentState) {
            case MOVE:
                setPossibleMove(jCellList);
                break;

            case BUILD:
                setPossibleBuild(jCellList);
                break;

            case ASK_ADDITIONAL_POWER:
                if (gui.getClientModel().getNextState().equals(DemandType.BUILD))
                    setPossibleUsePowerMove(jCellList);
                else if (gui.getClientModel().getNextState().equals(DemandType.CHOOSE_WORKER))
                    setPossibleBuild(jCellList);
                break;

            case USE_POWER:
                if (gui.getClientModel().getCurrentState().equals(DemandType.MOVE))
                    setPossibleUsePowerMove(jCellList);
                else if (gui.getClientModel().getCurrentState().equals(DemandType.BUILD))
                    setPossibleUsePowerBuild(jCellList);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.game.getJMap().isPowerActive()) {
            JButton src = (JButton) e.getSource();

            switch (src.getName()) {
                case "off":
                    activePowerButton(true);
                    cardButton.applyNormal();
                    this.game.getJMap().hidePowerCells();
                    powerButton.setName("on");
                    break;

                case "on":
                    activePowerButton(false);
                    cardButton.applyPower();
                    this.game.getJMap().showPowerCells();
                    powerButton.setName("off");
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void updateFromModel() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        JMap map = game.getJMap();
        DemandType currentState = gui.getClientModel().getCurrentState();

        if (!gui.getClientModel().getAnswer().getHeader().equals(AnswerType.SUCCESS)) {
            if (gui.getClientModel().getAnswer().getHeader().equals(AnswerType.CHANGE_TURN)) {
                mg.getGame().setCurrentPlayer(gui.getClientModel().getCurrentPlayer().getNickname());
            }

            gui.free();
            return;
        }

        List<ReducedAnswerCell> updatedCells = (List<ReducedAnswerCell>) gui.getAnswer().getPayload();

        switch (currentState) {
            case PLACE_WORKERS:
                updatePlaceWorkers(updatedCells);

                if (gui.getClientModel().isYourTurn())
                    map.workersPositioning();
                else
                    gui.free();
                break;

            case CHOOSE_WORKER:
                if (gui.getClientModel().isYourTurn()) {
                    game.getCurrentPlayer().chooseWorker();
                    map.validate();
                }

                List<String> owners = ((List<ReducedWorker>) gui.getClientModel().getWorkers()).stream()
                        .map(ReducedWorker::getOwner)
                        .distinct()
                        .collect(Collectors.toList());

                game.getPlayerList().forEach(p -> {
                    if (owners.contains(p.getNickname()) && p.getWorkers().isEmpty()) {
                        List<ReducedWorker> reducedWorkerList = ((List<ReducedWorker>) gui.getClientModel().getWorkers()).stream()
                                .filter(w -> w.getOwner().equals(p.getNickname()))
                                .collect(Collectors.toList());

                        reducedWorkerList.forEach(w -> p.setUpWorker(game.getJMap().getCell(w.getX(), w.getY())));
                    }

                    map.validate();
                }
                );
                break;

            case MOVE:
            case BUILD:
            case ASK_ADDITIONAL_POWER:
                if (gui.getClientModel().isYourTurn()) {
                    updatedCells.forEach(rac -> System.out.println(rac.getX() + ", " + rac.getY() + " " + rac.getActionList()));
                    setJCellLAction(updatedCells.stream().filter(rac -> rac.getActionList().contains(ReducedAction.parseString(currentState.toString()))).collect(Collectors.toList()), currentState);

                    if (!currentState.equals(DemandType.ASK_ADDITIONAL_POWER))
                        setJCellLAction(updatedCells.stream().filter(rac -> rac.getActionList().contains(ReducedAction.parseString(DemandType.USE_POWER.toString()))).collect(Collectors.toList()), DemandType.USE_POWER);
                }
                else {
                    List<JCell> cellsToModify = ((List<ReducedAnswerCell>) gui.getClientModel().getAnswer().getPayload()).stream()
                            .filter(rac -> rac.getAction(0).equals(ReducedAction.DEFAULT))
                            .map(rac -> map.getCell(rac.getX(), rac.getY()))
                            .collect(Collectors.toList());

                    for (JCell c : cellsToModify) {
                        ReducedAnswerCell rac = gui.getClientModel().getCell(c.getX(), c.getY());
                        if (rac == null) break;

                        if (rac.getLevel() != null && rac.getLevel().toInt() > ((JBlockDecorator) c).getDecoration().ordinal()) //build
                            ((JBlockDecorator) c).buildUp();
                    }
                }
                break;
        }
    }

    private void updatePlaceWorkers(List<ReducedAnswerCell> updatedCells) {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        JMap map = game.getJMap();

        JPlayer prevPlayer = game.getPlayer(gui.getClientModel().getPrevPlayer());
        if (!prevPlayer.getNickname().equals(gui.getClientModel().getPlayer().getNickname())) {
            updatedCells.forEach(updatedCell -> prevPlayer.setUpWorker(map.getCell(updatedCell.getX(), updatedCell.getY())));
            map.validate();
        }
    }
}