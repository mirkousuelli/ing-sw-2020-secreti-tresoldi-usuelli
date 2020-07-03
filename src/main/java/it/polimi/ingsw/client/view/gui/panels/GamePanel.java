package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.client.view.gui.component.JWorker;
import it.polimi.ingsw.client.view.gui.component.deck.JCard;
import it.polimi.ingsw.client.view.gui.component.map.JBlockDecorator;
import it.polimi.ingsw.client.view.gui.component.map.JCell;
import it.polimi.ingsw.client.view.gui.component.map.JCellStatus;
import it.polimi.ingsw.client.view.gui.component.map.JMap;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents the panel where the actual game is played: the player can see the map with all its elements (
 * pawns, buildings...) and can make actions depending on the current state of the game.
 * <p>
 * It contains the player's card, opponents' card and buttons that allow the player to quit, use his God power or end
 * his turn (for when he doesn't want to use his additional power, when his card would allow him to do it)
 * <p>
 * It extends {@link SantoriniPanel}
 */
public class GamePanel extends SantoriniPanel implements ActionListener {

    private static final String imgPath = "map.png";
    private final JGame game;
    private final JPlayer clientPlayer;

    private JPanel right;
    private JCard cardButton;
    private JPanel left;
    private JButton quitButton;
    private JButton powerButton;
    private JButton endTurnButton;

    private List<ReducedAnswerCell> oldUpdate;

    /**
     * Constructor of the panel where the actual game is played. The map is created and also the sections containing
     * the cards of the player and of the opponent(s)
     *
     * @param panelIndex the index of the panel
     * @param panels     the panels used
     */
    public GamePanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        this.game = ((ManagerPanel) panels).getGame();
        this.clientPlayer = ((ManagerPanel) panels).getClientPlayer();
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

        oldUpdate = new ArrayList<>();
    }

    /**
     * Method which define the map in the center of the screen
     */
    private void createMap() {
        int offset = 0;
        GridBagConstraints mapCon = new GridBagConstraints();

        mapCon.anchor = GridBagConstraints.CENTER;
        mapCon.gridx = 1;
        mapCon.gridy = 0;
        mapCon.gridwidth = 1;
        mapCon.gridheight = 2;
        mapCon.weightx = 0.075;
        mapCon.weighty = 1;
        mapCon.fill = GridBagConstraints.BOTH;

        if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0)
            offset = 20;

        mapCon.insets = new Insets(70, 30 + offset, 85, 70);

        add(this.game.getJMap(), mapCon);
    }

    /**
     * Method which defines the righ side of the screen.
     */
    private void createRightSection() {
        GridBagConstraints rightCon = new GridBagConstraints();

        rightCon.anchor = GridBagConstraints.WEST;
        rightCon.gridx = 0;
        rightCon.gridy = 0;
        rightCon.gridwidth = 1;
        rightCon.gridheight = 2;
        rightCon.weightx = 0.1;
        rightCon.weighty = 1;
        rightCon.fill = GridBagConstraints.BOTH;

        right = new JPanel(new GridLayout(2, 1));
        right.setVisible(true);
        right.setOpaque(false);
        right.setLayout(new GridBagLayout());

        add(right, rightCon);
    }

    /**
     * Method which creates the button to quit the game.
     */
    private void createQuitButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/quit_button.png"));
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(50, 0, 0, 0);

        quitButton = new JButton(icon);
        quitButton.setPreferredSize(new Dimension(100, 60));
        quitButton.setOpaque(false);
        quitButton.setContentAreaFilled(false);
        quitButton.setBorderPainted(false);
        right.add(quitButton, c);

        quitButton.addActionListener(e -> System.exit(1));
    }

    /**
     * Method which creates the power button in order to active the power and see the option highlighted in the map
     */
    private void createPowerButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/power_off.png"));
        Image img = icon.getImage().getScaledInstance(180, 45, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0f;
        c.weighty = 0f;

        powerButton = new JButton(icon);
        powerButton.setOpaque(false);
        powerButton.setContentAreaFilled(false);
        powerButton.setBorderPainted(false);
        powerButton.addActionListener(this);
        powerButton.setName("off");
        powerButton.setEnabled(false);
        game.getJMap().powerButtonManager(powerButton);
        right.add(powerButton, c);
    }

    /**
     * Method which created the end turn button in case some gods power have to be stopped in a specific moment.
     */
    private void createEndTurnButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/end_turn.png"));
        Image img = icon.getImage().getScaledInstance(180, 45, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0f;
        c.weighty = 0f;

        endTurnButton = new JButton(icon);
        endTurnButton.setOpaque(false);
        endTurnButton.setContentAreaFilled(false);
        endTurnButton.setBorderPainted(false);
        endTurnButton.addActionListener(this);
        endTurnButton.setName("endTurn");
        endTurnButton.setEnabled(false);
        right.add(endTurnButton, c);
    }

    /**
     * Method which switch on the power button.
     *
     * @param active {@code true} it active, {@code false} if not active
     */
    private void activePowerButton(boolean active) {
        powerButton.setEnabled(true);

        if (active) {
            ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/power_on.png"));
            Image img = icon.getImage().getScaledInstance(180, 45, Image.SCALE_SMOOTH);
            powerButton.setIcon(new ImageIcon(img));
            powerButton.setName("on");
        } else {
            ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/power_off.png"));
            Image img = icon.getImage().getScaledInstance(180, 45, Image.SCALE_SMOOTH);
            powerButton.setIcon(new ImageIcon(img));
            powerButton.setName("off");
        }

        revalidate();
    }

    /**
     * Function which creates the card section on the right panel.
     */
    private void createCardSection() {
        GridBagConstraints cardCon = new GridBagConstraints();
        GridBagConstraints playerCon = new GridBagConstraints();

        cardCon.gridx = 0;
        cardCon.gridy = 1;
        playerCon.insets = new Insets(125, 0, 0, 0);

        cardButton = this.clientPlayer.getJCard();
        clientPlayer.setCardViewSize(true);
        cardButton.add(clientPlayer, playerCon);
        right.add(cardButton, cardCon);
    }

    /**
     * Function which creates the left section of the panel, in order to receives opponents cards to be displayed
     */
    private void createLeftSection() {
        GridBagConstraints leftCon = new GridBagConstraints();

        leftCon.anchor = GridBagConstraints.EAST;
        leftCon.gridx = 2;
        leftCon.gridy = 0;
        leftCon.gridwidth = 1;
        leftCon.gridheight = 2;
        leftCon.weightx = 0.1;
        leftCon.weighty = 1;
        leftCon.fill = GridBagConstraints.BOTH;

        left = new JPanel(new GridLayout(2, 1));
        left.setVisible(true);
        left.setOpaque(false);
        left.setLayout(new GridBagLayout());

        add(left, leftCon);
    }

    /**
     * Method used to create opponents section through an identifier; the maximum number of opponent is 2, thus it fits
     * the screen both in 2 players and 3 players
     *
     * @param player opponent player
     * @param i      sequential identifier
     */
    private void createEnemySection(JPlayer player, int i) {
        GridBagConstraints c = new GridBagConstraints();
        GridBagConstraints playerCon = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = i;
        if (i > 0)
            c.insets = new Insets(10, 0, 0, 0);
        playerCon.insets = new Insets(125, 0, 0, 0);

        JCard card = player.getJCard();
        player.setCardViewSize(true);
        card.add(player, playerCon);
        left.add(player.getJCard(), c);
    }

    /**
     * Method which displays possible move cells
     */
    private void setPossibleMove(List<JCell> where) {
        game.getJMap().setPossibleMove(where);
        cardButton.applyNormal();
    }

    /**
     * Method which displays possible build cells
     */
    private void setPossibleBuild(List<JCell> where) {
        game.getJMap().setPossibleBuild(where);
        cardButton.applyNormal();
    }

    /**
     * Method which displays possible use power cells about move
     */
    private void setPossibleUsePowerMove(List<JCell> where) {
        game.getJMap().setPossibleUsePowerMove(where);
        activePowerButton(true);
    }

    /**
     * Method which displays possible use power cells about build
     */
    private void setPossibleUsePowerBuild(List<JCell> where) {
        game.getJMap().setPossibleUsePowerBuild(where);
        activePowerButton(true);
    }

    /**
     * Method that generates the demand from the given cells
     *
     * @param chosenJCells the list of cells where to generate the demand from
     * @param status       the status of the cell
     */
    public void generateDemand(List<JCell> chosenJCells, JCellStatus status) {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        JMap map = game.getJMap();
        DemandType currentState;
        int numOfAdditional = gui.getClientModel().getNumberOfAdditional();

        if (!gui.getClientModel().isYourTurn()) return;

        List<ReducedDemandCell> payload = chosenJCells.stream()
                .map(jCell -> new ReducedDemandCell(jCell.getXCoordinate(), jCell.getYCoordinate()))
                .collect(Collectors.toList());

        currentState = usePowerStatus(numOfAdditional, status);

        switch (currentState) {
            case PLACE_WORKERS:
                List<ReducedWorker> reducedWorkerList = payload.stream()
                        .map(rdc -> new ReducedWorker(mg.getClientPlayer().getNickname(), rdc.getX(), rdc.getY(), ((JBlockDecorator) map.getCell(rdc.getX(), rdc.getY())).getWorker().ordinal() % 2 != 0))
                        .collect(Collectors.toList());

                gui.generateDemand(DemandType.PLACE_WORKERS, reducedWorkerList);
                removeDecorations();
                return;

            case ASK_ADDITIONAL_POWER:
                if (status.equals(JCellStatus.USE_POWER)) {
                    currentState = DemandType.ADDITIONAL_POWER;
                    gui.getClientModel().setNextState(gui.getClientModel().getPrevState().equals(DemandType.MOVE) ? DemandType.BUILD : DemandType.CHOOSE_WORKER);
                } else if (status.equals(JCellStatus.BUILD)) {
                    currentState = DemandType.BUILD;
                    gui.getClientModel().setNextState(DemandType.CHOOSE_WORKER);
                    hidePowerButton();
                } else if (status.equals(JCellStatus.MOVE)) {
                    currentState = DemandType.MOVE;
                    gui.getClientModel().setNextState(DemandType.BUILD);
                    hidePowerButton();
                }
                break;

            default:
                break;
        }

        removeDecorations();

        gui.generateDemand(currentState, payload.size() > 1
                ? payload
                : payload.get(0)
        );
    }

    /**
     * Method which generates the demand to the server after having selected the power cell
     *
     * @param numOfAdditional number of power repetition usage
     * @param status          type of cell status
     */
    private DemandType usePowerStatus(int numOfAdditional, JCellStatus status) {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        DemandType currentState = gui.getClientModel().getCurrentState();

        if (!status.equals(JCellStatus.USE_POWER))
            return currentState;


        if (!gui.getClientModel().getCurrentState().equals(DemandType.ASK_ADDITIONAL_POWER)) {
            currentState = DemandType.USE_POWER;

            if (gui.getClientModel().getPlayer().getCard().getEffect().equals(Effect.BUILD) && gui.getClientModel().getCurrentState().equals(DemandType.MOVE))
                gui.getClientModel().setNextState(DemandType.MOVE);
        }

        if (numOfAdditional != 0) {
            if (numOfAdditional > 0)
                gui.getClientModel().setNumberOfAdditional(numOfAdditional - 1);

            if (numOfAdditional != 1)
                gui.getClientModel().setNextState(gui.getClientModel().getCurrentState());
            else
                hidePowerButton();

            gui.getClientModel().setAdditionalPowerUsed(numOfAdditional == 1);
        } else
            hidePowerButton();

        return currentState;
    }

    /**
     * Method which remove all the decoration above each cell of the map.
     */
    private void removeDecorations() {
        JMap map = game.getJMap();

        map.removeDecoration(JCellStatus.toJCellStatus(DemandType.CHOOSE_WORKER));
        map.removeDecoration(JCellStatus.toJCellStatus(DemandType.MOVE));
        map.removeDecoration(JCellStatus.toJCellStatus(DemandType.BUILD));
        map.removeDecoration(JCellStatus.toJCellStatus(DemandType.USE_POWER));
    }

    /**
     * Method which switches off the power button.
     */
    private void hidePowerButton() {
        activePowerButton(false);
        cardButton.applyNormal();
        powerButton.setEnabled(false);
        powerButton.setName("off");

        endTurnButton.setEnabled(false);
    }

    /**
     * Method that generates the demand from the given cell
     *
     * @param chosenJCell the cell where to generate the demand from
     * @param status      the status of the cell
     */
    public void generateDemand(JCell chosenJCell, JCellStatus status) {
        List<JCell> chosenCells = new ArrayList<>();

        chosenCells.add(chosenJCell);
        generateDemand(chosenCells, status);
    }

    /**
     * Method which defines cells (passed through a list) their status as a set.
     *
     * @param reducedAnswerCellList cell to be colored with their states
     * @param currentState          type of state
     */
    private void setJCellLAction(List<ReducedAnswerCell> reducedAnswerCellList, DemandType currentState) {
        GUI gui = ((ManagerPanel) panels).getGui();
        JMap map = game.getJMap();

        List<ReducedAction> reducedActionList = reducedAnswerCellList.stream()
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
                        } else if (gui.getClientModel().getPrevState().equals(DemandType.BUILD)) {
                            setPossibleUsePowerBuild(jCellList);
                            endTurnButton.setEnabled(true);
                        }
                    } else if (gui.getClientModel().getPlayer().getCard().getEffect().equals(Effect.MOVE))
                        setPossibleUsePowerMove(jCellList);
                    else if (gui.getClientModel().getPlayer().getCard().getEffect().equals(Effect.BUILD))
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
                    game.getJMap().hidePowerCells();
                    powerButton.setName("on");
                    endTurnButton.setEnabled(true);
                    break;

                case "on":
                    activePowerButton(false);
                    cardButton.applyPower();
                    game.getJMap().showPowerCells();
                    powerButton.setName("off");
                    endTurnButton.setEnabled(false);
                    break;

                case "endTurn":
                    GUI gui = mg.getGui();

                    endTurnButton.setEnabled(false);
                    hidePowerButton();
                    removeDecorations();

                    gui.generateDemand(DemandType.ASK_ADDITIONAL_POWER, new ReducedMessage("n"));
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
        List<ReducedAnswerCell> updatedCells;

        AnswerType answerType = (AnswerType) gui.getAnswer().getHeader();
        switch (answerType) {
            case CHANGE_TURN:
                mg.getGame().setCurrentPlayer(gui.getClientModel().getCurrentPlayer().getNickname());
                break;

            case DEFEAT:
                updatedCells = (List<ReducedAnswerCell>) gui.getAnswer().getPayload();

                if (gui.getClientModel().isEnded()) {
                    mg.addPanel(new EndPanel(answerType.toString(), panelIndex, panels));
                    ((EndPanel) mg.getCurrentPanel()).disablePLayAgainButton();
                    this.panelIndex.next(this.panels);
                } else {
                    game.removePlayer(((ReducedPlayer) gui.getAnswer().getPayload()).getNickname());
                    left.removeAll();
                    int enemy = 0;
                    for (JPlayer p : this.game.getPlayerList()) {
                        if (!p.equals(this.clientPlayer)) {
                            createEnemySection(p, enemy++);
                            p.setCardViewSize(true);
                        }
                    }
                    validate();
                    repaint();
                    if (gui.getClientModel().isYourTurn())
                        updateCells(updatedCells);
                }
                break;

            case CLOSE:
                mg.addPanel(new EndPanel("saved", panelIndex, panels));
                this.panelIndex.next(this.panels);
                break;

            case VICTORY:
                String player = ((ReducedPlayer) gui.getAnswer().getPayload()).getNickname();
                mg.addPanel(new EndPanel(player.equals(gui.getClientModel().getPlayer().getNickname()) ? "victory" : "lost", panelIndex, panels));
                this.panelIndex.next(this.panels);
                break;

            case SUCCESS:
                updatedCells = (List<ReducedAnswerCell>) gui.getAnswer().getPayload();

                updateWorkers(updatedCells);
                updateBuild(updatedCells);

                if (!gui.getClientModel().isYourTurn())
                    updateMove();
                else
                    updateCells(updatedCells);
                break;

            case ERROR:
                updateCells(oldUpdate);
                break;

            default:
                break;
        }

        gui.free();
    }

    /**
     * Method which updates cells status.
     */
    private void updateCells(List<ReducedAnswerCell> updatedCells) {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        JMap map = game.getJMap();
        DemandType currentState = gui.getClientModel().getCurrentState();
        oldUpdate = updatedCells;

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
            case ADDITIONAL_POWER:
                setJCellLAction(updatedCells, currentState);
                break;

            default:
                break;
        }

    }

    /**
     * Method which updates workers' cells
     *
     * @param updatedCells workers' cells
     */
    private void updateWorkers(List<ReducedAnswerCell> updatedCells) {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        JMap map = game.getJMap();

        JPlayer prevPlayer = game.getPlayer(gui.getClientModel().getPrevPlayer());

        if (prevPlayer == null) return;
        if (!prevPlayer.getWorkers().isEmpty()) return;
        if (prevPlayer.getNickname().equals(gui.getClientModel().getPlayer().getNickname())) return;
        if (!gui.getAnswer().getContext().equals(UpdatedPartType.WORKER)) return;

        updatedCells.forEach(updatedCell -> {
            JCell jCellWorker = map.getCell(updatedCell.getX(), updatedCell.getY());

            if (updatedCell.getWorker().isGender()) {
                prevPlayer.setUpMaleWorker(jCellWorker);
                prevPlayer.getMaleWorker().setId(updatedCell.getWorker().getId());
            } else {
                prevPlayer.setUpFemaleWorker(jCellWorker);
                prevPlayer.getFemaleWorker().setId(updatedCell.getWorker().getId());
            }
        });
    }

    /**
     * Method which updates cells after a build and manage the decoratore pattern.
     *
     * @param updatedCells cells to be updated after a build
     */
    private void updateBuild(List<ReducedAnswerCell> updatedCells) {
        JMap map = game.getJMap();
        JCell jCell;

        for (ReducedAnswerCell rac : updatedCells) {
            jCell = map.getCell(rac.getX(), rac.getY());

            if (rac.getLevel().toInt() == jCell.getStatus().ordinal() + 1)
                ((JBlockDecorator) jCell).buildUp();
            else if (rac.getLevel().toInt() > jCell.getStatus().ordinal() + 1)
                ((JBlockDecorator) jCell).addDecoration(JCellStatus.DOME);

            map.validate();
            map.repaint();

            validate();
            repaint();
        }
    }

    /**
     * Method which updates workers move above the map.
     */
    private void updateMove() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (gui.getClientModel().getWorkers().isEmpty()) return;
        if (!gui.getAnswer().getContext().equals(UpdatedPartType.BOARD)) return;

        List<ReducedPlayer> playerList = gui.getClientModel().getOpponents();
        playerList.add(gui.getClientModel().getPlayer());

        JPlayer jPlayer;

        for (ReducedPlayer p : playerList) {
            jPlayer = game.getPlayer(p.getNickname());

            moveJWorker(p, jPlayer.getMaleWorker());
            moveJWorker(p, jPlayer.getFemaleWorker());
        }
    }

    /**
     * Method which make the proper worker move inside the map.
     */
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

    /**
     * Predicate which controls if is not the same worker cell
     *
     * @return {@code true} if it is the same cell, if not {@code false}
     */
    private boolean isNotSameCell(JCell jCell, ReducedWorker reducedWorker) {
        if (jCell == null) return false;

        return jCell.getXCoordinate() != reducedWorker.getX() || jCell.getYCoordinate() != reducedWorker.getY();
    }

    /**
     * Method which gets the player's worker through its id
     *
     * @param player   current player
     * @param workers  list of player's workers
     * @param workerId worker's id
     */
    private ReducedWorker getWorkerWithId(ReducedPlayer player, List<ReducedWorker> workers, int workerId) {
        return workers.stream()
                .filter(w -> w.getOwner().equals(player.getNickname()))
                .filter(w -> w.getId() == workerId)
                .reduce(null, (a, b) -> a != null
                        ? a
                        : b
                );
    }


    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        Font f = new Font("SansSerif", Font.BOLD, 20);
        gr.setFont(f);
        gr.setColor(Color.WHITE);

        GUI gui = ((ManagerPanel) panels).getGui();

        if ((gui.getClientModel().isYourTurn())) {
            switch (gui.getClientModel().getCurrentState().toString()) {

                case "askAdditionalPower":
                    if (gui.getClientModel().getPrevState().toString().equalsIgnoreCase("move")) {
                        gr.drawString("BUILD", 505, 48);
                        break;
                    }

                case "placeWorkers":
                    gr.drawString("PLACE WORKERS", 445, 48);
                    break;

                case "chooseWorker":
                    gr.drawString("CHOOSE WORKER", 450, 48);
                    break;

                case "move":
                    gr.drawString("MOVE", 505, 48);
                    break;

                case "build":
                    gr.drawString("BUILD", 505, 48);
                    break;

                default:
                    break;

            }
        }
    }
}