package it.polimi.ingsw.communication.message;

public abstract class Message<H, P> {

    protected final H header;
    protected final P payload;

    Message(H header, P payload) {
        this.header = header;
        this.payload = payload;
    }

    public H getHeader() {
        return this.header;
    }

    public P getPayload() {
        return this.payload;
    }

}
