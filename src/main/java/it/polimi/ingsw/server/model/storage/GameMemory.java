package it.polimi.ingsw.server.model.storage;

import it.polimi.ingsw.communication.message.payload.ILevel;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Class that permits the write, save and load of a game: it is used to allow the feature of persistence
 */
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
    private static final int MALE = 0;
    private static final int FEMALE = 1;

    /* board */
    private static final int X = 0;
    private static final int Y = 1;
    private static final int LEVEL = 2;
    private static final int PREV = 3;

    private static final Logger LOGGER = Logger.getLogger(GameMemory.class.getName());

    /**
     * Method that allows to write on the chosen document
     *
     * @param doc  the document where to write
     * @param path the string to write
     * @throws TransformerException  an exceptional condition that occurred during the transformation process
     * @throws FileNotFoundException signals that an attempt to open the file has failed
     * @throws URISyntaxException    indicates that a string could not be parsed as a URI reference
     */
    private static void write(Document doc, String path) throws TransformerException, FileNotFoundException, URISyntaxException {
        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, GameMemory.class.getResource("/xml/game_grammar.dtd").toURI().toString());

        // send DOM to file
        StreamResult streamResult = new StreamResult(new FileOutputStream(path));
        tr.transform(new DOMSource(doc), streamResult);

        try {
            streamResult.getOutputStream().close();
        } catch (IOException e) {
            LOGGER.info(() -> "couldn't write " + path);
        }
    }

    /**
     * Method that saves the current game: all elements of the game are saved, like the board (with the correct level of
     * every cell and eventual pawn on it), the current state, the current player and eventual maluses active
     *
     * @param game the game that is saved
     * @param path the path where to save
     */
    public static void save(Game game, String path) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element gameNode = doc.createElement("game");

            /* lobby */
            Element lobbyNode = doc.createElement("lobby");

            for (int i = 0; i < game.getNumPlayers(); i++) {
                Element playerNode = doc.createElement("player");
                Element nicknameNode = doc.createElement("nickname");
                Element godNode = doc.createElement("god");
                Player player = game.getPlayer(i);

                if (player.equals(game.getCurrentPlayer())) {
                    playerNode.setAttribute("state", game.getState().getName());
                }

                nicknameNode.setTextContent(player.getNickName());
                playerNode.appendChild(nicknameNode);

                godNode.setTextContent(player.getCard().getName());
                playerNode.appendChild(godNode);

                Element pawnsNode = doc.createElement("pawns");
                for (int j = 0; j < player.getWorkers().size(); j++) {
                    Element workerNode = doc.createElement("worker");
                    Element xNode = doc.createElement("x");
                    Element yNode = doc.createElement("y");
                    Worker worker = player.getWorkers().get(j);

                    workerNode.setAttribute("current", player.getCurrentWorker().equals(worker) ? "true" : "false");
                    workerNode.setAttribute("gender", worker.isMale() ? "male" : "female");

                    xNode.setTextContent(String.valueOf(worker.getX()));
                    yNode.setTextContent(String.valueOf(worker.getY()));

                    workerNode.appendChild(xNode);
                    workerNode.appendChild(yNode);
                    pawnsNode.appendChild(workerNode);
                }
                playerNode.appendChild(pawnsNode);

                if (player.getMalusList().size() > 0) {
                    Node malusListNode = doc.createElement("malusList");
                    for (int k = 0; k < player.getMalusList().size(); k++) {
                        Element malusNode = doc.createElement("malus");
                        Element typeNode = doc.createElement("type");
                        Element forbiddenNode = doc.createElement("forbidden");
                        Malus malus = player.getMalusList().get(k);

                        malusNode.setAttribute("permanent", malus.isPermanent() ? "true" : "false");
                        typeNode.setTextContent(malus.getMalusType().toString());
                        malusNode.appendChild(typeNode);

                        if (!malus.isPermanent()) {
                            Element numTurnNode = doc.createElement("numTurn");
                            numTurnNode.setTextContent(String.valueOf(malus.getNumberOfTurns()));
                            malusNode.appendChild(numTurnNode);
                        }

                        for (int l = 0; l < malus.getDirection().size(); l++) {
                            Element directionNode = doc.createElement("direction");
                            directionNode.setTextContent(malus.getDirection().get(l).toString());
                            forbiddenNode.appendChild(directionNode);
                        }
                        malusNode.appendChild(forbiddenNode);
                        malusListNode.appendChild(malusNode);
                    }
                    playerNode.appendChild(malusListNode);
                }
                lobbyNode.appendChild(playerNode);
            }
            gameNode.appendChild(lobbyNode);

            /* board */
            Element boardNode = doc.createElement("board");

            for (int m = 0; m < game.getBoard().DIM; m++) {
                for (int n = 0; n < game.getBoard().DIM; n++) {
                    Element cellNode = doc.createElement("cell");
                    Element xNode = doc.createElement("x");
                    Element yNode = doc.createElement("y");
                    Element levelNode = doc.createElement("level");
                    Element prevNode = doc.createElement("prev");
                    Block cell = (Block) game.getBoard().getCell(m, n);

                    xNode.setTextContent(String.valueOf(m));
                    cellNode.appendChild(xNode);

                    yNode.setTextContent(String.valueOf(n));
                    cellNode.appendChild(yNode);

                    levelNode.setTextContent(cell.getLevel().getName());
                    cellNode.appendChild(levelNode);

                    prevNode.setTextContent(cell.getPreviousLevel().getName());
                    cellNode.appendChild(prevNode);

                    boardNode.appendChild(cellNode);
                }
            }
            gameNode.appendChild(boardNode);
            doc.appendChild(gameNode);
            GameMemory.write(doc, path);
        } catch (ParserConfigurationException | TransformerException | FileNotFoundException | URISyntaxException e) {
            LOGGER.info(() -> "couldn't save " + path);
        }
    }

    /**
     * Method that saves the given block, with information about its current and previous level
     *
     * @param block the block that is saved
     * @param path  the path where to save
     */
    public static void save(Block block, String path) {
        try {
            int x = block.getX();
            int y = block.getY();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            Node gameNode = doc.getDocumentElement();
            Node boardNode = gameNode.getChildNodes().item(BOARD);
            NodeList cellNode = boardNode.getChildNodes();

            int k = 0;
            while (!(Integer.parseInt(cellNode.item(k).getChildNodes().item(X).getTextContent()) == x && Integer.parseInt(cellNode.item(k).getChildNodes().item(Y).getTextContent()) == y)) {
                k++;
            }
            cellNode.item(k).getChildNodes().item(LEVEL).setTextContent(block.getLevel().getName());
            cellNode.item(k).getChildNodes().item(PREV).setTextContent(block.getPreviousLevel().getName());
            factory.setIgnoringElementContentWhitespace(false);
            GameMemory.write(doc, path);
        } catch (ParserConfigurationException | TransformerException | IOException | SAXException | URISyntaxException e) {
            LOGGER.info(() -> "couldn't save " + path);
        }
    }

    /**
     * Method that saves the current worker and its corresponding player
     *
     * @param worker the worker that is saved
     * @param player the name of the player
     * @param path   the path where to save
     */
    public static void save(Worker worker, Player player, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            Node gameNode = doc.getDocumentElement();
            NodeList playersNode = gameNode.getChildNodes().item(LOBBY).getChildNodes();

            int i = 0;
            while (!player.getNickName().equals(playersNode.item(i).getChildNodes().item(NICKNAME).getTextContent())) {
                i++;
            }

            Node playerNode = playersNode.item(i);
            NodeList pawnsNode = playerNode.getChildNodes().item(PAWNS).getChildNodes();
            int gender = worker.isMale() ? MALE : FEMALE;
            NodeList workerNode = pawnsNode.item(gender).getChildNodes();
            pawnsNode.item(gender).getAttributes().getNamedItem("current").setTextContent(player.getCurrentWorker().equals(worker) ? "true" : "false");
            workerNode.item(X).setTextContent(String.valueOf(worker.getX()));
            workerNode.item(Y).setTextContent(String.valueOf(worker.getY()));

            GameMemory.write(doc, path);
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException | URISyntaxException parserConfigurationException) {
            parserConfigurationException.printStackTrace();
        }
    }

    /**
     * Method that saves the current state
     *
     * @param state the state that is saved
     * @param path  the path where to save
     */
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
            GameMemory.write(doc, path);
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException | URISyntaxException e) {
            LOGGER.info(() -> "couldn't save " + path);
        }
    }

    /**
     * Method that saves the current player and the state
     *
     * @param currentPlayer the current player
     * @param state         the state
     * @param path          the path where to save
     */
    public static void save(Player currentPlayer, State state, String path) {
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
                    playerNode.item(i).getAttributes().removeNamedItem("state");
                }
                i++;
            }

            i = 0;
            while (!currentPlayer.getNickName().equals(playerNode.item(i).getChildNodes().item(NICKNAME).getTextContent())) {
                i++;
            }
            ((Element) playerNode.item(i)).setAttribute("state", state.toString());
            GameMemory.write(doc, path);
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException | URISyntaxException e) {
            LOGGER.info(() -> "couldn't save " + path);
        }
    }

    /**
     * Method that saves the list of players, with information about their workers and eventual maluses active
     *
     * @param players the list of players that is saved
     * @param path    the path where to save
     */
    public static void save(List<Player> players, String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(path);

            Node gameNode = doc.getDocumentElement();
            gameNode.removeChild(gameNode.getFirstChild());

            Element lobbyNode = doc.createElement("lobby");

            for (int i = 0; i < players.size(); i++) {
                Element playerNode = doc.createElement("player");
                Element nicknameNode = doc.createElement("nickname");
                Element godNode = doc.createElement("god");

                nicknameNode.setTextContent(players.get(i).getNickName());
                playerNode.appendChild(nicknameNode);

                godNode.setTextContent(players.get(i).getCard().getName());
                playerNode.appendChild(godNode);

                Element pawnsNode = doc.createElement("pawns");
                for (int j = 0; j < players.get(i).getWorkers().size(); j++) {
                    Element workerNode = doc.createElement("worker");
                    Element xNode = doc.createElement("x");
                    Element yNode = doc.createElement("y");
                    Worker worker = players.get(i).getWorkers().get(j);

                    workerNode.setAttribute("current", players.get(i).getCurrentWorker().equals(worker) ? "true" : "false");
                    workerNode.setAttribute("gender", worker.isMale() ? "male" : "female");
                    xNode.setTextContent(String.valueOf(worker.getX()));
                    yNode.setTextContent(String.valueOf(worker.getY()));

                    workerNode.appendChild(xNode);
                    workerNode.appendChild(yNode);
                    pawnsNode.appendChild(workerNode);
                }
                playerNode.appendChild(pawnsNode);

                if (players.get(i).getMalusList().size() > 0) {
                    Node malusListNode = doc.createElement("malusList");
                    for (int k = 0; k < players.get(i).getMalusList().size(); k++) {
                        Element malusNode = doc.createElement("malus");
                        Element typeNode = doc.createElement("type");
                        Element forbiddenNode = doc.createElement("forbidden");
                        Malus malus = players.get(i).getMalusList().get(k);

                        malusNode.setAttribute("permanent", malus.isPermanent() ? "true" : "false");
                        typeNode.setTextContent(malus.getMalusType().toString());
                        malusNode.appendChild(typeNode);

                        if (!malus.isPermanent()) {
                            Element numTurnNode = doc.createElement("numTurn");
                            numTurnNode.setTextContent(String.valueOf(malus.getNumberOfTurns()));
                            malusNode.appendChild(numTurnNode);
                        }

                        for (int l = 0; l < malus.getDirection().size(); l++) {
                            Element directionNode = doc.createElement("direction");
                            directionNode.setTextContent(malus.getDirection().get(l).toString());
                            forbiddenNode.appendChild(directionNode);
                        }
                        malusNode.appendChild(forbiddenNode);
                        malusListNode.appendChild(malusNode);
                    }
                    playerNode.appendChild(malusListNode);
                }
                lobbyNode.appendChild(playerNode);
            }
            Node boardNode = gameNode.getFirstChild().cloneNode(true);
            gameNode.removeChild(gameNode.getFirstChild());
            gameNode.appendChild(lobbyNode);
            gameNode.appendChild(boardNode);
            GameMemory.write(doc, path);
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException | URISyntaxException e) {
            LOGGER.info(() -> "couldn't save " + path);
        }
    }

    /**
     * Method that allows the game to be loaded back: it sets all the previously saved pieces of information, allowing
     * the game to continue from where it was saved last
     *
     * @param path the path where the file to load is located
     * @return the saved game
     * @throws ParserConfigurationException indicates a serious configuration error
     * @throws SAXException                 a basic error or warning information
     */
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

                    if (worker.isMale())
                        worker.setId(2);
                    else
                        worker.setId(1);

                    player.addWorker(worker);

                    if (workerNode.item(j).getAttributes().getNamedItem("current").getTextContent().equals("true")) {
                        player.setCurrentWorker(worker);
                    }
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
                Block cell = (Block) game.getBoard().getCell(x, y);

                cell.setLevel((Level) ILevel.parseString(cellNode.getChildNodes().item(LEVEL).getTextContent()));
                cell.setPreviousLevel((Level) ILevel.parseString(cellNode.getChildNodes().item(PREV).getTextContent()));
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            LOGGER.info(() -> "couldn't load " + path);
            return null;
        }

        return game;
    }
}
