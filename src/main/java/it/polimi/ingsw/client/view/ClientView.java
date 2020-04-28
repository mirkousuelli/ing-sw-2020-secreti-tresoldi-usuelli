package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;

import java.util.Scanner;

public abstract class ClientView<S> implements Runnable {

    protected final ReducedPlayer player;
    protected final ClientModel<S> clientModel;

    private Demand<S> demand;
    private Answer<S> answer;
    private boolean isActive;
    private boolean isChanged;
    public final Object lockDemand;

    public ClientView(ReducedPlayer player, ClientModel<S> clientModel) {
        this.player = player;
        this.clientModel = clientModel;
        lockDemand = new Object();

        setActive(false);
        setChanged(false);
    }

    public ClientView(String playerName, ClientModel<S> clientModel) {
        this(new ReducedPlayer(playerName), clientModel);
    }

    public ReducedPlayer getPlayer() {
        return player;
    }

    protected synchronized void setDemand(Demand<S> demand) {
            this.demand = demand;
    }

    public synchronized Demand<S> fetchDemand() {
        setChanged(false);
        return demand;
    }

    public synchronized boolean isChanged() {
        return isChanged;
    }

    protected synchronized void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public synchronized boolean isActive() {
        return isActive;
    }

    public synchronized void setActive(boolean active) {
        isActive = active;
    }

    public synchronized Answer<S> getAnswer() {
        return answer;
    }

    public synchronized void setAnswer(Answer<S> answer) {
        this.answer = answer;
    }

    protected void endGame() {
        setActive(false);
    }

    protected void setInitialRequest() {
        synchronized (this) {
            setDemand(new Demand<S>(DemandType.CONNECT, (S) player.getNickname()));
            setChanged(true);
        }

        synchronized (lockDemand) {
            lockDemand.notifyAll();
        }
    }
}
