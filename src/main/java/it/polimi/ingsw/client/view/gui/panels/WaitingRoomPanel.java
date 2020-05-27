package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
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

        if (mg.getGame().getJDeck().getList().isEmpty()) {
            List<ReducedCard> reducedCardList = gui.getClientModel().getDeck();
            List<God> godList = reducedCardList.stream().map(ReducedCard::getGod).collect(Collectors.toList());
            mg.getGame().setJDeck(new JDeck(godList));
        }

        switch (gui.getClientModel().getCurrentState()) {
            case CHOOSE_DECK:
                mg.addPanel(new ChooseCardsPanel(panelIndex, panels, mg.getGame().getJDeck()));
                ((ChooseCardsPanel) mg.getCurrentPanel()).numPlayer = gui.getClientModel().getNumberOfPlayers();
                ((ChooseCardsPanel) mg.getCurrentPanel()).enableChoose(true);
                gui.free();
                break;

            case CHOOSE_CARD:
                if (!gui.getClientModel().getCurrentPlayer().isCreator()) {
                    mg.addPanel(new ChooseGodPanel(panelIndex, panels, mg.getGame().getJDeck()));
                    ((ChooseGodPanel) mg.getCurrentPanel()).enableChoose(gui.getClientModel().isYourTurn());
                }
                else
                    return;
                break;

            case START:
                setUpJPlayers();
                gui.free();
                return;

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

        if (mg.getGame().getNumPlayer() > 0) return;

        for (ReducedPlayer p : playerList) {
            mg.getGame().addPlayer(p.getNickname(), toColorIndex(p.getColor()));

            if (p.getNickname().equals(gui.getClientModel().getCurrentPlayer().getNickname()))
                mg.getGame().setCurrentPlayer(mg.getGame().getPlayer(mg.getGame().getNumPlayer() - 1));

            if (p.getNickname().equals(gui.getClientModel().getPlayer().getNickname()))
                mg.setClientPlayer(mg.getGame().getPlayer(mg.getGame().getNumPlayer() - 1));
        }
    }

    private int toColorIndex (String color) {
        switch (color) {
            case "cyan":
                return 0;

            case "yellow":
                return 1;

            case "purple":
                return 2;

            default:
                return -1;
        }
    }
}