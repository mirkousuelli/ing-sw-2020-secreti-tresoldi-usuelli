package it.polimi.ingsw.communication.message;

public abstract class Message<H, P> {

    private H header;
    private P payload;

    public Message(H header, P payload) {
        this.header = header;
        this.payload = payload;
    }

    public Message(){}

    public H getHeader() {
        return this.header;
    }

    public P getPayload() {
        return this.payload;
    }

    public void setHeader(H header) {
        this.header = header;
    }

    public void setPayload(P payload) {
        this.payload = payload;
    }

}
