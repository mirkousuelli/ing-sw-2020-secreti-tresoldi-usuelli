package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;

import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SantoriniRunnable<S> implements Runnable {

    private boolean isActive;
    private boolean isChanged;

    private Demand<S> demand;
    protected Answer<S> answer;

    public final Object lockDemand;
    public final Object lockAnswer;

    private static final Logger LOGGER = Logger.getLogger(SantoriniRunnable.class.getName());

    public SantoriniRunnable() {
        this.lockDemand = new Object();
        this.lockAnswer = new Object();

        setActive(false);
        setChanged(false);
    }

    @Override
    public void run() {
        setActive(true);
        setChanged(false);

        try {
            startThreads();
        } catch (InterruptedException | NoSuchElementException e) {
            LOGGER.log(Level.SEVERE, "Connection closed from the client side", e);
        } finally {
            setActive(false);
        }
    }

    protected synchronized boolean isActive() {
        return isActive;
    }

    protected synchronized void setActive(boolean active) {
        isActive = active;
    }

    public synchronized boolean isChanged() {
        return isChanged;
    }

    public synchronized void setChanged(boolean changed) {
        isChanged = changed;
    }

    protected void setDemand(Demand<S> demand) {
        synchronized (lockDemand) {
            this.demand = demand;
        }
    }

    public Demand<S> getDemand() {
        Demand<S> temp;

        synchronized (lockDemand) {
            temp = demand;
        }
        return temp;
    }

    public Answer<S> getAnswer() {
        Answer<S> temp;

        synchronized (lockAnswer) {
            temp = answer;
        }

        return temp;
    }

    protected void setAnswer(Answer<S> answer) {
        synchronized (lockAnswer) {
            this.answer = answer;
        }
    }

    protected abstract void startThreads() throws InterruptedException;
}
