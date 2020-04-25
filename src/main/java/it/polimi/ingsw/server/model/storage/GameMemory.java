package it.polimi.ingsw.server.model.storage;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.God;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.*;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import org.xml.sax.*;

import java.io.IOException;
import java.util.List;

public class GameMemory{
    /* game */
    private static final int PLAYERS = 0;
    private static final int TURN = 1;
    private static final int BOARD = 2;

    /* players */
    private static final int NICKNAME = 0;
    private static final int GOD = 1;

    /* turn */
    private static final int PLAYER = 0;
    private static final int STATE = 1;

    /* board */
    private static final int X = 0;
    private static final int Y = 1;
    private static final int LEVEL = 2;
    private static final int WORKER = 3;

    public static void save(Game game, String path) {
        List<Player> player;
        Deck deck;
        Board board;
        GameState state;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);


        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Game load(String path) throws ParserConfigurationException, SAXException {
        Game game = new Game();
        Board board = new Board();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            NodeList confNodes = doc.getDocumentElement().getChildNodes();
            Node playersNode = confNodes.item(PLAYERS);
            Node turnNode = confNodes.item(TURN);
            Node boardNode = confNodes.item(BOARD);

            /* players */
            NodeList playerNode = playersNode.getChildNodes();
            for (int i = 0; i <= playerNode.getLength(); i++) {
                String nickname = playerNode.item(i).getChildNodes().item(NICKNAME).getNodeValue();

                game.addPlayer(nickname);
                game.setCurrentPlayer(game.getPlayer(nickname));
                game.assignCard(God.parseString(playerNode.item(i).getChildNodes().item(GOD).getNodeValue()));
            }

            /* turn */
            game.setCurrentPlayer((game.getPlayer(turnNode.getChildNodes().item(PLAYER).getChildNodes().item(NICKNAME).getNodeValue())));
            State state = State.parseString(turnNode.getChildNodes().item(STATE).getNodeValue());
            game.setState(state.toGameState(game));

            /* board */
            NodeList cells = boardNode.getChildNodes();
            for (int i = 0; i < cells.getLength(); i++) {
                Node cellNode = cells.item(i);
                Block cell = (Block) board.getCell(Integer.parseInt(cellNode.getChildNodes().item(X).getNodeValue()), Integer.parseInt(cellNode.getChildNodes().item(Y).getNodeValue()));
                cell.setLevel(Level.parseString(cellNode.getChildNodes().item(LEVEL).getNodeValue()));

                if (cellNode.getChildNodes().getLength() == WORKER) {
                    Worker worker = new Worker(cell);
                    game.getPlayer(cellNode.getChildNodes().item(WORKER).getChildNodes().item(PLAYER).getChildNodes().item(NICKNAME).getNodeValue()).addWorker(worker);
                }
            }

        }
        catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        return game;
    }
}
