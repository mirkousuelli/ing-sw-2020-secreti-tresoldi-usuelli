package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class standardizing the threads' management in the view. It implements {@code Runnable} and allows to customize what {@code Runnable}'s run does through the abstract method startThreads
 */
public abstract class SantoriniRunnable<S> implements Runnable {

    private boolean isActive = false;
    private boolean isChanged = false;

    private Demand<S> demand;
    private Answer<S> answer;

    final Object lockDemand = new Object();
    final Object lockAnswer = new Object();
    public final Object lock = new Object();

    protected static final Logger LOGGER = Logger.getLogger(SantoriniRunnable.class.getName());

    /**
     * Constructor visible only in the package, it acts as the default constructor but it is not public
     */
    SantoriniRunnable() {

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

    boolean isActive() {
        boolean ret;

        synchronized (lock) {
            ret = isActive;
        }

        return ret;
    }

    void setActive(boolean active) {
        synchronized (lock) {
            isActive = active;
        }
    }

    boolean isChanged() {
        boolean ret;

        synchronized (lock) {
            ret = isChanged;
        }

        return ret;
    }

    void setChanged(boolean changed) {
        synchronized (lock) {
            isChanged = changed;
        }
    }

    protected void setDemand(Demand<S> demand) {
        synchronized (lockDemand) {
            this.demand = demand;
        }
    }

    Demand<S> getDemand() {
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
