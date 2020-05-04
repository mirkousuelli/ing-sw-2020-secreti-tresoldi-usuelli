package it.polimi.ingsw.server.model.cards.gods;

import it.polimi.ingsw.server.model.cards.Deck;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import java.util.Collections;
import java.util.List;

public class GodParser {
    private final String XMLFILE = "src/main/java/it/polimi/ingsw/server/model/cards/gods/xml/gods.xml";

    private SAXParserFactory factory;
    private SAXParser parser;
    private GodHandler handler;

    public GodParser(Deck deck) throws ParserConfigurationException, SAXException {
        this.factory = SAXParserFactory.newInstance();
        this.parser = factory.newSAXParser();
        this.handler = new GodHandler(deck);
    }

    public void parseCards(List<God> gods) {
        try{
            Collections.sort(gods);
            this.handler.setGods(gods);
            this.parser.parse(this.XMLFILE, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}