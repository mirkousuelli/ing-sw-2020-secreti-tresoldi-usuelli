package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReduceDemandChoice;

public abstract class ClientView<S> implements Runnable {

    protected final ClientModel<S> clientModel;

    private Demand<S> demand;
    protected Answer<S> answer;
    private boolean isActive;
    private boolean isChanged;
    private boolean isFree;

    public final Object lockDemand;
    public final Object lockFree;

    public ClientView(ClientModel<S> clientModel) {
        this.clientModel = clientModel;
        lockDemand = new Object();
        lockFree = new Object();

        setActive(false);
        setChanged(false);
        setFree(true);
    }

    protected synchronized void setDemand(Demand<S> demand) {
        synchronized (lockDemand) {
            this.demand = demand;
        }
    }

    public synchronized Demand<S> getDemand() {
        Demand<S> temp;

        synchronized (lockDemand) {
            temp = demand;
        }
        return temp;
    }

    public synchronized boolean isChanged() {
        return isChanged;
    }

    public synchronized void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public synchronized boolean isActive() {
        return isActive;
    }

    public synchronized void setActive(boolean active) {
        isActive = active;
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

    public synchronized Answer<S> getAnswer() {
        return answer;
    }

    protected synchronized void setAnswer(Answer<S> answer) {
        this.answer = answer;
    }

    protected void endGame() {
        setActive(false);
    }

    protected void setInitialRequest() {
        synchronized (this) {
            setDemand(new Demand<S>(DemandType.CONNECT, (S) (new ReduceDemandChoice(clientModel.getPlayer().getNickname()))));
            setChanged(true);
        }

        synchronized (lockDemand) {
            lockDemand.notifyAll();
        }
    }
}
