package it.polimi.ingsw.model.cards.xml;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.cards.powers.tags.Effect;
import it.polimi.ingsw.model.cards.powers.tags.Timing;
import it.polimi.ingsw.model.cards.powers.tags.WorkerPosition;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

public class HandlerDTD extends DefaultHandler {

    private Deck deck;
    private List<God> godsList;
    private God currGod;
    private Card currCard;
    private int index;

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
        index = 0;

        this.godsList = gods;
        this.currGod = godsList.get(index);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (attributes.getValue("name").equalsIgnoreCase(this.currGod.toString())) {

            currCard = new Card();
            this.read = true;

        } else if (read) {

            switch (qName) {
                case "NAME":
                    this.name = true;
                    break;

                case "DESCRIPTION":
                    this.description = true;
                    break;

                case "WORKER":
                    this.currCard.getPower().setWorkerType(WorkerType.parseString(attributes.getValue("who")));
                    break;

                case "WORKERPOS":
                    this.currCard.getPower().setWorkerInitPos(WorkerPosition.parseString(attributes.getValue("where")));
                    break;

                case "EFFECT":
                    this.currCard.getPower().setEffect(Effect.parseString(attributes.getValue("what")));
                    break;

                case "TIMING":
                    this.currCard.getPower().setTiming(Timing.parseString(attributes.getValue("when")));
                    break;

                case "CONSTRAINTS":
                    this.currCard.getPower().getConstraints().setSameCell(attributes.getValue("samecell").equalsIgnoreCase("true"));
                    this.currCard.getPower().getConstraints().setNotSameCell(attributes.getValue("notsamecell").equalsIgnoreCase("true"));
                    this.currCard.getPower().getConstraints().setPerimCell(attributes.getValue("perimcell").equalsIgnoreCase("true"));
                    this.currCard.getPower().getConstraints().setNotPerimCell(attributes.getValue("notperimcell").equalsIgnoreCase("true"));
                    this.currCard.getPower().getConstraints().setUnderItself(attributes.getValue("underitself").equalsIgnoreCase("true"));
                    break;

                case "NUMADD":
                    numadd = true;
                    break;

                case "MOVE":
                    this.currCard.getPower().setEffect(Effect.parseString(attributes.getValue("type")));
                    break;

                case "BUILD":
                    this.currCard.getPower().setEffect(Effect.parseString(attributes.getValue("type")));
                    break;

                case "MALUS":
                    this.currCard.getPower().setEffect(Effect.parseString(attributes.getValue("type")));
                    this.currCard.getPower().setEffect(Effect.parseString(attributes.getValue("permanent")));
                    this.currCard.getPower().setEffect(Effect.parseString(attributes.getValue("personal")));
                    this.currCard.getPower().setEffect(Effect.parseString(attributes.getValue("direction")));
                    break;

                case "NUMTURNS":
                    numturns = true;
                    break;

                case "WIN":
                    this.currCard.getPower().setEffect(Effect.parseString(attributes.getValue("type")));
                    break;

                default:
                    break;
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("GOD")) {
            this.deck.addCard(currCard);
            index += 1;
            this.currGod = this.godsList.get(index);
            this.read = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

        String str = new String(ch, start, length);

        if (name) {
            currCard.setName(str);
            name  = false;
        }

        if (description) {
            currCard.setDescription(str);
            description = false;
        }

        if (numadd) {
            this.currCard.getPower().getConstraints().setNumberOfAdditional(Integer.parseInt(str));
            numadd = false;
        }

        if (numturns) {
            //this,currCard.getPower().malus.setNumberOfTurns(Integer.parseInt(str));
            numturns = false;
        }
    }
}
