package it.polimi.ingsw.model.cards.xml;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.cards.powers.*;
import it.polimi.ingsw.model.cards.powers.tags.Effect;
import it.polimi.ingsw.model.cards.powers.tags.Timing;
import it.polimi.ingsw.model.cards.powers.tags.WorkerPosition;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.BlockType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.WinType;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusType;
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
                            this.currCard.setPower(new MovePower());
                            break;
                        case "build":
                            this.currCard.setPower(new BuildPower());
                            break;
                        case "malus":
                            this.currCard.setPower(new MalusPower());
                            break;
                        case "win":
                            this.currCard.setPower(new WinConditionPower());
                            break;
                        default:
                            this.currCard.setPower(new Power());
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
                    this.currCard.getPower(indexPower).getMalus().setMalusType(MalusType.parseString(attributes.getValue("type")));
                    this.currCard.getPower(indexPower).getMalus().setPermanent(attributes.getValue("permanent").equalsIgnoreCase("true"));
                    this.currCard.getPower(indexPower).getMalus().setPersonal(attributes.getValue("personal").equalsIgnoreCase("true"));
                    break;

                case "UP":
                    this.currCard.getPower(indexPower).getMalus().addDirectionElement(MalusLevel.UP);
                    break;

                case "DOWN":
                    this.currCard.getPower(indexPower).getMalus().addDirectionElement(MalusLevel.DOWN);
                    break;

                case "SAME":
                    this.currCard.getPower(indexPower).getMalus().addDirectionElement(MalusLevel.SAME);
                    break;

                case "DEFAULT":
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
            this.currCard.getPower(indexPower).getMalus().setNumberOfTurns(Integer.parseInt(str));
            numturns = false;
        }
    }
}
