package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.client.view.gui.component.JWorker;
import it.polimi.ingsw.client.view.gui.component.deck.JCard;
import it.polimi.ingsw.client.view.gui.component.map.*;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
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
    private JButton endTurnButton;

    public GamePanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        this.game = ((ManagerPanel)panels).getGame();
        this.clientPlayer = ((ManagerPanel)panels).getClientPlayer();
        this.clientPlayer.setCardViewSize(true);

        createRightSection();
        createPowerButton();
        createEndTurnButton();
        createQuitButton();
        createCardSection();
        createMap();
        createLeftSection();

        int enemy = 0;
        for (JPlayer p : this.game.getPlayerList()) {
            if (!p.equals(this.clientPlayer)) {
                createEnemySection(p, enemy++);
                p.setCardViewSize(true);
            }
        }

        game.getJMap().setGamePanel(this);
        game.getJMap().setManagerPanel((ManagerPanel) panels);
    }

    void createMap() {
        GridBagConstraints mapCon = new GridBagConstraints();

        mapCon.anchor = GridBagConstraints.CENTER;
        mapCon.gridx = 1;
        mapCon.gridy = 0;
        mapCon.gridwidth = 1;
        mapCon.gridheight = 2;
        mapCon.weightx = 0.075;
        mapCon.weighty = 1;
        mapCon.fill = GridBagConstraints.BOTH;
        mapCon.insets = new Insets(70,50,85,70);

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
        c.insets = new Insets(50,0,0,0);

        quitButton = new JButton(icon);
        quitButton.setPreferredSize(new Dimension(100,60));
        quitButton.setOpaque(false);
        quitButton.setContentAreaFilled(false);
        quitButton.setBorderPainted(false);
        right.add(quitButton, c);

        quitButton.addActionListener(this);
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
        //c.insets = new Insets(10,20,0,0);

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

    void createEndTurnButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon("img/buttons/end_turn.png");
        Image img = icon.getImage().getScaledInstance( 180, 45, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0f;
        c.weighty = 0f;
        //c.insets = new Insets(0,20,0,0);

        endTurnButton = new JButton(icon);
        endTurnButton.setOpaque(false);
        endTurnButton.setContentAreaFilled(false);
        endTurnButton.setBorderPainted(false);
        endTurnButton.addActionListener(this);
        endTurnButton.setName("endTurn");
        endTurnButton.setEnabled(false);
        right.add(endTurnButton, c);
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
        //cardCon.insets = new Insets(100,20,0,0);
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

    public void generateDemand(List<JCell> chosenJCells, JCellStatus status) {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        JMap map = game.getJMap();
        DemandType currentState = gui.getClientModel().getCurrentState();
        int numOfAdditional = gui.getClientModel().getNumberOfAdditional();

        if (!gui.getClientModel().isYourTurn()) return;

        List<ReducedDemandCell> payload = chosenJCells.stream()
                .map(jCell -> new ReducedDemandCell(jCell.getXCoordinate(), jCell.getYCoordinate()))
                .collect(Collectors.toList());

        if (status.equals(JCellStatus.USE_POWER)) {
            if (!gui.getClientModel().getCurrentState().equals(DemandType.ASK_ADDITIONAL_POWER))
                currentState = DemandType.USE_POWER;

            if (numOfAdditional > 0)
                gui.getClientModel().setNumberOfAdditional(numOfAdditional - 1);
            if (numOfAdditional != 1)
                gui.getClientModel().setNextState(gui.getClientModel().getCurrentState());
            else
                hidePowerButton();

            gui.getClientModel().setAdditionalPowerUsed(numOfAdditional == 0);
        }

        if (currentState.equals(DemandType.PLACE_WORKERS)) {
            List<ReducedWorker> reducedWorkerList = payload.stream()
                    .map(rdc -> new ReducedWorker(mg.getClientPlayer().getNickname(), rdc.getX(), rdc.getY(), ((JBlockDecorator) map.getCell(rdc.getX(), rdc.getY())).getWorker().ordinal() % 2 != 0))
                    .collect(Collectors.toList());

            gui.generateDemand(DemandType.PLACE_WORKERS, reducedWorkerList);
            removeDecorations();
            return;
        }

        if (currentState.equals(DemandType.ASK_ADDITIONAL_POWER)) {
            if (status.equals(JCellStatus.USE_POWER)) {
                currentState = DemandType.ADDITIONAL_POWER;
                gui.getClientModel().setNextState(gui.getClientModel().getPrevState().equals(DemandType.MOVE) ? DemandType.BUILD : DemandType.CHOOSE_WORKER);
            }
            else if (status.equals(JCellStatus.BUILD)) {
                currentState = DemandType.BUILD;
                gui.getClientModel().setNextState(DemandType.CHOOSE_WORKER);
                hidePowerButton();
            }
            else if (status.equals(JCellStatus.MOVE)) {
                currentState = DemandType.MOVE;
                gui.getClientModel().setNextState(DemandType.BUILD);
                hidePowerButton();
            }
        }

        removeDecorations();

        gui.generateDemand(currentState, payload.size() > 1
                ? payload
                : payload.get(0)
        );
    }

    private void removeDecorations() {
        JMap map = game.getJMap();

        map.removeDecoration(JCellStatus.toJCellStatus(DemandType.CHOOSE_WORKER));
        map.removeDecoration(JCellStatus.toJCellStatus(DemandType.MOVE));
        map.removeDecoration(JCellStatus.toJCellStatus(DemandType.BUILD));
        map.removeDecoration(JCellStatus.toJCellStatus(DemandType.USE_POWER));
    }

    private void hidePowerButton() {
        activePowerButton(false);
        cardButton.applyNormal();
        powerButton.setEnabled(false);
        powerButton.setName("off");

        endTurnButton.setEnabled(false);
    }

    public void generateDemand(JCell chosenJCell, JCellStatus status) {
        List<JCell> chosenCells = new ArrayList<>();

        chosenCells.add(chosenJCell);
        generateDemand(chosenCells, status);
    }

    private void setJCellLAction(List<ReducedAnswerCell> reducedAnswerCellList, DemandType currentState) {
        GUI gui = ((ManagerPanel) panels).getGui();
        JMap map = game.getJMap();

        List<ReducedAction> reducedActionList  = reducedAnswerCellList.stream()
                .map(ReducedAnswerCell::getActionList)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        reducedActionList.forEach(reducedAction -> {
            List<JCell> jCellList = reducedAnswerCellList.stream()
                    .filter(rac -> rac.getActionList().contains(reducedAction))
                    .map(rac -> map.getCell(rac.getX(), rac.getY()))
                    .collect(Collectors.toList());

            switch (reducedAction) {
                case MOVE:
                    setPossibleMove(jCellList);
                    break;

                case BUILD:
                    setPossibleBuild(jCellList);
                    break;

                case USEPOWER:
                    if (currentState.equals(DemandType.ASK_ADDITIONAL_POWER)) {
                        if (gui.getClientModel().getPrevState().equals(DemandType.MOVE)) {
                            setPossibleUsePowerMove(jCellList);
                        }
                        else if (gui.getClientModel().getPrevState().equals(DemandType.BUILD)) {
                            setPossibleUsePowerBuild(jCellList);
                            endTurnButton.setEnabled(true);
                        }
                    }
                    else if (currentState.equals(DemandType.MOVE))
                        setPossibleUsePowerMove(jCellList);
                    else if (currentState.equals(DemandType.BUILD))
                        setPossibleUsePowerBuild(jCellList);
                    break;

                default:
                    break;
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.game.getJMap().isPowerActive()) {
            JButton src = (JButton) e.getSource();
            ManagerPanel mg = (ManagerPanel) panels;

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

                case "endTurn":
                    GUI gui = mg.getGui();

                    endTurnButton.setEnabled(false);
                    hidePowerButton();
                    removeDecorations();
                    gui.generateDemand(DemandType.ASK_ADDITIONAL_POWER, new ReducedMessage("n"));
                    break;

                case "quit":
                    mg.addPanel(new EndPanel("save", panelIndex, panels));
                    this.panelIndex.next(this.panels);
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
        List<ReducedAnswerCell> updatedCells;

        AnswerType answerType = (AnswerType) gui.getClientModel().getAnswer().getHeader();
        switch (answerType) {
            case CHANGE_TURN:
                mg.getGame().setCurrentPlayer(gui.getClientModel().getCurrentPlayer().getNickname());
                gui.free();
                return;

            case DEFEAT:
            case CLOSE:
                mg.addPanel(new EndPanel(answerType.toString(), panelIndex, panels));
                this.panelIndex.next(this.panels);
                return;

            case VICTORY:
                String player = ((ReducedPlayer) gui.getAnswer().getPayload()).getNickname();
                mg.addPanel(new EndPanel(player.equals(gui.getClientModel().getPlayer().getNickname()) ? "victory" : "lost", panelIndex, panels));
                this.panelIndex.next(this.panels);
                return;

            case SUCCESS:
                updatedCells = (List<ReducedAnswerCell>) gui.getAnswer().getPayload();

                updateWorkers(updatedCells);
                updateBuild(updatedCells);

                if (!gui.getClientModel().isYourTurn()) {
                    updateMove();
                    gui.free();
                    return;
                }
                break;

            case ERROR:
                break;

            default:
                break;
        }

        switch (currentState) {
            case PLACE_WORKERS:
                map.workersPositioning();
                break;

            case CHOOSE_WORKER:
                game.getCurrentPlayer().chooseWorker();
                break;

            case MOVE:
            case BUILD:
            case ASK_ADDITIONAL_POWER:
                updatedCells = (List<ReducedAnswerCell>) gui.getAnswer().getPayload();
                //updatedCells.forEach(rac -> System.out.println(rac.getX() + ", " + rac.getY() + " " + rac.getActionList()));
                setJCellLAction(updatedCells, currentState);
                break;
        }
    }

    private void updateWorkers(List<ReducedAnswerCell> updatedCells) {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        JMap map = game.getJMap();

        JPlayer prevPlayer = game.getPlayer(gui.getClientModel().getPrevPlayer());

        if (!prevPlayer.getWorkers().isEmpty()) return;
        if (prevPlayer.getNickname().equals(gui.getClientModel().getPlayer().getNickname())) return;
        if (!gui.getAnswer().getContext().equals(UpdatedPartType.WORKER)) return;

        updatedCells.forEach(updatedCell -> {
            JCell jCellWorker = map.getCell(updatedCell.getX(), updatedCell.getY());

            if (updatedCell.getWorker().isGender()) {
                prevPlayer.setUpMaleWorker(jCellWorker);
                prevPlayer.getMaleWorker().setId(updatedCell.getWorker().getId());

                //System.out.println("male " + prevPlayer.getMaleWorker().getLocation().getXCoordinate() + "," + prevPlayer.getMaleWorker().getLocation().getYCoordinate() + " " + prevPlayer.getMaleWorker().getId());
            }
            else {
                prevPlayer.setUpFemaleWorker(jCellWorker);
                prevPlayer.getFemaleWorker().setId(updatedCell.getWorker().getId());


                //System.out.println("female " + prevPlayer.getFemaleWorker().getLocation().getXCoordinate() + "," + prevPlayer.getFemaleWorker().getLocation().getYCoordinate() + " " + prevPlayer.getFemaleWorker().getId());
            }
        });
    }

    private void updateBuild(List<ReducedAnswerCell> updatedCells) {
        JMap map = game.getJMap();
        JCell jCell;

        for (ReducedAnswerCell rac : updatedCells) {
            jCell = map.getCell(rac.getX(), rac.getY());

            /*System.out.print(jCell.getStatus() + " ");
            System.out.print(rac.getLevel() + " ");
            System.out.println(rac.getX() + "," + rac.getY());*/

            if (rac.getLevel().toInt() == jCell.getStatus().ordinal() + 1)
                ((JBlockDecorator) jCell).buildUp();
            else if (rac.getLevel().toInt() > jCell.getStatus().ordinal() + 1)
                ((JBlockDecorator) jCell).addDecoration(JCellStatus.DOME);
        }
    }

    private void updateMove() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (gui.getClientModel().getWorkers().isEmpty()) return;
        if (!gui.getAnswer().getContext().equals(UpdatedPartType.BOARD)) return;

        List<ReducedPlayer> playerList = (List<ReducedPlayer>) gui.getClientModel().getOpponents();
        playerList.add(gui.getClientModel().getPlayer());

        JPlayer jPlayer;

        for (ReducedPlayer p : playerList) {
            jPlayer = game.getPlayer(p.getNickname());

            moveJWorker(p, jPlayer.getMaleWorker());
            moveJWorker(p, jPlayer.getFemaleWorker());
        }
    }

    private void moveJWorker(ReducedPlayer p, JWorker jWorker) {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        JMap map = game.getJMap();

        ReducedWorker reducedWorker = getWorkerWithId(p, (List<ReducedWorker>) gui.getClientModel().getWorkers(), jWorker.getId());
        JCell cellToMove = map.getCell(reducedWorker.getX(), reducedWorker.getY());

        if (isNotSameCell(jWorker.getLocation(), reducedWorker)) {
            if (gui.getClientModel().getCurrentPlayer().getCard().isPushPower())
                map.pushWorker(jWorker, cellToMove);
            else if (gui.getClientModel().getCurrentPlayer().getCard().isSwapPower())
                map.switchWorkers(jWorker, cellToMove);
            else
                jWorker.setLocation(cellToMove);
        }
    }

    private boolean isNotSameCell(JCell jCell, ReducedWorker reducedWorker) {
        if (jCell == null) return false;

        return jCell.getXCoordinate() != reducedWorker.getX() || jCell.getYCoordinate() != reducedWorker.getY();
    }

    private ReducedWorker getWorkerWithId(ReducedPlayer player, List<ReducedWorker> workers, int workerId) {
        return workers.stream()
                .filter(w -> w.getOwner().equals(player.getNickname()))
                .filter(w -> w.getId() == workerId)
                .reduce(null, (a, b) -> a != null
                        ? a
                        : b
                );
    }
}