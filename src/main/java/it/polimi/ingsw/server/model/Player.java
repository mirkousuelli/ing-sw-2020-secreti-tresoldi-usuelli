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
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a player
 * <p>
 * It contains its nickname, the list of workers, the card he owns, the worker he last picked as current and
 * a list of maluses he has (caused by opponents' Gods)
 */
public class Player {
    public final String nickName;
    private final List<Worker> workers;
    private Card card;
    private Worker currentWorker;
    private final List<Malus> malusList;
    private static final int NUM_WORKERS = 2;

    /**
     * Constructor of the player, initialising elements like his workers and card
     *
     * @param nickName the string representing player's nickname
     */
    public Player(String nickName) {
        this.nickName = nickName;
        workers = new ArrayList<>(NUM_WORKERS);
        card = null;
        currentWorker = null;
        malusList = new ArrayList<>();
    }

    public String getNickName() {
        return nickName;
    }

    /**
     * Method that connects a worker with his owner and sets his initial position on the board
     *
     * @param id       a number between 1 and 2 that identifies each worker of every player
     * @param position the block where the worker is placed at the beginning
     * @return {@code true} if the worker is initialised properly, {@code false} if the chosen id is not correct or
     * if the chosen position is occupied
     */
    public boolean initializeWorkerPosition(int id, Block position) {
        if (!position.isFree()) return false;
        if (id != 1 && id != 2) return false;

        this.addWorker(new Worker(id, position));

        if (id == 1)
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
     * @param newWorker the worker that is added
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
     * @param worker the worker that is removed
     */
    public void removeWorker(Worker worker) {
        workers.remove(worker);
    }

    /**
     * Method that removes both workers from the list of workers of the player
     */
    public void removeWorkers() {
        workers.stream().map(Worker::getLocation).forEach(Block::removePawn);
        workers.clear();
    }

    public List<Worker> getWorkers() {
        return new ArrayList<>(workers);
    }

    public Worker getCurrentWorker() {
        return currentWorker;
    }

    /**
     * Method that returns the worker with the chosen id
     *
     * @param id worker's id
     * @return the worker with the chosen id
     */
    public Worker getWorker(int id) {
        for (Worker w : workers) {
            if (w.getId() == id)
                return w;
        }

        return null;
    }

    public void setCurrentWorker(Worker currentWorker) {
        this.currentWorker = currentWorker;
    }

    public Card getCard() {
        return card;
    }

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

    public List<Malus> getMalusList() {
        return malusList;
    }

    /**
     * Method that removes the malus from the player
     */
    public void removeMalus() {
        malusList.removeIf(m -> !m.isPermanent() && m.getNumberOfTurns() <= m.getNumberOfTurnsUsed());
    }

    /**
     * Method that removes permanent malus from the player
     *
     * @return the permanent malus that is removed
     */
    public Malus removePermanentMalus() {
        for (Malus m : malusList) {
            if (m.isPermanent()) {
                malusList.removeIf(malus -> malus.equals(m));
                return m;
            }
        }

        return null;
    }

    /**
     * Method that resets a player: it removes maluses, his card, and his workers (that are also removed from the board)
     */
    public void reset() {
        malusList.clear();
        card = null;
        workers.forEach(w -> w.getLocation().removePawn());
        workers.clear();
    }
}