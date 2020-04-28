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
import java.util.List;

public class GameMemory {
    /* game */
    private static final int LOBBY = 0;
    private static final int TURN = 1;
    private static final int BOARD = 2;

    /* lobby */
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

            /* lobby */
            Element lobbyNode = doc.createElement("lobby");

            for (int i = 0; i < game.getNumPlayers(); i++) {
                Element playerNode = doc.createElement("player");
                Element nicknameNode = doc.createElement("nickname");
                Element godNode = doc.createElement("god");

                nicknameNode.setNodeValue(game.getPlayer(i).getNickName());
                playerNode.appendChild(nicknameNode);

                godNode.setNodeValue(game.getPlayer(i).getCard().getName());
                playerNode.appendChild(godNode);

                lobbyNode.appendChild(playerNode);
            }

            gameNode.appendChild(lobbyNode);

            /* turn */
            Element turnNode = doc.createElement("turn");
            Element currPlayerNode = doc.createElement("player");
            Element currNicknameNode = doc.createElement("nickname");
            Element stateNode = doc.createElement("state");

            currNicknameNode.setNodeValue(game.getCurrentPlayer().getNickName());
            currPlayerNode.appendChild(currNicknameNode);
            turnNode.appendChild(currPlayerNode);

            stateNode.setNodeValue(game.getState().toString());
            turnNode.appendChild(stateNode);

            gameNode.appendChild(turnNode);

            /* board */
            Element boardNode = doc.createElement("board");

            for (int i = 0; i < game.getBoard().DIM; i++) {
                for (int j = 0; j < game.getBoard().DIM; j++) {
                    Element cellNode = doc.createElement("cell");
                    Element xNode = doc.createElement("x");
                    Element yNode = doc.createElement("y");
                    Element levelNode = doc.createElement("level");
                    Block cell = (Block) game.getBoard().getCell(i, j);

                    xNode.setNodeValue(String.valueOf(i));
                    cellNode.appendChild(xNode);

                    yNode.setNodeValue(String.valueOf(j));
                    cellNode.appendChild(yNode);

                    levelNode.setNodeValue(cell.getLevel().toString());
                    cellNode.appendChild(levelNode);
                }
            }

            for (int i = 0; i < game.getNumPlayers(); i++) {
                Player player = game.getPlayer(i);
                for (int j = 0; j < player.getNumWorkers(); j++) {
                    Worker worker = player.getWorkers().get(j);
                    int x = worker.getX();
                    int y = worker.getY();

                    Node workerBoardNode = gameNode.getLastChild();
                    NodeList workerCellNode = workerBoardNode.getChildNodes();

                    int k = 0;
                    while (!(Integer.parseInt(workerCellNode.item(k).getChildNodes().item(X).getNodeValue()) == x && Integer.parseInt(workerCellNode.item(k).getChildNodes().item(Y).getNodeValue()) == y)) {
                        k++;
                    }

                    Element workerNode = doc.createElement("worker");
                    Element workerPlayerNode = doc.createElement("player");
                    Element workerNicknameNode = doc.createElement("nickname");

                    workerNicknameNode.setNodeValue(player.getNickName());
                    workerPlayerNode.appendChild(workerNicknameNode);
                    workerNode.appendChild(workerPlayerNode);
                    workerNode.setAttribute("gender", (worker.isMale() ? "male" : "female"));

                    workerCellNode.item(k).appendChild(workerNode);
                }
            }

            gameNode.appendChild(boardNode);

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save(Block block, String path) {
        try {
            int x = block.getX();
            int y = block.getY();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            Node boardNode = doc.getDocumentElement().getLastChild();
            NodeList cellNode = boardNode.getChildNodes();

            int k = 0;
            while (!(Integer.parseInt(cellNode.item(k).getChildNodes().item(X).getNodeValue()) == x && Integer.parseInt(cellNode.item(k).getChildNodes().item(Y).getNodeValue()) == y)) {
                k++;
            }

            cellNode.item(k).getChildNodes().item(LEVEL).setNodeValue(block.getLevel().toString());

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save(Worker worker, Player player, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            /* old position */
            Block cell = worker.getPreviousLocation();

            int x = cell.getX();
            int y = cell.getY();

            Node boardNode = doc.getDocumentElement().getLastChild();
            NodeList cellNode = boardNode.getChildNodes();

            int k = 0;
            while (!(Integer.parseInt(cellNode.item(k).getChildNodes().item(X).getNodeValue()) == x && Integer.parseInt(cellNode.item(k).getChildNodes().item(Y).getNodeValue()) == y)) {
                k++;
            }

            cellNode.item(k).removeChild(cellNode.item(k).getLastChild());

            /* new position */
            x = worker.getX();
            y = worker.getY();

            k = 0;
            while (!(Integer.parseInt(cellNode.item(k).getChildNodes().item(X).getNodeValue()) == x && Integer.parseInt(cellNode.item(k).getChildNodes().item(Y).getNodeValue()) == y)) {
                k++;
            }

            Element workerNode = doc.createElement("worker");
            Element workerPlayerNode = doc.createElement("player");
            Element workerNicknameNode = doc.createElement("nickname");

            workerNicknameNode.setNodeValue(player.getNickName());
            workerPlayerNode.appendChild(workerNicknameNode);
            workerNode.appendChild(workerPlayerNode);
            workerNode.setAttribute("gender", (worker.isMale() ? "male" : "female"));

            cellNode.item(k).appendChild(workerNode);

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save(GameState state, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            Node turnNode = doc.getDocumentElement().getChildNodes().item(TURN);

            turnNode.getChildNodes().item(STATE).setNodeValue(state.toString());

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save(Player currentPlayer, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            Node turnNode = doc.getDocumentElement().getChildNodes().item(TURN);
            Node currNicknameNode = turnNode.getChildNodes().item(PLAYER).getChildNodes().item(NICKNAME);
            currNicknameNode.setNodeValue(currentPlayer.getNickName());

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static void save(List<Player> players, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            Node lobbyNode = doc.getDocumentElement().getFirstChild();

            /* cleaning */
            while (lobbyNode.getFirstChild() != null)
                lobbyNode.removeChild(lobbyNode.getFirstChild());

            /* adding */
            for (int i = 0; i < players.size(); i++) {
                Element playerNode = doc.createElement("player");
                Element nicknameNode = doc.createElement("nickname");
                Element godNode = doc.createElement("god");

                nicknameNode.setNodeValue(players.get(i).getNickName());
                playerNode.appendChild(nicknameNode);

                godNode.setNodeValue(players.get(i).getCard().getName());
                playerNode.appendChild(godNode);

                lobbyNode.appendChild(playerNode);
            }

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static Game load(String path) throws ParserConfigurationException, SAXException {
        Game game = new Game();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            NodeList confNodes = doc.getDocumentElement().getChildNodes();
            Node lobbyNode = confNodes.item(LOBBY);
            Node turnNode = confNodes.item(TURN);
            Node boardNode = confNodes.item(BOARD);

            /* lobby */
            NodeList playerNode = lobbyNode.getChildNodes();
            for (int i = 0; i < playerNode.getLength(); i++) {
                String nickname = playerNode.item(i).getChildNodes().item(NICKNAME).getTextContent();

                game.addPlayer(nickname);
                game.setCurrentPlayer(game.getPlayer(nickname));
                game.assignCard(God.parseString(playerNode.item(i).getChildNodes().item(GOD).getTextContent()));
            }

            /* turn */
            game.setCurrentPlayer((game.getPlayer(turnNode.getChildNodes().item(PLAYER).getChildNodes().item(NICKNAME).getTextContent())));
            State state = State.parseString(turnNode.getChildNodes().item(STATE).getTextContent());
            game.setState(state);

            /* board */
            NodeList cells = boardNode.getChildNodes();
            for (int i = 0; i < cells.getLength(); i++) {
                Node cellNode = cells.item(i);
                Block cell = (Block) game.getBoard().getCell(Integer.parseInt(cellNode.getChildNodes().item(X).getTextContent()), Integer.parseInt(cellNode.getChildNodes().item(Y).getTextContent()));
                cell.setLevel(Level.parseString(cellNode.getChildNodes().item(LEVEL).getTextContent()));

                if (cellNode.getChildNodes().getLength() == WORKER + 1) {
                    Worker worker = new Worker(cell);
                    worker.setGender(cellNode.getChildNodes().item(WORKER).getAttributes().getNamedItem("gender").getTextContent().equals("male"));
                    game.getPlayer(cellNode.getChildNodes().item(WORKER).getChildNodes().item(PLAYER).getChildNodes().item(NICKNAME).getTextContent()).addWorker(worker);
                }
            }

        }
        catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        return game;
    }
}
