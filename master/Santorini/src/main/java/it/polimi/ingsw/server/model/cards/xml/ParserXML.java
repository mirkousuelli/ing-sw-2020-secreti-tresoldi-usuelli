package it.polimi.ingsw.server.model.cards.xml;

import it.polimi.ingsw.server.model.cards.Deck;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import it.polimi.ingsw.server.model.cards.God;
import org.xml.sax.SAXException;

import java.util.Collections;
import java.util.List;

public class ParserXML {
    private final String XMLFILE = "src/main/java/it/polimi/ingsw/server/model/cards/xml/gods.xml";

    private SAXParserFactory factory;
    private SAXParser parser;
    private HandlerDTD handler;

    public ParserXML(Deck deck) throws ParserConfigurationException, SAXException {
        this.factory = SAXParserFactory.newInstance();
        this.parser = factory.newSAXParser();
        this.handler = new HandlerDTD(deck);
    }

    public void parseCards(List<God> gods) {
        try{
            //Collections.sort(gods, (a, b) -> a.getType().compareTo(b.getType()));
            Collections.sort(gods);
            this.handler.setGods(gods);
            this.parser.parse(this.XMLFILE, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
