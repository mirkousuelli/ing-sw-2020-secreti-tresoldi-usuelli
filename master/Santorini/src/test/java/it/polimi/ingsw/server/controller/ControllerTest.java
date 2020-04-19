package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.state.Game;
import it.polimi.ingsw.server.model.state.states.Build;
import it.polimi.ingsw.server.model.state.states.Move;
import it.polimi.ingsw.server.network.ServerClientHandler;
import it.polimi.ingsw.server.network.ServerClientHandlerSocket;
import it.polimi.ingsw.server.view.RemoteView;
import it.polimi.ingsw.server.view.View;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

class ControllerTest {

    //@Test
    void testMessage() throws FileNotFoundException {
        List<View> playerViews = new ArrayList<>();
        List<Player> players = new ArrayList<>();
        List<ServerClientHandler> connections = new ArrayList<>();
        Game model = new Game(3);
        Controller controller = new Controller(model);

        players.add(new Player("Pl1"));
        players.add(new Player("Pl2"));
        players.add(new Player("Pl3"));

        /*connections.add(new ServerClientHandlerSocket());
        connections.add(new ServerClientHandlerSocket());
        connections.add(new ServerClientHandlerSocket());*/

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
        playerViews.get(0).processMessage(new Demand(DemandType.BUILD,"Pl1"));

        //unicast - ko currentPlayer
        model.setState(new Move(model));
        playerViews.get(2).processMessage(new Demand(DemandType.MOVE,"Pl2"));

        //broadcast - ok
        model.setState(new Build(model));
        playerViews.get(0).processMessage(new Demand(DemandType.BUILD,"Pl3"));
    }
}