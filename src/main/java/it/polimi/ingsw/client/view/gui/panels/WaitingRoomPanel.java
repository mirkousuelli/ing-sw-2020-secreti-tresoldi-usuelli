package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.deck.JDeck;
import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class WaitingRoomPanel extends SantoriniPanel {

    private static final String imgPath = "menu.png";

    public WaitingRoomPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createWaitStand();
    }

    public void createWaitStand() {
        ImageIcon icon = new ImageIcon("img/buttons/waiting.png");
        Image img = icon.getImage().getScaledInstance( 800, 400, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        JLabel stand = new JLabel(icon);
        stand.setOpaque(false);

        add(stand, new GridBagConstraints());
    }

    @Override
    public void updateFromModel() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (mg.getGame().getDeck().getList().isEmpty()) {
            List<ReducedCard> reducedCardList = gui.getClientModel().getDeck();
            List<God> godList = reducedCardList.stream().map(ReducedCard::getGod).collect(Collectors.toList());
            mg.getGame().setDeck(new JDeck(godList));
        }

        switch (gui.getClientModel().getCurrentState()) {
            case CHOOSE_DECK:
                mg.addPanel(new ChooseCardsPanel(panelIndex, panels, mg.getGame().getDeck()));
                ((ChooseCardsPanel) mg.getCurrentPanel()).numPlayer = gui.getClientModel().getNumberOfPlayers();
                ((ChooseCardsPanel) mg.getCurrentPanel()).enableChoose(true);
                gui.free();
                break;

            case CHOOSE_CARD:
                mg.addPanel(new ChooseGodPanel(panelIndex, panels, mg.getGame().getDeck()));
                ((ChooseGodPanel) mg.getCurrentPanel()).enableChoose(gui.getClientModel().isYourTurn());
                break;

            case START:
                setUpJPlayers();
                gui.free();
                return;

            case PLACE_WORKERS:
                mg.addPanel(new GamePanel(panelIndex, panels));
                break;

            default:
                gui.free();
                return;
        }

        this.panelIndex.next(this.panels);
    }

    private void setUpJPlayers() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        List<ReducedPlayer> playerList = gui.getClientModel().getOpponents();
        playerList.add(gui.getClientModel().getPlayer());

        List<String> playerNameList = playerList.stream().map(ReducedPlayer::getNickname).collect(Collectors.toList());

        if (mg.getGame().getNumPlayer() > 0) return;

        for (String name : playerNameList) {
            mg.getGame().addPlayer(name, mg.getGame().getNumPlayer());

            if (name.equals(gui.getClientModel().getCurrentPlayer().getNickname()))
                mg.getGame().setCurrentPlayer(mg.getGame().getPlayer(mg.getGame().getNumPlayer() - 1));
        }
    }
}