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

import java.util.Arrays;

public class HandlerDTD extends DefaultHandler {

    private final String WHO = "WORKER";
    private final String WHERE = "WORKERPOS";
    private final String WHAT = "EFFECT";
    private final String WHEN = "TIMING";
    private final String WHY = "CONSTRAINTS";

    private Deck deck;
    private God god;
    private Card newCard;

    private boolean name;
    private boolean description;
    private boolean numadd;

    public HandlerDTD(Deck deck) {
        super();

        this.deck = deck;
        this.god = null;

        this.name = false;
        this.description = false;
        this.numadd = false;
    }

    public void setGod(God god) {
        this.god = god;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("GOD")) {
            if (attributes.getValue("name").equalsIgnoreCase(this.god.toString())) {
                newCard = new Card();
            }
        }

        if (qName.equalsIgnoreCase("NAME")) {
            this.name = true;
        }

        if (qName.equalsIgnoreCase("DESCRIPTION")) {
            this.description = true;
        }

        /* POWER */

        if (qName.equalsIgnoreCase("WORKER")) {
            this.newCard.getPower().setWorkerType(WorkerType.parseString(attributes.getValue("who")));
        }

        if (qName.equalsIgnoreCase("WORKERPOS")) {
            this.newCard.getPower().setWorkerInitPos(WorkerPosition.parseString(attributes.getValue("where")));
        }

        if (qName.equalsIgnoreCase("EFFECT")) {
            this.newCard.getPower().setEffect(Effect.parseString(attributes.getValue("what")));
        }

        if (qName.equalsIgnoreCase("TIMING")) {
            this.newCard.getPower().setTiming(Timing.parseString(attributes.getValue("when")));
        }

        if (qName.equalsIgnoreCase("CONSTRAINTS")) {
            this.newCard.getPower().getConstraints().setSameCell(attributes.getValue("samecell").equalsIgnoreCase("true"));
            this.newCard.getPower().getConstraints().setNotSameCell(attributes.getValue("notsamecell").equalsIgnoreCase("true"));
            this.newCard.getPower().getConstraints().setPerimCell(attributes.getValue("perimcell").equalsIgnoreCase("true"));
            this.newCard.getPower().getConstraints().setNotPerimCell(attributes.getValue("notperimcell").equalsIgnoreCase("true"));
            this.newCard.getPower().getConstraints().setUnderItself(attributes.getValue("underitself").equalsIgnoreCase("true"));
        }

        if (qName.equalsIgnoreCase("NUMADD")) {
            numadd = true;
        }

        /* HOW */

        if (qName.equalsIgnoreCase("MOVE")) {
            this.newCard.getPower().   setEffect(Effect.parseString(attributes.getValue("type")));
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("GOD")) {
            this.deck.addCard(newCard);
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        String str = new String(ch, start, length);

        if (name) {
            newCard.setName(str);
            name  = false;
        }

        if (description) {
            newCard.setDescription(str);
            description = false;
        }

        if (numadd) {
            this.newCard.getPower().getConstraints().setNumberOfAdditional(Integer.parseInt(str));
            numadd = false;
        }
    }
}
