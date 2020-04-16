package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.xml.network.object.MessageXML;

public abstract class Message<H, P> {

    private final H header;
    private final P payload;

    public Message(H header, P payload) {
        this.header = header;
        this.payload = payload;
    }

    public H getHeader() {
        return this.header;
    }

    public P getPayload() {
        return this.payload;
    }

    public abstract MessageXML<H, P> messageToXML();

}
