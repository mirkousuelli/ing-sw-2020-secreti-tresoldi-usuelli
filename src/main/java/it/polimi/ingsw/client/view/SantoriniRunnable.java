package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SantoriniRunnable<S> implements Runnable {

    private boolean isActive = false;
    private boolean isChanged = false;

    private Demand<S> demand;
    private Answer<S> answer;

    public final Object lockDemand = new Object();
    public final Object lockAnswer = new Object();
    public final Object lock = new Object();

    private static final Logger LOGGER = Logger.getLogger(SantoriniRunnable.class.getName());

    public SantoriniRunnable() {

    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
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

    /**
     * Customizes what the object's {@code run} method does
     *
     * @throws InterruptedException the exception thrown when the thread is interrupted
     */
    protected abstract void startThreads() throws InterruptedException;
}
