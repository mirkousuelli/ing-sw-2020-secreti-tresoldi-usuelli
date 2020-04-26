package it.polimi.ingsw.server.model.storage;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.*;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.network.message.Lobby;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import org.xml.sax.*;

import java.io.IOException;

public class GameMemory{
    /* game */
    private static final int LOBBY = 0;
    private static final int TURN = 1;
    private static final int BOARD = 2;

    /* lobby */
    private static final int ID = 0;
    private static final int PLAYERS = 1;
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

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            Element gameNode = doc.createElement("game");
            Element lobbyNode = doc.createElement("lobby");
            Element turnNode = doc.createElement("turn");
            Element boardNode = doc.createElement("board");

            // TODO: complete game save with ALL the tag contained in game_grammar.dtd
            //gameNode.appendChild();


        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save(Block block, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            // TODO: complete block update in saving file

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save(Worker worker, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            // TODO: complete worker update inside the board

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save(GameState state, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            // TODO: complete worker update inside the board

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save(Player currentPlayer, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            // TODO: complete worker update inside the board

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save(Lobby lobby, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            // TODO: complete worker update inside the board

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
            Node lobbyNode = confNodes.item(LOBBY);
            Node turnNode = confNodes.item(TURN);
            Node boardNode = confNodes.item(BOARD);

            /* players */
            NodeList playerNode = lobbyNode.getChildNodes();
            //game.getLobby().setID(playerNode.item(ID).getNodeValue());
            // TODO: add attributes reading part
            for (int i = PLAYERS; i <= playerNode.getLength(); i++) {
                String nickname = playerNode.item(i).getChildNodes().item(NICKNAME).getNodeValue();

                game.getLobby().addPlayer(nickname);
                game.setCurrentPlayer(game.getLobby().getPlayer(nickname));
                game.assignCard(God.parseString(playerNode.item(i).getChildNodes().item(GOD).getNodeValue()));
            }

            /* turn */
            game.setCurrentPlayer((game.getLobby().getPlayer(turnNode.getChildNodes().item(PLAYER).getChildNodes().item(NICKNAME).getNodeValue())));
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
                    game.getLobby().getPlayer(cellNode.getChildNodes().item(WORKER).getChildNodes().item(PLAYER).getChildNodes().item(NICKNAME).getNodeValue()).addWorker(worker);
                }
            }

        }
        catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        return game;
    }
}
