package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.xml.network.object.AnswerXML;

public class Answer<S> extends Message<AnswerType, S> {

    protected final DemandType context;

    public Answer(AnswerType header, DemandType context, S payload) {
        super(header, payload);
        this.context = context;
    }

    public Answer(AnswerXML<S> xml) {
        super(xml.getHeader(), xml.getPayload());
        this.context = xml.getContext();
    }

    public DemandType getContext() {
        return this.context;
    }

    @Override
    public AnswerXML<S> messageToXML() {

        AnswerXML<S> toSend = new AnswerXML<>();

        toSend.setHeader(this.getHeader());
        toSend.setContext(this.getContext());
        toSend.setPayload(this.getPayload());

        return toSend;

    }
}
