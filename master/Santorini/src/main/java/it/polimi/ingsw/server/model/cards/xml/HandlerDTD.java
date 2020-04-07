package it.polimi.ingsw.server.model.cards.xml;

import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.God;
import it.polimi.ingsw.server.model.cards.powers.*;
import it.polimi.ingsw.server.model.cards.powers.tags.*;
import it.polimi.ingsw.server.model.cards.powers.tags.effectType.BlockType;
import it.polimi.ingsw.server.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.server.model.cards.powers.tags.effectType.WinType;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class HandlerDTD extends DefaultHandler {

    private Deck deck;
    private List<God> godsList;
    private God currGod;
    private God readGod;
    private Card currCard;
    private int indexGod;
    private int indexPower;

    private boolean read;
    private boolean name;
    private boolean description;
    private boolean numadd;
    private boolean numturns;
    private boolean personal;

    public HandlerDTD(Deck deck) {
        super();

        this.deck = deck;
        this.godsList = null;

        this.read = false;
        this.name = false;
        this.description = false;
        this.numadd = false;
        this.numturns = false;
    }

    public void setGods(List<God> gods) {
        indexGod = 0;
        indexPower = 0;

        this.godsList = gods;
        this.currGod = godsList.get(indexGod);
        this.readGod = this.currGod;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("GOD")) {

            readGod = God.parseString(attributes.getValue("id"));

            if (attributes.getValue("id").equalsIgnoreCase(this.currGod.toString())) {
                currCard = new Card();
                this.read = true;
                indexPower = 0;
            }
        } else if (read) {

            qName = qName.toUpperCase();

            switch (qName) {
                case "NAME":
                    this.name = true;
                    break;

                case "DESCRIPTION":
                    this.description = true;
                    break;

                case "EFFECT":
                    switch (attributes.getValue("what")) {
                        case "move":
                            this.currCard.addPower(new MovePower());
                            break;
                        case "build":
                            this.currCard.addPower(new BuildPower());
                            break;
                        case "malus":
                            this.currCard.addPower(new MalusPower());
                            break;
                        case "win":
                            this.currCard.addPower(new WinConditionPower());
                            break;
                        default:
                            this.currCard.addPower(new Power());
                            break;
                    }

                    this.currCard.getPower(indexPower).setEffect(Effect.parseString(attributes.getValue("what")));
                    break;

                case "WORKER":
                    this.currCard.getPower(indexPower).setWorkerType(WorkerType.parseString(attributes.getValue("who")));
                    break;

                case "WORKERPOS":
                    this.currCard.getPower(indexPower).setWorkerInitPos(WorkerPosition.parseString(attributes.getValue("where")));
                    break;

                case "TIMING":
                    this.currCard.getPower(indexPower).setTiming(Timing.parseString(attributes.getValue("when")));
                    break;

                case "CONSTRAINTS":
                    this.currCard.getPower(indexPower).getConstraints().setSameCell(attributes.getValue("samecell").equalsIgnoreCase("true"));
                    this.currCard.getPower(indexPower).getConstraints().setNotSameCell(attributes.getValue("notsamecell").equalsIgnoreCase("true"));
                    this.currCard.getPower(indexPower).getConstraints().setPerimCell(attributes.getValue("perimcell").equalsIgnoreCase("true"));
                    this.currCard.getPower(indexPower).getConstraints().setNotPerimCell(attributes.getValue("notperimcell").equalsIgnoreCase("true"));
                    this.currCard.getPower(indexPower).getConstraints().setUnderItself(attributes.getValue("underitself").equalsIgnoreCase("true"));
                    break;

                case "NUMADD":
                    numadd = true;
                    break;

                case "MOVE":
                    this.currCard.getPower(indexPower).setAllowedMove(MovementType.parseString(attributes.getValue("type")));
                    break;

                case "BUILD":
                    this.currCard.getPower(indexPower).setAllowedBlock(BlockType.parseString(attributes.getValue("type")));
                    break;

                case "MALUS":
                    this.currCard.getPower(indexPower).setMalus(new Malus());
                    this.currCard.getPower(indexPower).getMalus().setMalusType(MalusType.parseString(attributes.getValue("type")));
                    this.currCard.getPower(indexPower).getMalus().setPermanent(attributes.getValue("permanent").equalsIgnoreCase("true"));
                    //this.currCard.getPower(indexPower).getMalus().setPersonal(attributes.getValue("personal").equalsIgnoreCase("true"));
                    personal = false;
                    break;

                case "PERSONALMALUS":
                    this.currCard.getPower(indexPower).setPersonalMalus(new Malus());
                    this.currCard.getPower(indexPower).getPersonalMalus().setMalusType(MalusType.parseString(attributes.getValue("type")));
                    this.currCard.getPower(indexPower).getPersonalMalus().setPermanent(attributes.getValue("permanent").equalsIgnoreCase("true"));
                    //this.currCard.getPower(indexPower).getMalus().setPersonal(attributes.getValue("personal").equalsIgnoreCase("true"));
                    personal = true;
                    break;

                case "UP":
                    if (personal)
                        this.currCard.getPower(indexPower).getPersonalMalus().addDirectionElement(MalusLevel.UP);
                    else
                        this.currCard.getPower(indexPower).getMalus().addDirectionElement(MalusLevel.UP);
                    break;

                case "DOWN":
                    if (personal)
                        this.currCard.getPower(indexPower).getPersonalMalus().addDirectionElement(MalusLevel.DOWN);
                    else
                        this.currCard.getPower(indexPower).getMalus().addDirectionElement(MalusLevel.DOWN);
                    break;

                case "SAME":
                    if (personal)
                        this.currCard.getPower(indexPower).getPersonalMalus().addDirectionElement(MalusLevel.SAME);
                    else
                        this.currCard.getPower(indexPower).getMalus().addDirectionElement(MalusLevel.SAME);
                    break;

                case "DEFAULT":
                    if (personal)
                        this.currCard.getPower(indexPower).getPersonalMalus().addDirectionElement(MalusLevel.DEFAULT);
                    else
                        this.currCard.getPower(indexPower).getMalus().addDirectionElement(MalusLevel.DEFAULT);
                    break;

                case "NUMTURNS":
                    numturns = true;
                    break;

                case "WIN":
                    this.currCard.getPower(indexPower).setAllowedWin(WinType.parseString(attributes.getValue("type")));
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("GOD") && readGod.equals(currGod)) {
            this.deck.addCard(currCard);
            indexGod += 1;
            indexPower = 0;
            if (indexGod < godsList.size()) {
                this.currGod = this.godsList.get(indexGod);
            }
            this.read = false;
        } else if (qName.equalsIgnoreCase("POWER") && readGod.equals(currGod)) {
            indexPower += 1;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        String str = new String(ch, start, length);

        if (name) {
            currCard.setName(str);
            name  = false;
        } else if (description) {
            currCard.setDescription(str);
            description = false;
        } else if (numadd) {
            this.currCard.getPower(indexPower).getConstraints().setNumberOfAdditional(Integer.parseInt(str));
            numadd = false;
        } else if (numturns) {
            if (personal)
                this.currCard.getPower(indexPower).getPersonalMalus().setNumberOfTurns(Integer.parseInt(str));
            else
                this.currCard.getPower(indexPower).getMalus().setNumberOfTurns(Integer.parseInt(str));
            numturns = false;
        }
    }
}
