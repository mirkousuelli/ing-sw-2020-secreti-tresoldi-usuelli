package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.gui.frame.SantoriniFrame;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;

import javax.swing.*;

public class GUI<S> extends ClientView<S> {

    private JFrame frame;
    private final Object lockReady;
    private boolean isReady;

    public GUI() {
        lockReady = new Object();
        isReady = false;
    }

    public void createAndStartGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new SantoriniFrame(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    protected void startThreads(Thread watchDogThread) throws InterruptedException {
        synchronized (lockReady) {
            while (!isReady) lockReady.wait();
        }

        Thread read = asyncReadFromModel();
        watchDogThread.join();
        read.interrupt();
    }

    @Override
    protected void update() {
        synchronized (clientModel.lock) {
            //System.out.println("EEEEEEEEEEEEEEEEEEEEE");
            ((SantoriniFrame) frame).getMain().getCurrentPanel().updateFromModel();
        }
    }

    public void initialRequest(String name, String ip, int port) {
        new Thread(
                this
        ).start();

        runThreads(name, ip, port);

        synchronized (lockReady) {
            isReady = true;
            lockReady.notifyAll();
        }
    }

    public void generateDemand(DemandType demandType, S payload) {
        createDemand(new Demand<>(demandType, payload));
        becomeFree();
    }

    public void free() {
        becomeFree();
    }
}
