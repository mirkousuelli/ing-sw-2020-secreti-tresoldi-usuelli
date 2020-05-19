package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SantoriniRunnable<S> implements Runnable {

    private boolean isActive = false;
    private boolean isChanged = false;

    private Demand<S> demand;
    protected Answer<S> answer;

    public final Object lockDemand;
    public final Object lockAnswer;
    public final Object lock;

    private static boolean isViewActive = true;
    public static final Object lockWatchDog = new Object();
    private static final Logger LOGGER = Logger.getLogger(SantoriniRunnable.class.getName());

    public SantoriniRunnable() {
        lockDemand = new Object();
        lockAnswer = new Object();
        lock = new Object();
    }

    @Override
    public void run() {
        setActive(true);

        try {
            Thread watchDogThread = watchDogThread();
            startThreads(watchDogThread);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Connection closed from the client side", e);
        } finally {
            setActive(false);
        }
    }

    protected Thread watchDogThread() {
        Thread t = new Thread(
                () -> {
                    try {
                        synchronized (lockWatchDog) {
                            while (isViewActive) lockWatchDog.wait();
                        }
                    } catch (Exception e){
                        if (!(e instanceof InterruptedException))
                            LOGGER.log(Level.INFO, e, () -> "Failed to receive!!" + e.getMessage());
                    }
                }
        );
        t.start();
        return t;
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

        synchronized (lockWatchDog) {
            if (!active) {
                isViewActive = false;
                lockWatchDog.notifyAll();
            }
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

    protected abstract void startThreads(Thread watchDogThread) throws InterruptedException;
}
