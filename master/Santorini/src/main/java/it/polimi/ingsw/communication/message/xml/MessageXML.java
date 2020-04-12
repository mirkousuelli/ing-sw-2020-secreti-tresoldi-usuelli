package it.polimi.ingsw.communication.message.xml;

import it.polimi.ingsw.communication.message.Message;

public abstract class MessageXML<H, P> {

    private H header;

    private P payload;

    public MessageXML() {}

    public H getHeader() {
        return header;
    }

    public void setHeader(H header) {
        this.header = header;
    }

    public P getPayload() {
        return payload;
    }

    public void setPayload(P payload) {
        this.payload = payload;
    }

}
