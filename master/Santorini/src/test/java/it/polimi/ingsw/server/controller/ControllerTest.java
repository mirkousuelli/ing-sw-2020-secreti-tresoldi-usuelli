package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.state.Game;
import it.polimi.ingsw.server.model.state.states.Build;
import it.polimi.ingsw.server.model.state.states.Move;
import it.polimi.ingsw.server.network.ClientHandlerSocket;
import it.polimi.ingsw.server.view.RemoteView;
import it.polimi.ingsw.server.view.View;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ControllerTest {

    @Test
    void testMessage() {
        List<View> playerViews = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        List<ClientHandlerSocket> connections = new ArrayList<>();
        Game model = new Game(3);
        Controller controller = new Controller(model);

        players.add(new Player("Febs"));
        players.add(new Player("Fabbbbbbbbio"));
        players.add(new Player("Fabio"));

        connections.add(new ClientHandlerSocket());
        connections.add(new ClientHandlerSocket());
        connections.add(new ClientHandlerSocket());

        playerViews.add(new RemoteView(players.get(0).nickName, connections.get(0)));
        playerViews.add(new RemoteView(players.get(1).nickName, connections.get(1)));
        playerViews.add(new RemoteView(players.get(2).nickName, connections.get(2)));

        for (Player p : players)
            model.addPlayer(p.getNickName());

        for (View p : playerViews) {
            model.addObserver(p);
            p.addObserver(controller);
        }

        model.setCurrentPlayer(model.getPlayers().get(0));

        //unicast - ko currentState
        model.setState(new Move(model));
        playerViews.get(0).processMessage(new Demand(DemandType.BUILD,"Febs"));

        //unicast - ko currentPlayer
        model.setState(new Move(model));
        playerViews.get(2).processMessage(new Demand(DemandType.MOVE,"Fabio"));

        //broadcast - ok
        model.setState(new Build(model));
        playerViews.get(0).processMessage(new Demand(DemandType.BUILD,"Fabbbio"));
    }
}