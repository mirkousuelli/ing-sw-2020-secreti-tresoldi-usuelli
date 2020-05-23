/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a player
 * <p>
 *     It contains its nickname, the list of workers, the card he owns, the worker he last picked as current and
 *     a list of maluses he has (caused by opponents' Gods)
 */
public class Player {
    public final String nickName;
    private final List<Worker> workers;
    private Card card;
    private Worker currentWorker;
    private final List<Malus> malusList;
    private final int NUM_WORKERS = 2;

    /**
     * Constructor of the player, initialising elements like his workers and card
     *
     * @param nickName String representing the nickname of the player
     */
    public Player(String nickName) {
        this.nickName = nickName;
        workers = new ArrayList<>(NUM_WORKERS);
        card = null;
        currentWorker = null;
        malusList = new ArrayList<>();
    }

    /**
     * @return the nickname of the player
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @return the number of workers of the player
     */
    public int getNumWorkers() {
        return NUM_WORKERS;
    }

    /**
     * Method that connects a worker with his owner and sets his initial position on the board
     *
     * @param id Number between 0 and 1 that represents each worker of every player
     * @param position Block where the worker is placed at the beginning
     * @return {@code true} if the worker is initialised properly, {@code false} if the chosen id is not correct or if the chosen position is occupied
     */
    public boolean initializeWorkerPosition(int id, Block position) {
        if (!position.isFree()) return false;
        if (id != 1 && id != 2) return false;

        this.addWorker(new Worker(id, position));

        if  (id == 1)
            setCurrentWorker(getWorker(id));

        // setting male and female
        if (this.workers.size() == NUM_WORKERS) {
            this.workers.get(0).setGender(!this.workers.get(1).isMale());
        }

        return true;
    }

    /**
     * Method that adds the worker to the list of workers of the player
     *
     * @param newWorker Worker that is added
     * @return {@code true} if the worker is added properly, {@code false} if the player has already got both his workers
     */
    public boolean addWorker(Worker newWorker) {
        if (this.workers.size() < NUM_WORKERS) {
            if (this.workers.size() == 1) {
                newWorker.setGender(!this.workers.get(0).isMale());
            }
            this.workers.add(newWorker);
            return true;
        }
        return false;
    }

    /**
     * Method that removes the worker from the list of workers of the player
     *
     * @param worker Worker that is removed
     */
    public void removeWorker(Worker worker) {
        workers.remove(worker);
    }

    /**
     * @return the list of workers of the player
     */
    public List<Worker> getWorkers() {
        return new ArrayList<>(workers);
    }

    /**
     * @return the current worker
     */
    public Worker getCurrentWorker() {
        return currentWorker;
    }

    /**
     * @param id of the worker
     * @return the worker with the chosen id
     */
    public Worker getWorker(int id) {
        for (Worker w : workers) {
            if (w.getId() == id)
                return w;
        }

        return null;
    }

    /**
     * Method that sets the current worker of the player to the chosen one
     *
     * @param currentWorker the current worker of the player
     */
    public void setCurrentWorker(Worker currentWorker) {
        this.currentWorker = currentWorker;
    }

    /**
     * @return the player's card
     */
    public Card getCard() {
        return card;
    }

    /**
     * Method that sets the player's card
     *
     * @param card the current worker of the player
     */
    public void setCard(Card card) {
        this.card = card;
    }

    /**
     * Method that adds a malus to the player
     *
     * @param malusPlayer the Malus that is added to the malus list of the player
     */
    public void addMalus(Malus malusPlayer) {
        Malus found = malusList.stream()
                .filter(m -> m.equals(malusPlayer))
                .reduce(null, (m1, m2) -> m1 != null ? m1 : m2);

        if (found == null)
            malusList.add(malusPlayer);
    }

    /**
     * @return the list of the maluses that the player has
     */
    public List<Malus> getMalusList() {
        return new ArrayList<>(malusList);
    }

    /**
     * Method that removes the malus from the player
     */
    public void removeMalus() {
        malusList.removeIf(m -> !m.isPermanent() && m.getNumberOfTurns() == 0);
    }

    /**
     * Method that removes permanent malus from the player
     */
    public Malus removePermanentMalus() {
        for (Malus m : malusList) {
            if (m.isPermanent()) {
                malusList.removeIf(malus -> malus.equals(m));
                return  m;
            }
        }

        return null;
    }
}