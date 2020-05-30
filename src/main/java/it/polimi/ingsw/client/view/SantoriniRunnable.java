package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SantoriniRunnable implements Runnable {

    private boolean isActive = false;
    private boolean isChanged = false;

    private Demand demand;
    private Answer answer;

    public final Object lockDemand = new Object();
    public final Object lockAnswer = new Object();
    public final Object lock = new Object();

    private static final Logger LOGGER = Logger.getLogger(SantoriniRunnable.class.getName());

    public SantoriniRunnable() {}

    @Override
    public void run() {
        setActive(true);

        try {
            startThreads();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Connection closed from the client side", e);
        } finally {
            setActive(false);
        }
    }

    public boolean isActive() {
        boolean ret;

        synchronized (lock) {
            ret = isActive;
        }

        return ret;
    }

    public void setActive(boolean active) {
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

    protected void setDemand(Demand demand) {
        synchronized (lockDemand) {
            this.demand = demand;
        }
    }

    public Demand getDemand() {
        Demand temp;

        synchronized (lockDemand) {
            temp = demand;
        }
        return temp;
    }

    public Answer getAnswer() {
        Answer temp;

        synchronized (lockAnswer) {
            temp = answer;
        }

        return temp;
    }

    protected void setAnswer(Answer answer) {
        synchronized (lockAnswer) {
            this.answer = answer;
        }
    }

    protected abstract void startThreads() throws InterruptedException;
}
