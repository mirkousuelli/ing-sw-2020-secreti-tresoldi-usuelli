package it.polimi.ingsw.model.cards.xml;

import it.polimi.ingsw.model.cards.Deck;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import it.polimi.ingsw.model.cards.God;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParserXML {
    private final String XMLFILE = "gods.xml";

    private SAXParserFactory factory;
    private SAXParser parser;
    private HandlerDTD handler;

    public ParserXML(Deck deck) throws ParserConfigurationException, SAXException {
        this.factory = SAXParserFactory.newInstance();
        this.parser = factory.newSAXParser();
        this.handler = new HandlerDTD(deck);
    }

    public void parseCard(God god) {
        try{
            this.handler.setGod(god);
            this.parser.parse(this.XMLFILE, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseDeck() {
        try{
            for (God god : God.values()) {
                this.parseCard(god);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
