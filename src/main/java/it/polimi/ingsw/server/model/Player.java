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

public class Player {
    /*@class
     * it is used to represent a player, it contains a reference to the player's card and one to its workers
     */

    public final String nickName;
    private final List<Worker> workers;
    private Card card;
    private Worker currentWorker;
    private final List<Malus> malusList;
    private final int NUM_WORKERS = 2;

    public Player(String nickName) {
        /*@constructor
         * it sets the name of the player and creates its 2 workers
         */

        this.nickName = nickName;
        workers = new ArrayList<>(NUM_WORKERS);
        card = null;
        currentWorker = null;
        malusList = new ArrayList<>();
    }

    public String getNickName() {
        return nickName;
    }

    public int getNumWorkers() {
        return NUM_WORKERS;
    }

    public boolean initializeWorkerPosition(int id, Block position) {
        /*@function
         * it sets the initial position of the current worker
         */

        if (!position.isFree()) return false;
        if (id != 1 && id != 2) return false;

        this.addWorker(new Worker(id, position));

        // setting male and female
        if (this.workers.size() == NUM_WORKERS) {
            this.workers.get(0).setGender(!this.workers.get(1).isMale());
        }

        return true;
    }

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

    public void removeWorker(Worker worker) {
        workers.remove(worker);
    }

    public List<Worker> getWorkers() {
        /*@getter
         * it returns player's workers
         */

        return new ArrayList<>(workers);
    }

    public Worker getCurrentWorker() {
        /*@getter
         * it gets the current worker
         */

        return currentWorker;
    }

    public Worker getWorker(int id) {
        for (Worker w : workers) {
            if (w.getId() == id)
                return w;
        }

        return null;
    }

    public void setCurrentWorker(Worker currentWorker) {
        /*@setter
         * it sets the current worker to the designated one
         */

        this.currentWorker = currentWorker;
    }

    public Card getCard() {
        /*@getter
         * it gets the player's card
         */

        return card;
    }

    public void setCard(Card card) {
        /*@setter
         * it sets the player's card
         */

        this.card = card;
    }

    public void addMalus(Malus malusPlayer) {
        Malus found = malusList.stream()
                .filter(m -> m.equals(malusPlayer))
                .reduce(null, (m1, m2) -> m1 != null ? m1 : m2);

        if (found == null)
            malusList.add(malusPlayer);
    }

    public List<Malus> getMalusList() {
        return new ArrayList<>(malusList);
    }

    public void removeMalus() {
        malusList.removeIf(m -> !m.isPermanent() && m.getNumberOfTurns() == 0);
    }
}