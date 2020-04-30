package it.polimi.ingsw.server.model.storage;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.*;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class GameMemory {
    /* game */
    private static final int LOBBY = 0;
    private static final int BOARD = 1;

    /* lobby */
    private static final int NICKNAME = 0;
    private static final int GOD = 1;
    private static final int PAWNS = 2;
    private static final int MALUS = 3;
    private static final int TYPE = 0;
    private static final int NUMTURN = 1;
    private static final int DIRECTION = 2;

    /* board */
    private static final int X = 0;
    private static final int Y = 1;
    private static final int LEVEL = 2;

    private static void saveFile(Document doc, String path) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        DOMImplementation domImpl = doc.getImplementation();
        DocumentType doctype = domImpl.createDocumentType("doctype",
                "Santorini",
                "game_grammar.dtd");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(path));
        transformer.transform(source, result);
    }

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
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            NodeList confNodes = doc.getDocumentElement().getChildNodes();
            Node lobbyNode = confNodes.item(LOBBY);
            NodeList playerNode = lobbyNode.getChildNodes();

            int i = 0;
            while (i < playerNode.getLength()) {
                if (playerNode.item(i).getAttributes().getLength() > 0) {
                    playerNode.item(i).getAttributes().getNamedItem("state").setTextContent(state.getName());
                }
                i++;
            }

            GameMemory.saveFile(doc, path);

        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void save(Player currentPlayer, String path) {
        /*try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            Node turnNode = doc.getDocumentElement().getChildNodes().item(TURN);
            Node currNicknameNode = turnNode.getChildNodes().item(PLAYER).getChildNodes().item(NICKNAME);
            currNicknameNode.setNodeValue(currentPlayer.getNickName());

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }*/
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
        String currentPlayer = "";

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            NodeList confNodes = doc.getDocumentElement().getChildNodes();
            Node lobbyNode = confNodes.item(LOBBY);
            Node boardNode = confNodes.item(BOARD);

            /* lobby */
            NodeList playerNode = lobbyNode.getChildNodes();
            for (int i = 0; i < playerNode.getLength(); i++) {
                String nickname = playerNode.item(i).getChildNodes().item(NICKNAME).getTextContent();
                Player player = new Player(nickname);

                game.addPlayer(player);

                if (playerNode.item(i).getAttributes().getLength() > 0) {
                    currentPlayer = nickname;
                    game.setState(Objects.requireNonNull(State.parseString(playerNode.item(i).getAttributes().getNamedItem("state").getTextContent())));
                }

                game.setCurrentPlayer(player);
                game.assignCard(God.parseString(playerNode.item(i).getChildNodes().item(GOD).getTextContent()));

                NodeList workerNode = playerNode.item(i).getChildNodes().item(PAWNS).getChildNodes();
                for (int j = 0; j < workerNode.getLength(); j++) {
                    int x = Integer.parseInt(workerNode.item(j).getChildNodes().item(X).getTextContent());
                    int y = Integer.parseInt(workerNode.item(j).getChildNodes().item(Y).getTextContent());

                    Worker worker = new Worker((Block) game.getBoard().getCell(x, y));
                    worker.setGender(workerNode.item(j).getAttributes().getNamedItem("gender").getTextContent().equals("male"));

                    player.addWorker(worker);
                }

                if (playerNode.item(i).getChildNodes().getLength() > MALUS) {
                    NodeList malusNode = playerNode.item(i).getChildNodes().item(MALUS).getChildNodes();
                    for (int k = 0; k < malusNode.getLength(); k++) {
                        int offset = 0;
                        Malus malus = new Malus();
                        malus.setMalusType(MalusType.parseString(malusNode.item(k).getChildNodes().item(TYPE).getTextContent()));
                        malus.setPermanent((malusNode.item(k).getAttributes().getNamedItem("permanent").getTextContent()).equals("true"));

                        if (!malus.isPermanent())
                            malus.setNumberOfTurns(Integer.parseInt(malusNode.item(k).getChildNodes().item(NUMTURN).getTextContent()));
                        else
                            offset = 1;

                        NodeList directionNode = malusNode.item(k).getChildNodes().item(DIRECTION - offset).getChildNodes();
                        for (int l = 0; l < directionNode.getLength(); l++) {
                            malus.addDirectionElement(MalusLevel.parseString(directionNode.item(l).getTextContent()));
                        }
                        player.addMalus(malus);
                    }
                }
            }

            /* turn */
            game.setCurrentPlayer(game.getPlayer(currentPlayer));

            /* board */
            NodeList cells = boardNode.getChildNodes();
            for (int i = 0; i < cells.getLength(); i++) {
                Node cellNode = cells.item(i);
                int x = Integer.parseInt(cellNode.getChildNodes().item(X).getTextContent());
                int y = Integer.parseInt(cellNode.getChildNodes().item(Y).getTextContent());
                Block cell = (Block) game.getBoard().getCell(x ,y);

                cell.setLevel(Level.parseString(cellNode.getChildNodes().item(LEVEL).getTextContent()));
            }
        }
        catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        return game;
    }
}
