package it.polimi.ingsw.server.model.cards.gods;

import it.polimi.ingsw.server.model.cards.Deck;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import java.util.Collections;
import java.util.List;

public class GodParser {
    private final String XMLFILE = "/xml/gods.xml";

    private SAXParserFactory factory;
    private SAXParser parser;
    private GodHandler handler;

    public GodParser(Deck deck) throws ParserConfigurationException, SAXException {
        factory = SAXParserFactory.newInstance();
        parser = factory.newSAXParser();
        handler = new GodHandler(deck);
    }

    public void parseCards(List<God> gods) {
        try{
            Collections.sort(gods);
            handler.setGods(gods);
            parser.parse(this.getClass().getResource(XMLFILE).toURI().toString(), handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
