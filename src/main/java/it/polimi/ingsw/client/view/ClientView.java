package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

public abstract class ClientView<S> extends SantoriniRunnable<S> {

    protected final ClientModel<S> clientModel;
    private boolean isFree;
    public final Object lockFree;

    public ClientView(ClientModel<S> clientModel) {
        super();
        this.clientModel = clientModel;

        lockFree = new Object();
        setFree(true);
    }

    public boolean isFree() {
        boolean ret;

        synchronized (lockFree) {
            ret = isFree;
        }

        return ret;
    }

    public void setFree(boolean free) {
        synchronized (lockFree) {
            isFree = free;
        }
    }

    protected void endGame() {
        setActive(false);
    }

    protected void setInitialRequest() {
        setFree(false);

        synchronized (this) {
            setDemand(new Demand<S>(DemandType.CONNECT, (S) (new ReducedMessage(clientModel.getPlayer().getNickname()))));
            setChanged(true);
        }

        setFree(true);

        synchronized (lockDemand) {
            lockDemand.notifyAll();
        }
    }
}
