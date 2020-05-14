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
    public final Object lock;

    private static final Logger LOGGER = Logger.getLogger(SantoriniRunnable.class.getName());

    public SantoriniRunnable() {
        lockDemand = new Object();
        lockAnswer = new Object();
        lock = new Object();

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

    protected boolean isActive() {
        boolean ret;

        synchronized (lock) {
            ret = isActive;
        }

        return ret;
    }

    protected void setActive(boolean active) {
        synchronized (lock) {
            isActive = active;
        }
    }

    public boolean isChanged() {
        boolean ret;

        synchronized (lock) {
            ret = isChanged;
        }

        return ret;
    }

    public void setChanged(boolean changed) {
        synchronized (lock) {
            isChanged = changed;
        }
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
