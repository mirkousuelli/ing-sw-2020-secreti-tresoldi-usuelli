package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.gui.frame.SantoriniFrame;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;

import javax.swing.*;

/**
 * Class that represents the graphical user interface. It starts if the person opens the .jar file, otherwise if the
 * game is launched by command line the interface will be the CLI.
 * <p>
 * It extends the abstract class {@link ClientView}
 */
public class GUI<S> extends ClientView<S> {

    private JFrame frame;
    private final Object lockReady;
    private boolean isReady;

    /**
     * Constructor of the GUI, creating the object and setting it to not ready yet (it will become available after the
     * initial request).
     */
    public GUI() {
        lockReady = new Object();
        isReady = false;
    }

    /**
     * Method that creates the GUI and then starts it
     */
    public void createAndStartGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new SantoriniFrame(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    protected void startThreads() throws InterruptedException {
        synchronized (lockReady) {
            while (!isReady) lockReady.wait();
        }

        Thread read = asyncReadFromModel();

        read.join();
    }

    @Override
    protected void update() {
        synchronized (clientModel.lock) {
            if (getAnswer().getHeader().equals(AnswerType.RELOAD))
                ((SantoriniFrame) frame).getMain().reload();
            else
                ((SantoriniFrame) frame).getMain().getCurrentPanel().updateFromModel();
        }
    }

    /**
     * Method that receives the initial requests and start threads, receiving the nickname of the player, its ip and
     * the port that is used
     *
     * @param name nickname of the person that sends the initial request
     * @param ip   the ip of the player
     * @param port the port used
     */
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

    /**
     * Method that generates the demand with the given type and payload
     *
     * @param demandType type of the demand
     * @param payload    the content that is sent with the demand
     */
    public void generateDemand(DemandType demandType, S payload) {
        createDemand(new Demand<>(demandType, payload));
        becomeFree();
    }

    /**
     * Method that sets the interface to free
     */
    public void free() {
        becomeFree();
    }
}
